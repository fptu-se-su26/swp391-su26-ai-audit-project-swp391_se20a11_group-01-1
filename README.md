# Restaurant Management System

> SWP391 — Software Development Project  
> Class: **SE20A11** · Semester: **SU26** · Group: **01**

## 1. Project Overview

The **Restaurant Management System (RMS)** is a web-based application that supports the main operational workflows of a restaurant, including customer account management, menu browsing, table management, reservations, ordering, kitchen processing, payment, feedback, and administrative reporting.

The project is developed by a five-member team following an industry-style workflow:

```text
Requirement → Issue → Branch → Commit → Pull Request → Review → Merge → Test → Release
```

This repository is also used for the course AI audit. All important AI-assisted work must be recorded, reviewed, verified, and explainable by the team.

## 2. Project Information

| Item | Description |
|---|---|
| Course | SWP391 — Application Development Project |
| Class | SE20A11 |
| Semester | Summer 2026 |
| Group | 01 |
| Topic | Restaurant Management System |
| Repository | `fptu-se-su26/swp391-su26-ai-audit-project-swp391_se20a11_group-01-1` |
| Backend | Java 21, Spring Boot 3.5, Maven |
| Frontend | React, JavaScript, HTML, CSS |
| Database | Microsoft SQL Server |
| Architecture | Layered REST application |
| Main API prefix | `/api` |

## 3. Team Members

| No. | Student ID | Full Name | GitHub Username | Project Role | Main Responsibility |
|---:|---|---|---|---|---|
| 1 | DE191019 | Phan Nguyễn | `Nguyendeptraibodoi` | Team Leader & Integration Lead | Planning, backlog coordination, code review, integration, release control, user and menu modules |
| 2 | DE190425 | Phạm Văn Quyết | `quyetpham2k5` | Backend & Security Lead | Authentication, authorization, backend security, table/reservation services, payment integration |
| 3 | DE190986 | Nguyễn Tiến Lộc | `tienloc1234` | Order & Operations Developer | Cart, order lifecycle, staff workflow, kitchen workflow, operational APIs |
| 4 | DE190096 | Nguyễn Đức Thương | `thuong1703n` | Frontend Lead | UI/UX, React pages, responsive layout, client-side state, API integration |
| 5 | DE180571 | Trần Thanh Gia Huy | `huyttde12` | QA, Documentation & Reporting Lead | Test planning, issue tracking, evidence, reports, project documents, AI audit records |

Responsibilities indicate the primary owner. Team members may review, test, or support modules owned by other members.

## 4. System Actors

| Actor | Main Capabilities |
|---|---|
| Guest | View public information, browse the menu, register, log in, reset password |
| Customer | Manage profile, create reservations, place orders, track order history, submit feedback |
| Staff | Manage tables, confirm reservations, create or update orders, support customer service |
| Kitchen Staff | View kitchen queue, update preparation status, mark dishes as ready |
| Administrator | Manage accounts, roles, menu, categories, tables, coupons, reports, and system data |

## 5. Functional Scope and Current Status

Status must be updated from the latest tested release tag, not from plans alone.

| Module | Main Scope | Current Audit Status |
|---|---|---|
| Authentication | Register, login, change password, forgot/reset password, email OTP | Baseline implemented; JWT and complete endpoint authorization still require hardening |
| User Management | Profile and administrative account management | Partially implemented |
| Category & Menu | Category CRUD, food CRUD, menu browsing, availability control | Baseline implemented |
| Table Management | Table CRUD, active status, occupancy status, current-order information | Baseline implemented |
| Reservation | Create reservation, pre-order items, status update, check-in, cancellation | Baseline implemented |
| Cart & Order | Build cart, create order, calculate totals, update order status, release table | In progress |
| Staff Operations | Counter ordering, table support, reservation and order handling | In progress |
| Kitchen Operations | Kitchen queue and food preparation lifecycle | In progress |
| Coupon | Coupon CRUD, validation, usage control, discount calculation | Target functionality / not yet complete |
| Payment & Invoice | Cash/QR payment record, payment transaction, immutable invoice | Target functionality / not yet complete |
| Feedback | Customer feedback persistence and administrator moderation | Target functionality / not yet complete |
| Dashboard & Reports | Revenue, order, reservation, and operational statistics from real data | Target functionality / not yet complete |
| AI Support | Optional assistant or analytics functions | Experimental scope |

### Important Code Audit Note

The repository must not be described as production-ready until all of the following are verified:

- JWT access and refresh token handling are implemented.
- Protected endpoints enforce role-based authorization.
- Coupon, payment, invoice, feedback, and report data are persisted in SQL Server.
- Frontend screens use real backend APIs instead of static or local-only data.
- Required test cases pass on the release tag.
- SRS/RDS, SDS, Project Tracking, Issues Report, source code, and database scripts are consistent.

## 6. Technology Stack

### Backend

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Data JPA / Hibernate
- Spring Security
- Jakarta Validation
- Lombok
- Maven / Maven Wrapper
- Java Mail
- Microsoft SQL Server JDBC Driver

### Frontend

- React
- JavaScript
- HTML5 and CSS3
- React Context for shared client state
- REST API integration
- npm

### Development and Management Tools

- Git and GitHub
- IntelliJ IDEA or Visual Studio Code
- SQL Server Management Studio
- Postman
- Microsoft Word and Excel / Google Docs and Sheets
- PlantUML, Mermaid, or diagrams.net for diagrams

## 7. Logical Project Structure

```text
.
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/rms/restaurant_management_system/
│   │   │   │   ├── config/          # Security, CORS, and application configuration
│   │   │   │   ├── controller/      # REST API controllers
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/     # API request models
│   │   │   │   │   └── response/    # API response models
│   │   │   │   ├── entity/          # JPA entities
│   │   │   │   ├── enums/           # Domain status values
│   │   │   │   ├── exception/       # Error handling
│   │   │   │   ├── repository/      # Spring Data repositories
│   │   │   │   └── service/
│   │   │   │       ├── interfaces/  # Service contracts
│   │   │   │       └── impl/        # Business logic
│   │   │   └── resources/           # Configuration and database resources
│   │   └── test/                     # Backend tests
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/               # Reusable UI components
│   │   ├── context/                  # Authentication and shared state
│   │   ├── pages/                    # Guest, customer, staff, kitchen, and admin pages
│   │   ├── services/                 # API clients
│   │   └── styles/                   # Shared styling
│   └── package.json
├── docs/
│   ├── AI_AUDIT_LOG.md
│   ├── PROMPTS.md
│   ├── REFLECTION.md
│   ├── CHANGELOG.md
│   └── project-deliverables/
├── .github/
│   ├── ISSUE_TEMPLATE/
│   └── workflows/
├── README.md
└── .gitignore
```

The physical structure may change during development. This section must be updated whenever packages or modules are added, renamed, or removed.

## 8. Prerequisites

Install the following tools before running the project:

| Tool | Recommended Version |
|---|---|
| JDK | 21 |
| Maven | 3.9+ or the included Maven Wrapper |
| Node.js | 20 LTS |
| npm | 10+ |
| SQL Server | 2019 or later |
| Git | Latest stable version |

Verify the main tools:

```bash
java -version
node -v
npm -v
git --version
```

## 9. Database Setup

### 9.1 Create the database

Open SQL Server Management Studio and run:

```sql
CREATE DATABASE RestaurantManagementDB;
GO
```

Use the database script or migrations supplied with the selected release tag to create tables and initial data.

### 9.2 Configure backend environment

Do not commit real credentials to Git. The committed configuration contains no database, JWT, mail or PayOS secret. Start from the backend environment template:

```powershell
cd srccode/backend/restaurant_management_system
Copy-Item src/main/resources/application-local.example.properties src/main/resources/application-local.properties
```

`application-local.properties` and `.env` are ignored by Git. Spring Boot does not automatically load `.env`; export the variables in the shell or configure them in the IDE. The complete variable list is in `.env.example`.

Minimum local environment:

```text
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=RestaurantManagementDB;encrypt=true;trustServerCertificate=true
DB_USERNAME=your_sql_server_username
DB_PASSWORD=your_sql_server_password
MAIL_USERNAME=your_email_address
MAIL_PASSWORD=your_email_app_password
JWT_SECRET=use_a_random_secret_of_at_least_32_characters
PAYOS_CLIENT_ID=your_payos_client_id
PAYOS_API_KEY=your_payos_api_key
PAYOS_CHECKSUM_KEY=your_payos_checksum_key
```

PowerShell example for the current terminal session:

```powershell
$env:DB_USERNAME = "your_sql_server_username"
$env:DB_PASSWORD = "your_sql_server_password"
$env:JWT_SECRET = "replace-with-at-least-32-random-bytes"
$env:MAIL_USERNAME = "your_email_address"
$env:MAIL_PASSWORD = "your_email_app_password"
$env:PAYOS_CLIENT_ID = "your_payos_client_id"
$env:PAYOS_API_KEY = "your_payos_api_key"
$env:PAYOS_CHECKSUM_KEY = "your_payos_checksum_key"
```

The `local` profile may use `JPA_DDL_AUTO=update` for developer convenience. The `prod` profile always uses `validate`; database structure must be applied using reviewed migrations before deployment. Production has no fallback for credentials or cryptographic keys and will fail fast when a required value is missing.

## 10. How to Run

### 10.1 Clone the repository

```bash
git clone https://github.com/fptu-se-su26/swp391-su26-ai-audit-project-swp391_se20a11_group-01-1.git
cd swp391-su26-ai-audit-project-swp391_se20a11_group-01-1
```

### 10.2 Run the backend

Open a terminal in the backend directory after configuring the local environment:

```bash
cd srccode/backend/restaurant_management_system
```

Windows:

```powershell
.\mvnw.cmd clean spring-boot:run
```

macOS/Linux:

```bash
chmod +x mvnw
./mvnw clean spring-boot:run
```

Run automated tests with the isolated `test` profile:

```powershell
.\mvnw.cmd clean test
```

The test profile uses an in-memory database and test-only credentials; it does not connect to a developer SQL Server.

### 10.3 Backend database tests

The default backend test command runs unit tests, MVC security tests and H2 repository integration tests. SQL Server Testcontainers tests are tagged `integration` and excluded by default, so Docker is not required for normal development.

```powershell
cd srccode/backend/restaurant_management_system
mvn --batch-mode --no-transfer-progress clean test
```

To verify SQL Server migrations and database locking, start Docker and opt in explicitly:

```powershell
$env:RUN_SQLSERVER_TESTS = "true"
mvn --batch-mode --no-transfer-progress `
  "-Dtest.excluded-groups=none" `
  "-Dgroups=integration" `
  "-Dtest=SqlServerMigrationIntegrationTest" test
```

The container downloads the SQL Server 2022 image, creates a disposable database, executes the hardening migrations twice to verify rerunnability, checks rollback behavior, and verifies row-lock serialization. No project database credentials are used. GitHub Actions runs both the fast H2 suite and the SQL Server container suite for backend-related pull requests.

The backend is expected to run at:

```text
http://localhost:8080
```

### 10.3 Run the frontend

Open another terminal:

```bash
cd frontend
npm install
npm start
```

The frontend is expected to run at:

```text
http://localhost:3000
```

### 10.4 Verify the connection

1. Confirm that SQL Server is running.
2. Confirm that the backend starts without datasource errors.
3. Open the frontend URL.
4. Test a public menu request.
5. Test registration and login.
6. Test one end-to-end business flow such as reservation or ordering.

## 11. Main API Groups

The following API groups represent the current or target application structure:

| API Group | Purpose |
|---|---|
| `/api/auth/**` | Registration, login, password change, forgot/reset password |
| `/api/users/**` | Profile and administrative account operations |
| `/api/categories/**` | Category management |
| `/api/foods/**` | Food and menu management |
| `/api/tables/**` | Restaurant table management |
| `/api/reservations/**` | Reservation lifecycle |
| `/api/orders/**` | Order lifecycle |
| `/api/coupons/**` | Coupon administration and validation |
| `/api/payments/**` | Payment processing |
| `/api/invoices/**` | Invoice retrieval |
| `/api/feedback/**` | Customer feedback and moderation |
| `/api/reports/**` | Dashboard and reporting data |

An endpoint must not be documented as complete until its controller, service, persistence, authorization, validation, test, and evidence are available in the audited release.

## 12. Testing

### Backend tests

```bash
cd backend
./mvnw test
```

Windows:

```powershell
cd backend
.\mvnw.cmd test
```

### Frontend tests

```bash
cd frontend
npm test
```

### API tests

Postman test collections should cover:

- Valid and invalid authentication
- Authorization by role
- Duplicate and invalid input
- Category and food CRUD
- Table state transitions
- Reservation creation and status transitions
- Order creation and status transitions
- Coupon validation
- Payment and invoice generation
- Feedback persistence
- Report accuracy

### Minimum evidence for a completed function

- Requirement or issue ID
- Source commit or pull request
- Backend/API test result
- Frontend screenshot when applicable
- Database evidence when data is changed
- Updated SRS/RDS and SDS reference
- Reviewer confirmation

## 13. Git Workflow

### 13.1 Required workflow

```text
Issue → Branch → Commit → Pull Request → Review → Merge
```

Avoid direct pushes to `main`.

### 13.2 Branch naming

```text
feature/<student-id>-<task-name>
bugfix/<student-id>-<error-name>
docs/<student-id>-<document-name>
test/<student-id>-<test-scope>
refactor/<student-id>-<scope>
```

Examples:

```text
feature/de190425-jwt-authentication
feature/de190986-kitchen-order-queue
bugfix/de190096-admin-account-page
docs/de180571-update-issues-report
test/de191019-reservation-api
```

### 13.3 Commit message format

```text
[StudentID] type: short description
```

Examples:

```text
[DE190425] feat: add reservation status validation
[DE190986] fix: release table after order cancellation
[DE190096] refactor: connect admin account page to API
[DE180571] test: add payment API test cases
[DE191019] docs: update final release evidence
```

Supported types:

```text
feat, fix, docs, test, refactor, style, chore, build, ci
```

### 13.4 Pull request checklist

Before requesting review:

- The issue is linked.
- The branch is up to date with the integration branch.
- Code follows Java and frontend conventions.
- No password, token, key, or secret is committed.
- New logic has tests.
- Existing tests pass.
- Database changes include a script or migration.
- SRS/RDS, SDS, and Project Tracking are updated when required.
- AI-assisted work is recorded.
- Reviewer feedback is resolved.

## 14. Definition of Done

A work item can be marked **Done** only when:

1. The requirement and acceptance criteria are clear.
2. The implementation exists in the audited branch or release tag.
3. Input validation and business rules are implemented.
4. Authentication and authorization are enforced where required.
5. Database changes are versioned.
6. Backend and frontend are integrated.
7. Relevant tests pass.
8. No critical defect remains open.
9. Documentation and traceability are updated.
10. Evidence is stored and reviewable.

A UI prototype, static page, local-storage-only implementation, or untested API is not sufficient for final `Done` status.

## 15. Project Documents

The final release package should contain:

| Deliverable | Purpose |
|---|---|
| Project Tracking | Full function list, owner, iteration, status, and traceability |
| RDS/SRS | System requirements, actors, use cases, business rules, and acceptance criteria |
| SDS | Architecture, database design, class design, sequence diagrams, and queries |
| Issues Report | Requirements, tasks, defects, risks, status history, and evidence |
| Database Script | Final schema, constraints, indexes, and required initial data |
| Final Release Document | Package description, installation guide, and user manual |
| Test Evidence | Unit, integration, API, UI, and acceptance results |
| Demo Video | Main workflows and final product demonstration |
| Presentation | Final project overview, design, results, and lessons learned |

## 16. AI Audit Requirements

Important AI use must be recorded in:

```text
docs/AI_AUDIT_LOG.md
docs/PROMPTS.md
docs/CHANGELOG.md
docs/REFLECTION.md
```

Each meaningful AI record should identify:

- Date and responsible member
- Tool and model used
- Purpose and original prompt
- AI output used or rejected
- Human verification performed
- Files or code affected
- Risks, corrections, and lessons learned
- Related issue, commit, or pull request

AI output must not be merged without human review. Every team member must be able to explain and defend the work submitted under their name.

## 17. Security Rules

- Never commit SQL Server passwords, Gmail App Passwords, JWT secrets, API keys, or personal access tokens.
- Store secrets in environment variables or local configuration excluded by `.gitignore`.
- Rotate a credential immediately if it is exposed.
- Hash passwords with BCrypt or an equivalent approved password encoder.
- Do not trust role information sent by the frontend.
- Validate authorization in the backend.
- Use parameterized access through JPA/repositories.
- Validate all request DTOs.
- Return safe error messages without stack traces or sensitive data.
- Restrict CORS to approved frontend origins for the release environment.
- Protect state-changing endpoints.
- Record security defects in the Issues Report.

## 18. Known Final-Evaluation Gaps

The following areas require explicit verification before the final release:

- JWT access token and refresh token lifecycle
- Role-based endpoint protection
- Administrative account CRUD
- Coupon persistence and server-side discount calculation
- Payment transaction persistence
- Immutable invoice generation
- Feedback persistence and moderation
- Real database-driven dashboard statistics
- Complete automated and manual test evidence
- Consistency among source code, database, Project Tracking, RDS/SRS, SDS, Issues Report, and Final Release Document

These items must remain `Doing` or `Not Started` until code and evidence satisfy the Definition of Done.

## 19. Troubleshooting

### Backend cannot connect to SQL Server

Check:

- SQL Server service is running.
- TCP/IP is enabled.
- Port `1433` is available.
- Database name is correct.
- SQL Server Authentication is enabled.
- Username and password are valid.
- JDBC URL contains the correct host or instance.

### Frontend cannot call backend

Check:

- Backend is running on port `8080`.
- Frontend API base URL is correct.
- CORS configuration includes the frontend origin.
- Browser developer tools show the actual HTTP error.
- Protected requests include the required authentication token after JWT is implemented.

### Email OTP is not sent

Check:

- SMTP username is correct.
- A Gmail App Password is used instead of the normal account password.
- SMTP authentication and STARTTLS are enabled.
- The sender account allows the configured application.
- No credential is stored in the repository.

### A feature appears in the report but not in the product

Treat this as a traceability defect:

1. Create or reopen the related issue.
2. Change the function status to `Doing` or `Not Started`.
3. Update RDS/SRS, SDS, Project Tracking, and Issues Report.
4. Implement and test the missing scope.
5. Attach evidence before restoring `Done`.

## 20. License and Academic Use

This project is developed for the SWP391 course at FPT University. It is intended for academic evaluation and learning purposes.

Team members are responsible for ensuring that all third-party libraries, assets, and copied materials are used in accordance with their licenses and are properly attributed.
