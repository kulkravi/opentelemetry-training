package com.example.otel;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.exporter.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

public class OpenTelemetryService {

    private OpenTelemetrySdk openTelemetrySdk;
    private String serviceName;
    private String serviceVersion;
    private String collectorTracesURL;

    public OpenTelemetrySdk getOpenTelemetrySdk() {
        return openTelemetrySdk;
    }
    public String getServiceName() {
        return serviceName;
    }
    public String getServiceVersion() {
        return serviceVersion;
    }
    public String getCollectionTracesURL() {
        return collectorTracesURL;
    }
    public OpenTelemetryService(String serviceName, String serviceVersion, String collectorTracesURL) {
        this.openTelemetrySdk = initializeOpenTelemetry();
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.collectorTracesURL = collectorTracesURL;
    }
    public OpenTelemetrySdk initializeOpenTelemetry() {

        final AttributeKey<String> SERVICE_NAME = AttributeKey.stringKey("service.name");
        final AttributeKey<String> SERVICE_VERSION = AttributeKey.stringKey("service.version");

        Resource resource = Resource.create(Attributes.builder()
            .put(SERVICE_NAME, serviceName)
            .put(SERVICE_VERSION, serviceVersion)
            .build());

        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create()))
        .addSpanProcessor(BatchSpanProcessor
            .builder(ZipkinSpanExporter
                .builder()
                .setEndpoint(collectorTracesURL)
                .build())
            .build())
        .setResource(resource)
        .build();

        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
            .setTracerProvider(sdkTracerProvider)
            .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
            .build();

        return sdk;
    }
}
