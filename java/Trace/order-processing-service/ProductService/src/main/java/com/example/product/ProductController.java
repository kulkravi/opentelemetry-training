package com.example.product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final List<Product> products = new ArrayList<>();
  private final AtomicLong counter = new AtomicLong();
  private final Logger logger;
  private OpenTelemetrySdk openTelemetrySdk;
  ServiceConfiguration productServiceConfiguration;
  private Tracer tracer;

  @Autowired
  ProductController(ServiceConfiguration productServiceConfiguration, OpenTelemetryService openTelemetryService) {

    this.productServiceConfiguration = productServiceConfiguration;
    this.logger = LoggerFactory.getLogger(productServiceConfiguration.getServiceName());
    openTelemetrySdk = openTelemetryService.getOpenTelemetrySdk();
    tracer = openTelemetrySdk.getTracer(this.productServiceConfiguration.getServiceName());
    System.out.println(this.productServiceConfiguration.getServiceName());
  }

  @PostMapping
  public Product addProduct(@RequestBody Product product) {
    logger.debug("Starting add product");
    final Span currentSpan = tracer.spanBuilder("Add Product").startSpan();
    Scope scope = currentSpan.makeCurrent();
    try {
      if (product.getName() == null) {
        logger.error("Product name not supplied while adding one: product(" + product + ")");
        throw new Exception("Product name not supplied while adding one: product(" + product + ")");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    product.setId(String.valueOf(counter.incrementAndGet()));
    products.add(product);
    scope.close();
    currentSpan.end();
    logger.debug("Ending add product");
    return product;
  }

  @GetMapping
  public List<Product> getAllProducts() {
    final Span currentSpan = tracer.spanBuilder("Get Products").startSpan();
    Scope scope = currentSpan.makeCurrent();
    logger.debug("Products: getAllProducts");
    scope.close();
    currentSpan.end();
    return products;
  }

  @GetMapping(path = "/{productName}")
  public synchronized Product getPriceByProductName(@PathVariable("productName") String productName) {
    final Span currentSpan = tracer.spanBuilder("Get Product").startSpan();
    Scope scope = currentSpan.makeCurrent();
    logger.debug("Products: getPriceByProductName " + productName);
    Product productObject = products.stream().filter(product -> product.getName().equals(productName)).findFirst().orElse(null);
    scope.close();
    currentSpan.end();
    return productObject;
  }
}