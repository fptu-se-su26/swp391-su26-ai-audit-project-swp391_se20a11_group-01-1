# Repository Audit

## 1. Executive Summary
Dự án hiện tại đang ở trạng thái chuẩn bị ban đầu (Phase 0). Việc xóa source code cũ đã được chủ dự án xác nhận là có chủ đích và không khôi phục. Dự án mới sẽ được xây dựng lại từ đầu dựa trên working tree hiện tại, sử dụng các tài liệu thiết kế (ERD, UCD, UI, SQL) làm đầu vào tham khảo. Do đó, 127 file deleted không còn bị coi là blocker.

## 2. Git Status
* Branch: `QuyetPV_DE190425`
* Remote: `origin https://github.com/fptu-se-su26/swp391-su26-ai-audit-project-swp391_se20a11_group-01-1.git`
* Working tree: 127 tracked files were intentionally deleted by the project owner before the new implementation phase.
* Thay đổi chưa commit: Bao gồm các file bị xóa và file untracked.
* Commit gần nhất: `4fd05b97 Update project with latest changes`

## 3. Repository Structure
- `.github/`: Cấu hình pull request/issue template và workflows.
- `diagram/`: Chứa các bản thiết kế UI (.drawio), ERD (.drawio), UCD (.pdf, .docx) và các script SQL.
- `docs/`: Chứa log (`AI_AUDIT_LOG.md`, `CHANGELOG.md`, `PROMPTS.md`, `REFLECTION.md`) và thư mục thành viên.
- `src/`: Thư mục rỗng (source cũ đã xóa).
- `Ke_hoach_phan_chia_task_Restaurant_Management_System.md`: Kế hoạch phân chia task.

## 4. Technology Stack
* Backend: Java 21, Spring Boot 3.5, Maven (dự kiến).
* Frontend: React, TS, Vite (dự kiến).
* Database: MySQL (đã có schema.sql định nghĩa bảng).

## 5. Backend Status
* Trạng thái: NOT STARTED.
* Không còn source code hiện hữu. Toàn bộ source sẽ được làm lại từ đầu theo Backlog.

## 6. Frontend Status
* Trạng thái: NOT STARTED.
* Không có source code hiện hữu. Toàn bộ source sẽ được làm lại từ đầu theo Backlog.

## 7. Database Status
* Trạng thái: PARTIAL.
* Schema: Có `schema.sql` và `schema (1).sql`.
* Migration: Chưa chạy, cần tạo script migration.
* Seed: Có `seed.sql` (chứa dữ liệu mẫu).
* Entity mapping: Chưa có.
* Constraint: Đã định nghĩa trong `schema.sql`.

## 8. Build and Test Results
Không có source code để thực thi lệnh build hoặc test. Không có lỗi build/test.

## 9. Implemented Features
Chưa có chức năng chạy được.

## 10. Partial Features
Tài liệu thiết kế và database schema (xem mục 15).

## 11. Missing Features
Toàn bộ các chức năng backend, frontend và test.

## 12. TODO, FIXME and Incomplete Code
Không có source code để tìm.

## 13. Security and Data Risks
* Low: Làm việc với working tree có các file deleted có thể gây nhầm lẫn khi commit, tuy nhiên đã được chủ dự án xác nhận sẽ bỏ qua 127 file deleted này trong các commit của dự án mới.

## 14. Architecture Conflicts
Không có. Các tài liệu thiết kế ERD/UI hiện có được coi là tài liệu chuẩn cho dự án.

## 15. Backlog Status
| Task/Module | Trạng thái | Bằng chứng | Phần còn thiếu |
| ----------- | ---------- | ---------- | -------------- |
| P0-01 | DONE | Báo cáo này | Không |
| Database schema | PARTIAL | `diagram/schema.sql`, `diagram/seed.sql` | Migration script chạy được (DB-02) |
| UI Design & Sitemap | PARTIAL | Các file `.drawio` trong thư mục `diagram/` | Frontend code thực tế |
| ERD & UCD | PARTIAL | Các file ERD và Use Case trong thư mục `diagram/` | Entity class backend |
| Project Plan | PARTIAL | `Ke_hoach_phan_chia_task...md` | Cần cập nhật tiến độ |
| Backend source | NOT STARTED | `src` đang trống | Toàn bộ |
| Frontend source | NOT STARTED | `src` đang trống | Toàn bộ |
| Testing | NOT STARTED | Chưa có | Toàn bộ |
| Deployment | NOT STARTED | Chưa có | Toàn bộ |

### Danh sách các tài liệu dùng làm đầu vào cho các task tiếp theo:
1. `Ke_hoach_phan_chia_task_Restaurant_Management_System.md` (Markdown): Kế hoạch task dự án. Dùng làm reference tổng quan.
2. `diagram/Restaurant_System_ERD_No_Overlap.drawio` (Draw.io): Sơ đồ ERD. Dùng làm đầu vào cho DB-01, DB-02, BE-01.
3. `diagram/schema.sql` & `diagram/seed.sql` (SQL script): Database schema và seed data. Dùng cho DB-02, DB-03.
4. `diagram/P5-T01_Restaurant_System_Sitemap.drawio` (Draw.io): Sitemap. Dùng cho Frontend Phase.
5. `diagram/P5-T02_Restaurant_System_Wireframe.drawio` (Draw.io): Wireframe cơ sở.
6. `diagram/P5-T03_Restaurant_System_UI_Customer.drawio` (Draw.io): UI Customer. Dùng cho Phase 12.
7. `diagram/P5-T04_Restaurant_System_UI_Staff.drawio` (Draw.io): UI Staff. Dùng cho Phase 13.
8. `diagram/P5-T05_Restaurant_System_UI_Kitchen_FIXED.drawio` (Draw.io): UI Kitchen. Dùng cho Phase 14.
9. `diagram/P5-T06_Restaurant_System_UI_Admin.drawio` (Draw.io): UI Admin. Dùng cho Phase 15.
10. `diagram/restaurant_ucd_revised_no_manage.drawio.pdf` (PDF) và `diagram/Use_Case_Specification_Restaurant_System.docx` (Word): Sơ đồ UCD và đặc tả. Dùng làm tài liệu tham khảo nghiệp vụ khi code API.

## 16. Recommended Execution Order
1. P0-02 — Chốt kiến trúc tổng thể.
2. P1-01 — Khởi tạo backend.
3. P1-02 — Khởi tạo frontend.
4. DB-01, DB-02, DB-03 — Cấu hình database và migration.

## 17. Recommended Next Task
P0-02 — Chốt kiến trúc tổng thể.
