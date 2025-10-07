package com.nhom_09.productservice.kafka;


import com.nhom_09.productservice.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final KafkaService kafkaService;

    @KafkaListener(topics = "OrderCreated", groupId = "product-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Đã nhận sự kiện OrderCreated cho ID đơn hàng: {}", event.getOrderId());
        kafkaService.reserveStock(event);
    }

    @KafkaListener(topics = "ReturnStock", groupId = "product-group")
    public void handleReturnStock(String orderId) {
        log.info("Đã nhận sự kiện ReturnStock cho ID đơn hàng: {}", orderId);
        kafkaService.returnStock(orderId);
    }
}
