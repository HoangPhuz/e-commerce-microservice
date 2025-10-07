package com.nhom_09.productservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ URL /images/** tới thư mục uploads/images/ trên hệ thống file
        registry.addResourceHandler("/images/**")
                //hệ thống gửi file ảnh laị cho request
                .addResourceLocations("file:uploads/images/");
    }
}
