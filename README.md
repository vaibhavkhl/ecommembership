# Membership Program

A demo backend + frontend for a subscription-based membership program: tiered plans (Monthly/Quarterly/Yearly), configurable tier benefits (Silver/Gold/Platinum), and the subscribe/upgrade/downgrade/cancel lifecycle.

---

# Backend

A high-performance, modern REST API built using Spring Boot 4, Java 25 (LTS), and PostgreSQL.

## Technical Specifications
* **Java Runtime:** Java 25 (LTS)
* **Framework:** Spring Boot 4.1.0
* **Build System:** Gradle (Kotlin DSL)
* **Database Target:** PostgreSQL

## Running the backend
```
./gradlew bootRun
```
Starts on `http://localhost:8080`. Liquibase applies the schema and seed data automatically against the Postgres instance configured in [`application.yaml`](src/main/resources/application.yaml).

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
* **`spring-boot-starter-validation`**
  * *Purpose:* Bean validation (`@Valid`, `@NotNull`, ...) on incoming request DTOs.
* **`spring-boot-starter-actuator`**
  * *Purpose:* Exposes essential infrastructure metrics, readiness/liveness health checks, and service diagnostic points.

### 2. Runtime Drivers & Utilities
* **`postgresql`** (`runtimeOnly`)
  * *Purpose:* The official wire-protocol driver required by the connection pool to execute queries against your PostgreSQL instances.
* **`micrometer-registry-prometheus`** (`runtimeOnly`)
  * *Purpose:* Formats Actuator metrics out of the box so Prometheus servers can scrape them seamlessly for visualization (e.g., Grafana).
* **`lombok`** (`compileOnly` & `annotationProcessor`)
  * *Purpose:* Automatically hooks into the compile-time cycle to eliminate Java boilerplate like getters, setters, and builder patterns.

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

## API Endpoints

**Catalog** (read-only)
| Method | URL | Description |
|---|---|---|
| GET | `/api/plans` | List active plans |
| GET | `/api/tiers` | List active tiers with resolved benefits |
| GET | `/api/pricing` | Current price for every plan × tier |

**Subscription** (`{userId}` — demo user seeded with id `1`)
| Method | URL | Description |
|---|---|---|
| GET | `/api/users/{userId}/subscription` | Current/latest membership |
| POST | `/api/users/{userId}/subscription` | Subscribe (plan + tier) |
| PATCH | `/api/users/{userId}/subscription` | Upgrade/downgrade tier and/or switch plan |
| DELETE | `/api/users/{userId}/subscription` | Cancel (access continues until `endDate`) |

## Membership Domain — Core Entities

Schema lives in [`src/main/resources/db/changelog/changes/001-create-membership-schema.sql`](src/main/resources/db/changelog/changes/001-create-membership-schema.sql), seed data in [`002-seed-membership-data.sql`](src/main/resources/db/changelog/changes/002-seed-membership-data.sql), both applied via Liquibase.

| Entity | Path | Description |
|---|---|---|
| `AppUser` | [`user/AppUser.java`](src/main/java/com/example/demo/user/AppUser.java) | A registered user; carries the `cohortCode` used by cohort-based tier criteria. |
| `MembershipPlan` | [`membership/plan/MembershipPlan.java`](src/main/java/com/example/demo/membership/plan/MembershipPlan.java) | A billing cadence (Monthly/Quarterly/Yearly) with its duration in days. |
| `PlanCode` | [`membership/plan/PlanCode.java`](src/main/java/com/example/demo/membership/plan/PlanCode.java) | Enum of supported plan cadences: `MONTHLY`, `QUARTERLY`, `YEARLY`. |
| `MembershipTier` | [`membership/tier/MembershipTier.java`](src/main/java/com/example/demo/membership/tier/MembershipTier.java) | A benefit level (Silver/Gold/Platinum) with a numeric `rank` for upgrade/downgrade comparisons. |
| `TierCode` | [`membership/tier/TierCode.java`](src/main/java/com/example/demo/membership/tier/TierCode.java) | Enum of supported tiers: `SILVER`, `GOLD`, `PLATINUM`. |
| `TierCriteria` | [`membership/tier/TierCriteria.java`](src/main/java/com/example/demo/membership/tier/TierCriteria.java) | A configurable rule (order count, monthly order value, cohort) a user must meet to auto-qualify for a tier. |
| `CriteriaType` | [`membership/tier/CriteriaType.java`](src/main/java/com/example/demo/membership/tier/CriteriaType.java) | Enum of rule kinds a `TierCriteria` row can express: `MIN_ORDER_COUNT`, `MIN_ORDER_VALUE_MONTHLY`, `COHORT`. |
| `PlanTierPricing` | [`membership/pricing/PlanTierPricing.java`](src/main/java/com/example/demo/membership/pricing/PlanTierPricing.java) | Price for a given (plan, tier) pair, versioned by `effectiveFrom`/`effectiveTo` so price changes don't rewrite history. |
| `BenefitDefinition` | [`membership/benefit/BenefitDefinition.java`](src/main/java/com/example/demo/membership/benefit/BenefitDefinition.java) | Catalog of benefit types (free delivery, discount %, early access, priority support, exclusive coupons). |
| `BenefitCode` | [`membership/benefit/BenefitCode.java`](src/main/java/com/example/demo/membership/benefit/BenefitCode.java) | Enum of supported benefit types backing `BenefitDefinition`. |
| `TierBenefit` | [`membership/benefit/TierBenefit.java`](src/main/java/com/example/demo/membership/benefit/TierBenefit.java) | Grants a `BenefitDefinition` to a `MembershipTier`, configured via a plain string value (e.g. discount percent). |
| `Subscription` | [`subscription/Subscription.java`](src/main/java/com/example/demo/subscription/Subscription.java) | A user's current/past membership: which plan + tier, lifecycle status, and validity window. At most one `ACTIVE` row per user is enforced at the DB level. |
| `SubscriptionStatus` | [`subscription/SubscriptionStatus.java`](src/main/java/com/example/demo/subscription/SubscriptionStatus.java) | Enum of subscription lifecycle states: `ACTIVE`, `CANCELLED`, `EXPIRED`, `PENDING`. |

Service layer: `CatalogService` (read-only plan/tier/pricing access) and `SubscriptionService` (subscribe/change/cancel/get-current) in [`membership/`](src/main/java/com/example/demo/membership/) and [`subscription/`](src/main/java/com/example/demo/subscription/), backed by Spring Data repositories per entity. Domain errors (409 already-subscribed, 404 not-found, 400 invalid request) are mapped centrally by [`GlobalExceptionHandler`](src/main/java/com/example/demo/common/exception/GlobalExceptionHandler.java).

---

# Frontend

A React + TypeScript SPA (Vite) that talks to the backend REST API — plan/tier browsing and the full subscription lifecycle (subscribe, upgrade/downgrade, cancel).

## Technical Specifications
* **Build tool:** Vite
* **Framework:** React 19 + TypeScript
* **Data fetching:** plain `fetch` via a small typed API client (no external state/data library)

## Running the frontend
```
cd frontend
npm install
npm run dev
```
Starts on `http://localhost:5173`. The dev server proxies `/api/*` requests to the backend on `http://localhost:8080` (see [`vite.config.ts`](frontend/vite.config.ts)), so both servers need to be running for the app to work.

## Structure

| Path | Purpose |
|---|---|
| [`src/api/`](frontend/src/api/) | Typed fetch client (`client.ts`) plus one module per API area (`catalog.ts`, `subscription.ts`) |
| [`src/types/`](frontend/src/types/) | TypeScript interfaces mirroring the backend DTOs |
| [`src/pages/PlansPage.tsx`](frontend/src/pages/PlansPage.tsx) | Browse plans/tiers/pricing and subscribe |
| [`src/pages/MyMembershipPage.tsx`](frontend/src/pages/MyMembershipPage.tsx) | View current membership; upgrade/downgrade tier, switch plan, or cancel |
| [`src/components/`](frontend/src/components/) | `PlanTierPicker` (plan × tier price grid), `TierBenefitsList`, `SubscriptionCard`, `StatusBadge` |
| [`src/hooks/useCurrentUser.ts`](frontend/src/hooks/useCurrentUser.ts) | Demo stand-in for auth — holds the active user id (defaults to the seeded demo user, id `1`) |
| [`src/App.tsx`](frontend/src/App.tsx) | App shell: tab navigation between the two pages, user-id selector |

No authentication or routing library — this is a two-tab demo SPA, not a production app shell.
