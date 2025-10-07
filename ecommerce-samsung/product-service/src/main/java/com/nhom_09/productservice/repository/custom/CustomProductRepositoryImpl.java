package com.nhom_09.productservice.repository.custom;

import com.nhom_09.productservice.dto.ProductSearchBuilder;
import com.nhom_09.productservice.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.query.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomProductRepositoryImpl  implements CustomProductRepository{
    private final MongoTemplate mongoTemplate;

    @Override
    public List<Product> searchProducts(ProductSearchBuilder productSearchBuilder) {
        final Query query = new Query();
        final List<Criteria> criteria = new ArrayList<>();

        String name = productSearchBuilder.getName();

        if(name!=null && !name.isBlank()){
            criteria.add(Criteria.where("name").regex(name, "i"));
        }
        String category = productSearchBuilder.getCategory();
        if(category!=null &&!category.isBlank()){
            criteria.add(Criteria.where("categories").in(category));
        }
        BigDecimal minPrice = productSearchBuilder.getMinPrice();
        BigDecimal maxPrice = productSearchBuilder.getMaxPrice();
        if(minPrice!=null && maxPrice!=null){
            criteria.add(Criteria.where("price").gte(minPrice).lte(maxPrice));
        } else if (minPrice!=null) {
            criteria.add(Criteria.where("price").gte(minPrice));
        } else if (maxPrice != null) {
            criteria.add(Criteria.where("price").lte(maxPrice));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Product.class);

    }
}
