package com.nhom_09.productservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank
    private String sku;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(0)
    private BigDecimal price;

    @NotNull
    @Min(0)
    private int initialQuantity;

    private List<String> categories;

    private Map<String, Object> attributes;

    private int availableQuantity;


}
