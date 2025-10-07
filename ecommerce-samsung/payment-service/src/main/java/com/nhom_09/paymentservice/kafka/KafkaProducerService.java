package com.nhom_09.paymentservice.kafka;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendPaymentSuccessEvent(String orderId) {
        kafkaTemplate.send("PaymentSuccess", orderId);
    }

    public void sendPaymentFailedEvent(String orderId) {
        kafkaTemplate.send("PaymentFailed", orderId);
    }
}
