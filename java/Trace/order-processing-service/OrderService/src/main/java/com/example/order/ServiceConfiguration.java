package com.example.order;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "order.processing")
public class ServiceConfiguration {

    private String serviceName;
    private String serviceVersion;
    private String productServiceURL;
    private String otelCollectorTracesURL;

    // Getters and Setters
    public String getProductServiceURL() {
        return productServiceURL;
    }

    public void setProductServiceURL(String productServiceURL) {
        this.productServiceURL = productServiceURL;
    }

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
