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
}

tasks.named<JavaExec>("bootRun") {
  environment("OTEL_SERVICE_NAME", "OrderService")
  jvmArgs("-javaagent:opentelemetry-javaagent.jar")
}
