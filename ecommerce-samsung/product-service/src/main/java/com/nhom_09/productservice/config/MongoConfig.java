package com.nhom_09.productservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing //Bật tính năng tự động điền các trường @CreatedDate, @LastModifiedDate
public class MongoConfig {
}
