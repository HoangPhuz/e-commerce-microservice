package com.nhom_09.paymentservice.kafka;

import com.nhom_09.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final KafkaService kafkaService;

    @KafkaListener(topics = "RefundMoney", groupId = "payment-group")
    public void handleRefundMoney(String orderId) {
        log.info("Đã nhận sự kiện RefundMoney cho đơn hàng ID: {}", orderId);
        kafkaService.processRefund(orderId);
    }
}
