package com.nhom_09.paymentservice.kafka;

import com.nhom_09.paymentservice.model.Payment;
import com.nhom_09.paymentservice.model.PaymentStatus;
import com.nhom_09.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final PaymentRepository paymentRepository;
    private final KafkaProducerService kafkaProducer;

    @Transactional
    public void processRefund(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán cho đơn hàng:" + orderId));

        // Idempotency Check: Chỉ hoàn tiền nếu thanh toán đã thành công và chưa được hoàn
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            payment.setPaymentStatus(PaymentStatus.REFUND_PENDING);
            paymentRepository.save(payment);

            log.info("Processing refund for order ID: {}", orderId);
            // --- Giả lập gọi API hoàn tiền của cổng thanh toán ---

            payment.setPaymentStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
            log.info("Refund successful for order ID: {}", orderId);
        } else {
            log.warn("Ignoring refund request for order ID: {} with status: {}", orderId, payment.getPaymentStatus());
        }
    }

}