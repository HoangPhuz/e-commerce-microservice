package com.nhom_09.productservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
@Data
@Builder
public class Product {
    @Id
    private String id;

    @Indexed(unique = true)
    private String sku;

    private String name;
    private String description;
    private BigDecimal price;
    private List<String> categories;
    private Map<String, Object> attributes;
    private String imageUrl;

    private int availableQuantity; // Số lượng có sẵn để bán
    private int reservedQuantity; // Số lượng đang tạm giữ

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;


}
