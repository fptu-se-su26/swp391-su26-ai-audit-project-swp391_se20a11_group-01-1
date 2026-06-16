# Documentation Standards

## 1. Danh sách tài liệu SWP cần nộp
* Template1_Project Tracking.xlsx
* Template2_RDS/SRS Document.docx
* Template3_SDS Document.docx
* Template4_Issues Report.xlsx
* Template5_Final Release Document.docx
* Template0 SWP391 AI Usage Report.xlsx

## 2. Mục đích từng template
* **Template1_Project Tracking**: Theo dõi tiến trình, effort dự án của các thành viên.
* **Template2_RDS/SRS**: Xác định yêu cầu phần mềm và đặc tả cơ sở dữ liệu.
* **Template3_SDS**: Đặc tả thiết kế phần mềm, kiến trúc, component.
* **Template4_Issues Report**: Ghi nhận và báo cáo lỗi (Bug/Defect) trong quá trình phát triển/kiểm thử.
* **Template5_Final Release Document**: Báo cáo tổng kết dự án và release note.
* **Template0_AI Usage Report**: Báo cáo trung thực việc sử dụng AI sinh mã, thiết kế.

## 3. Quy tắc đặt tên tài liệu
* Áp dụng theo chuẩn của môn SWP, đảm bảo có mã nhóm/mã dự án. Ví dụ: `[GroupCode]_TemplateX_Name`.

## 4. Traceability
Liên kết chặt chẽ giữa các thành phần dự án:
* Product Backlog -> SRS -> SDS -> Code -> Test Case -> Issue Report.

## 5. Quy tắc ghi AI Usage Report
* Nếu dùng AI (Copilot, Agent, ChatGPT), cần ghi chú minh bạch các task đã dùng, prompt mẫu, và mức độ chỉnh sửa sau khi sinh code.
