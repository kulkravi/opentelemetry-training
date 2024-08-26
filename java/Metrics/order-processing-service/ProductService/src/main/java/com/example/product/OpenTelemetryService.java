package com.example.product;

import org.springframework.context.annotation.Configuration;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

@Configuration
public class OpenTelemetryService {

    private OpenTelemetrySdk openTelemetrySdk;
    private String serviceName;
    private String serviceVersion;

    public OpenTelemetrySdk getOpenTelemetrySdk() {
        return openTelemetrySdk;
    }
    public String getServiceName() {
        return serviceName;
    }
    public String getServiceVersion() {
        return serviceVersion;
    }
    public OpenTelemetryService(ServiceConfiguration productServiceConfiguration) {
        openTelemetrySdk = initializeOpenTelemetry(productServiceConfiguration.getServiceName(), productServiceConfiguration.getServiceVersion());
    };
    public OpenTelemetrySdk initializeOpenTelemetry(String serviceName, String serviceVersion) {

        final AttributeKey<String> SERVICE_NAME = AttributeKey.stringKey("service.name");
        final AttributeKey<String> SERVICE_VERSION = AttributeKey.stringKey("service.version");

        Resource resource = Resource.create(Attributes.builder()
            .put(SERVICE_NAME, serviceName)
            .put(SERVICE_VERSION, serviceVersion)
            .build());

        SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
            .registerMetricReader(PeriodicMetricReader
                .builder(OtlpGrpcMetricExporter
                    .builder()
                    .build())
                .build())
            .setResource(resource)
            .build();

        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
            .setMeterProvider(sdkMeterProvider)
            .build();

        return sdk;
    }
}
