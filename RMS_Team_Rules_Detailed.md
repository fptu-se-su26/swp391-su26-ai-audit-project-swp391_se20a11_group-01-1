# Restaurant Management System — Team Rules

> **Project:** Restaurant Management System (RMS)  
> **Course:** SWP391 — Application Development Project  
> **Class / Group:** SE20A11 / Group 01  
> **Technology baseline:** Java 21, Spring Boot, React, Microsoft SQL Server  
> **Document purpose:** Establish mandatory collaboration, source-control, architecture, security, testing, documentation, and release rules for the whole team.

---

## 1. Purpose and Scope

This document defines how Group 01 develops, reviews, integrates, tests, documents, and releases the Restaurant Management System. The rules apply to every member, every iteration, every Git branch, every Pull Request, and every project artifact, including source code, database migrations, API contracts, test evidence, SRS/RDS, SDS, Project Tracking, Issues Report, Final Release Document, presentation material, and demonstration data.

The project contains many connected workflows: authentication, user management, menu management, restaurant-table management, reservation, cart, checkout, order processing, staff operations, kitchen operations, coupon validation, payment, invoice generation, customer feedback, reporting, and bounded AI support. A small change in one module can affect database constraints, API responses, frontend screens, security permissions, tests, and documentation. Therefore, members must work through controlled integration rather than isolated coding.

These rules are designed to achieve five objectives:

1. Keep the codebase compilable and demonstrable throughout the semester.
2. Prevent one member from silently changing shared contracts that block other members.
3. Preserve traceability from requirement to design, code, test, issue, and evidence.
4. Protect security-sensitive and financial workflows from shortcuts.
5. Make individual contribution visible through issues, branches, commits, reviews, tests, and delivered functions.

No member may ignore these rules because a deadline is near. Under time pressure, the team may reduce optional scope, but it must not reduce source-control discipline, security controls, database integrity, or evidence quality.

---

## 2. Project Working Principles

### 2.1 Work by end-to-end business capability

Tasks must be organized around a complete function or workflow, not random CRUD fragments. For example, “Process payment” includes request validation, authorization, service logic, database persistence, idempotency, error handling, API response, frontend integration, tests, and evidence. It must not be split into unrelated pieces without a clear integration owner.

Recommended units of work include:

- Authenticate user and establish a protected session.
- Search available tables and create a conflict-safe reservation.
- Add items to cart, validate availability, and place an order.
- Move confirmed order items through the kitchen state machine.
- Apply a coupon transactionally and persist usage.
- Process payment and generate one immutable invoice.
- Aggregate dashboard data from live SQL Server records.

### 2.2 One issue, one branch, one primary purpose

A branch must solve one issue or one closely related group of subtasks. A branch must not contain unrelated UI redesign, dependency upgrades, database changes, and business features unless the issue explicitly covers an integrated workflow.

### 2.3 Shared contracts require approval

The following items are controlled contracts:

- Entity names, relationships, primary keys, and foreign keys.
- Database table, column, index, constraint, and migration names.
- Enum values and valid status transitions.
- REST endpoint paths, HTTP methods, DTO fields, and error format.
- Authentication, JWT, role, and ownership rules.
- Money calculation, discount, payment, and invoice rules.
- Common frontend API client behavior.
- Publicly referenced Work IDs, test IDs, and evidence IDs.

A member must not change these contracts without notifying the leader and affected owners. Changes must be documented in the issue and Pull Request.

### 2.4 Documentation does not prove implementation

A function is not complete merely because it appears in the RDS, SDS, Project Tracking, or Final Release Document. Completion requires matching code, database schema, tests, and demonstrable evidence. Status must remain `Doing` or `Not Started` when implementation evidence is incomplete.

### 2.5 Prefer transparent gaps over false completion

When a feature is incomplete, the team must record the gap, dependency, risk, and next action. It is better to present an honest limitation than to mark a mock UI, hardcoded dashboard, client-side-only coupon, or simulated payment as completed functionality.

---

## 3. Team Roles and Decision Authority

| Member | Primary responsibility | Decision authority |
| --- | --- | --- |
| Phan Nguyễn — DE191019 | Team Leader, integration, admin/menu/report coordination | Final integration order, release readiness, scope and priority decisions |
| Phạm Văn Quyết — DE190425 | Backend & Security Lead | Authentication, authorization, API protection, ownership checks, security review, payment API coordination |
| Nguyễn Tiến Lộc — DE190986 | Order, kitchen, and transaction flow | Cart/order/kitchen state machine, transactional total calculation, payment transaction coordination |
| Nguyễn Đức Thương — DE190096 | Frontend Lead and customer experience | React structure, shared components, customer/staff screen consistency, frontend API integration |
| Trần Thanh Gia Huy — DE180571 | Reservation, coupon, AI, quality and analytics support | Reservation operations, coupon/invoice data support, quality evidence, analytics and AI integration |

### 3.1 Leader responsibilities

The leader coordinates scope, dependencies, integration sequence, branch protection, Pull Request readiness, release tagging, and document synchronization. The leader may reject a Pull Request that compiles but violates architecture, security, business rules, traceability, or evidence requirements.

### 3.2 Module-owner responsibilities

A module owner must:

- Clarify business rules before coding.
- Maintain the issue and acceptance criteria.
- Coordinate database and API changes with affected members.
- Review incoming changes that touch the owned module.
- Provide tests and evidence.
- Update related documentation references.
- Explain the module during demonstration or defense.

Ownership does not mean exclusive access. Other members may contribute, but the owner remains accountable for integration quality.

### 3.3 Reviewer responsibilities

A reviewer must evaluate logic, not only formatting. Approval means the reviewer has checked the relevant code path, contracts, tests, and risk. “Looks good” without inspection is not an acceptable review for security, reservation overlap, order totals, coupon usage, payment, or invoice changes.

---

## 4. Source of Truth and Document Synchronization

The project uses several controlled artifacts. Each artifact has a different purpose:

| Artifact | Source-of-truth responsibility |
| --- | --- |
| Project Tracking workbook | Work ID, owner, planned iteration, current status, target LOC band, evidence reference |
| RDS/SRS | Intended behavior, actors, use cases, business rules, validation, acceptance criteria |
| SDS | Architecture, database design, class responsibility, sequence, security, transaction, API and technical design |
| Source repository | Actual implementation |
| Database migration scripts | Actual reproducible schema and seed changes |
| Issues Report | Defects, tasks, gaps, severity, status history and corrective action |
| Test/evidence folders | Proof that implementation behaves as claimed |
| Final Release Document | Controlled package, installation, user manual, operations, acceptance and known limitations |

When artifacts conflict, the team must not silently choose the most convenient version. The owner must open an issue, identify the discrepancy, and synchronize the affected artifacts. A `Done` function must have consistent names and behavior across Project Tracking, RDS/SRS, SDS, code, tests, and evidence.

---

## 5. Communication and Decision Rules

### 5.1 Required communication

Members must notify the team before:

- Adding, deleting, or renaming an entity or database table.
- Changing an enum or status transition.
- Changing an endpoint path or DTO field.
- Modifying authentication or authorization configuration.
- Changing order total, coupon, payment, or invoice calculations.
- Upgrading a major dependency.
- Replacing a shared frontend component or API client.
- Rewriting another member’s active module.
- Rebasing or force-pushing a branch used by another member.

### 5.2 Decision recording

Important decisions must be recorded in one of the following places:

- GitHub issue discussion.
- Pull Request description.
- SDS design section.
- `docs/DECISIONS.md` or an equivalent Architecture Decision Record.
- Change Log for release-level changes.

A decision made only in a private message is not sufficient because other members and evaluators cannot trace it.

### 5.3 Conflict resolution order

When members disagree:

1. Recheck the official course requirement and controlled project documents.
2. Recheck the current implementation and database constraints.
3. Compare the options against acceptance criteria and demo workflow.
4. Ask the responsible module owner for a recommendation.
5. The leader makes the final project-level decision.
6. Record the result and update affected documents.

---

## 6. GitHub Issue Rules

Every significant task must have an issue before implementation. Minor typo corrections may be grouped into a documentation issue, but feature, bug, security, schema, and test work must remain traceable.

### 6.1 Required issue fields

Each issue should include:

- Work ID or related requirement ID.
- Clear title.
- Problem statement or business objective.
- In-scope and out-of-scope boundaries.
- Business rules and validations.
- Acceptance criteria.
- Owner and reviewers.
- Priority and iteration.
- Dependencies and blockers.
- Required evidence.
- Related RDS/SRS and SDS references.

### 6.2 Recommended labels

```text
type:feature
type:bug
type:security
type:database
type:test
type:docs
module:auth
module:user
module:menu
module:table
module:reservation
module:cart
module:order
module:kitchen
module:coupon
module:payment
module:invoice
module:feedback
module:report
module:ai
priority:critical
priority:high
priority:medium
status:blocked
status:review
```

### 6.3 Issue status rules

- `Not Started`: no verified implementation work has begun.
- `Doing`: implementation or test work is active but incomplete.
- `Review`: code is pushed and a Pull Request is ready for review.
- `Done`: merged implementation, test, documents, and evidence satisfy acceptance criteria.
- `Blocked`: progress cannot continue until a named dependency is resolved.

A mock screen or partial API must not be labeled `Done` if the acceptance criteria require persistence, authorization, real data, or integration.

---

## 7. Branch Strategy

The repository uses the following branches:

- `main`: stable release branch. It should always represent a demonstrable baseline.
- `develop`: integration branch for the current iteration.
- `feature/<issue-id>-<short-description>`: new functionality.
- `bugfix/<issue-id>-<short-description>`: defect correction.
- `security/<issue-id>-<short-description>`: security hardening.
- `refactor/<issue-id>-<short-description>`: restructuring without intended behavior change.
- `test/<issue-id>-<short-description>`: test-only work.
- `docs/<issue-id>-<short-description>`: documentation.
- `chore/<issue-id>-<short-description>`: dependencies, configuration, tooling, or cleanup.
- `hotfix/<issue-id>-<short-description>`: urgent correction against a released `main`.

### 7.1 Branch naming format

```text
<type>/<issue-id>-<short-kebab-case-description>
```

Good examples:

```text
feature/412-reservation-overlap-validation
feature/587-kitchen-item-status
security/603-jwt-authentication-filter
feature/711-payment-processing-api
bugfix/735-order-total-rounding
test/744-coupon-concurrency-tests
docs/752-update-payment-sequence
```

Bad examples:

```text
quyet-code
new-feature
fix
final-final
branch1
payment-and-everything
```

### 7.2 Branch creation

Before creating a branch:

```bash
git checkout develop
git pull origin develop
git checkout -b feature/412-reservation-overlap-validation
```

Do not create a new branch from an outdated local branch. Do not develop directly on `main` or `develop`.

### 7.3 Branch lifetime

Branches should be short-lived and focused. If a task is too large for one reviewable branch, divide it into ordered issues such as migration, backend service, API, frontend integration, and tests. Each part must remain compatible with the agreed contract.

### 7.4 Force push

Force push is forbidden on `main`, `develop`, and shared branches. On a personal feature branch, use `--force-with-lease` only when necessary and only after confirming that no other member has based work on the branch.

---

## 8. Commit Message Rules

Use Conventional Commits.

### 8.1 Format

```text
<type>(<scope>): <short imperative description>
```

Allowed types:

```text
feat
fix
refactor
test
docs
style
chore
build
ci
perf
revert
```

Recommended scopes:

```text
auth
security
user
menu
category
food
table
reservation
cart
order
staff
kitchen
coupon
payment
invoice
feedback
report
ai
api
db
migration
frontend
docs
test
config
```

Good examples:

```text
feat(auth): generate signed access token
feat(reservation): prevent overlapping active bookings
fix(order): recalculate total from persisted food prices
feat(kitchen): add item-level preparation status
feat(payment): persist idempotent payment transaction
test(coupon): cover concurrent final usage limit
docs(sds): add payment transaction sequence
chore(config): externalize sql server credentials
```

Bad examples:

```text
update code
fix bug
done
commit lan 2
quyet task
final code
them chuc nang
```

### 8.2 Commit content

- One commit should represent one understandable change.
- Do not mix formatting of the whole project with a functional change.
- Do not commit generated output, IDE files, secrets, logs, or local database files.
- A commit must not intentionally leave the project uncompilable unless it is clearly marked as a temporary draft on a private branch and is never proposed for merge.
- Use English for commit messages so the history remains consistent.
- Use present-tense imperative verbs: `add`, `fix`, `validate`, `remove`, `update`.

### 8.3 Commit before review

Before pushing, review:

```bash
git status
git diff
git diff --staged
```

Confirm that only intended files are included.

---

## 9. Pull Request Rules

### 9.1 Target branch

Normal work:

```text
feature / bugfix / security / refactor / test / docs -> develop
```

Release work:

```text
develop -> main
```

Hotfix:

```text
hotfix -> main
main -> develop
```

### 9.2 Pull Request title

```text
[TYPE][WORK-ID] Short description
```

Examples:

```text
[FEAT][AUTH-06] Add JWT authentication filter
[FIX][RES-04] Prevent concurrent reservation overlap
[FEAT][PAY-02] Add payment processing API
[TEST][COUPON-05] Add atomic usage-limit tests
[DOCS][RPT-01] Update dashboard KPI contract
```

### 9.3 Pull Request description template

```markdown
## Summary
- What was changed?
- What business result does it provide?

## Related Work
- Work ID:
- Issue:
- RDS/SRS reference:
- SDS reference:

## Implementation
- Main classes/files:
- Database migration:
- API changes:
- Frontend changes:

## Business Rules
- List the validated rules and state transitions.

## Test Evidence
- Unit tests:
- Integration/API tests:
- Manual test:
- Screenshots/Postman/SQL evidence:

## Security and Data Integrity
- Authentication/authorization:
- Ownership:
- Transaction/locking:
- Sensitive-data handling:

## Compatibility and Risk
- Breaking change:
- Dependencies:
- Known limitations:

## Checklist
- [ ] Builds successfully
- [ ] Tests pass
- [ ] No secret or generated file
- [ ] Documentation updated
- [ ] Evidence attached
```

### 9.4 Review requirements

At least one qualified reviewer is required. The following changes require review from the named owner:

- Security/authentication: Backend & Security Lead.
- Database schema or migration: leader plus affected backend owner.
- Reservation concurrency: reservation owner and Backend & Security Lead.
- Order/coupon/payment/invoice transactions: transaction owner and Backend & Security Lead.
- Shared React architecture or API client: Frontend Lead.
- Release baseline: Team Leader.

The author must not approve their own Pull Request as the only approval.

### 9.5 Required checks before merge

- Backend compiles and tests pass.
- Frontend builds without errors.
- Database migrations succeed on a clean database.
- API contract is tested.
- Role and ownership checks are verified.
- Error cases are covered.
- Documents and evidence references are updated.
- Merge conflicts are resolved locally.
- No secret, machine-specific path, or generated directory is included.

---

## 10. Merge and Conflict Rules

### 10.1 Preferred merge method

Use squash merge for small single-purpose branches when a clean history is desired. Use a normal merge when preserving several meaningful commits is important. The team must use one consistent approach within an iteration.

### 10.2 Conflict resolution

The developer who opens the Pull Request resolves conflicts with the latest `develop`:

```bash
git checkout develop
git pull origin develop
git checkout feature/412-reservation-overlap-validation
git merge develop
```

Resolve each conflict by understanding both changes. Do not automatically choose “ours” or “theirs” for entity, migration, API, or configuration files.

After resolution:

```bash
git add .
git commit -m "chore(git): resolve develop integration conflicts"
git push
```

### 10.3 High-risk conflicts

Conflicts in these files require an affected-owner review:

- `pom.xml`
- security configuration
- entity classes
- migration scripts
- shared DTOs
- shared API client
- route configuration
- global exception handling
- status enums
- application configuration

---

## 11. Backend Architecture Rules

The backend follows a layered modular-monolith structure.

```text
controller / restcontroller
        |
        v
dto + validation
        |
        v
service interface / service implementation
        |
        v
repository
        |
        v
entity + SQL Server
```

Additional cross-cutting packages include:

```text
config
security
exception
mapper
enums
integration
util
db/migration
```

### 11.1 Controller rules

Controllers may:

- Map HTTP requests and responses.
- Validate request shape through DTO annotations.
- Extract authenticated identity.
- Call one or more application services.
- Return standardized responses.

Controllers must not:

- Calculate authoritative prices or discounts.
- Implement status machines.
- Directly access repositories for business operations.
- Return password hashes, raw tokens, or unrestricted entities.
- Perform long transaction logic.
- Decide authorization based only on client-provided role data.

### 11.2 Service rules

Services own:

- Business validation.
- Role and ownership checks where appropriate.
- State transition validation.
- Money calculation.
- Transaction boundaries.
- Concurrency control.
- Provider integration through interfaces.
- Mapping domain exceptions to controlled outcomes.

Methods should be cohesive and testable. A service method should represent a business use case, not an unbounded collection of unrelated operations.

### 11.3 Repository rules

Repositories handle persistence and explicit queries. They must not contain UI behavior or external-provider logic. Complex search, locking, and reporting queries should be named clearly and covered by integration tests.

### 11.4 Entity rules

Entities represent persistence, not API contracts. Do not return JPA entities directly to the React client. Relationships must avoid accidental recursive serialization. Financial and historical snapshot fields must remain stable after master data changes.

### 11.5 DTO rules

Use explicit suffixes:

```text
LoginRequest
LoginResponse
ReservationCreateRequest
ReservationResponse
OrderCreateRequest
OrderDetailResponse
PaymentProcessRequest
InvoiceResponse
```

Inbound DTOs contain validation annotations. Outbound DTOs include only fields permitted for the actor.

---

## 12. Java Naming and Code-Style Rules

- Packages: lowercase dot-separated names.
- Classes and interfaces: `PascalCase`.
- Methods and variables: `lowerCamelCase`.
- Constants: `UPPER_SNAKE_CASE`.
- Boolean names should describe truth: `isActive`, `hasPermission`, `canCancel`.
- Use four spaces for indentation; do not mix tabs and spaces.
- Use braces for all `if`, `else`, `for`, `while`, and `try` blocks.
- Prefer one declaration and one statement per line.
- Keep methods focused and avoid deeply nested logic.
- Use comments to explain non-obvious business or concurrency decisions, not to repeat obvious code.
- Public services and complex methods should have useful Javadoc.
- Avoid wildcard imports.
- Remove unused imports and dead commented-out code.
- Use `BigDecimal` for money. Do not use `float` or `double` for persisted prices, discounts, payments, or invoice totals.
- Use controlled time-zone rules. Persist server timestamps consistently and format local display separately.
- Do not catch `Exception` broadly unless the boundary explicitly needs it and safely rethrows/maps the error.

---

## 13. Frontend Architecture Rules

The React frontend should use reusable components, centralized API clients, and role-based layouts.

Recommended structure:

```text
src/
├── api/
├── components/
├── context/
├── hooks/
├── layouts/
├── pages/
├── routes/
├── services/
├── utils/
└── assets/
```

### 13.1 Frontend responsibility

The frontend may provide immediate usability validation and display logic, but it is not the authority for:

- Prices.
- Discounts.
- Coupon eligibility.
- Payment success.
- Role authorization.
- Ownership.
- Reservation overlap.
- Order state validity.

The backend must revalidate every authoritative rule.

### 13.2 API access

Use a centralized HTTP client with:

- Configurable base URL.
- Authorization header handling.
- Standard error mapping.
- Controlled handling of 401 and 403.
- Timeout behavior.
- Request cancellation where appropriate.
- No scattered hardcoded URLs.

### 13.3 Component rules

- Shared tables, forms, dialogs, loading indicators, and error states should be reusable.
- Do not put an entire module into one oversized component.
- Avoid inline styles when a shared stylesheet or component style is appropriate.
- Every async screen must handle loading, success, empty, validation-error, authorization-error, and network-error states.
- Client-side role guards improve navigation but do not replace backend authorization.

---

## 14. REST API Rules

### 14.1 Base path

```text
/api/v1
```

### 14.2 Resource naming

Use lowercase plural nouns:

```text
/api/v1/users
/api/v1/categories
/api/v1/foods
/api/v1/tables
/api/v1/reservations
/api/v1/orders
/api/v1/coupons
/api/v1/payments
/api/v1/invoices
/api/v1/feedback
/api/v1/reports
```

Avoid verb-style CRUD paths:

```text
/api/v1/getOrders
/api/v1/createFood
/api/v1/deleteReservation
```

Workflow actions may use explicit sub-resources:

```text
PATCH /api/v1/reservations/{id}/confirm
PATCH /api/v1/reservations/{id}/cancel
PATCH /api/v1/orders/{id}/status
PATCH /api/v1/kitchen/items/{id}/status
POST  /api/v1/orders/{id}/payments
GET   /api/v1/invoices/{id}
```

### 14.3 HTTP semantics

- `GET`: retrieve without mutation.
- `POST`: create or execute a non-idempotent command.
- `PUT`: replace a complete resource when applicable.
- `PATCH`: partial update or controlled state transition.
- `DELETE`: use carefully; prefer deactivation when history exists.

### 14.4 Response and error format

Use one standard response/error model. Errors should include a stable code, safe message, timestamp, request path, correlation ID when available, and field errors for validation. Do not expose stack traces, SQL errors, provider secrets, token contents, or internal class names.

---

## 15. Database Rules

### 15.1 Naming

Use snake_case and clear plural table names:

```text
users
roles
categories
foods
restaurant_tables
reservations
reservation_items
restaurant_orders
order_items
payments
invoices
feedback
coupon_usages
```

Columns:

```text
created_at
updated_at
reservation_date
unit_price
final_total
payment_status
transaction_reference
```

Constraints and indexes:

```text
pk_users
fk_reservations_user
uq_users_email
uq_invoices_order_id
idx_orders_status_created_at
idx_reservations_table_date_time
```

### 15.2 Schema changes

All shared schema changes must use versioned migrations. Do not rely on manual SSMS changes or `ddl-auto=update` for the controlled final baseline. Never edit an already-applied shared migration; create a new migration or an approved forward fix.

### 15.3 Integrity

Use database constraints in addition to service validation:

- Unique email and business codes.
- Positive quantity and price.
- Required foreign keys.
- One feedback per eligible order.
- One immutable invoice per successfully paid order.
- Unique payment transaction or idempotency key.
- Reservation and assignment indexes.
- Optimistic version or explicit locking where concurrency matters.

### 15.4 Historical records

Order items, reservation pre-orders, payments, and invoices must preserve snapshots. A later food-name or price change must not rewrite historical financial records.

### 15.5 Deletion

Use soft deactivation when a record is referenced by history. Hard delete is allowed only for truly transient owned children and only when referential behavior is intentional.

---

## 16. Security Rules

Security is a backend responsibility and must be deny-by-default.

### 16.1 Authentication

- Passwords must be BCrypt encoded.
- Plaintext passwords must never be stored, logged, or returned.
- JWT signing keys and token lifetimes must be externalized.
- Refresh tokens, when used, must be stored as hashes and support expiration and revocation.
- Invalid, expired, malformed, or tampered tokens must return controlled 401 responses.

### 16.2 Authorization

- Admin-only operations must reject non-admin users.
- Customer-owned reservations, orders, invoices, payments, and feedback require ownership checks.
- Kitchen operations require kitchen or explicitly authorized admin roles.
- Staff actions must respect assignment and workflow rules where applicable.
- Frontend route guards are not security evidence.

### 16.3 Input and output safety

- Validate all inbound DTO fields.
- Normalize email and controlled identifiers.
- Restrict uploaded image type/size if uploads are supported.
- Do not expose internal entity graphs.
- Do not log authorization headers, raw tokens, passwords, payment secrets, or personal data unnecessarily.
- Apply output encoding and safe rendering to user-entered notes and feedback.

### 16.4 Secrets

Secrets belong in environment variables or ignored local configuration. The repository may contain `.env.example`, but never real values.

---

## 17. State-Machine and Business-Rule Change Control

The following workflows must use explicit allowed transitions:

- Reservation.
- Restaurant table operational state.
- Order.
- Kitchen item.
- Coupon lifecycle.
- Payment.
- Invoice.
- Feedback moderation.

A member must not add a new status or transition only to make a screen easier. Every change requires:

1. Updated enum or controlled value.
2. Updated service validation.
3. Updated database compatibility.
4. Updated frontend action availability.
5. Positive and negative tests.
6. Updated RDS/SRS and SDS.
7. Updated demonstration scenario when relevant.

Invalid jumps, reversals, and terminal-state mutations must fail predictably.

---

## 18. Transaction and Concurrency Rules

Use transactions for operations that must succeed or fail as one unit, including:

- Reservation conflict check and reservation creation.
- Order header and order-item creation.
- Coupon validation, usage increment, and order discount persistence.
- Payment persistence and order completion.
- Invoice creation after successful payment.
- Feedback eligibility check and feedback creation.

Concurrency-sensitive operations require an explicit strategy such as optimistic locking, pessimistic locking, unique constraints, idempotency keys, or serializable/locking queries. “It worked in one manual test” is not sufficient.

Rollback tests are required for financial and inventory-like integrity. A failed payment must not complete the order. A failed order must not consume a coupon. A duplicate request must not create duplicate payment or invoice records.

---

## 19. Testing Rules

### 19.1 Minimum test layers

- Unit tests for business rules and state transitions.
- Repository/integration tests for constraints and queries.
- API tests for HTTP contract, validation, role, and ownership.
- Frontend component or integration tests where feasible.
- Manual end-to-end tests for the demonstration workflow.
- Security negative tests.
- Concurrency/idempotency tests for reservation, coupon, and payment.

### 19.2 Test naming

```text
methodName_condition_expectedResult
```

Examples:

```text
createReservation_whenSlotOverlaps_shouldReject
processPayment_whenRequestRepeated_shouldReturnOriginalResult
viewOrder_whenUserIsNotOwner_shouldReturnForbidden
applyCoupon_whenUsageLimitReached_shouldReject
```

### 19.3 Evidence

Evidence may include:

- Automated test report.
- Postman request and response.
- Browser screenshot.
- SQL query result.
- Git commit or tag.
- API log with sensitive data removed.
- Short screen recording.

Every evidence item should reference the Work ID or Test ID.

---

## 20. Documentation Rules

- Use the same project name, actor names, statuses, Work IDs, endpoint names, and table names across all documents.
- Do not copy unrelated clinic, library, or e-commerce examples into the RMS baseline.
- Diagrams must reflect the actual Restaurant Management System.
- A use case, class, sequence, ERD, or state diagram must be updated when the underlying contract changes.
- Screenshots must come from the current project version.
- Installation instructions must work on a clean machine and must not depend on an undocumented personal path.
- Document known limitations honestly.
- Update the change log for material modifications.
- Keep editable source files and final rendered files when required.

---

## 21. Files That Must Not Be Committed

```text
target/
node_modules/
dist/
build/
.idea/
.vscode/
*.iml
*.log
.env
.env.*
application-local.properties
application-secret.properties
*.bak
*.tmp
coverage/
.DS_Store
Thumbs.db
```

Exceptions may be made for an intentionally versioned example configuration such as:

```text
.env.example
application-example.properties
```

Do not commit SQL Server database files, personal IDE metadata, Maven/npm caches, real credentials, private keys, or generated evidence containing sensitive information.

---

## 22. Performance and Reliability Rules

- Use pagination for large administrative lists.
- Avoid N+1 query patterns.
- Add indexes for frequent search and relationship columns.
- Bound report date ranges.
- Use timeouts for email, AI, and payment adapters.
- Provide safe fallback for external-service failure.
- Do not block the React UI indefinitely; display loading and retry states.
- Do not perform expensive report aggregation in controllers.
- Log failures with correlation context, but redact secrets.
- Preserve form state when a recoverable network error occurs.

---

## 23. Bug and Incident Handling

A defect issue must contain:

- Reproduction steps.
- Expected behavior.
- Actual behavior.
- Environment and commit/tag.
- Severity and affected role.
- Screenshots/logs with secrets removed.
- Suspected module.
- Regression test plan.

Critical defects include authentication bypass, unauthorized data access, incorrect order total, duplicate successful payment, mutable invoice, coupon overuse, reservation overlap, data loss, or inability to run the final demo. Critical defects block release until fixed or explicitly accepted as a disclosed limitation by the leader.

A bug fix must add or update a test whenever practical.

---

## 24. Release and Tagging Rules

A release candidate may be tagged only after:

- Clean checkout succeeds.
- SQL Server schema migrates on a clean database.
- Controlled seed data loads.
- Backend tests pass.
- Frontend build succeeds.
- All role accounts can log in.
- Critical workflows pass smoke testing.
- Documents open without corruption.
- Statuses match code and evidence.
- No real secret is present.
- Known limitations are recorded.
- Demo data and demo script are prepared.

Suggested tag format:

```text
iter1-v1.0.0
iter2-v1.1.0
iter3-v1.2.0
final-v2.0.0
```

The release tag must not be moved or overwritten. Corrections require a new tag.

---

## 25. Definition of Ready

A task is ready to start when:

- Work ID and owner are identified.
- Scope is clear.
- Business rules are written.
- Acceptance criteria are testable.
- Dependencies are known.
- Database/API impact is reviewed.
- Required design is available.
- Required sample data exists or is planned.
- Branch and issue naming are prepared.

A member should not begin a high-risk feature while key rules are still ambiguous.

---

## 26. Definition of Done

A task is complete only when all applicable conditions are satisfied:

- [ ] Implementation exists in the correct layer.
- [ ] Code compiles and the frontend builds.
- [ ] Database migration is reproducible.
- [ ] Business rules are enforced on the backend.
- [ ] Role and ownership checks are correct.
- [ ] Valid state transitions succeed.
- [ ] Invalid transitions fail safely.
- [ ] Transactions roll back on failure.
- [ ] DTO and API contracts are stable.
- [ ] Automated tests pass.
- [ ] Manual workflow is verified.
- [ ] Error, loading, and empty states are handled.
- [ ] No secret or generated file is committed.
- [ ] RDS/SRS and SDS references are updated.
- [ ] Project Tracking and issue status are updated.
- [ ] Evidence is attached and named by Work ID/Test ID.
- [ ] Pull Request is reviewed and merged.
- [ ] The feature can be explained and demonstrated by its owner.

---

## 27. Daily Working Checklist

Before coding:

```bash
git checkout develop
git pull origin develop
git checkout <your-feature-branch>
git merge develop
```

During work:

- Keep the issue updated.
- Commit small, meaningful changes.
- Run relevant tests frequently.
- Communicate contract changes immediately.
- Do not wait until the end of the iteration to integrate.

Before pushing:

```bash
git status
git diff
git add <specific-files>
git commit -m "feat(module): clear description"
git push -u origin <branch-name>
```

Before requesting review:

- Rebase/merge the latest `develop`.
- Run full applicable checks.
- Complete the Pull Request template.
- Attach evidence.
- Identify risks and known limitations.

---

## 28. Final Team Agreement

By contributing to the project, each member agrees to maintain a reviewable Git history, respect module ownership, protect shared contracts, produce verifiable evidence, and keep project documents synchronized with the actual system. The team’s goal is not merely to produce many files or many lines of code. The goal is to deliver an integrated Restaurant Management System whose requirements, design, implementation, database, tests, security, and demonstration tell the same truthful story.
