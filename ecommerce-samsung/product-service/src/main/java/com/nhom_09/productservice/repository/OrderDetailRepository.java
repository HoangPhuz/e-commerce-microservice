package com.nhom_09.productservice.repository;

import com.nhom_09.productservice.model.OrderDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderDetailRepository extends MongoRepository<OrderDetail, String> {
    List<OrderDetail> findByOrderId(String orderId);
}
