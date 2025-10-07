package com.nhom_09.productservice.repository.custom;

import com.nhom_09.productservice.dto.ProductSearchBuilder;
import com.nhom_09.productservice.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface CustomProductRepository {
    List<Product> searchProducts(ProductSearchBuilder productSearchBuilder);
}
