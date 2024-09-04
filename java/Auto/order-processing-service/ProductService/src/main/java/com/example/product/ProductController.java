package com.example.product;

import java.util.ArrayList;
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

@RestController
@RequestMapping("/products")
public class ProductController {

  private final List<Product> products = new ArrayList<>();
  private final AtomicLong counter = new AtomicLong();
  private static Logger logger;
  private static ServiceConfiguration productServiceConfiguration;

  @Autowired
  ProductController(ServiceConfiguration psc) {

    productServiceConfiguration = psc;
    logger = LoggerFactory.getLogger(productServiceConfiguration.getServiceName());
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
    logger.debug("Ending add product");
    return product;
  }

  @GetMapping
  public List<Product> getAllProducts() {
    logger.debug("Products: getAllProducts");
    return products;
  }

  @GetMapping(path = "/{productName}")
  public synchronized Product getPriceByProductName(@PathVariable("productName") String productName) {
    logger.debug("Products: getPriceByProductName " + productName);
    Product productObject = products.stream().filter(product -> product.getName().equals(productName)).findFirst().orElse(null);
    return productObject;
  }
}