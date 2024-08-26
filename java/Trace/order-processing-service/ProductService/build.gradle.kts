plugins {
    id("java")
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_22
    targetCompatibility = JavaVersion.VERSION_22
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.opentelemetry:opentelemetry-api");
    implementation("io.opentelemetry:opentelemetry-sdk");
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:1.37.0");
    implementation("io.opentelemetry:opentelemetry-exporter-logging");
    implementation("io.opentelemetry:opentelemetry-exporter-zipkin:1.40.0");
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0");
    compileOnly("javax.servlet:javax.servlet-api:4.0.1");
    implementation("com.linecorp.armeria:armeria:1.29.3");
}
