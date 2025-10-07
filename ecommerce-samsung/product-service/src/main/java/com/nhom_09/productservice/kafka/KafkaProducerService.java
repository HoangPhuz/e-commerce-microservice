package com.nhom_09.productservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendStockReservedEvent(String orderId) {
        kafkaTemplate.send("OrderStockReserved", orderId);
    }

    public void sendStockFailedEvent(String orderId) {
        kafkaTemplate.send("OrderStockFailed", orderId);
    }
}
