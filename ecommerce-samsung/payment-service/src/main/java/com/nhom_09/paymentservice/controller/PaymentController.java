package com.nhom_09.paymentservice.controller;

import com.nhom_09.paymentservice.dto.PaymentRequest;
import com.nhom_09.paymentservice.model.Payment;
import com.nhom_09.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.createPayment(request);
        return ResponseEntity.ok(payment);
    }
}
