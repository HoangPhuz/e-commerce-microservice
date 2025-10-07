package com.nhom_09.orderservice.kafka;

import com.nhom_09.orderservice.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedEvent(OrderCreatedEvent event){
        kafkaTemplate.send("OrderCreated", event);
    }

    public void sendReturnStockEvent(String orderId){
        kafkaTemplate.send("ReturnStock", orderId);
    }

    public void sendRefundMoneyEvent(String orderId){
        kafkaTemplate.send("RefundMoney", orderId);
    }

    public void sendOrderSuccessEvent(String orderId) {
        kafkaTemplate.send("OrderSuccess", orderId);
    }

    public void sendOrderCancelledEvent(String orderId) {
        kafkaTemplate.send("OrderCancelled", orderId);
    }



}
