package com.example.product;

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

import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.sdk.OpenTelemetrySdk;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final List<Product> products = new ArrayList<>();
  private final AtomicLong counter = new AtomicLong();
  private static Logger logger;
  private static ServiceConfiguration productServiceConfiguration;
  private LongCounter productCounter;
  private LongCounter productQueryCounter;

  ProductController(ServiceConfiguration psc, OpenTelemetryService openTelemetryService) {

    productServiceConfiguration = psc;
    logger = LoggerFactory.getLogger(productServiceConfiguration.getServiceName());
    OpenTelemetrySdk openTelemetrySdk = openTelemetryService.getOpenTelemetrySdk();


    productCounter = openTelemetrySdk.getMeter(ProductController.class.getName())
      .counterBuilder("Product-processing.count")
      .setDescription("How many products have the been saved")
      .build();

    productQueryCounter = openTelemetrySdk.getMeter(ProductController.class.getName())
      .counterBuilder("Product-processing.query-count")
      .setDescription("How many times products have been queried")
      .build();
  }

  @PostMapping
  public Product addProduct(@RequestBody Product product) {
    logger.debug("Starting add product");
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
    productCounter.add(1);

    logger.debug("Ending add product");
    return product;
  }

  @GetMapping
  public List<Product> getAllProducts() {
    logger.debug("Products: getAllProducts");
    productQueryCounter.add(1);
    return products;
  }

  @GetMapping(path = "/{productName}")
  public synchronized Product getPriceByProductName(@PathVariable("productName") String productName) {
    logger.debug("Products: getPriceByProductName " + productName);
    Product productObject = products.stream().filter(product -> product.getName().equals(productName)).findFirst().orElse(null);
    productQueryCounter.add(1);
    return productObject;
  }
}