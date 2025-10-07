package com.nhom_09.productservice.service;

import com.nhom_09.productservice.dto.request.ProductRequest;
import com.nhom_09.productservice.dto.ProductSearchBuilder;
import com.nhom_09.productservice.model.Product;
import com.nhom_09.productservice.repository.ProductRepository;
import com.nhom_09.productservice.repository.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.core.internal.shaded.fdp.v2_19_2.JavaBigDecimalParser.parseBigDecimal;
import static com.nhom_09.productservice.utils.MapUtil.parseBigDecimalSafe;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    @Transactional
    public Product createProduct(ProductRequest productRequest,  MultipartFile imageFile){
        if(productRepository.existsBySku(productRequest.getSku())){
            throw new IllegalArgumentException("SKU " + productRequest.getSku() + " already exists.");
        }
        // 1. Lưu file ảnh và lấy tên file
        String filename = fileStorageService.save(imageFile);

        // 2. Tạo URL đầy đủ để truy cập file
        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path(filename)
                .toUriString();

        Product product = Product.builder()
                .sku(productRequest.getSku())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .categories(productRequest.getCategories())
                .attributes(productRequest.getAttributes())
                .availableQuantity(productRequest.getInitialQuantity())
                .reservedQuantity(0)
                .imageUrl(imageUrl)
                .build();
        Product savedProduct =  productRepository.save(product);
        log.info("Tạo sản phẩm thành công");

        return savedProduct;

    }

    @Transactional
    public Product updateProduct(String sku, ProductRequest productRequest){
        Product existingProduct = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có mã SKU: " + sku));
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setCategories(productRequest.getCategories());
        existingProduct.setAttributes(productRequest.getAttributes());
        // Cập nhật số lượng tồn kho có thể được xử lý ở một API riêng nếu cần
        existingProduct.setAvailableQuantity(productRequest.getAvailableQuantity());

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Cập nhật sản phẩm {} thành công", sku);
        return updatedProduct;
    }

    @Transactional
    public void deleteProduct(String sku) {
        Product productToDelete = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có mã SKU: " + sku));

        String imageUrl = productToDelete.getImageUrl();
        if (imageUrl != null && !imageUrl.isBlank()) {
            try {
                // Trích xuất tên file từ URL (ví dụ: từ http://.../images/abc.jpg -> lấy abc.jpg)
                String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                // Gọi service để xóa file vật lý
                fileStorageService.delete(filename);
            } catch (Exception e) {
                log.error("Lỗi khi trích xuất hoặc xóa file ảnh cho sản phẩm SKU {}: {}", sku, e.getMessage());
                // Tiếp tục thực hiện xóa sản phẩm khỏi CSDL dù xóa file có thể thất bại
            }
        }


        productRepository.delete(productToDelete);
        log.info("Xóa sản phẩm {} thành công", sku);
    }

    public List<Product> searchProducts(Map<String, String> field) {
        String name = field.getOrDefault("name", null);
        String category = field.getOrDefault("category", null);
        BigDecimal minPrice = parseBigDecimalSafe(field.get("minPrice"));
        BigDecimal maxPrice = parseBigDecimalSafe(field.get("maxPrice"));

        ProductSearchBuilder productSearchBuilder = new ProductSearchBuilder().builder()
                        .name(field.get("name"))
                        .category(field.get("category"))
                        .minPrice(minPrice)
                        .maxPrice(maxPrice)
                        .build();


        log.info("Đang tìm kiếm sản phẩm với các tiêu chí: name={}, category={}, minPrice={}, maxPrice={}",
                name, category, minPrice, maxPrice);

        return productRepository.searchProducts(productSearchBuilder);
    }


    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có mã SKU: " + sku));
    }



}
