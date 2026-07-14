# Core API Service Starter

A high-performance, modern REST API starter built using Spring Boot 4, Java 25 (LTS), and PostgreSQL. 

---

## Technical Specifications
* **Java Runtime:** Java 25 (LTS)
* **Framework:** Spring Boot 4.1.0
* **Build System:** Gradle (Kotlin DSL)
* **Database Target:** PostgreSQL

---

## Core Dependencies

### 1. Application Architecture Layer
* **`spring-boot-starter-webmvc`**
  * *Purpose:* Provides the foundational components for building multi-threaded, robust RESTful APIs.
* **`spring-boot-starter-restclient`**
  * *Purpose:* Equips the app with a modern, synchronous, and fluent HTTP client to handle outbound service communication cleanly.
* **`spring-boot-starter-data-jpa`**
  * *Purpose:* Integrates Hibernate ORM data abstractions to map application models directly to relational database tables.
* **`spring-boot-starter-liquibase`**
  * *Purpose:* Manages version-controlled, incremental database schema changes to eliminate unpredictable DDL drift.
* **`spring-boot-starter-actuator`**
  * *Purpose:* Exposes essential infrastructure metrics, readiness/liveness health checks, and service diagnostic points.

### 2. Runtime Drivers & Utilities
* **`postgresql`** (`runtimeOnly`)
  * *Purpose:* The official wire-protocol driver required by the connection pool to execute queries against your PostgreSQL instances.
* **`micrometer-registry-prometheus`** (`runtimeOnly`)
  * *Purpose:* Formats Actuator metrics out of the box so Prometheus servers can scrape them seamlessly for visualization (e.g., Grafana).
* **`lombok`** (`compileOnly` & `annotationProcessor`)
  * *Purpose:* Automatically hooks into the compile-time cycle to eliminate Java boilerplate like getters, setters, and builder patterns.

---

## Test Automation Suite

* **`spring-boot-starter-webmvc-test`**
  * *Purpose:* Facilitates lightweight, isolated slice testing of the HTTP web layer (`MockMvc`) without booting the entire context.
* **`spring-boot-starter-restclient-test`**
  * *Purpose:* Simplifies the simulation and wire-mock assertions of outbound HTTP client behaviors.
* **`spring-boot-starter-data-jpa-test`**
  * *Purpose:* Isolates the repository layers to verify native SQL mappings, executing test queries inside rollback-guaranteed transactions.
* **`spring-boot-starter-liquibase-test`**
  * *Purpose:* Asserts the overall syntax and integrity of your Liquibase changelog execution files before they run in production.
* **`spring-boot-starter-actuator-test`**
  * *Purpose:* Verifies health check formatting configurations and metrics under automation constraints.
* **`junit-platform-launcher`** (`testRuntimeOnly`)
  * *Purpose:* Essential structural hook for build processes and IDEs to systematically discover and run JUnit 5 test cases.