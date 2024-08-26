package com.example.product;

import java.util.Collections;
import java.util.Enumeration;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.sdk.OpenTelemetrySdk;

public class ProductInboundInterceptor implements HandlerInterceptor {
    OpenTelemetrySdk openTelemetrySdk;
    public ProductInboundInterceptor(OpenTelemetrySdk openTelemetrySdk) {
        this.openTelemetrySdk = openTelemetrySdk;
    }

    private enum extractHeaders implements TextMapGetter<HttpServletRequest> {
      INSTANCE;
      @Override
      public Iterable<String> keys(@Nonnull HttpServletRequest carrier) {
        Enumeration<String> headerNames=carrier.getHeaderNames();
        return Collections.list(headerNames);
      }

      @Nullable
      public String get(@Nullable HttpServletRequest carrier, @Nonnull String key) {
        if (carrier == null) {
          return null;
        }
        return carrier.getHeader(key);
      }
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // If this API is called by another service that has enabled tracing, let's make that the parent context.
        final Context context = openTelemetrySdk.getPropagators().getTextMapPropagator().extract(Context.current(), request, extractHeaders.INSTANCE);
        context.makeCurrent();
        return true;
    }
}