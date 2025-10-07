package com.nhom_09.productservice.dto;

import com.nhom_09.productservice.dto.request.OrderItemRequest;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreatedEvent {
    private String orderId;
    private String userId;
    //private OrderStatus orderStatus;
    //private BigDecimal totalAmount;
    private List<OrderItemRequest> orderItemRequests;
}
