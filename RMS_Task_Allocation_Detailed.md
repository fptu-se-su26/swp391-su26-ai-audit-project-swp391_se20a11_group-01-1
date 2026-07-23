# Restaurant Management System — Detailed Task Allocation

> **Project:** Restaurant Management System (RMS)  
> **Course:** SWP391 — Application Development Project  
> **Class / Group:** SE20A11 / Group 01  
> **Baseline:** 90 auditable functions distributed equally across five members  
> **Technology:** Java 21, Spring Boot, React, Microsoft SQL Server  
> **Usage:** Team planning, GitHub issue creation, iteration coordination, integration review, evidence preparation, and final defense.

---

## 1. Document Purpose

This document converts the project’s audited functional baseline into a practical team execution plan. It defines ownership, boundaries, dependencies, implementation expectations, acceptance criteria, testing responsibilities, documentation duties, and integration checkpoints for the Restaurant Management System. It is intentionally more detailed than a simple member table because the project contains connected workflows where one member’s output becomes another member’s input.

The allocation follows end-to-end business capability rather than arbitrary CRUD splitting. Each member owns eighteen functional units across the semester. The allocation balances visible screens with backend services, database work, authorization, validation, testing, documentation, and evidence. Target LOC bands are planning indicators only; final contribution must be demonstrated through reviewable Git history, integrated code, quality, tests, and working functionality.

Project Tracking remains the source of truth for live status. The status included here is a drafting baseline used to identify completed scope and remaining gaps. A function described in this document is not automatically implemented. Before final evaluation, the team must synchronize Project Tracking, RDS/SRS, SDS, source code, database migrations, Issues Report, tests, evidence, and the Final Release Document.

## 2. Product Scope

The RMS supports the following integrated capabilities:

- **Authentication & Security:** 14 tracked functions.
- **Cart & Order Management:** 12 tracked functions.
- **Reservation Management:** 12 tracked functions.
- **Menu Management:** 7 tracked functions.
- **Payment & Invoice:** 7 tracked functions.
- **Staff Operations:** 7 tracked functions.
- **Table Management:** 6 tracked functions.
- **Coupon Management:** 5 tracked functions.
- **Feedback Management:** 4 tracked functions.
- **Kitchen Operations:** 4 tracked functions.
- **Reporting & Analytics:** 4 tracked functions.
- **User Management:** 4 tracked functions.
- **AI Support:** 2 tracked functions.
- **System Foundation:** 2 tracked functions.

The main operational workflow is:

```text
Guest / Customer Registration and Login
        -> Browse Menu and View Table Availability
        -> Create Reservation or Build Shopping Cart
        -> Checkout / Counter Order
        -> Staff Order Operations
        -> Kitchen Preparation
        -> Coupon Validation
        -> Payment Processing
        -> Immutable Invoice
        -> Feedback
        -> Dashboard / Reports / Controlled AI Support
```

## 3. Architecture and Integration Boundary

```text
React Web Client
        | HTTPS / JSON
        v
Spring Security + JWT / Authorization
        |
        v
REST Controllers + DTO Validation
        |
        v
Application / Domain Services
        |
        +------------------+------------------+
        |                  |                  |
        v                  v                  v
JPA Repositories     External Adapters     Reporting Queries
        |             Email / AI / Payment
        v
Microsoft SQL Server
```

Mandatory boundaries:

- React handles view state, user interaction, loading/error states, and API invocation; it does not decide authoritative totals, discounts, payment success, ownership, or permissions.
- REST controllers map HTTP requests, validate DTO shape, call services, and return standardized responses; they do not contain long business transactions.
- Services own business rules, state transitions, authorization/ownership checks, money calculations, locking, transactions, and external-adapter coordination.
- Repositories own persistence and explicit queries; entities are not returned directly to untrusted clients.
- SQL Server enforces final relational integrity through keys, constraints, indexes, uniqueness, and controlled migrations.

## 4. Actors and Access Scope

| Actor | Main capabilities | Key restrictions |
| --- | --- | --- |
| Guest | Browse public menu, register, reset password, use safe chatbot guidance | Cannot access protected customer/staff/admin data |
| Customer | Manage own profile, reservations, cart, orders, payment, invoice and feedback | May access only owned records and valid workflow actions |
| Staff | Operate reservations, create counter orders, manage assigned tables, record permitted payments | Must respect role, assignment and state rules |
| Kitchen Staff | View kitchen queue, update preparation status, review kitchen history | Cannot perform unrelated administration or customer ownership operations |
| Administrator | Manage users, menu, tables, coupons, feedback, reports and configuration | Still subject to audit, state, transaction and data-integrity rules |
| Email / AI / Payment adapters | Provide bounded external capabilities | Must use timeouts, safe fallback, externalized secrets and controlled data |

## 5. Team and Ownership

| Member | Role | Owned units | Complete/Updated | Doing | Not Started |
| --- | --- | ---: | ---: | ---: | ---: |
| Phan Nguyễn (DE191019) | Team Leader / Integration / Admin, Menu and Reporting | 18 | 13 | 0 | 5 |
| Phạm Văn Quyết (DE190425) | Backend & Security Lead | 18 | 11 | 4 | 3 |
| Nguyễn Tiến Lộc (DE190986) | Order, Kitchen and Transaction Flow Developer | 18 | 12 | 2 | 4 |
| Nguyễn Đức Thương (DE190096) | Frontend Lead / Customer Experience | 18 | 12 | 5 | 1 |
| Trần Thanh Gia Huy (DE180571) | Reservation, Coupon, AI, Quality and Analytics Support | 18 | 12 | 0 | 6 |

Every member owns exactly eighteen units. Ownership means the member is accountable for requirement clarification, design coordination, implementation, tests, evidence, documentation consistency, Pull Request quality, and defense of the function. Other members may support the implementation, but support does not remove the owner’s accountability.

## 6. Execution Priorities

The project should be completed in dependency order:

1. **Foundation and security:** authentication, password handling, JWT, role matrix, standard errors, user identity.
2. **Master data:** categories, foods, tables and account administration.
3. **Reservation:** availability, capacity, overlap prevention, lifecycle operations and seed/migration stability.
4. **Cart and order:** server-side item validation, snapshots, totals, ownership and controlled status transitions.
5. **Kitchen and staff operations:** assigned tables, queue, preparation status and history.
6. **Coupon and payment:** server-side eligibility, atomic usage, amount verification, idempotency and transactional completion.
7. **Invoice and feedback:** immutable invoice, authorized viewing, one feedback per eligible order, moderation.
8. **Reporting and AI:** live SQL aggregates first; AI interpretation only after authorized, accurate reporting data exists.

Optional polish must not delay critical security, transaction, persistence, or evidence gaps.

## 7. Cross-Member Dependency Map

| Producer | Output | Main consumers | Integration rule |
| --- | --- | --- | --- |
| Phạm Văn Quyết | Authentication principal, JWT, role/ownership policy, payment API security | All frontend and backend modules | Protected APIs cannot be considered complete before representative 401/403 and ownership tests pass |
| Phan Nguyễn | User/menu/admin/report contracts and release coordination | Frontend, reservation, order, reporting | Shared DTO/endpoint changes require issue and reviewer notification |
| Nguyễn Tiến Lộc | Cart/order/kitchen/payment transaction services | Frontend, coupon, invoice, reports | Totals and state transitions are calculated and validated server-side |
| Nguyễn Đức Thương | Shared React layouts, forms, customer/staff screens, API integration | All visible workflows | UI must consume real APIs and handle loading, empty, validation, 401/403 and network errors |
| Trần Thanh Gia Huy | Reservation operations, coupon/invoice schema support, quality evidence, AI | Order, payment, reporting and final evaluation | Concurrency and persistence must be proven with SQL/test evidence |

## 8. Detailed Member Assignments

The following subsections list every audited functional unit. For each unit, the owner must preserve the stated business objective, business rules, acceptance criteria, evidence reference, and current risk. The exact implementation may evolve, but any contract change must be reviewed and synchronized across Project Tracking, RDS/SRS, SDS, code, tests, and evidence.

### 8.1 Phan Nguyễn (DE191019)

**Role:** Team Leader / Integration / Admin, Menu and Reporting  
**Owned functions:** 18  
**Baseline progress:** 13 Done/Updated, 0 Doing, 5 Not Started.

The leader owns integration-sensitive administration, menu, user and reporting scope. In addition to the listed functions, the leader coordinates iteration planning, release branches, conflict resolution, document synchronization, demo order, and final sign-off. The leader should avoid becoming a bottleneck by publishing DTOs, endpoints, seed data and integration decisions early.

#### 8.1.1 AUTH-11 — Registration Screen & Validation

- **Feature / type:** Authentication & Security / UI Screen
- **Actor(s):** Guest
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter1 / Done
- **Requirement and design references:** RDS/SRS II.1.11; SDS II.1.11

**Objective**

Collects customer account information, validates required fields and password confirmation, and displays API validation errors.

**Business rules and validation**

Email format and required profile fields are validated; password confirmation must match; submit is disabled while processing.

**Acceptance criteria**

Valid data creates an account; invalid fields show messages; duplicate email feedback is shown without page crash.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-11 / TC-AUTH-11`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed and integrated.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.2 AUTH-12 — Registration API & Duplicate Email Check

- **Feature / type:** Authentication & Security / Backend API
- **Actor(s):** Guest
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Done
- **Requirement and design references:** RDS/SRS II.1.12; SDS II.1.12

**Objective**

Creates a customer account with the default role after validating input and uniqueness constraints.

**Business rules and validation**

Email is unique case-insensitively; password is encoded; clients cannot self-assign privileged roles.

**Acceptance criteria**

New account is persisted with customer role; duplicate email and invalid input are rejected; tests verify role protection.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-12 / TC-AUTH-12`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Database constraint and service validation must remain consistent.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.3 USER-01 — View Own Profile

- **Feature / type:** User Management / UI / Backend API
- **Actor(s):** Customer, Staff, Kitchen Staff, Admin
- **Priority / complexity / target LOC band:** Medium / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Done
- **Requirement and design references:** RDS/SRS II.2.1; SDS II.2.1

**Objective**

Displays the authenticated user's profile and contact information using the current security identity.

**Business rules and validation**

A user can only read their own profile through this function; sensitive fields and password hashes are excluded.

**Acceptance criteria**

Profile loads after login, contains only permitted fields, and rejects attempts to read another user's data.

**Evidence and current control note**

- Evidence/Test reference: `EV-USER-01 / TC-USER-01`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Connected to authenticated profile data.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.4 USER-04 — Admin Change Role / Lock Account

- **Feature / type:** User Management / Admin API
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Not Started
- **Requirement and design references:** RDS/SRS II.2.4; SDS II.2.4

**Objective**

Allows safe role updates and account activation/locking with protection for critical accounts and active sessions.

**Business rules and validation**

Admin cannot remove the last active administrator or lock the currently required system account; changes are audited.

**Acceptance criteria**

Allowed changes persist and affect authorization; unsafe changes are blocked; account lock prevents login immediately.

**Evidence and current control note**

- Evidence/Test reference: `EV-USER-04 / TC-USER-04`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Role/status APIs, UI actions, audit logging, and regression tests are missing.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.5 MENU-01 — Category List

- **Feature / type:** Menu Management / Admin Screen / API
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** Medium / Medium / 120
- **Planned iteration / baseline status:** Iter1 / Done
- **Requirement and design references:** RDS/SRS II.3.1; SDS II.3.1

**Objective**

Lists categories with status, search, and item usage information for administration.

**Business rules and validation**

Only active categories appear in customer menu; admin list may include inactive categories.

**Acceptance criteria**

Admin can search and filter categories; list reflects database state and handles no-data cases.

**Evidence and current control note**

- Evidence/Test reference: `EV-MENU-01 / TC-MENU-01`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.6 MENU-02 — Create / Update Category

- **Feature / type:** Menu Management / Admin Screen / API
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter1 / Done
- **Requirement and design references:** RDS/SRS II.3.2; SDS II.3.2

**Objective**

Creates and edits menu categories with normalized names and optional descriptions.

**Business rules and validation**

Category names are required and unique case-insensitively; invalid input does not alter data.

**Acceptance criteria**

Create/update persists valid data; duplicates and blank names are rejected with clear feedback.

**Evidence and current control note**

- Evidence/Test reference: `EV-MENU-02 / TC-MENU-02`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed with duplicate-name validation.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.7 MENU-03 — Deactivate Category & Referential Rule

- **Feature / type:** Menu Management / Admin API
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Done
- **Requirement and design references:** RDS/SRS II.3.3; SDS II.3.3

**Objective**

Deactivates a category while preserving historical references and preventing unsafe deletion when foods still depend on it.

**Business rules and validation**

Referenced categories are not hard-deleted; deactivation hides them from new customer selections.

**Acceptance criteria**

Deactivation is safe and reversible; existing historical food/order data remains valid.

**Evidence and current control note**

- Evidence/Test reference: `EV-MENU-03 / TC-MENU-03`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Referential integrity behavior is documented.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.8 MENU-04 — Admin Food List / Search / Filter

- **Feature / type:** Menu Management / Admin Screen / API
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter1 / Updated
- **Requirement and design references:** RDS/SRS II.3.4; SDS II.3.4

**Objective**

Lists foods with category, price, availability, status, and keyword/category filters.

**Business rules and validation**

Pagination and filters must use server data; inactive items remain visible to admin only.

**Acceptance criteria**

Search/filter results are correct; list refreshes after changes; no-data and API-error states are shown.

**Evidence and current control note**

- Evidence/Test reference: `EV-MENU-04 / TC-MENU-04`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Enhanced search and availability display.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.9 MENU-05 — Create / Update Food Item

- **Feature / type:** Menu Management / Admin Screen / API
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Updated
- **Requirement and design references:** RDS/SRS II.3.5; SDS II.3.5

**Objective**

Creates and edits food name, category, price, description, image reference, status, and availability.

**Business rules and validation**

Price must be positive; category must exist and be active for new assignments; duplicate business rules are enforced.

**Acceptance criteria**

Valid foods persist and appear in the proper menu; invalid price/category/data are rejected; updates preserve order history snapshots.

**Evidence and current control note**

- Evidence/Test reference: `EV-MENU-05 / TC-MENU-05`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Integrated with cart, order, kitchen, and reporting data.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.10 MENU-07 — Customer Menu Browse / Detail / Filter

- **Feature / type:** Menu Management / Customer Screen
- **Actor(s):** Guest, Customer
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter1 / Done
- **Requirement and design references:** RDS/SRS II.3.7; SDS II.3.7

**Objective**

Displays active available foods grouped by category with details, keyword search, and category filtering.

**Business rules and validation**

Only active/available foods and active categories are exposed; prices come from server data.

**Acceptance criteria**

Customer can browse and filter live menu data; unavailable items are hidden/disabled; cart entry points work.

**Evidence and current control note**

- Evidence/Test reference: `EV-MENU-07 / TC-MENU-07`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Responsive customer menu is completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.11 TABLE-04 — Table Number Uniqueness

- **Feature / type:** Table Management / Database / Service Validation
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter2 / Done
- **Requirement and design references:** RDS/SRS II.4.4; SDS II.4.4

**Objective**

Prevents duplicate table numbers through service validation and a database uniqueness constraint.

**Business rules and validation**

Comparison is normalized; concurrent creation cannot produce duplicates.

**Acceptance criteria**

Duplicate requests return a conflict/validation error and only one table record exists after concurrent tests.

**Evidence and current control note**

- Evidence/Test reference: `EV-TABLE-04 / TC-TABLE-04`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Database constraint evidence required in final package.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.12 SYS-01 — Standard API Response & Validation Errors

- **Feature / type:** System Foundation / Cross-cutting Backend
- **Actor(s):** All Actors
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Updated
- **Requirement and design references:** RDS/SRS II.6.1; SDS II.6.1

**Objective**

Uses a common response/error structure for validation, business, authentication, authorization, and unexpected failures.

**Business rules and validation**

Error responses include stable code, message, timestamp, and field errors where applicable; stack traces are not exposed.

**Acceptance criteria**

Representative APIs return the same format for success and each error class; frontend handles them without special-case crashes.

**Evidence and current control note**

- Evidence/Test reference: `EV-SYS-01 / TC-SYS-01`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Security 401/403 mapping still needs final alignment.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.13 ORDER-09 — Admin / Staff Order List & Filters

- **Feature / type:** Staff Operations / Admin/Staff Screen / API
- **Actor(s):** Admin, Staff
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Updated
- **Requirement and design references:** RDS/SRS II.8.9; SDS II.8.9

**Objective**

Provides order search and filters by date, status, customer, table, staff, and payment state.

**Business rules and validation**

Role restrictions and pagination apply; revenue-sensitive fields are limited to authorized roles.

**Acceptance criteria**

Filters return correct orders; list updates after status/payment changes; non-authorized access is blocked.

**Evidence and current control note**

- Evidence/Test reference: `EV-ORDER-09 / TC-ORDER-09`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Payment filter must be finalized after payment persistence.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.14 STAFF-04 — Cancel Order with Reason & Audit

- **Feature / type:** Staff Operations / Staff/Admin API
- **Actor(s):** Admin, Staff
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Updated
- **Requirement and design references:** RDS/SRS II.9.4; SDS II.9.4

**Objective**

Cancels an eligible order with a required reason and records who performed the action.

**Business rules and validation**

Paid/completed orders cannot be casually cancelled; cancellation must preserve historical order items and financial records.

**Acceptance criteria**

Allowed cancellation records reason/actor/time; forbidden state is rejected; order remains visible in history.

**Evidence and current control note**

- Evidence/Test reference: `EV-STAFF-04 / TC-STAFF-04`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Final payment-aware cancellation policy is required.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.15 INV-03 — View / Download Invoice

- **Feature / type:** Payment & Invoice / UI / Backend API
- **Actor(s):** Customer, Staff, Admin
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.13.3; SDS II.13.3

**Objective**

Allows authorized users to view invoice details and download/print a stable invoice representation.

**Business rules and validation**

Customer can access only own invoice; staff/admin access follows role; issued invoice is read-only.

**Acceptance criteria**

Authorized view/download works; unauthorized access fails; displayed/PDF totals match database.

**Evidence and current control note**

- Evidence/Test reference: `EV-INV-03 / TC-INV-03`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Requires invoice generation and authorization.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.16 RPT-01 — Dashboard KPI Aggregation API

- **Feature / type:** Reporting & Analytics / Backend Report API
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.15.1; SDS II.15.1

**Objective**

Calculates operational KPIs such as paid revenue, completed orders, active reservations, and customer counts from live data.

**Business rules and validation**

Revenue includes only successful paid/completed orders; cancelled/unpaid orders are excluded; date boundaries are explicit.

**Acceptance criteria**

API values reconcile with direct SQL for the same period; empty period returns zero/no-data safely.

**Evidence and current control note**

- Evidence/Test reference: `EV-RPT-01 / TC-RPT-01`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Dashboard currently uses static/mock figures.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.17 RPT-02 — Revenue Trend & Date Filter

- **Feature / type:** Reporting & Analytics / Backend API / Admin UI
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.15.2; SDS II.15.2

**Objective**

Groups paid revenue by day/month and supports date-range filtering for dashboard charts.

**Business rules and validation**

Grouping uses consistent timezone; filters are inclusive as documented; only verified successful payments count.

**Acceptance criteria**

Chart data matches SQL aggregation; changing date range refreshes correctly; empty ranges render safely.

**Evidence and current control note**

- Evidence/Test reference: `EV-RPT-02 / TC-RPT-02`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Requires payment persistence first.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.1.18 RPT-03 — Top Food & Order-status Reports

- **Feature / type:** Reporting & Analytics / Backend API / Admin UI
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.15.3; SDS II.15.3

**Objective**

Reports top-selling foods and order counts by status using persisted order items and orders.

**Business rules and validation**

Cancelled orders are excluded from sales ranking; historical item names/prices use snapshots where appropriate.

**Acceptance criteria**

Rankings/counts reconcile with SQL; filters and tie handling are documented; non-admin access is blocked.

**Evidence and current control note**

- Evidence/Test reference: `EV-RPT-03 / TC-RPT-03`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Current charts are mock/static.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

### 8.2 Phạm Văn Quyết (DE190425)

**Role:** Backend & Security Lead  
**Owned functions:** 18  
**Baseline progress:** 11 Done/Updated, 4 Doing, 3 Not Started.

The Backend & Security Lead owns the project’s trust boundary. These tasks must be implemented with backend-enforced authentication, authorization, ownership, safe errors, transaction awareness and negative testing. Frontend route guards or UI hiding are not accepted as security controls. Payment work must coordinate with order totals, idempotency, database persistence and invoice generation.

#### 8.2.1 AUTH-02 — Credential Authentication API

- **Feature / type:** Authentication & Security / Backend API
- **Actor(s):** Guest
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Done
- **Requirement and design references:** RDS/SRS II.1.2; SDS II.1.2

**Objective**

Authenticates a submitted email and password against an active user account and returns a standardized success or failure response.

**Business rules and validation**

Inactive/locked accounts cannot log in; comparisons use encoded passwords; authentication failures must not reveal whether an email exists.

**Acceptance criteria**

Correct credentials succeed; wrong password, unknown email, and inactive account return safe errors; API contract is covered by tests.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-02 / TC-AUTH-02`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Core credential authentication is implemented; token hardening is tracked separately.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.2 AUTH-03 — Password Hashing & Credential Verification

- **Feature / type:** Authentication & Security / Security Service
- **Actor(s):** Guest, All Users
- **Priority / complexity / target LOC band:** Critical / Medium / 120
- **Planned iteration / baseline status:** Iter1 / Done
- **Requirement and design references:** RDS/SRS II.1.3; SDS II.1.3

**Objective**

Encodes passwords before persistence and verifies credentials through the configured password encoder.

**Business rules and validation**

Plaintext passwords must never be stored or returned; encoding configuration is consistent across registration, reset, and login.

**Acceptance criteria**

Database contains only encoded passwords; registration/reset/login use the same encoder; negative password tests pass.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-03 / TC-AUTH-03`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Security review must confirm no plaintext values in logs or seed scripts.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.3 AUTH-04 — JWT Access Token Generation

- **Feature / type:** Authentication & Security / Security Service
- **Actor(s):** Authenticated Users
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Doing
- **Requirement and design references:** RDS/SRS II.1.4; SDS II.1.4

**Objective**

Creates a signed access token containing the user identity, role claims, issue time, and expiration after successful authentication.

**Business rules and validation**

Signing key is externalized; token lifetime is configurable; only required non-sensitive claims are included.

**Acceptance criteria**

A valid login returns a signed token; signature and expiration can be verified; tampered tokens are rejected.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-04 / TC-AUTH-04`.
- Baseline interpretation: Active implementation gap; must not be represented as complete until code, tests, documents, and evidence agree.
- Risk/update note: Token generation is incomplete and must be finalized before the final release tag.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.4 AUTH-05 — JWT Refresh Token Persistence

- **Feature / type:** Authentication & Security / Backend Service / DB
- **Actor(s):** Authenticated Users
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Not Started
- **Requirement and design references:** RDS/SRS II.1.5; SDS II.1.5

**Objective**

Stores refresh-token hashes and lifecycle metadata so an expired access token can be renewed without re-entering credentials.

**Business rules and validation**

Refresh tokens are one-way hashed, revocable, unique, time-limited, and bound to a user account.

**Acceptance criteria**

Valid refresh creates a new access token; expired/revoked/reused tokens fail; raw refresh tokens are not persisted.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-05 / TC-AUTH-05`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Required database migration, entity, repository, service, and tests are missing.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.5 AUTH-06 — JWT Authentication Filter

- **Feature / type:** Authentication & Security / Security Filter
- **Actor(s):** Authenticated Users
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Doing
- **Requirement and design references:** RDS/SRS II.1.6; SDS II.1.6

**Objective**

Reads the bearer token on every protected request, validates it, and populates the authenticated security context.

**Business rules and validation**

Public endpoints bypass authentication; malformed headers and invalid tokens do not create a security context.

**Acceptance criteria**

Protected requests with valid tokens reach controllers; missing/invalid/expired tokens return 401; public routes remain accessible.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-06 / TC-AUTH-06`.
- Baseline interpretation: Active implementation gap; must not be represented as complete until code, tests, documents, and evidence agree.
- Risk/update note: Filter and integration tests are incomplete.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.6 AUTH-07 — Token Expiry & Invalid Token Handling

- **Feature / type:** Authentication & Security / Exception Handling
- **Actor(s):** Authenticated Users
- **Priority / complexity / target LOC band:** Critical / Medium / 120
- **Planned iteration / baseline status:** Iter1 / Doing
- **Requirement and design references:** RDS/SRS II.1.7; SDS II.1.7

**Objective**

Returns consistent error responses for expired, malformed, unsupported, or incorrectly signed tokens.

**Business rules and validation**

Security errors use the common API error format; sensitive token details are not logged or returned.

**Acceptance criteria**

Each invalid-token scenario returns the expected HTTP status and message; frontend can distinguish expiry from authorization failure.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-07 / TC-AUTH-07`.
- Baseline interpretation: Active implementation gap; must not be represented as complete until code, tests, documents, and evidence agree.
- Risk/update note: Standard exception mapping remains incomplete.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.7 AUTH-08 — Endpoint Role Authorization Matrix

- **Feature / type:** Authentication & Security / Authorization Policy
- **Actor(s):** Admin, Staff, Kitchen Staff, Customer
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Doing
- **Requirement and design references:** RDS/SRS II.1.8; SDS II.1.8

**Objective**

Defines explicit access rules for all protected API groups and matches them with frontend role navigation.

**Business rules and validation**

Admin-only write APIs reject non-admins; users may access only owned resources; deny-by-default applies to unmatched protected routes.

**Acceptance criteria**

Authorization tests prove 401 for unauthenticated and 403 for wrong-role requests across representative endpoints.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-08 / TC-AUTH-08`.
- Baseline interpretation: Active implementation gap; must not be represented as complete until code, tests, documents, and evidence agree.
- Risk/update note: SecurityConfig is currently too permissive and needs a complete endpoint matrix.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.8 AUTH-10 — Logout & Token Revocation

- **Feature / type:** Authentication & Security / Backend API
- **Actor(s):** Authenticated Users
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter1 / Not Started
- **Requirement and design references:** RDS/SRS II.1.10; SDS II.1.10

**Objective**

Ends an authenticated session by revoking the current refresh token and clearing client authentication state.

**Business rules and validation**

Revoked tokens cannot be reused; logout is idempotent; client removes access and refresh credentials.

**Acceptance criteria**

After logout, refresh fails and protected requests require a new login; repeated logout requests do not create errors.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-10 / TC-AUTH-10`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Depends on refresh-token persistence.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.9 AUTH-14 — Reset Password Token Validation

- **Feature / type:** Authentication & Security / Backend API
- **Actor(s):** Guest
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Done
- **Requirement and design references:** RDS/SRS II.1.14; SDS II.1.14

**Objective**

Validates the reset token/code, saves a new encoded password, and invalidates the token after successful use.

**Business rules and validation**

Expired, invalid, or reused tokens are rejected; new password rules and confirmation are enforced.

**Acceptance criteria**

Valid token updates the password once; old password stops working; expired/reused token tests pass.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-14 / TC-AUTH-14`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Positive and negative token cases are included.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.10 TABLE-02 — Admin Create / Update Table

- **Feature / type:** Table Management / Admin Screen / API
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Updated
- **Requirement and design references:** RDS/SRS II.4.2; SDS II.4.2

**Objective**

Creates and edits restaurant table master data including number, capacity, status, and active flag.

**Business rules and validation**

Table number is unique; capacity is positive and within configured limits; referenced tables are not unsafely deleted.

**Acceptance criteria**

Valid changes persist; duplicates and invalid capacity are rejected; updates appear in reservation/staff screens.

**Evidence and current control note**

- Evidence/Test reference: `EV-TABLE-02 / TC-TABLE-02`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Operational status handling was strengthened.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.11 TABLE-03 — Table Capacity Validation

- **Feature / type:** Table Management / Business Service
- **Actor(s):** Admin, Staff, Customer
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter2 / Done
- **Requirement and design references:** RDS/SRS II.4.3; SDS II.4.3

**Objective**

Validates table capacity during table maintenance, reservation matching, and staff assignment.

**Business rules and validation**

Capacity must be a positive integer; party size cannot exceed selected table capacity.

**Acceptance criteria**

Invalid capacity and oversized party selections are rejected consistently in UI and API tests.

**Evidence and current control note**

- Evidence/Test reference: `EV-TABLE-03 / TC-TABLE-03`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Shared validation is used by reservation logic.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.12 RES-02 — Search Available Tables

- **Feature / type:** Reservation Management / Backend API
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Updated
- **Requirement and design references:** RDS/SRS II.5.2; SDS II.5.2

**Objective**

Returns tables available for a requested time range and party size.

**Business rules and validation**

Inactive/unavailable/undersized tables and overlapping active reservations are excluded.

**Acceptance criteria**

Results match direct database checks; unavailable tables are never returned; boundary times are tested.

**Evidence and current control note**

- Evidence/Test reference: `EV-RES-02 / TC-RES-02`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Query and conflict rules were strengthened.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.13 RES-03 — Reservation Capacity Matching

- **Feature / type:** Reservation Management / Business Service
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter2 / Done
- **Requirement and design references:** RDS/SRS II.5.3; SDS II.5.3

**Objective**

Matches party size with eligible table capacity and validates a manually selected table before saving.

**Business rules and validation**

Selected table capacity must be greater than or equal to party size; capacity cannot be bypassed from the client.

**Acceptance criteria**

Oversized reservations are rejected server-side; valid matching returns appropriate tables.

**Evidence and current control note**

- Evidence/Test reference: `EV-RES-03 / TC-RES-03`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.14 RES-04 — Reservation Overlap & Concurrency Prevention

- **Feature / type:** Reservation Management / Business Service / DB Lock
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Updated
- **Requirement and design references:** RDS/SRS II.5.4; SDS II.5.4

**Objective**

Prevents two active reservations from occupying the same table during overlapping time ranges, including concurrent submissions.

**Business rules and validation**

Overlap considers configured duration/statuses; final availability is checked inside the transaction using locking.

**Acceptance criteria**

Sequential and concurrent overlap tests allow only one conflicting reservation; non-overlapping reservations succeed.

**Evidence and current control note**

- Evidence/Test reference: `EV-RES-04 / TC-RES-04`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Pessimistic locking and concurrency tests must be retained in release evidence.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.15 RES-05 — Reservation Persistence Transaction

- **Feature / type:** Reservation Management / Backend Service / DB
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Done
- **Requirement and design references:** RDS/SRS II.5.5; SDS II.5.5

**Objective**

Creates the reservation atomically after revalidating user, table, time, capacity, and overlap rules.

**Business rules and validation**

Failed validation rolls back fully; createdBy/customer identity comes from the authenticated context.

**Acceptance criteria**

Successful reservation has complete data; failures create no partial record; ownership is correct.

**Evidence and current control note**

- Evidence/Test reference: `EV-RES-05 / TC-RES-05`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.16 ORDER-07 — Order Ownership Authorization

- **Feature / type:** Cart & Order Management / Authorization Policy
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.8.7; SDS II.8.7

**Objective**

Protects order history, detail, cancellation, payment, invoice, and feedback operations by authenticated ownership.

**Business rules and validation**

Customer cannot act on another customer's order; staff/admin access follows explicit operational roles.

**Acceptance criteria**

ID-guessing tests return 403/404; authorized owner and allowed staff roles succeed.

**Evidence and current control note**

- Evidence/Test reference: `EV-ORDER-07 / TC-ORDER-07`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Must be reverified after final JWT authorization changes.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.17 KITCHEN-04 — Kitchen Access Authorization

- **Feature / type:** Kitchen Operations / Authorization Policy
- **Actor(s):** Kitchen Staff, Admin
- **Priority / complexity / target LOC band:** Critical / Medium / 120
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.10.4; SDS II.10.4

**Objective**

Restricts kitchen queue and status actions to kitchen staff or explicitly authorized admin roles.

**Business rules and validation**

Customer/general staff tokens cannot call kitchen mutation APIs; read/write permissions are explicit.

**Acceptance criteria**

Authorization tests prove correct role access and reject all disallowed roles.

**Evidence and current control note**

- Evidence/Test reference: `EV-KITCHEN-04 / TC-KITCHEN-04`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Must be rerun after JWT filter finalization.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.2.18 PAY-02 — Payment Processing API

- **Feature / type:** Payment & Invoice / Backend API
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.12.2; SDS II.12.2

**Objective**

Processes an eligible order payment request and returns an authoritative payment result.

**Business rules and validation**

Order must exist, be payable, and be owned/operated by an authorized actor; amount comes from the order.

**Acceptance criteria**

Successful request creates payment; invalid order/state/role fails; API is covered for cash/QR outcomes.

**Evidence and current control note**

- Evidence/Test reference: `EV-PAY-02 / TC-PAY-02`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: No verified /api/payments/process lifecycle currently exists.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

### 8.3 Nguyễn Tiến Lộc (DE190986)

**Role:** Order, Kitchen and Transaction Flow Developer  
**Owned functions:** 18  
**Baseline progress:** 12 Done/Updated, 2 Doing, 4 Not Started.

The order and transaction owner controls the central operational state machine. Cart data, order snapshots, totals, kitchen states, coupon application and payment completion must remain consistent under failure and repeated requests. This member must coordinate closely with the Security Lead, Frontend Lead and coupon/invoice owner.

#### 8.3.1 CART-01 — Add Item to Cart

- **Feature / type:** Cart & Order Management / Customer UI
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.7.1; SDS II.7.1

**Objective**

Adds an available menu item with selected quantity and notes to the customer's cart.

**Business rules and validation**

Only active/available items can be added; quantity is positive and within the configured maximum.

**Acceptance criteria**

Valid item appears once with correct quantity; unavailable/invalid item is blocked; feedback is immediate.

**Evidence and current control note**

- Evidence/Test reference: `EV-CART-01 / TC-CART-01`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.2 CART-02 — Update Cart Quantity & Revalidate Availability

- **Feature / type:** Cart & Order Management / Customer UI / Service
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.7.2; SDS II.7.2

**Objective**

Changes cart item quantity and rechecks current food availability and price constraints.

**Business rules and validation**

Quantity cannot be zero/negative or exceed limit; unavailable items are flagged before checkout.

**Acceptance criteria**

Valid quantity updates subtotal; invalid quantity is rejected; unavailable items cannot proceed unnoticed.

**Evidence and current control note**

- Evidence/Test reference: `EV-CART-02 / TC-CART-02`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.3 CART-03 — Remove Item / Clear Cart

- **Feature / type:** Cart & Order Management / Customer UI
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** Medium / Simple / 60
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.7.3; SDS II.7.3

**Objective**

Removes one item or clears all cart items with confirmation where appropriate.

**Business rules and validation**

Removing/clearing affects only the current customer/session; operation is idempotent.

**Acceptance criteria**

Selected item is removed; clear empties the cart; repeated actions do not cause errors.

**Evidence and current control note**

- Evidence/Test reference: `EV-CART-03 / TC-CART-03`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.4 CART-04 — Cart Subtotal Calculation

- **Feature / type:** Cart & Order Management / Business Calculation
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.7.4; SDS II.7.4

**Objective**

Calculates subtotal from current authoritative food price and quantity before discounts and payment.

**Business rules and validation**

Money uses DECIMAL precision; client-calculated totals are display-only and never trusted by order creation.

**Acceptance criteria**

Subtotal matches server calculation for multiple items/quantities; decimal rounding follows policy.

**Evidence and current control note**

- Evidence/Test reference: `EV-CART-04 / TC-CART-04`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Server total must remain the source of truth.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.5 ORDER-02 — Server-side Create Order Transaction

- **Feature / type:** Cart & Order Management / Backend Service / DB
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Updated
- **Requirement and design references:** RDS/SRS II.8.2; SDS II.8.2

**Objective**

Creates order header and items atomically after validating identity, food availability, table/order type, and prices.

**Business rules and validation**

All items succeed or the transaction rolls back; order ownership/creator comes from authenticated context.

**Acceptance criteria**

Successful order contains complete header/items; any invalid item causes full rollback; duplicate submission is controlled.

**Evidence and current control note**

- Evidence/Test reference: `EV-ORDER-02 / TC-ORDER-02`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Coupon and payment integration are tracked as separate gaps.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.6 ORDER-03 — Persist Order Items & Price Snapshot

- **Feature / type:** Cart & Order Management / Database / Service
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.8.3; SDS II.8.3

**Objective**

Stores each ordered item with quantity, unit price snapshot, notes, and relationship to the order.

**Business rules and validation**

Historical prices do not change when menu prices are edited; quantity and unit price are positive.

**Acceptance criteria**

Order details reproduce the original order after menu updates; database relationships and totals reconcile.

**Evidence and current control note**

- Evidence/Test reference: `EV-ORDER-03 / TC-ORDER-03`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.7 ORDER-04 — Recalculate Final Total & Reject Client Manipulation

- **Feature / type:** Cart & Order Management / Business Service
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Doing
- **Requirement and design references:** RDS/SRS II.8.4; SDS II.8.4

**Objective**

Recalculates subtotal, discount, and final amount from database values rather than accepting client totals.

**Business rules and validation**

Discount cannot exceed permitted amount; monetary fields use fixed precision; invalid coupon/payment state is rejected.

**Acceptance criteria**

Manipulated client total is ignored; persisted total matches server calculation; tests cover rounding and invalid discounts.

**Evidence and current control note**

- Evidence/Test reference: `EV-ORDER-04 / TC-ORDER-04`.
- Baseline interpretation: Active implementation gap; must not be represented as complete until code, tests, documents, and evidence agree.
- Risk/update note: Blocked by complete server-side coupon implementation.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.8 ORDER-05 — View Own Order History

- **Feature / type:** Cart & Order Management / Customer Screen / API
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.8.5; SDS II.8.5

**Objective**

Lists current and previous orders for the authenticated customer with status, time, and amount summary.

**Business rules and validation**

Only owned orders are returned; pagination and sorting are deterministic.

**Acceptance criteria**

Customer sees only own orders; status and totals match database; filters/no-data work.

**Evidence and current control note**

- Evidence/Test reference: `EV-ORDER-05 / TC-ORDER-05`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.9 ORDER-08 — Staff Counter Order Creation

- **Feature / type:** Staff Operations / Staff Screen / API
- **Actor(s):** Staff
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.8.8; SDS II.8.8

**Objective**

Allows staff to create an order for walk-in or seated customers using live menu and optional table selection.

**Business rules and validation**

Staff identity is recorded; table must be eligible; unavailable foods and manipulated prices are rejected.

**Acceptance criteria**

Staff can create a valid order; order appears in staff/kitchen flows; invalid table/item input rolls back.

**Evidence and current control note**

- Evidence/Test reference: `EV-ORDER-08 / TC-ORDER-08`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.10 ORDER-10 — Controlled Order Status Transitions

- **Feature / type:** Staff Operations / Backend Service
- **Actor(s):** Admin, Staff, Kitchen Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Updated
- **Requirement and design references:** RDS/SRS II.8.10; SDS II.8.10

**Objective**

Moves orders through allowed states such as pending, confirmed, preparing, ready, served, completed, or cancelled.

**Business rules and validation**

Transitions follow a defined state machine; completed/cancelled orders are final except authorized correction policy.

**Acceptance criteria**

Valid sequence succeeds; invalid jumps/reversal fail; kitchen/table/payment side effects remain consistent.

**Evidence and current control note**

- Evidence/Test reference: `EV-ORDER-10 / TC-ORDER-10`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Completion must be tied to persisted successful payment.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.11 KITCHEN-01 — Kitchen Order Queue

- **Feature / type:** Kitchen Operations / Kitchen Screen / API
- **Actor(s):** Kitchen Staff
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Updated
- **Requirement and design references:** RDS/SRS II.10.1; SDS II.10.1

**Objective**

Shows confirmed/preparing orders needing kitchen action, prioritized by time and operational status.

**Business rules and validation**

Only kitchen-relevant orders/items appear; access is role-restricted; ordering is deterministic.

**Acceptance criteria**

Queue refreshes from live data; completed/cancelled items leave active queue; wrong roles are denied.

**Evidence and current control note**

- Evidence/Test reference: `EV-KITCHEN-01 / TC-KITCHEN-01`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Priority and refresh behavior were enhanced.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.12 KITCHEN-02 — Item-level Preparation Notes & Status

- **Feature / type:** Kitchen Operations / Kitchen Screen / API
- **Actor(s):** Kitchen Staff
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Updated
- **Requirement and design references:** RDS/SRS II.10.2; SDS II.10.2

**Objective**

Displays item notes and updates preparation status at item/order level according to the chosen workflow.

**Business rules and validation**

Kitchen cannot modify prices/customer data; status changes follow allowed preparation sequence.

**Acceptance criteria**

Notes are visible; valid preparation updates persist; invalid transitions and unauthorized fields are rejected.

**Evidence and current control note**

- Evidence/Test reference: `EV-KITCHEN-02 / TC-KITCHEN-02`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Completed with item note handling.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.13 KITCHEN-03 — Kitchen History Search & Filter

- **Feature / type:** Kitchen Operations / Kitchen Screen / API
- **Actor(s):** Kitchen Staff, Admin
- **Priority / complexity / target LOC band:** Medium / Medium / 120
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.10.3; SDS II.10.3

**Objective**

Lists prepared, completed, and cancelled kitchen work with date/status/order filters.

**Business rules and validation**

History is read-only for finalized records; role restrictions and pagination apply.

**Acceptance criteria**

Search/filter returns correct history; finalized data cannot be edited; empty results are handled.

**Evidence and current control note**

- Evidence/Test reference: `EV-KITCHEN-03 / TC-KITCHEN-03`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.14 COUPON-04 — Apply Coupon Server-side to Order

- **Feature / type:** Coupon Management / Business Service
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Doing
- **Requirement and design references:** RDS/SRS II.11.4; SDS II.11.4

**Objective**

Applies the validated coupon inside the order transaction and persists discount and final total.

**Business rules and validation**

Client discount values are ignored; discount cannot exceed subtotal; one coupon policy is enforced unless specified otherwise.

**Acceptance criteria**

Persisted order total matches server calculation; manipulated requests fail; transaction rolls back on invalid coupon.

**Evidence and current control note**

- Evidence/Test reference: `EV-COUPON-04 / TC-COUPON-04`.
- Baseline interpretation: Active implementation gap; must not be represented as complete until code, tests, documents, and evidence agree.
- Risk/update note: Current discount behavior is client-side or incomplete.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.15 PAY-03 — Payment Amount Verification & Idempotency

- **Feature / type:** Payment & Invoice / Business Service
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.12.3; SDS II.12.3

**Objective**

Verifies payment amount against the final order total and prevents duplicate processing of the same request/order.

**Business rules and validation**

Client amount is advisory only; idempotency key/order uniqueness prevents duplicate successful payments.

**Acceptance criteria**

Amount mismatch fails; repeated request returns the original result or safe conflict; no duplicate records are created.

**Evidence and current control note**

- Evidence/Test reference: `EV-PAY-03 / TC-PAY-03`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Required for financial integrity.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.16 PAY-04 — Persist Payment & Complete Order Transactionally

- **Feature / type:** Payment & Invoice / Backend Service / DB
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.12.4; SDS II.12.4

**Objective**

Stores payment details and updates the order to completed only within one successful transaction.

**Business rules and validation**

Failed payment leaves order unpaid; one successful payment per order; state transition follows order rules.

**Acceptance criteria**

Success creates one payment and completes order; failure rolls back; SQL evidence reconciles amount/status.

**Evidence and current control note**

- Evidence/Test reference: `EV-PAY-04 / TC-PAY-04`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: UI-only status change is not acceptable for final audit.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.17 FB-02 — Feedback Persistence & Completed-order Eligibility

- **Feature / type:** Feedback Management / Backend API / DB
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.14.2; SDS II.14.2

**Objective**

Persists feedback only when the authenticated customer owns a completed eligible order.

**Business rules and validation**

Cancelled/unpaid/incomplete/other-user orders are ineligible; customer identity comes from authentication.

**Acceptance criteria**

Eligible feedback remains after refresh; ineligible/unauthorized requests fail; database relation is correct.

**Evidence and current control note**

- Evidence/Test reference: `EV-FB-02 / TC-FB-02`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Feedback entity/table/API are missing.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.3.18 FB-03 — One Feedback per Order & Input Validation

- **Feature / type:** Feedback Management / Business Service / DB
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.14.3; SDS II.14.3

**Objective**

Enforces one active feedback record per customer order and validates rating/comment.

**Business rules and validation**

Unique constraint prevents duplicates under concurrency; rating is within configured range; comment is sanitized/limited.

**Acceptance criteria**

Duplicate/concurrent submissions create at most one record; invalid input returns field errors.

**Evidence and current control note**

- Evidence/Test reference: `EV-FB-03 / TC-FB-03`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Requires database uniqueness and integration tests.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

### 8.4 Nguyễn Đức Thương (DE190096)

**Role:** Frontend Lead / Customer Experience  
**Owned functions:** 18  
**Baseline progress:** 12 Done/Updated, 5 Doing, 1 Not Started.

The Frontend Lead owns the usability and integration quality of customer and staff experiences. Every screen must use real APIs, display validation correctly, preserve safe state, handle loading/empty/error cases and avoid duplicating authoritative backend rules. Shared components and API clients should be reused to keep behavior consistent.

#### 8.4.1 AUTH-01 — Login Screen & Client Validation

- **Feature / type:** Authentication & Security / UI Screen
- **Actor(s):** Guest, All Users
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter1 / Done
- **Requirement and design references:** RDS/SRS II.1.1; SDS II.1.1

**Objective**

Presents email/password fields, validates required format, shows field-level errors, and redirects authenticated users to the correct role workspace.

**Business rules and validation**

Email is required and normalized; password is never exposed in logs; repeated submissions are prevented while a request is processing.

**Acceptance criteria**

Valid input submits once; invalid input remains on screen with clear messages; successful login redirects by role without exposing credentials.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-01 / TC-AUTH-01`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: UI flow is available and connected to the authentication request.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.2 AUTH-09 — Standard 401/403 Security Responses

- **Feature / type:** Authentication & Security / Exception Handling
- **Actor(s):** All Users
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter1 / Doing
- **Requirement and design references:** RDS/SRS II.1.9; SDS II.1.9

**Objective**

Converts authentication and authorization failures into predictable JSON responses consumed by the frontend.

**Business rules and validation**

401 is used for missing/invalid authentication; 403 is used for authenticated users without permission.

**Acceptance criteria**

Frontend receives stable error codes/messages; no HTML error page is returned by secured APIs; tests cover both statuses.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-09 / TC-AUTH-09`.
- Baseline interpretation: Active implementation gap; must not be represented as complete until code, tests, documents, and evidence agree.
- Risk/update note: Must align with the global API response convention.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.3 AUTH-13 — Forgot Password Request

- **Feature / type:** Authentication & Security / UI / Backend API
- **Actor(s):** Guest
- **Priority / complexity / target LOC band:** Medium / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Done
- **Requirement and design references:** RDS/SRS II.1.13; SDS II.1.13

**Objective**

Allows a user to request a reset token or code without revealing whether the submitted email is registered.

**Business rules and validation**

Response is generic; reset tokens expire and are single-purpose; request rate should be limited.

**Acceptance criteria**

Request always returns a safe response; valid accounts receive a usable reset token/code; repeated abuse is handled.

**Evidence and current control note**

- Evidence/Test reference: `EV-AUTH-13 / TC-AUTH-13`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Implemented; final evidence should include valid and unknown email cases.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.4 USER-02 — Update Own Profile

- **Feature / type:** User Management / UI / Backend API
- **Actor(s):** Customer, Staff, Kitchen Staff, Admin
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Updated
- **Requirement and design references:** RDS/SRS II.2.2; SDS II.2.2

**Objective**

Allows permitted personal/contact fields and avatar data to be updated while preserving role, status, and credentials.

**Business rules and validation**

Protected fields cannot be changed; duplicate contact information and invalid formats are rejected.

**Acceptance criteria**

Permitted changes persist after refresh; protected-field tampering is ignored/rejected; validation messages are clear.

**Evidence and current control note**

- Evidence/Test reference: `EV-USER-02 / TC-USER-02`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Updated with stronger validation and avatar/contact handling.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.5 USER-03 — Admin User List & Search

- **Feature / type:** User Management / Admin Screen / API
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Doing
- **Requirement and design references:** RDS/SRS II.2.3; SDS II.2.3

**Objective**

Provides paginated account listing with search and filters by role and account status.

**Business rules and validation**

Only administrators can access; returned data excludes passwords and security secrets; pagination is stable.

**Acceptance criteria**

Admin can load, search, filter, and open account details; non-admin requests return 403; empty results are handled.

**Evidence and current control note**

- Evidence/Test reference: `EV-USER-03 / TC-USER-03`.
- Baseline interpretation: Active implementation gap; must not be represented as complete until code, tests, documents, and evidence agree.
- Risk/update note: Admin page and backend listing API are incomplete.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.6 MENU-06 — Food Image & Availability Management

- **Feature / type:** Menu Management / UI / Backend API
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter1 / Updated
- **Requirement and design references:** RDS/SRS II.3.6; SDS II.3.6

**Objective**

Updates food image metadata and operational availability separately from active master-data status.

**Business rules and validation**

Unavailable food cannot be newly added to cart/order; existing historical orders retain the original item reference.

**Acceptance criteria**

Image/availability changes persist; customer menu updates; unavailable item ordering is blocked server-side.

**Evidence and current control note**

- Evidence/Test reference: `EV-MENU-06 / TC-MENU-06`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Server-side availability validation remains part of order acceptance testing.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.7 TABLE-01 — View Table List & Availability

- **Feature / type:** Table Management / UI / Backend API
- **Actor(s):** Customer, Staff, Admin
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Done
- **Requirement and design references:** RDS/SRS II.4.1; SDS II.4.1

**Objective**

Displays restaurant tables with table number, capacity, status, and availability appropriate to the current role.

**Business rules and validation**

Customers see reservable information; staff/admin may see operational status; inactive tables are excluded from new reservations.

**Acceptance criteria**

Role-appropriate table data loads correctly; statuses match database state; no-data and error states are handled.

**Evidence and current control note**

- Evidence/Test reference: `EV-TABLE-01 / TC-TABLE-01`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.8 RES-01 — Create Reservation Form

- **Feature / type:** Reservation Management / Customer Screen
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter2 / Done
- **Requirement and design references:** RDS/SRS II.5.1; SDS II.5.1

**Objective**

Collects reservation date, time, party size, selected table, and optional notes with clear validation.

**Business rules and validation**

Past times are not allowed; party size is positive; selected table must remain available at submission.

**Acceptance criteria**

Valid form submits once; invalid date/time/party/table shows clear messages; success displays reservation details.

**Evidence and current control note**

- Evidence/Test reference: `EV-RES-01 / TC-RES-01`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.9 RES-06 — View My Reservations

- **Feature / type:** Reservation Management / Customer Screen / API
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Done
- **Requirement and design references:** RDS/SRS II.5.6; SDS II.5.6

**Objective**

Lists the current customer's upcoming and historical reservations with status and summary information.

**Business rules and validation**

Only reservations owned by the authenticated customer are returned; sorting is deterministic.

**Acceptance criteria**

Customer sees only own records; upcoming/history filters work; unauthorized ID guessing is blocked.

**Evidence and current control note**

- Evidence/Test reference: `EV-RES-06 / TC-RES-06`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.10 RES-07 — Reservation Detail

- **Feature / type:** Reservation Management / Customer Screen / API
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** Medium / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Done
- **Requirement and design references:** RDS/SRS II.5.7; SDS II.5.7

**Objective**

Shows one owned reservation with table, time, party, status, notes, and allowed actions.

**Business rules and validation**

Ownership is enforced server-side; actions depend on time and status.

**Acceptance criteria**

Owned detail loads; other users receive 403/404; edit/cancel buttons appear only when allowed.

**Evidence and current control note**

- Evidence/Test reference: `EV-RES-07 / TC-RES-07`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.11 CART-05 — Cart Persistence Across Navigation / Session

- **Feature / type:** Cart & Order Management / Customer UI / Persistence
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** Medium / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.7.5; SDS II.7.5

**Objective**

Preserves the cart across page navigation and expected session lifecycle while keeping it scoped to the current user.

**Business rules and validation**

One user's cart is never exposed to another; stale items are revalidated before checkout.

**Acceptance criteria**

Cart survives expected navigation/refresh; logout/user switch does not leak items; stale data is handled.

**Evidence and current control note**

- Evidence/Test reference: `EV-CART-05 / TC-CART-05`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.12 ORDER-01 — Checkout Form & Order Notes

- **Feature / type:** Cart & Order Management / Customer UI
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.8.1; SDS II.8.1

**Objective**

Displays order summary and captures dining/table information, contact details, and order notes before submission.

**Business rules and validation**

Required fields depend on order type; notes have length limits; final values are revalidated by backend.

**Acceptance criteria**

Customer can review accurate items/subtotal; invalid checkout data is blocked; submission occurs once.

**Evidence and current control note**

- Evidence/Test reference: `EV-ORDER-01 / TC-ORDER-01`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.13 ORDER-06 — View Own Order Detail

- **Feature / type:** Cart & Order Management / Customer Screen / API
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.8.6; SDS II.8.6

**Objective**

Displays order items, price breakdown, status history, payment/invoice availability, and notes.

**Business rules and validation**

Ownership is enforced; price breakdown comes from persisted authoritative values.

**Acceptance criteria**

Owned order detail loads accurately; other customers cannot access it; invoice link appears only when issued.

**Evidence and current control note**

- Evidence/Test reference: `EV-ORDER-06 / TC-ORDER-06`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Payment/invoice section remains conditional.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.14 STAFF-03 — Staff Workspace / Assigned Tables

- **Feature / type:** Staff Operations / Staff Screen
- **Actor(s):** Staff
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.9.3; SDS II.9.3

**Objective**

Shows the logged-in staff member's assigned tables, current reservations, and active orders.

**Business rules and validation**

Data is scoped to the authenticated staff member and current active assignments.

**Acceptance criteria**

Staff sees only assigned work; updates reflect order/table status changes; empty state is usable.

**Evidence and current control note**

- Evidence/Test reference: `EV-STAFF-03 / TC-STAFF-03`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.15 COUPON-02 — Admin Coupon List / Create / Update

- **Feature / type:** Coupon Management / Admin Screen / API
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.11.2; SDS II.11.2

**Objective**

Provides secure coupon search, creation, editing, activation, and deactivation.

**Business rules and validation**

Only admin can mutate coupons; used coupons are not hard-deleted; overlapping validity and limits are validated.

**Acceptance criteria**

Admin CRUD persists correctly; invalid/duplicate data is rejected; non-admin mutation returns 403.

**Evidence and current control note**

- Evidence/Test reference: `EV-COUPON-02 / TC-COUPON-02`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Backend and frontend management flows are missing.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.16 PAY-01 — Payment Method Selection UI

- **Feature / type:** Payment & Invoice / Customer / Staff UI
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter4 / Doing
- **Requirement and design references:** RDS/SRS II.12.1; SDS II.12.1

**Objective**

Allows selection of supported payment methods such as cash or QR and displays payable amount from the server.

**Business rules and validation**

UI cannot mark an order paid by itself; method availability follows order status and role.

**Acceptance criteria**

Selected method is submitted to the payment API; displayed amount matches server; unsupported method is blocked.

**Evidence and current control note**

- Evidence/Test reference: `EV-PAY-01 / TC-PAY-01`.
- Baseline interpretation: Active implementation gap; must not be represented as complete until code, tests, documents, and evidence agree.
- Risk/update note: Existing UI simulates completion and must be connected to persisted payment.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.17 FB-01 — Submit Feedback Screen

- **Feature / type:** Feedback Management / Customer UI
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter4 / Doing
- **Requirement and design references:** RDS/SRS II.14.1; SDS II.14.1

**Objective**

Collects rating and comment for an eligible completed order and displays validation/API responses.

**Business rules and validation**

Rating range and comment length are validated; UI alone does not persist final feedback.

**Acceptance criteria**

Eligible customer can submit once; invalid rating/comment is blocked; refresh shows persisted result after backend completion.

**Evidence and current control note**

- Evidence/Test reference: `EV-FB-01 / TC-FB-01`.
- Baseline interpretation: Active implementation gap; must not be represented as complete until code, tests, documents, and evidence agree.
- Risk/update note: Current feedback is browser-local and is lost after refresh.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.4.18 RPT-04 — Dashboard Frontend Live-data Integration

- **Feature / type:** Reporting & Analytics / Admin UI
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Doing
- **Requirement and design references:** RDS/SRS II.15.4; SDS II.15.4

**Objective**

Replaces hardcoded dashboard values with secured report API calls, loading states, errors, and no-data states.

**Business rules and validation**

No production metric remains hardcoded; requests include authentication; chart labels/units match API.

**Acceptance criteria**

Network evidence shows real API calls; displayed totals match response/SQL; API failure does not crash page.

**Evidence and current control note**

- Evidence/Test reference: `EV-RPT-04 / TC-RPT-04`.
- Baseline interpretation: Active implementation gap; must not be represented as complete until code, tests, documents, and evidence agree.
- Risk/update note: Frontend exists but is not connected to verified database aggregates.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

### 8.5 Trần Thanh Gia Huy (DE180571)

**Role:** Reservation, Coupon, AI, Quality and Analytics Support  
**Owned functions:** 18  
**Baseline progress:** 12 Done/Updated, 0 Doing, 6 Not Started.

This role combines reservation operations, coupon/invoice data support, quality evidence, analytics and AI scope. The member must emphasize data integrity, concurrency, traceability, moderation/audit behavior and the distinction between verified live data and advisory AI output.

#### 8.5.1 TABLE-05 — Table Operational Status Update

- **Feature / type:** Table Management / Staff / Admin API
- **Actor(s):** Staff, Admin
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Updated
- **Requirement and design references:** RDS/SRS II.4.5; SDS II.4.5

**Objective**

Updates operational table state such as available, reserved, occupied, or unavailable with controlled transitions.

**Business rules and validation**

Transitions must respect active reservations/orders and cannot silently overwrite conflicting state.

**Acceptance criteria**

Allowed transitions succeed; invalid transitions return business errors; reservation/order flows remain synchronized.

**Evidence and current control note**

- Evidence/Test reference: `EV-TABLE-05 / TC-TABLE-05`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Status transition rules were expanded.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.2 TABLE-06 — Table Search & Filter

- **Feature / type:** Table Management / UI / Backend API
- **Actor(s):** Staff, Admin
- **Priority / complexity / target LOC band:** Medium / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Done
- **Requirement and design references:** RDS/SRS II.4.6; SDS II.4.6

**Objective**

Supports search and filtering by table number, capacity, active flag, and operational status.

**Business rules and validation**

Filters are server-compatible and stable with pagination; role restrictions remain applied.

**Acceptance criteria**

Combined filters return correct records and reset correctly; empty results display cleanly.

**Evidence and current control note**

- Evidence/Test reference: `EV-TABLE-06 / TC-TABLE-06`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.3 RES-08 — Update Future Reservation

- **Feature / type:** Reservation Management / Customer API
- **Actor(s):** Customer
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Done
- **Requirement and design references:** RDS/SRS II.5.8; SDS II.5.8

**Objective**

Updates an eligible future reservation after rechecking capacity, table availability, time, and overlap.

**Business rules and validation**

Past, completed, cancelled, seated, or otherwise locked reservations cannot be modified.

**Acceptance criteria**

Allowed changes persist and revalidate conflicts; forbidden statuses/times are rejected; original data remains on failure.

**Evidence and current control note**

- Evidence/Test reference: `EV-RES-08 / TC-RES-08`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.4 RES-09 — Cancel Reservation Rule

- **Feature / type:** Reservation Management / Customer / Staff API
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** High / Medium / 120
- **Planned iteration / baseline status:** Iter2 / Done
- **Requirement and design references:** RDS/SRS II.5.9; SDS II.5.9

**Objective**

Cancels an eligible reservation while preserving the record and releasing table availability.

**Business rules and validation**

Cancellation is status/time dependent; cancellation reason may be required for staff actions.

**Acceptance criteria**

Eligible cancellation updates status; repeated cancellation is idempotent; table becomes available when appropriate.

**Evidence and current control note**

- Evidence/Test reference: `EV-RES-09 / TC-RES-09`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.5 RES-10 — Admin / Staff Reservation List

- **Feature / type:** Reservation Management / Admin/Staff Screen / API
- **Actor(s):** Admin, Staff
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Updated
- **Requirement and design references:** RDS/SRS II.5.10; SDS II.5.10

**Objective**

Provides paginated search and filters by customer, table, date range, and reservation status.

**Business rules and validation**

Access is role-restricted; date filters are inclusive and stable; sensitive customer fields are minimized.

**Acceptance criteria**

Authorized users can find reservations accurately; non-authorized access is rejected; export scope is controlled if used.

**Evidence and current control note**

- Evidence/Test reference: `EV-RES-10 / TC-RES-10`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Operational filters and workflow actions were added.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.6 RES-11 — Confirm / Seat / Complete Reservation

- **Feature / type:** Reservation Management / Staff API
- **Actor(s):** Staff, Admin
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Updated
- **Requirement and design references:** RDS/SRS II.5.11; SDS II.5.11

**Objective**

Moves reservations through operational states and synchronizes the table state where required.

**Business rules and validation**

Only permitted transitions are allowed; seating requires an available assigned table; completion releases the table.

**Acceptance criteria**

Valid transition sequence succeeds; skipped/reversed transitions fail; table status remains consistent.

**Evidence and current control note**

- Evidence/Test reference: `EV-RES-11 / TC-RES-11`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Status workflow was integrated with table operations.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.7 RES-12 — No-show / Expired Reservation Handling

- **Feature / type:** Reservation Management / Staff Function
- **Actor(s):** Staff, Admin
- **Priority / complexity / target LOC band:** Medium / Medium / 120
- **Planned iteration / baseline status:** Iter2 / Updated
- **Requirement and design references:** RDS/SRS II.5.12; SDS II.5.12

**Objective**

Marks eligible past reservations as no-show or expired according to operational policy.

**Business rules and validation**

Only past confirmed reservations qualify; changes are auditable and must not affect unrelated reservations.

**Acceptance criteria**

Eligible records can be marked; future/already-final records are rejected; table availability is restored.

**Evidence and current control note**

- Evidence/Test reference: `EV-RES-12 / TC-RES-12`.
- Baseline interpretation: Implemented and revised; requires regression verification against the latest shared contracts.
- Risk/update note: Final automation/manual policy should be confirmed in SRS.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.8 SYS-02 — Database Migration & Seed for Table / Reservation

- **Feature / type:** System Foundation / Database Migration
- **Actor(s):** Development Team
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter2 / Done
- **Requirement and design references:** RDS/SRS II.6.2; SDS II.6.2

**Objective**

Provides ordered migration scripts and safe reference seed data for table and reservation modules.

**Business rules and validation**

Migrations run on a clean database and an existing development database; seed data uses deterministic keys and no secrets.

**Acceptance criteria**

Flyway/migration history is successful; schema constraints/indexes exist; application starts with the clean database.

**Evidence and current control note**

- Evidence/Test reference: `EV-SYS-02 / TC-SYS-02`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Include migration output in the release evidence.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.9 STAFF-01 — Assign Tables to Staff

- **Feature / type:** Staff Operations / Admin/Staff Screen / API
- **Actor(s):** Admin, Staff
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.9.1; SDS II.9.1

**Objective**

Creates time-bounded staff-to-table assignments used by the service workspace.

**Business rules and validation**

Staff/table must be active; assignment times are valid; assignment creator is auditable.

**Acceptance criteria**

Valid assignment appears in workspace; invalid/inactive references are rejected; history is retained.

**Evidence and current control note**

- Evidence/Test reference: `EV-STAFF-01 / TC-STAFF-01`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.10 STAFF-02 — Prevent Active Assignment Conflict

- **Feature / type:** Staff Operations / Business Service
- **Actor(s):** Admin, Staff
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter3 / Done
- **Requirement and design references:** RDS/SRS II.9.2; SDS II.9.2

**Objective**

Prevents overlapping active assignments that violate the configured one-table/one-staff service policy.

**Business rules and validation**

Conflict rules are evaluated transactionally and respect assignment end time/status.

**Acceptance criteria**

Conflicting assignment is rejected; non-overlapping assignment succeeds; concurrent tests preserve consistency.

**Evidence and current control note**

- Evidence/Test reference: `EV-STAFF-02 / TC-STAFF-02`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.11 COUPON-01 — Coupon Entity & Database Migration

- **Feature / type:** Coupon Management / Database Migration
- **Actor(s):** Admin, Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.11.1; SDS II.11.1

**Objective**

Creates coupon and usage persistence with code, type, value, limits, validity period, status, and audit fields.

**Business rules and validation**

Coupon code is unique; values and dates are valid; monetary/percentage constraints and indexes are enforced.

**Acceptance criteria**

Migration succeeds on a clean database; constraints reject invalid/duplicate records; entity mapping passes tests.

**Evidence and current control note**

- Evidence/Test reference: `EV-COUPON-01 / TC-COUPON-01`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: No verified coupon tables/entities currently exist.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.12 COUPON-03 — Coupon Eligibility Validation API

- **Feature / type:** Coupon Management / Backend API
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.11.3; SDS II.11.3

**Objective**

Validates coupon status, date, minimum amount, usage limits, role/customer eligibility, and applicable order context.

**Business rules and validation**

Validation uses server time/data; an invalid coupon cannot affect order total; error reasons are controlled.

**Acceptance criteria**

Valid coupon returns authoritative discount preview; expired/inactive/over-limit/ineligible coupons are rejected.

**Evidence and current control note**

- Evidence/Test reference: `EV-COUPON-03 / TC-COUPON-03`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Required before checkout can be audit-safe.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.13 COUPON-05 — Coupon Usage Limit & Atomic Persistence

- **Feature / type:** Coupon Management / Database / Business Service
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.11.5; SDS II.11.5

**Objective**

Records coupon usage atomically and enforces global/per-user usage limits during concurrent order creation.

**Business rules and validation**

Usage is counted only for the configured successful state; rollback removes failed usage; concurrency cannot exceed limits.

**Acceptance criteria**

Concurrent final-usage tests allow only permitted usage; failed order creates no orphan usage record.

**Evidence and current control note**

- Evidence/Test reference: `EV-COUPON-05 / TC-COUPON-05`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Requires coupon usage table and transaction tests.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.14 INV-01 — Invoice Entity & Unique Order Rule

- **Feature / type:** Payment & Invoice / Database Migration
- **Actor(s):** Customer, Staff, Admin
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.13.1; SDS II.13.1

**Objective**

Creates immutable invoice persistence linked one-to-one with a successfully paid order.

**Business rules and validation**

One invoice per paid order; invoice number is unique; financial snapshot fields are non-editable after issue.

**Acceptance criteria**

Migration creates constraints; duplicate invoice creation fails; invoice values remain unchanged after menu updates.

**Evidence and current control note**

- Evidence/Test reference: `EV-INV-01 / TC-INV-01`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: No verified invoice table/entity exists.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.15 INV-02 — Generate Immutable Invoice after Payment

- **Feature / type:** Payment & Invoice / Backend Service
- **Actor(s):** Customer, Staff
- **Priority / complexity / target LOC band:** Critical / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.13.2; SDS II.13.2

**Objective**

Generates the invoice automatically within/after the successful payment transaction using order and payment snapshots.

**Business rules and validation**

No invoice is generated for failed payment; retry cannot create a second invoice; totals reconcile exactly.

**Acceptance criteria**

Each successful payment produces exactly one invoice; failed/duplicate payment produces none/one as appropriate.

**Evidence and current control note**

- Evidence/Test reference: `EV-INV-02 / TC-INV-02`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: Depends on PAY-04 and INV-01.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.16 FB-04 — Admin Feedback Search / Moderation

- **Feature / type:** Feedback Management / Admin Screen / API
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** High / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Not Started
- **Requirement and design references:** RDS/SRS II.14.4; SDS II.14.4

**Objective**

Lists feedback with filters and permits controlled moderation status without rewriting the original rating history.

**Business rules and validation**

Only admin moderates; original author/rating/comment remain auditable; moderation action records actor/time/reason.

**Acceptance criteria**

Admin can search/filter/review/moderate; non-admin is denied; moderation persists after refresh.

**Evidence and current control note**

- Evidence/Test reference: `EV-FB-04 / TC-FB-04`.
- Baseline interpretation: Planned backlog item; implementation and evidence are still required.
- Risk/update note: No verified moderation API exists.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.17 AI-01 — AI Analytics with Controlled Data & Logging

- **Feature / type:** AI Support / Admin AI Function
- **Actor(s):** Admin
- **Priority / complexity / target LOC band:** Medium / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Done
- **Requirement and design references:** RDS/SRS II.16.1; SDS II.16.1

**Objective**

Provides AI-assisted interpretation of selected report data with controlled inputs, advisory output, and audit logging.

**Business rules and validation**

AI output is advisory and cannot directly alter financial/operational data; prompts exclude secrets and unnecessary PII.

**Acceptance criteria**

Authorized admin receives a logged result; failures have safe fallback; output clearly states limitations.

**Evidence and current control note**

- Evidence/Test reference: `EV-AI-01 / TC-AI-01`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed with controlled analytics scope.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

#### 8.5.18 AI-02 — Customer Chatbot FAQ & Safe Fallback

- **Feature / type:** AI Support / Customer AI Function
- **Actor(s):** Guest, Customer
- **Priority / complexity / target LOC band:** Medium / Complex / 240
- **Planned iteration / baseline status:** Iter4 / Done
- **Requirement and design references:** RDS/SRS II.16.2; SDS II.16.2

**Objective**

Answers common restaurant/menu/reservation/order questions and routes unsupported requests to a safe fallback.

**Business rules and validation**

Chatbot cannot expose another user's data or perform privileged actions without authenticated APIs; uncertain answers are disclosed.

**Acceptance criteria**

FAQ answers are relevant; unsafe/unknown prompts receive fallback; session-aware links do not leak data.

**Evidence and current control note**

- Evidence/Test reference: `EV-AI-02 / TC-AI-02`.
- Baseline interpretation: Implemented and verified in the current baseline; re-run regression checks before the final tag.
- Risk/update note: Completed.

**Owner completion checklist**

- [ ] Confirm the current issue and acceptance criteria.
- [ ] Implement or verify the correct backend/frontend/database layers.
- [ ] Test the happy path, validation failures, authorization failures and relevant state/concurrency cases.
- [ ] Attach evidence and update Project Tracking, RDS/SRS, SDS and Issues Report when applicable.
- [ ] Open a focused Pull Request and obtain the required review.

## 9. Iteration Plan

| Iteration | Planned units | Main objective | Exit condition |
| --- | ---: | --- | --- |
| Iter1 | 25 | Authentication, security, user and menu foundation | Role-based login, safe credentials, account/menu baseline, initial documents and evidence |
| Iter2 | 20 | Restaurant tables and reservation workflow | Stable schema, availability search, conflict-safe reservation, reservation operations and seed data |
| Iter3 | 23 | Cart, order, staff and kitchen operations | Server-calculated order transaction, ownership, staff workspace, kitchen queue and controlled state flow |
| Iter4 | 22 | Coupon, payment, invoice, feedback, reports, AI and final hardening | Financial persistence, live reports, complete evidence, final release package and demo |

### 9.1 Iteration 1 — Foundation

- Freeze package naming, API response format and base security strategy.
- Deliver login, registration, reset-password foundation and password hashing.
- Establish user profile/admin account behavior.
- Deliver category/food management and customer menu.
- Create initial test data and Postman requests.
- Update RDS/SRS and SDS for all completed Iter1 functions.

### 9.2 Iteration 2 — Table and Reservation

- Finalize restaurant table entity, capacity, status and uniqueness.
- Implement availability search and reservation-capacity matching.
- Prevent overlap in a concurrency-safe transaction.
- Support customer reservation detail/update/cancel.
- Support staff/admin confirmation, seating, completion, no-show and expiration.
- Deliver clean migration and seed evidence.

### 9.3 Iteration 3 — Cart, Order, Staff and Kitchen

- Implement cart add/update/remove/persistence.
- Revalidate food availability and calculate totals server-side.
- Persist order and immutable item snapshots transactionally.
- Enforce customer ownership and role access.
- Implement counter order, staff assignment and cancellation audit.
- Implement kitchen queue, notes, item status and history.

### 9.4 Iteration 4 — Financial, Quality and Release

- Implement coupon schema, eligibility, server-side application and atomic usage.
- Implement payment API, amount verification, idempotency and transactional order completion.
- Generate exactly one immutable invoice and provide authorized view/download.
- Persist eligible feedback once per order and support moderation.
- Replace dashboard mock data with live aggregate APIs.
- Keep AI functions bounded, advisory, authorized and logged.
- Close security gaps, run regression, synchronize all documents and prepare the final release.

## 10. Blocking Dependencies

| Dependent work | Must wait for | Reason |
| --- | --- | --- |
| Protected frontend pages | Stable login/JWT/error contract | The frontend must know how to store/submit credentials and handle 401/403 safely |
| Reservation creation | Table schema, capacity and active-state rules | Availability and persistence depend on authoritative table data |
| Order checkout | Food availability, price and order DTO contract | The server must reject stale or manipulated client data |
| Coupon application | Coupon schema and eligibility API | Discount cannot remain client-side or non-persistent |
| Payment processing | Final server-calculated order total and eligible order state | Payment amount and completion must be authoritative |
| Invoice generation | Successful persisted payment | Invoice must represent an immutable financial snapshot |
| Feedback | Completed owned order | Eligibility and one-record rule depend on order state and ownership |
| Revenue dashboard | Persisted successful payments/invoices | Mock or order-created totals are not accepted as paid revenue |
| AI analytics | Authorized live report aggregates | AI must interpret controlled data rather than invent or access unrestricted records |

## 11. Shared API Contract Rules

All modules use `/api/v1` and stable resource-oriented paths. Representative contracts:

```text
POST   /api/v1/auth/login
POST   /api/v1/auth/refresh
POST   /api/v1/auth/logout
POST   /api/v1/auth/register
POST   /api/v1/auth/forgot-password
POST   /api/v1/auth/reset-password

GET    /api/v1/foods
POST   /api/v1/foods
PATCH  /api/v1/foods/{id}/availability

GET    /api/v1/tables/availability
POST   /api/v1/reservations
PATCH  /api/v1/reservations/{id}/confirm
PATCH  /api/v1/reservations/{id}/cancel

POST   /api/v1/orders
GET    /api/v1/orders/my
PATCH  /api/v1/orders/{id}/status
PATCH  /api/v1/kitchen/items/{id}/status

POST   /api/v1/coupons/validate
POST   /api/v1/orders/{id}/payments
GET    /api/v1/invoices/{id}
POST   /api/v1/orders/{id}/feedback
GET    /api/v1/reports/dashboard
```

Endpoint names shown here are coordination examples. The actual repository contract takes precedence once approved, but any change must be reflected in issues, DTOs, frontend services, tests, RDS/SRS and SDS.

## 12. Core Data Ownership and Integration

| Data area | Main coordination owner | Required integrity |
| --- | --- | --- |
| Users, roles, refresh/reset tokens | Phạm Văn Quyết with Phan Nguyễn | Unique normalized identity, BCrypt, revocation, no plaintext secrets |
| Categories and foods | Phan Nguyễn with Nguyễn Đức Thương | Unique/active master data, positive price, image/availability validation |
| Restaurant tables and reservations | Phạm Văn Quyết with Trần Thanh Gia Huy | Capacity, uniqueness, active-state rules, overlap prevention and version/locking |
| Cart, orders and order items | Nguyễn Tiến Lộc | Server totals, item snapshots, ownership, transaction rollback and controlled state |
| Staff assignments and kitchen items | Nguyễn Tiến Lộc with Trần Thanh Gia Huy | Non-overlapping assignment, queue consistency and valid item transitions |
| Coupons and usage | Trần Thanh Gia Huy with Nguyễn Tiến Lộc | Eligibility, global/per-user limits, atomic use and rollback |
| Payments and invoices | Phạm Văn Quyết, Nguyễn Tiến Lộc and Trần Thanh Gia Huy | Amount verification, idempotency, one successful result, one immutable invoice |
| Feedback and reports | Trần Thanh Gia Huy, Phan Nguyễn and Nguyễn Đức Thương | Eligible ownership, uniqueness, moderation audit and SQL-reconciled aggregates |

## 13. Status and State-Machine Coordination

Controlled transitions must be documented and tested. Example intent:

```text
Reservation:
PENDING -> CONFIRMED -> SEATED -> COMPLETED
PENDING / CONFIRMED -> CANCELLED
CONFIRMED -> NO_SHOW / EXPIRED (under defined rules)

Order:
PENDING -> CONFIRMED -> PREPARING -> READY -> SERVED -> COMPLETED
Eligible non-terminal states -> CANCELLED

Kitchen item:
NEW -> ACKNOWLEDGED -> PREPARING -> READY -> COMPLETED
Eligible state -> CANCELLED / SKIPPED under controlled rules

Invoice:
Created only after successful payment; immutable after issue
```

The actual enum values must match the repository and SDS. No member may invent a new status only in the UI. Every transition requires backend validation and negative tests.

## 14. Testing Allocation

| Test area | Primary owner | Required support |
| --- | --- | --- |
| Authentication, JWT, role and ownership | Phạm Văn Quyết | Phan Nguyễn and Nguyễn Đức Thương for account/UI cases |
| Menu and user administration | Phan Nguyễn | Nguyễn Đức Thương for UI integration |
| Table and reservation concurrency | Phạm Văn Quyết and Trần Thanh Gia Huy | Full team for end-to-end reservation demo |
| Cart, order totals and kitchen state | Nguyễn Tiến Lộc | Nguyễn Đức Thương for UI and Phạm Văn Quyết for authorization |
| Coupon atomic usage | Trần Thanh Gia Huy and Nguyễn Tiến Lộc | Phạm Văn Quyết for protected API cases |
| Payment and invoice integrity | Phạm Văn Quyết, Nguyễn Tiến Lộc and Trần Thanh Gia Huy | Phan Nguyễn/Nguyễn Đức Thương for view/report integration |
| Feedback and reports | Trần Thanh Gia Huy and Phan Nguyễn | Nguyễn Đức Thương for live frontend data |
| Regression, evidence and demo | All members; leader coordinates | Each owner must provide Work ID evidence |

Minimum negative cases include wrong role, wrong owner, invalid state, duplicate request, invalid amount, expired/inactive record, missing required field, concurrency conflict, provider failure and database rollback.

## 15. Documentation Allocation

- **Project Tracking:** leader maintains structure; every owner updates status, evidence and notes for owned Work IDs.
- **RDS/SRS:** owners maintain business rules, validation, use cases and acceptance criteria for their functions.
- **SDS:** owners maintain class, sequence, database, API, security and transaction designs.
- **Issues Report:** defect discoverer records the issue; module owner coordinates resolution; tester verifies closure.
- **Final Release Document:** leader controls the baseline; each owner supplies installation, user-manual, operations, test and limitation content.
- **Presentation/demo:** each member explains owned modules and evidence; leader controls the overall narrative and timing.
- **AI usage records:** the member using AI records meaningful prompts, verification, corrections and ownership according to course policy.

## 16. GitHub Issue and Branch Plan

Recommended issue title:

```text
[WORK-ID] Implement short functional name
```

Recommended branch:

```text
feature/<issue-number>-<short-name>
bugfix/<issue-number>-<short-name>
security/<issue-number>-<short-name>
test/<issue-number>-<short-name>
docs/<issue-number>-<short-name>
```

Each member should produce multiple focused branches across the semester. A single branch containing all eighteen tasks is not acceptable because it hides progress, increases conflict risk and prevents meaningful review.

## 17. Definition of Ready

- [ ] Work ID, owner, priority and iteration are known.
- [ ] Business objective and acceptance criteria are clear.
- [ ] Database/API/security impact is identified.
- [ ] Dependencies and reviewers are named.
- [ ] Required design is available.
- [ ] Test data and evidence approach are planned.
- [ ] A focused GitHub issue exists.

## 18. Definition of Done

- [ ] Correct layers are implemented.
- [ ] Backend compiles and frontend builds.
- [ ] Clean-database migration succeeds.
- [ ] Business rules and state transitions are enforced on the backend.
- [ ] Role and ownership checks pass positive and negative tests.
- [ ] Transactions and rollback behavior are verified.
- [ ] Loading, empty, validation and error states work in the UI.
- [ ] Unit/integration/API/manual tests pass.
- [ ] Evidence is named and linked by Work ID/Test ID.
- [ ] RDS/SRS, SDS, Project Tracking and Issues Report are synchronized.
- [ ] Pull Request is reviewed and merged.
- [ ] The owner can demonstrate and explain the implementation.

## 19. Current Baseline and Priority Gap Queue

The 90-item baseline contains **42 Done**, **18 Updated**, **11 Doing**, and **19 Not Started** functions. Done/Updated functions still require final regression against the final tag.

Critical open items should be handled before optional polish:

- **AUTH-04 — JWT Access Token Generation** (Phạm Văn Quyết (DE190425), Doing): Token generation is incomplete and must be finalized before the final release tag.
- **AUTH-06 — JWT Authentication Filter** (Phạm Văn Quyết (DE190425), Doing): Filter and integration tests are incomplete.
- **AUTH-07 — Token Expiry & Invalid Token Handling** (Phạm Văn Quyết (DE190425), Doing): Standard exception mapping remains incomplete.
- **AUTH-08 — Endpoint Role Authorization Matrix** (Phạm Văn Quyết (DE190425), Doing): SecurityConfig is currently too permissive and needs a complete endpoint matrix.
- **USER-03 — Admin User List & Search** (Nguyễn Đức Thương (DE190096), Doing): Admin page and backend listing API are incomplete.
- **ORDER-04 — Recalculate Final Total & Reject Client Manipulation** (Nguyễn Tiến Lộc (DE190986), Doing): Blocked by complete server-side coupon implementation.
- **COUPON-04 — Apply Coupon Server-side to Order** (Nguyễn Tiến Lộc (DE190986), Doing): Current discount behavior is client-side or incomplete.
- **RPT-04 — Dashboard Frontend Live-data Integration** (Nguyễn Đức Thương (DE190096), Doing): Frontend exists but is not connected to verified database aggregates.
- **AUTH-05 — JWT Refresh Token Persistence** (Phạm Văn Quyết (DE190425), Not Started): Required database migration, entity, repository, service, and tests are missing.
- **USER-04 — Admin Change Role / Lock Account** (Phan Nguyễn (DE191019), Not Started): Role/status APIs, UI actions, audit logging, and regression tests are missing.
- **COUPON-01 — Coupon Entity & Database Migration** (Trần Thanh Gia Huy (DE180571), Not Started): No verified coupon tables/entities currently exist.
- **COUPON-02 — Admin Coupon List / Create / Update** (Nguyễn Đức Thương (DE190096), Not Started): Backend and frontend management flows are missing.
- **COUPON-03 — Coupon Eligibility Validation API** (Trần Thanh Gia Huy (DE180571), Not Started): Required before checkout can be audit-safe.
- **COUPON-05 — Coupon Usage Limit & Atomic Persistence** (Trần Thanh Gia Huy (DE180571), Not Started): Requires coupon usage table and transaction tests.
- **PAY-02 — Payment Processing API** (Phạm Văn Quyết (DE190425), Not Started): No verified /api/payments/process lifecycle currently exists.
- **PAY-03 — Payment Amount Verification & Idempotency** (Nguyễn Tiến Lộc (DE190986), Not Started): Required for financial integrity.
- **PAY-04 — Persist Payment & Complete Order Transactionally** (Nguyễn Tiến Lộc (DE190986), Not Started): UI-only status change is not acceptable for final audit.
- **INV-01 — Invoice Entity & Unique Order Rule** (Trần Thanh Gia Huy (DE180571), Not Started): No verified invoice table/entity exists.
- **INV-02 — Generate Immutable Invoice after Payment** (Trần Thanh Gia Huy (DE180571), Not Started): Depends on PAY-04 and INV-01.
- **FB-02 — Feedback Persistence & Completed-order Eligibility** (Nguyễn Tiến Lộc (DE190986), Not Started): Feedback entity/table/API are missing.
- **RPT-01 — Dashboard KPI Aggregation API** (Phan Nguyễn (DE191019), Not Started): Dashboard currently uses static/mock figures.

Recommended completion sequence:

1. Close JWT generation/filter/error/role-matrix gaps and externalize all secrets.
2. Complete server-side order total and coupon persistence/atomic usage.
3. Complete payment amount verification, idempotency, transaction persistence and protected API.
4. Create immutable invoice persistence and generation.
5. Persist feedback and enforce eligibility/uniqueness.
6. Replace all mock dashboard values with SQL-reconciled live APIs.
7. Run cross-role regression and synchronize evidence/document statuses.

## 20. Final Demonstration Flow

1. Guest browses the menu and table availability.
2. Customer registers or logs in.
3. Customer updates profile information.
4. Customer creates a reservation; the system validates capacity and overlap.
5. Staff confirms/seats the reservation or creates a counter order.
6. Customer or staff adds items and submits an order.
7. Backend recalculates prices and persists order-item snapshots.
8. Kitchen Staff processes the queue and updates preparation status.
9. Eligible coupon is validated and applied server-side.
10. Customer or staff processes payment; repeated request does not duplicate payment.
11. The system completes the order transactionally and generates one immutable invoice.
12. Customer views the invoice and submits one eligible feedback record.
13. Administrator moderates feedback and views live dashboard/reports.
14. Administrator optionally runs bounded AI analytics on authorized aggregate data.
15. The team shows audit evidence, tests, database proof and known limitations.

## 21. Release Readiness Checklist

- [ ] Clean clone and controlled checkout are verified.
- [ ] Java 21, Maven, Node/npm and SQL Server prerequisites are documented.
- [ ] Database migrations and seed succeed on a clean database.
- [ ] All role accounts are tested.
- [ ] Critical workflows pass end-to-end testing.
- [ ] No real credentials or private tokens are committed.
- [ ] All `Doing` and `Not Started` claims are either completed or disclosed.
- [ ] Project Tracking, RDS/SRS, SDS, Issues Report and Final Release Document agree.
- [ ] Final tag, commit reference, demo link and evidence paths are recorded.
- [ ] Every member can explain their eighteen owned functional units.

## 22. Final Coordination Statement

This allocation is intended to keep individual responsibility clear while preserving one integrated product. No member should optimize only their own screen or service at the expense of shared contracts. The final evaluation must show that authentication, reservation, order, kitchen, coupon, payment, invoice, feedback, reporting and AI support operate as one coherent Restaurant Management System. The leader and module owners must continually compare planned behavior with the actual repository and must keep incomplete gaps transparent until they are truly implemented, tested, documented and evidenced.
