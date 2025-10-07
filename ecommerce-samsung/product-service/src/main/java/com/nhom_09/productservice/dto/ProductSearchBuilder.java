package com.nhom_09.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductSearchBuilder {
    private String name;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
