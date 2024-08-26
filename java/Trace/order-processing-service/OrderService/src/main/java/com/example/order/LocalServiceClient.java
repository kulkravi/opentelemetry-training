package com.example.order;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.order.OpenTelemetryService;
import com.example.product.Product;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import jakarta.annotation.Nullable;
import reactor.core.publisher.Mono;

@Service
public class LocalServiceClient {

    private static final Logger logger = LoggerFactory.getLogger("OrderService");
    private Tracer tracer;
    private String productServiceURL;
    private OpenTelemetrySdk openTelemetrySdk;
    HashMap<String, String> currentMap;

    public LocalServiceClient(ServiceConfiguration orderServiceConfiguration, OpenTelemetryService openTelemetryService) {

        logger.info("Started Local Client");
        openTelemetrySdk = openTelemetryService.getOpenTelemetrySdk();
        productServiceURL = orderServiceConfiguration.getProductServiceURL();
        System.out.println("Product service is at " + productServiceURL);
        tracer = openTelemetrySdk.getTracer(orderServiceConfiguration.getServiceName());
    }

    private enum InjectHeaders implements TextMapSetter<HashMap<String, String>> {
    INSTANCE;

      @Override
      public void set(@Nullable HashMap<String, String> carrier, String key, String value) {
        if (carrier == null) {
          return;
        }
        carrier.put(key, value);
        return;
      }
    }
    private void addDefaultHeaders(final HttpHeaders headers) {
      for (var pair : currentMap.entrySet()) {
        headers.add(pair.getKey(), pair.getValue());
      }
    }
    public Mono<Product> getProduct(String name) {
        WebClient webClient;
        logger.debug("Getting product details for " + name);
        Mono<Product> result;
        HashMap<String, String> map = new HashMap<String, String>();
        
        final Span currentSpan = tracer.spanBuilder("Product Client").startSpan();
        Scope scope = currentSpan.makeCurrent();
        openTelemetrySdk.getPropagators().getTextMapPropagator().inject(Context.current(), map, InjectHeaders.INSTANCE);

        WebClient.Builder webClientBuilder = WebClient.builder();
        currentMap = map;
        webClient = webClientBuilder
                   .baseUrl(productServiceURL)
                   .defaultHeaders(this::addDefaultHeaders)
                   .build();

        result = webClient.get()
                .uri("/products/".concat(name))
                .retrieve()
                .bodyToMono(Product.class);
        scope.close();
        currentSpan.end();
        return result;
    }
}
