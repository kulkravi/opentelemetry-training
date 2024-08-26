plugins {
    id("java")
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_22
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":ProductService"));
    implementation("org.springframework.boot:spring-boot-starter-web");
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.0");
    implementation("io.opentelemetry:opentelemetry-api");
    implementation("io.opentelemetry:opentelemetry-sdk");
    implementation("io.opentelemetry:opentelemetry-semconv:1.30.1-alpha");
    implementation("io.opentelemetry:opentelemetry-exporter-logging");
    implementation("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure");
    implementation("io.opentelemetry:opentelemetry-exporter-prometheus:1.39.0-alpha");
    implementation("org.springframework.boot:spring-boot-starter-webflux");
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:1.37.0");
}
