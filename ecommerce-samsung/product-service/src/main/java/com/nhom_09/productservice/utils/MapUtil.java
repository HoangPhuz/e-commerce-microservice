package com.nhom_09.productservice.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class MapUtil {
    public static BigDecimal parseBigDecimalSafe(String value){
        try {
            return (value != null && !value.isBlank()) ? new BigDecimal(value) : null;
        } catch (NumberFormatException e) {
            log.warn("Không thể parse BigDecimal từ giá trị: {}", value);
            return null;
        }
    }

}
