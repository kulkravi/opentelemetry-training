package com.example.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class ProductService{
    public static void main(String[] args) {
        LoggerFactory.getLogger("ProductService");
        SpringApplication.run(ProductService.class, args);
    }
}