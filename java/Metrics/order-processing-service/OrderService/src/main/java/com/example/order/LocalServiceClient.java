package com.example.order;

import com.example.product.Product;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

// This is the Rest client to get product information from ProductService.

@Service
public class LocalServiceClient {

    private static Logger logger;
    private static String productServiceURL;
    HashMap<String, String> currentMap;

    public LocalServiceClient(ServiceConfiguration orderServiceConfiguration) {

        logger = LoggerFactory.getLogger(orderServiceConfiguration.getServiceName());
        logger.info("Started Local Client");
        productServiceURL = orderServiceConfiguration.getProductServiceURL();
    }

    public Mono<Product> getProduct(String name) {
        WebClient webClient;
        logger.debug("Getting product details for " + name);
        Mono<Product> result;
        
        WebClient.Builder webClientBuilder = WebClient.builder();
        webClient = webClientBuilder
                   .baseUrl(productServiceURL)
                   .build();

        result = webClient.get()
                .uri("/products/".concat(name))
                .retrieve()
                .bodyToMono(Product.class);
        return result;
    }
}

