package com.example.product;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "product.processing")
public class ServiceConfiguration {

    private String serviceName;
    private String serviceVersion;
    private String otelCollectorTracesURL;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getOtelCollectorTracesURL() {
        return otelCollectorTracesURL;
    }

    public void setOtelCollectorTracesURL(String otelCollectorTracesURL) {
        this.otelCollectorTracesURL = otelCollectorTracesURL;
    }
}
