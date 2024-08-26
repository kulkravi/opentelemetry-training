package com.example.order;

import com.example.product.Product;

import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.api.metrics.LongCounter;

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

    private LongCounter orderCounter;
    private LongCounter orderQueryCounter;

    public OrderController(ServiceConfiguration osc, OpenTelemetryService openTelemetryService, LocalServiceClient lsc) {
        orderServiceConfiguration = osc;
        logger = LoggerFactory.getLogger(orderServiceConfiguration.getServiceName());
        localServiceClient = lsc;
        logger.debug("Starting Order Controller");
        OpenTelemetrySdk openTelemetrySdk = openTelemetryService.getOpenTelemetrySdk();

        orderCounter = openTelemetrySdk.getMeter(OrderController.class.getName()).counterBuilder("Order-processing.count")
            .setDescription("How many orders have the been placed")
            .build();
        orderQueryCounter = openTelemetrySdk.getMeter(OrderController.class.getName()).counterBuilder("Order-processing.query-count")
            .setDescription("How many times orders have been queried")
            .build();
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
            orderCounter.add(1);
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
        orderQueryCounter.add(1);
        return orders;
    }
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable String id) {
        logger.debug("Orders: getOrderById" + id);
        orderQueryCounter.add(1);
        return orders.stream().filter(order -> order.getId().equals(id)).findFirst().orElse(null);
    }
}

