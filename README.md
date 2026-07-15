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

**Admin** (configure the benefit catalog and tier auto-qualification rules)
| Method | URL | Description |
|---|---|---|
| GET | `/api/admin/benefit-definitions` | List benefit types |
| POST | `/api/admin/benefit-definitions` | Create a new benefit type (`code`, `description`) — `code` is free-form, not a fixed enum |
| GET | `/api/admin/tier-benefits` | List which tiers grant which benefits, with config |
| POST | `/api/admin/tier-benefits` | Assign a benefit to a tier (`tierId`, `benefitId`, `configValue` — e.g. `"10"` for a 10% discount) |
| PUT | `/api/admin/tier-benefits/{id}` | Update a tier-benefit's config value / active flag |
| GET | `/api/admin/tier-criteria` | List tier auto-qualification rules |
| POST | `/api/admin/tier-criteria` | Create a rule (`tierId`, `criteriaType` — free-form, e.g. `MIN_ORDER_COUNT` — `configValue`) |
| PUT | `/api/admin/tier-criteria/{id}` | Update a rule's config value / active flag |

## Membership Domain — Core Entities

Schema lives in [`src/main/resources/db/changelog/changes/001-create-membership-schema.sql`](src/main/resources/db/changelog/changes/001-create-membership-schema.sql), seed data in [`002-seed-membership-data.sql`](src/main/resources/db/changelog/changes/002-seed-membership-data.sql), both applied via Liquibase.

| Entity | Path | Description |
|---|---|---|
| `AppUser` | [`user/AppUser.java`](src/main/java/com/example/demo/user/AppUser.java) | A registered user; carries the `cohortCode` used by cohort-based tier criteria. |
| `MembershipPlan` | [`membership/plan/MembershipPlan.java`](src/main/java/com/example/demo/membership/plan/MembershipPlan.java) | A billing cadence (Monthly/Quarterly/Yearly) with its duration in days. |
| `PlanCode` | [`membership/plan/PlanCode.java`](src/main/java/com/example/demo/membership/plan/PlanCode.java) | Enum of supported plan cadences: `MONTHLY`, `QUARTERLY`, `YEARLY`. |
| `MembershipTier` | [`membership/tier/MembershipTier.java`](src/main/java/com/example/demo/membership/tier/MembershipTier.java) | A benefit level (Silver/Gold/Platinum) with a numeric `rank` for upgrade/downgrade comparisons. |
| `TierCode` | [`membership/tier/TierCode.java`](src/main/java/com/example/demo/membership/tier/TierCode.java) | Enum of supported tiers: `SILVER`, `GOLD`, `PLATINUM`. |
| `TierCriteria` | [`membership/tier/TierCriteria.java`](src/main/java/com/example/demo/membership/tier/TierCriteria.java) | A configurable rule a user must meet to auto-qualify for a tier. `criteriaType` is a free-form string (e.g. `MIN_ORDER_COUNT`, `MIN_ORDER_VALUE_MONTHLY`, `COHORT`, or any admin-defined rule name), not a fixed enum. |
| `PlanTierPricing` | [`membership/pricing/PlanTierPricing.java`](src/main/java/com/example/demo/membership/pricing/PlanTierPricing.java) | Price for a given (plan, tier) pair, versioned by `effectiveFrom`/`effectiveTo` so price changes don't rewrite history. |
| `BenefitDefinition` | [`membership/benefit/BenefitDefinition.java`](src/main/java/com/example/demo/membership/benefit/BenefitDefinition.java) | Catalog of benefit types (free delivery, discount %, early access, priority support, exclusive coupons, or any admin-defined type). `code` is a free-form string, not a fixed enum, so new benefit types can be added at runtime via the Admin screen. |
| `TierBenefit` | [`membership/benefit/TierBenefit.java`](src/main/java/com/example/demo/membership/benefit/TierBenefit.java) | Grants a `BenefitDefinition` to a `MembershipTier`, configured via a plain string value (e.g. discount percent). |
| `Subscription` | [`subscription/Subscription.java`](src/main/java/com/example/demo/subscription/Subscription.java) | A user's current/past membership: which plan + tier, lifecycle status, and validity window. At most one `ACTIVE` row per user is enforced at the DB level. |
| `SubscriptionStatus` | [`subscription/SubscriptionStatus.java`](src/main/java/com/example/demo/subscription/SubscriptionStatus.java) | Enum of subscription lifecycle states: `ACTIVE`, `CANCELLED`, `EXPIRED`, `PENDING`. |

Service layer: `CatalogService` (read-only plan/tier/pricing access) and `SubscriptionService` (subscribe/change/cancel/get-current) in [`membership/`](src/main/java/com/example/demo/membership/) and [`subscription/`](src/main/java/com/example/demo/subscription/), backed by Spring Data repositories per entity. `AdminBenefitService` and `AdminTierCriteriaService` in [`membership/admin/`](src/main/java/com/example/demo/membership/admin/) provide the write side (CRUD) for the benefit catalog and tier criteria, kept separate from the read-only `CatalogService`. Domain errors (409 already-subscribed/duplicate, 404 not-found, 400 invalid request) are mapped centrally by [`GlobalExceptionHandler`](src/main/java/com/example/demo/common/exception/GlobalExceptionHandler.java).

---

# Frontend

A React + TypeScript SPA (Vite) that talks to the backend REST API — plan/tier browsing and the full subscription lifecycle (subscribe, upgrade/downgrade, cancel).

## Technical Specifications
* **Build tool:** Vite
* **Framework:** React 19 + TypeScript
* **Styling:** Tailwind CSS 4 (via `@tailwindcss/vite`)
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
| [`src/api/`](frontend/src/api/) | Typed fetch client (`client.ts`) plus one module per API area (`catalog.ts`, `subscription.ts`, `admin.ts`) |
| [`src/types/`](frontend/src/types/) | TypeScript interfaces mirroring the backend DTOs |
| [`src/pages/PlansPage.tsx`](frontend/src/pages/PlansPage.tsx) | Browse plans/tiers/pricing and subscribe |
| [`src/pages/MyMembershipPage.tsx`](frontend/src/pages/MyMembershipPage.tsx) | View current membership; upgrade/downgrade tier, switch plan, or cancel |
| [`src/pages/AdminPage.tsx`](frontend/src/pages/AdminPage.tsx) | Admin screen: configure benefit definitions, tier benefits, and tier criteria |
| [`src/components/`](frontend/src/components/) | `PlanTierPicker` (plan × tier price grid), `TierBenefitsList`, `SubscriptionCard`, `StatusBadge` |
| [`src/components/admin/`](frontend/src/components/admin/) | `BenefitDefinitionsPanel` (create benefit types), `TierBenefitsPanel` (assign benefits to tiers with a config value, e.g. discount %), `TierCriteriaPanel` (assign auto-qualification rules to tiers) — each with inline edit and an active/inactive toggle |
| [`src/hooks/useCurrentUser.ts`](frontend/src/hooks/useCurrentUser.ts) | Demo stand-in for auth — holds the active user id (defaults to the seeded demo user, id `1`) |
| [`src/App.tsx`](frontend/src/App.tsx) | App shell: tab navigation between My Membership / Plans & Tiers / Admin, user-id selector |

No authentication or routing library — this is a demo SPA with tab navigation, not a production app shell. The Admin tab is an open route (no access control), consistent with the rest of the demo.

---

# Deployment (Railway)

Deployed as a **single service**: the backend serves the built frontend as static resources, so there's one URL, one process, and no CORS to configure in production.

## Build

Railway detects the root [`Dockerfile`](Dockerfile) and uses it automatically instead of its default builder (Railpack). Three stages:
1. `node:22-alpine` — installs and builds the frontend (`frontend/dist`).
2. `eclipse-temurin:25-jdk` — copies `frontend/dist` into `src/main/resources/static`, then runs `./gradlew bootJar`.
3. `eclipse-temurin:25-jre` — copies just the built jar in; this is the image that actually runs.

[.dockerignore](.dockerignore) keeps `node_modules`, `build/`, `.gradle/`, etc. out of the build context.

## Configuration

[application.yaml](src/main/resources/application.yaml) reads the datasource and port from environment variables, falling back to local defaults so nothing changes for `./gradlew bootRun`:
* `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD` — Postgres connection.
* `PORT` — the port Spring Boot listens on; Railway assigns this automatically per service.

Liquibase runs its migrations and seed data automatically on boot against whichever database those variables point to.

## A freshly-deployed database starts empty of subscriptions

The seed data (plans, tiers, benefits, pricing, the demo user) is inserted by Liquibase on every environment's first boot — but `Subscription` rows are created only by real user actions (`POST /api/users/{userId}/subscription`). 
