package com.nhom_09.productservice.controller;

import com.nhom_09.productservice.dto.request.ProductRequest;
import com.nhom_09.productservice.model.Product;
import com.nhom_09.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Phân quyền chỉ cho ADMIN
    public ResponseEntity<Product> createProduct(@RequestPart("product") ProductRequest request,
                                                 @RequestPart("image") MultipartFile imageFile) {

        Product createdProduct = productService.createProduct(request, imageFile);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{sku}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable String sku, @Valid @RequestBody ProductRequest request) {
        Product updatedProduct = productService.updateProduct(sku, request);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{sku}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String sku) {
        productService.deleteProduct(sku);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam Map<String, String> field) {
        return ResponseEntity.ok(productService.searchProducts(field));
    }

}
