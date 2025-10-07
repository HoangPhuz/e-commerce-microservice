package com.nhom_09.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotBlank
    private String orderId;
    @NotNull
    private BigDecimal amount;
}
