package com.example.order;

import com.example.product.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/orders")
public class OrderController {
    // Automatically generate order #
    private final AtomicLong orderId = new AtomicLong();
    // List of orders
    private final List<Order> orders = new ArrayList<>();
    private static Logger logger;
    private static ServiceConfiguration orderServiceConfiguration;
    private static LocalServiceClient localServiceClient;

    public OrderController(ServiceConfiguration osc, LocalServiceClient lsc) {
        orderServiceConfiguration = osc;
        logger = LoggerFactory.getLogger(orderServiceConfiguration.getServiceName());
        localServiceClient = lsc;
        logger.debug("Starting Order Controller");
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        logger.debug("Starting create order");

        try {
            String productName = order.getItem();
            logger.debug("Getting the product details for " + productName);
            Product product = localServiceClient.getProduct(productName).block();
            logger.debug("Got the product details for " + productName);
            // Populate order details and store it in the list
            order.setId(String.valueOf(orderId.incrementAndGet()));
            order.setPrice(product.getPrice());
            // Save the order
            orders.add(order);
            logger.info("Added " + order.toString());
        } catch (Exception e) {
            logger.error("Failed to add new order " + order.toString() + e.getMessage());
            e.printStackTrace();
        } 
        logger.debug("Ending create order");
        return order;
    }
    @GetMapping
    public List<Order> getAllOrders() {
        logger.debug("Orders: getAllOrders");
        return orders;
    }
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable String id) {
        logger.debug("Orders: getOrderById" + id);
        return orders.stream().filter(order -> order.getId().equals(id)).findFirst().orElse(null);
    }
}

