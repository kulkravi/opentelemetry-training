package com.example.order;

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

import com.example.product.Product;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;

@RestController
@RequestMapping("/orders")
public class OrderController {
    // Automatically generate order #
    private final AtomicLong orderId = new AtomicLong();
    // List of orders
    private final List<Order> orders = new ArrayList<>();
    private Tracer tracer;
    private Logger logger;

    private ServiceConfiguration orderServiceConfiguration;
    private LocalServiceClient localServiceClient;
    private OpenTelemetrySdk openTelemetrySdk;

    public OrderController(ServiceConfiguration orderServiceConfiguration, OpenTelemetryService openTelemetryService, LocalServiceClient localServiceClient) {
        this.orderServiceConfiguration = orderServiceConfiguration;
        this.logger = LoggerFactory.getLogger(orderServiceConfiguration.getServiceName());
        openTelemetrySdk = openTelemetryService.getOpenTelemetrySdk();
        tracer = openTelemetrySdk.getTracer(this.orderServiceConfiguration.getServiceName());
        this.localServiceClient = localServiceClient;
    }

    @PostMapping
    public synchronized Order createOrder(@RequestBody Order order) {
        final Span currentSpan = tracer.spanBuilder("Add Order").startSpan();
        Scope scope = currentSpan.makeCurrent();
        logger.debug("Starting create order");
        currentSpan.addEvent("Starting create order");

        try {
            String productName = order.getItem();
            logger.debug("Getting the product details for " + productName);
            currentSpan.setAttribute("Product", productName);
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
            currentSpan.addEvent("Failed to add new order " + order.toString() + e.getMessage());
            e.printStackTrace();
        } finally {
            currentSpan.addEvent("Ending create order");
            scope.close();
            currentSpan.end();
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