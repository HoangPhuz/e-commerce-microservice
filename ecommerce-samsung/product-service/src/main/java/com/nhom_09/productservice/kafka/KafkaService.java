package com.nhom_09.productservice.kafka;

import com.nhom_09.productservice.dto.OrderCreatedEvent;
import com.nhom_09.productservice.dto.request.OrderItemRequest;
import com.nhom_09.productservice.model.OrderDetail;
import com.nhom_09.productservice.model.OrderDetailStatus;
import com.nhom_09.productservice.model.Product;
import com.nhom_09.productservice.repository.OrderDetailRepository;
import com.nhom_09.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final KafkaProducerService kafkaProducerService;


    @Transactional
    public void reserveStock(OrderCreatedEvent event) {
        List<String> skus = event.getOrderItemRequests().stream().map(it-> it.getSku()).toList();
        Map<String, Product> productMap = productRepository.findBySkuIn(skus).stream()
                .collect(Collectors.toMap(Product::getSku, Function.identity()));

        boolean allInStock = true;
        for (OrderItemRequest item : event.getOrderItemRequests()) {
            Product product = productMap.get(item.getSku());
            if (product == null || product.getAvailableQuantity()  < item.getQuantity()) {
                allInStock = false;
                break;
            }
        }

        if (allInStock) {
            List<OrderDetail> detailsToSave = new ArrayList<>();
            for (OrderItemRequest item : event.getOrderItemRequests()) {
                Product product = productMap.get(item.getSku());
                product.setAvailableQuantity(product.getAvailableQuantity() - item.getQuantity());
                product.setReservedQuantity(product.getReservedQuantity() + item.getQuantity());
                productRepository.save(product);

                // TẠO BẢN GHI TRẠNG THÁI
                OrderDetail detail = new OrderDetail();
                detail.setOrderId(event.getOrderId());
                detail.setSku(item.getSku());
                detail.setQuantity(item.getQuantity());
                detail.setStatus(OrderDetailStatus.RESERVED);
                detailsToSave.add(detail);

            }
            orderDetailRepository.saveAll(detailsToSave); // Lưu lại trạng thái
            kafkaProducerService.sendStockReservedEvent(event.getOrderId());
        } else {
            kafkaProducerService.sendStockFailedEvent(event.getOrderId());
        }
    }

    @Transactional
    public void returnStock(String orderId) {

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);

        if (orderDetails.isEmpty()) {
            log.warn("Không tìm thấy thông tin đơn hàng cho mã đơn hàng: {}. Không thể trả hàng về kho.", orderId);
            return;
        }

        List<String> skus = orderDetails.stream().map(OrderDetail::getSku).toList();
        Map<String, Product> productMap = productRepository.findAllById(skus).stream()
                .collect(Collectors.toMap(Product::getSku, Function.identity()));

        for (OrderDetail detail : orderDetails) {
            if (detail.getStatus() == OrderDetailStatus.RESERVED) {
                //Nếu trạng thái là RESERVED -> tiến hành hoàn kho
                Product product = productMap.get(detail.getSku());
                if (product != null) {
                    product.setReservedQuantity(product.getReservedQuantity() - detail.getQuantity());
                    product.setAvailableQuantity(product.getAvailableQuantity() + detail.getQuantity());
                    productRepository.save(product);

                    //Cập nhật trạng thái để lần sau không xử lý nữa
                    detail.setStatus(OrderDetailStatus.STOCK_RETURNED);
                    orderDetailRepository.save(detail);
                    log.info("Đã trả lại {} của SKU {} về kho cho đơn hàng ID: {}", detail.getQuantity(), detail.getSku(), orderId);
                }
            } else {
                    //Nếu trạng thái đã là STOCK_RETURNED -> Bỏ qua
                   log.info("Sự kiện ReturnStock trùng lặp cho SKU {} của đơn hàng ID: {}. Bỏ qua.", detail.getSku(), orderId);
            }
        }



    }
}
