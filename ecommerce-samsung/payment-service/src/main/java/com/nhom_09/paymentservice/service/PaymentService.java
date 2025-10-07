package com.nhom_09.paymentservice.service;


import com.nhom_09.paymentservice.dto.PaymentRequest;
import com.nhom_09.paymentservice.kafka.KafkaProducerService;
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
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaProducerService kafkaProducerService;


    @Transactional
    public Payment createPayment(PaymentRequest request) {
        // Idempotency Check: Kiểm tra xem thanh toán đã tồn tại chưa
        if (paymentRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new IllegalStateException("Thanh toán cho đơn đặt hàng " + request.getOrderId() + " đã được xử lý.");
        }

        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        //Giả lập quá trình gọi cổng thanh toán
        boolean paymentSuccess = Math.random() > 0.2; // 80% thành công

        if (paymentSuccess) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            kafkaProducerService.sendPaymentSuccessEvent(payment.getOrderId());
            log.info("Thanh toán thành công cho ID đơn hàng: {}", payment.getOrderId());
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            kafkaProducerService.sendPaymentFailedEvent(payment.getOrderId());
            log.error("Thanh toán cho đơn hàng ID: {} đã thất bại", payment.getOrderId());
        }
        return paymentRepository.save(payment);
    }

}
