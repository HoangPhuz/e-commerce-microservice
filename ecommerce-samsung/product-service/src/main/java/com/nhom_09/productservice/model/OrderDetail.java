package com.nhom_09.productservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "order_details")
@Data
public class OrderDetail {
    @Id
    private String id;
    private String orderId; // Liên kết logic đến Order
    private String sku;
    private int quantity;
    private OrderDetailStatus status; // Trạng thái của việc giữ kho
}
