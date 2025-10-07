package com.nhom_09.orderservice.repository;

import com.nhom_09.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
