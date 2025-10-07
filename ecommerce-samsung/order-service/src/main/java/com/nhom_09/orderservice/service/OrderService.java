package com.nhom_09.orderservice.service;

import com.nhom_09.orderservice.dto.OrderCreatedEvent;
import com.nhom_09.orderservice.dto.request.CreateOrderRequest;
import com.nhom_09.orderservice.dto.request.OrderItemRequest;
import com.nhom_09.orderservice.kafka.KafkaProducerService;
import com.nhom_09.orderservice.model.Order;
import com.nhom_09.orderservice.model.OrderLineItem;
import com.nhom_09.orderservice.model.OrderStatus;
import com.nhom_09.orderservice.model.PaymentMethod;
import com.nhom_09.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaProducerService kafkaProducerService;


    @Transactional
    public Order createOrder(CreateOrderRequest request, Jwt jwt){
        log.info("Tạo đơn hàng cho người dùng: {}", jwt.getClaimAsString("userId"));

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setId(UUID.randomUUID().toString());
        order.setUserId(jwt.getClaimAsString("userId"));
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderStatus(OrderStatus.PENDING);

        List<OrderLineItem> orderLineItemList = request.getItems().stream()
                .map(it -> {
                    OrderLineItem lineItem = new OrderLineItem();
                    lineItem.setSku(it.getSku());
                    lineItem.setQuantity(it.getQuantity());
                    lineItem.setPriceAtPurchase(it.getPrice());
                    lineItem.setOrder(order); // Thiết lập mối quan hệ
                    return lineItem;
                }).toList();

        order.setOrderLineItems(orderLineItemList);

        BigDecimal totalAmount = orderLineItemList.stream()
                .map(it -> it.getPriceAtPurchase().multiply(BigDecimal.valueOf(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        log.info("Đơn hàng {} đã được tạo thành công với trạng thái PENDING", savedOrder.getId());
        //List<OrderItemRequest> orderItemRequest =  savedOrder.getOrderLineItems();

        List<OrderItemRequest> itemDetails = request.getItems();

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent().builder()
                .orderId(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .orderStatus(savedOrder.getOrderStatus())
                .totalAmount(savedOrder.getTotalAmount())
                .orderItemRequests(itemDetails)
                .build();

        kafkaProducerService.sendOrderCreatedEvent(orderCreatedEvent);
        log.info("Đã gửi sự kiện OrderCreated đến Kafka cho ID đơn hàng: {}", savedOrder.getId());
        return savedOrder;
    }


    @KafkaListener(topics = "OrderStockReserved", groupId = "order-group")
    @Transactional
    public void handleStockReserved(String orderId){

        Order order = orderRepository.findById(orderId.replaceAll("^\"|\"$", "")).orElse(null);

        if(order == null) return;

        if(order.getPaymentMethod() == PaymentMethod.BANKING){
            switch (order.getOrderStatus()){
                case PENDING:
                    order.setOrderStatus(OrderStatus.STOCK_RESERVED);
                    orderRepository.save(order);
                    break;
                case PAYMENT_SUCCESS:
                    order.setOrderStatus(OrderStatus.SUCCESS);
                    orderRepository.save(order);
                    kafkaProducerService.sendOrderSuccessEvent(orderId);
                    break;
                case PAYMENT_FAILED:
                    order.setOrderStatus(OrderStatus.CANCELLED);
                    orderRepository.save(order);
                    kafkaProducerService.sendReturnStockEvent(orderId);
                    break;
            }
        }
        else{
            order.setOrderStatus(OrderStatus.SUCCESS);
            orderRepository.save(order);
            //Gửi sự kiện OrderSuccess để các service khác tổng hợp dữ liệu
            kafkaProducerService.sendOrderSuccessEvent(orderId);

        }

    }

    @KafkaListener(topics = "PaymentSuccess", groupId = "order-group")
    @Transactional
    public void handlePaymentSuccess(String orderId){
        Order order = orderRepository.findById(orderId.replaceAll("^\"|\"$", "")).orElse(null);

        if(order == null) return;


        switch (order.getOrderStatus()){
            case PENDING:
                order.setOrderStatus(OrderStatus.PAYMENT_SUCCESS);
                orderRepository.save(order);
                break;
            case STOCK_RESERVED:
                order.setOrderStatus(OrderStatus.SUCCESS);
                orderRepository.save(order);
                kafkaProducerService.sendOrderSuccessEvent(orderId);
                break;
            case STOCK_FAILED:
                order.setOrderStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                kafkaProducerService.sendRefundMoneyEvent(orderId);
                break;
            case CANCELLED:
                kafkaProducerService.sendRefundMoneyEvent(orderId);
                break;
        }

    }

    @KafkaListener(topics = "OrderStockFailed", groupId = "order-group")
    @Transactional
    public void handleStockFailure(String orderId){
        Order order = orderRepository.findById(orderId.replaceAll("^\"|\"$", "")).orElse(null);

        if(order == null) return;

        if(order.getPaymentMethod() == PaymentMethod.BANKING){
            switch (order.getOrderStatus()){
                case PENDING:
                    order.setOrderStatus(OrderStatus.STOCK_FAILED);
                    orderRepository.save(order);
                    break;
                case PAYMENT_SUCCESS:
                    order.setOrderStatus(OrderStatus.CANCELLED);
                    orderRepository.save(order);
                    kafkaProducerService.sendRefundMoneyEvent(orderId);
                    break;
                case PAYMENT_FAILED:
                    order.setOrderStatus(OrderStatus.CANCELLED);
                    orderRepository.save(order);
                    kafkaProducerService.sendOrderCancelledEvent(orderId);
                    break;
            }

        }
        else{
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            kafkaProducerService.sendOrderCancelledEvent(orderId);

        }

    }

    @KafkaListener(topics = "PaymentFailed", groupId = "order-group")
    @Transactional
    public void handlePaymentFailed(String orderId){
        Order order = orderRepository.findById(orderId.replaceAll("^\"|\"$", "")).orElse(null);

        if(order == null) return;

        switch (order.getOrderStatus()){
            case PENDING:
                order.setOrderStatus(OrderStatus.PAYMENT_FAILED);
                orderRepository.save(order);
                break;
            case STOCK_RESERVED:
                order.setOrderStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                kafkaProducerService.sendReturnStockEvent(orderId);
                break;
            case STOCK_FAILED:
                order.setOrderStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                kafkaProducerService.sendOrderCancelledEvent(orderId);
                break;
        }

    }

}
