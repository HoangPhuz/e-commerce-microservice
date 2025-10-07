package com.nhom_09.orderservice.dto;

import com.nhom_09.orderservice.dto.request.OrderItemRequest;
import com.nhom_09.orderservice.model.OrderLineItem;
import com.nhom_09.orderservice.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreatedEvent {
    private String orderId;
    private String userId;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private List<OrderItemRequest> orderItemRequests;


}
