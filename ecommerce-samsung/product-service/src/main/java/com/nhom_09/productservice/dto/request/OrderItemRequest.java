package com.nhom_09.productservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequest {
    private String sku;

    private String productName;

    private int quantity;

    private BigDecimal price;
}
