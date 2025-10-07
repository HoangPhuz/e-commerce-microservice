package com.nhom_09.orderservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhom_09.orderservice.model.OrderLineItem;
import com.nhom_09.orderservice.model.OrderStatus;
import com.nhom_09.orderservice.model.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateOrderRequest {



    private String userId;

    @NotEmpty(message = "Đơn hàng phải có ít nhất 1 sản phẩm.")
    private List<OrderItemRequest> items;

    @NotBlank(message = "Phương thức thanh toán không được để trống.")
    private PaymentMethod paymentMethod; // <-- THÊM TRƯỜNG MỚI


}
