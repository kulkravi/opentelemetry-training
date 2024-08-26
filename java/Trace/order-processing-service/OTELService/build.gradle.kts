plugins {
    id("java");
    // id("java-library");
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
    implementation("io.opentelemetry:opentelemetry-api");
    implementation("io.opentelemetry:opentelemetry-sdk");
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:1.37.0");
    implementation("io.opentelemetry:opentelemetry-exporters-logging:0.9.1");
    implementation("io.opentelemetry:opentelemetry-exporter-zipkin:1.40.0");
}

// tasks.named("bootJar") {
    // enabled = false
// }
