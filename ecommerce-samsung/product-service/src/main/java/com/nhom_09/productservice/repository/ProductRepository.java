package com.nhom_09.productservice.repository;

import com.nhom_09.productservice.model.Product;
import com.nhom_09.productservice.repository.custom.CustomProductRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String>, CustomProductRepository {
    List<Product> findBySkuIn(List<String> skus);

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);
}
