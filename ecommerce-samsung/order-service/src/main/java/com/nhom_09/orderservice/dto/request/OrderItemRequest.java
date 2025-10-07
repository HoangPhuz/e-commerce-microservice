package com.nhom_09.orderservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class OrderItemRequest {
    @NotBlank(message = "SKU không được để trống.")
    private String sku;

    @NotBlank(message = "Tên sản phẩm không được trống.")
    private String productName;

    @NotNull(message = "Số lượng không được để trống.")
    @Min(value = 1, message = "Số lượng ít nhất là 1")
    private int quantity;

    @NotNull(message = "Giá tiền không được để trống.")
    @Min(value = 0, message = "Giá tiền không được là số âm")
    private BigDecimal price;
}
