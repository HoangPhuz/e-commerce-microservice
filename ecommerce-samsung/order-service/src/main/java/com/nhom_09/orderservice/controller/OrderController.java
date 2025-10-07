package com.nhom_09.orderservice.controller;

import com.nhom_09.orderservice.dto.request.CreateOrderRequest;
import com.nhom_09.orderservice.model.Order;
import com.nhom_09.orderservice.service.OrderService;
import io.micrometer.core.instrument.config.validate.Validated;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;



    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request, @AuthenticationPrincipal Jwt jwt) {
        Order createdOrder = orderService.createOrder(request, jwt);
        // Trả về 202 Accepted để thể hiện rằng yêu cầu đã được chấp nhận và đang được xử lý bất đồng bộ
        return new ResponseEntity<>(createdOrder, HttpStatus.ACCEPTED);
    }




}
