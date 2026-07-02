# KẾ HOẠCH PHÂN CHIA TASK DỰ ÁN

## Restaurant Management System

*Tài liệu dùng làm căn cứ phân công công việc, theo dõi tiến độ, kiểm thử và bàn giao dự án*

| **Thông tin**           | **Nội dung**                                                                           |
|-------------------------|----------------------------------------------------------------------------------------|
| Tên dự án               | Restaurant Management System                                                           |
| Số thành viên           | 5 thành viên                                                                           |
| Phạm vi                 | Từ phân tích yêu cầu, UCD, ERD, database, frontend, backend, testing đến demo hệ thống |
| Công cụ quản lý đề xuất | Jira/Trello, Slack/Zalo/Teams, GitHub/GitLab, Postman, draw.io, Word/PowerPoint        |
| Công nghệ đề xuất       | Frontend: React + TypeScript + Vite; Backend: Spring Boot; Database: MySQL/SQL Server  |

## 1. Mục đích tài liệu

Tài liệu này được lập để nhóm có căn cứ rõ ràng khi chia task, theo dõi tiến độ và đánh giá trách nhiệm của từng thành viên trong dự án Restaurant Management System. Nội dung bao phủ toàn bộ quá trình từ phân tích yêu cầu, thiết kế Use Case Diagram, thiết kế ERD, xây dựng database, phát triển backend, frontend, tích hợp, kiểm thử, hoàn thiện báo cáo và chuẩn bị demo.

- Xác định rõ vai trò của từng thành viên trong nhóm.

- Chia nhỏ công việc theo từng giai đoạn để dễ quản lý.

- Quy định sản phẩm đầu ra cho từng task.

- Giúp nhóm tránh tình trạng trùng việc, thiếu việc hoặc không biết ai chịu trách nhiệm.

- Làm căn cứ để báo cáo tiến độ với giảng viên và chuẩn bị thuyết trình cuối kỳ.

## 2. Nguyên tắc làm việc chung

| **Mã** | **Nguyên tắc**                               | **Mô tả**                                                                                           |
|--------|----------------------------------------------|-----------------------------------------------------------------------------------------------------|
| NT01   | Mỗi task phải có người phụ trách chính       | Một task chỉ có một người chịu trách nhiệm cuối cùng, các thành viên khác có thể hỗ trợ.            |
| NT02   | Mỗi task phải có sản phẩm đầu ra             | Ví dụ: file SRS, sơ đồ UCD, ERD, API, màn hình UI, test case, báo cáo lỗi.                          |
| NT03   | Không merge code trực tiếp vào nhánh chính   | Mọi chức năng cần làm trên branch riêng và tạo pull request để review.                              |
| NT04   | Task phải được cập nhật trạng thái hằng ngày | Trạng thái gồm: To Do, In Progress, Review, Done, Blocked.                                          |
| NT05   | Thiết kế trước, code sau                     | Các phần UCD, ERD, database schema và API contract cần thống nhất trước khi code.                   |
| NT06   | Ưu tiên luồng demo chính                     | Login, xem thực đơn, đặt món, thanh toán, xuất hóa đơn, bếp cập nhật trạng thái, admin xem báo cáo. |

## 3. Phân vai tổng thể cho 5 thành viên

| **Thành viên** | **Vai trò**                        | **Nhiệm vụ chính**                                                                      |
|----------------|------------------------------------|-----------------------------------------------------------------------------------------|
| Member 1       | Project Leader / Business Analyst  | Quản lý tiến độ, phân tích yêu cầu, SRS, UCD, use case specification, tổng hợp báo cáo. |
| Member 2       | Database Designer / Data Engineer  | Thiết kế ERD, chuẩn hóa dữ liệu, database schema, seed data, database documentation.    |
| Member 3       | Backend Developer                  | Xây dựng API, business logic, authentication, order, payment, invoice, kitchen, admin.  |
| Member 4       | Frontend Developer                 | Thiết kế giao diện, routing, component, gọi API, màn hình Customer/Staff/Kitchen/Admin. |
| Member 5       | Tester / DevOps / Technical Writer | Git workflow, test plan, test case, bug report, deployment guide, slide, demo script.   |

## 4. Phạm vi chức năng dự án

| **Nhóm chức năng**             | **Mô tả**                                                                           |
|--------------------------------|-------------------------------------------------------------------------------------|
| Authentication & Authorization | Đăng nhập, đăng ký, phân quyền Customer/Staff/Kitchen/Admin.                        |
| Menu & Food Catalog            | Quản lý danh mục món, món ăn, giá, hình ảnh, trạng thái còn/hết.                    |
| Cart & Online Order            | Khách xem món, thêm vào giỏ, áp dụng coupon, đặt đơn.                               |
| Staff Operation                | Nhân viên phục vụ tạo đơn tại quầy, quản lý bàn, xác nhận thanh toán, in hóa đơn.   |
| Kitchen Operation              | Nhân viên bếp xem món cần chế biến, nhận/từ chối món, cập nhật trạng thái chế biến. |
| Payment & Invoice              | Thanh toán, xác nhận thanh toán, tạo hóa đơn, tải PDF hóa đơn.                      |
| Reservation                    | Khách đặt bàn, nhân viên xác nhận yêu cầu đặt bàn.                                  |
| Admin Management               | Quản lý người dùng, nhân viên, thực đơn, coupon, tồn kho, báo cáo doanh thu.        |
| Testing & Reporting            | Test case, bug report, hướng dẫn chạy project, báo cáo và slide thuyết trình.       |

## 5. Kế hoạch triển khai theo 4 tuần

| **Thời gian** | **Mục tiêu**               | **Sản phẩm đầu ra**                                                                       |
|---------------|----------------------------|-------------------------------------------------------------------------------------------|
| Tuần 1        | Phân tích và thiết kế      | SRS, actor, UCD, ERD draft, setup repo FE/BE, setup Jira/Git workflow.                    |
| Tuần 2        | Xây dựng nền tảng          | Database schema, seed data, auth API, food API, login UI, menu UI, cart UI, Postman test. |
| Tuần 3        | Hoàn thiện chức năng chính | Order, coupon, payment, invoice, staff, kitchen, admin dashboard, tích hợp FE-BE.         |
| Tuần 4        | Kiểm thử và bàn giao       | Test toàn hệ thống, sửa bug, hoàn thiện báo cáo, slide, demo script, backup project.      |

## 6. Phân chia task chi tiết theo giai đoạn

### 6.1. Giai đoạn 1 - Khởi động dự án và setup công cụ

| **Mã task** | **Task**                | **Phụ trách**      | **Sản phẩm đầu ra**    | **Tiêu chí hoàn thành**                                 |
|-------------|-------------------------|--------------------|------------------------|---------------------------------------------------------|
| P1-T01      | Tạo kênh trao đổi nhóm  | Member 1           | Slack/Zalo/Teams group | Kênh trao đổi có đủ 5 thành viên.                       |
| P1-T02      | Tạo Jira/Trello board   | Member 5           | Board quản lý task     | Có các cột: To Do, In Progress, Review, Done, Blocked.  |
| P1-T03      | Tạo repository Frontend | Member 4           | Repo FE                | Repo tạo thành công, có README và branch main/dev.      |
| P1-T04      | Tạo repository Backend  | Member 3           | Repo BE                | Repo tạo thành công, có README và branch main/dev.      |
| P1-T05      | Quy định Git workflow   | Member 5           | Tài liệu Git workflow  | Có quy tắc đặt tên branch, commit, pull request, merge. |
| P1-T06      | Chốt công nghệ dự án    | Member 1 + cả nhóm | Danh sách tech stack   | FE, BE, DB, tool test, tool vẽ sơ đồ được thống nhất.   |

### 6.2. Giai đoạn 2 - Phân tích yêu cầu và SRS

| **Mã task** | **Task**                             | **Phụ trách**       | **Sản phẩm đầu ra**     | **Tiêu chí hoàn thành**                                                          |
|-------------|--------------------------------------|---------------------|-------------------------|----------------------------------------------------------------------------------|
| P2-T01      | Viết mô tả tổng quan hệ thống        | Member 1            | Phần overview trong SRS | Nêu rõ mục tiêu, đối tượng sử dụng, phạm vi hệ thống.                            |
| P2-T02      | Xác định actor                       | Member 1            | Danh sách actor         | Có Customer, Staff, Kitchen Staff, Admin, Payment Gateway, Notification Service. |
| P2-T03      | Xác định functional requirements     | Member 1            | Danh sách chức năng     | Mỗi nhóm actor có chức năng tương ứng.                                           |
| P2-T04      | Xác định non-functional requirements | Member 1 + Member 5 | Yêu cầu phi chức năng   | Có bảo mật, hiệu năng, usability, maintainability.                               |
| P2-T05      | Rà soát yêu cầu với database         | Member 2            | Ghi chú dữ liệu cần lưu | Mỗi chức năng quan trọng có dữ liệu hỗ trợ.                                      |
| P2-T06      | Rà soát yêu cầu với backend          | Member 3            | Ghi chú API cần có      | Mỗi chức năng chính có thể chuyển thành API.                                     |
| P2-T07      | Rà soát yêu cầu với frontend         | Member 4            | Danh sách màn hình      | Mỗi use case chính có màn hình tương ứng.                                        |
| P2-T08      | Hoàn thiện tài liệu SRS              | Member 1 + Member 5 | File SRS bản 1          | Tài liệu rõ ràng, đủ mục và có thể đưa vào báo cáo.                              |

### 6.3. Giai đoạn 3 - Thiết kế Use Case Diagram và đặc tả Use Case

| **Mã task** | **Task**                               | **Phụ trách**       | **Sản phẩm đầu ra**    | **Tiêu chí hoàn thành**                                                    |
|-------------|----------------------------------------|---------------------|------------------------|----------------------------------------------------------------------------|
| P3-T01      | Lập danh sách Use Case                 | Member 1            | Use case list          | Có nhóm Customer, Staff, Kitchen Staff, Admin.                             |
| P3-T02      | Xác định association                   | Member 1            | Bảng actor-use case    | Actor nào dùng chức năng nào được ghi rõ.                                  |
| P3-T03      | Xác định include/extend/generalization | Member 1 + Member 3 | Bảng relationship      | Không có quan hệ treo, không nối sai hướng.                                |
| P3-T04      | Vẽ UCD bản nháp                        | Member 1            | File PlantUML/draw.io  | Có system boundary, actor, use case, relationship.                         |
| P3-T05      | Review UCD theo logic thực tế          | Member 3 + Member 5 | Danh sách góp ý        | Phát hiện use case thiếu, relationship sai, actor sai.                     |
| P3-T06      | Chỉnh sửa UCD bản cuối                 | Member 1            | UCD final              | Sơ đồ rõ, không chồng chéo, có chú thích.                                  |
| P3-T07      | Viết đặc tả use case chính             | Member 1            | Use Case Specification | Mỗi UC có actor, precondition, main flow, alternative flow, postcondition. |
| P3-T08      | Viết phần giải thích UCD cho báo cáo   | Member 5            | Nội dung thuyết minh   | Có giải thích Association, Include, Extend, Generalization.                |

### 6.4. Giai đoạn 4 - Thiết kế ERD và Database

| **Mã task** | **Task**                             | **Phụ trách**       | **Sản phẩm đầu ra**    | **Tiêu chí hoàn thành**                                                  |
|-------------|--------------------------------------|---------------------|------------------------|--------------------------------------------------------------------------|
| P4-T01      | Xác định entity                      | Member 2            | Danh sách entity       | Có User, Role, Customer, Employee, FoodItem, Order, Payment, Invoice...  |
| P4-T02      | Xác định attribute                   | Member 2            | Danh sách thuộc tính   | Mỗi entity có PK, FK, thuộc tính nghiệp vụ.                              |
| P4-T03      | Xác định relationship và cardinality | Member 2            | Bảng quan hệ           | Có 1:1, 1:N, N:N được tách qua bảng trung gian.                          |
| P4-T04      | Chuẩn hóa dữ liệu                    | Member 2            | Ghi chú 1NF/2NF/3NF    | Không trùng bảng, không thuộc tính lặp, không phụ thuộc bắc cầu rõ ràng. |
| P4-T05      | Vẽ ERD bản nháp                      | Member 2            | File draw.io ERD       | Có entity, attribute, relationship diamond, cardinality.                 |
| P4-T06      | Review ERD theo luồng thực tế        | Member 1 + Member 3 | Danh sách góp ý        | Order-Payment-Invoice, Cart, Reservation, Inventory hợp lý.              |
| P4-T07      | Hoàn thiện ERD final                 | Member 2            | ERD final              | Không màu hoặc có màu tùy yêu cầu, dễ đọc, đúng rule.                    |
| P4-T08      | Viết SQL schema                      | Member 2            | schema.sql             | Tạo được toàn bộ bảng, khóa chính, khóa ngoại.                           |
| P4-T09      | Viết seed data                       | Member 2            | seed.sql               | Có dữ liệu mẫu: user, role, món ăn, bàn, coupon.                         |
| P4-T10      | Viết tài liệu database               | Member 2            | Database documentation | Mô tả bảng, khóa, quan hệ, enum, trạng thái.                             |

### 6.5. Giai đoạn 5 - Thiết kế giao diện và luồng màn hình

| **Mã task** | **Task**                    | **Phụ trách**       | **Sản phẩm đầu ra** | **Tiêu chí hoàn thành**                                           |
|-------------|-----------------------------|---------------------|---------------------|-------------------------------------------------------------------|
| P5-T01      | Lập sitemap hệ thống        | Member 4            | Sitemap             | Có luồng Customer, Staff, Kitchen, Admin.                         |
| P5-T02      | Thiết kế wireframe          | Member 4            | Wireframe           | Có layout các màn hình chính.                                     |
| P5-T03      | Thiết kế UI Customer        | Member 4            | UI Customer         | Login, menu, food detail, cart, checkout, order history, invoice. |
| P5-T04      | Thiết kế UI Staff           | Member 4            | UI Staff            | Table status, counter order, payment confirm, print invoice.      |
| P5-T05      | Thiết kế UI Kitchen         | Member 4            | UI Kitchen          | Cooking orders, update status.                                    |
| P5-T06      | Thiết kế UI Admin           | Member 4            | UI Admin            | Dashboard, user, employee, menu, coupon, report.                  |
| P5-T07      | Xây dựng component dùng lại | Member 4            | Component base      | Button, Table, Modal, Form, Sidebar, Navbar.                      |
| P5-T08      | Review UI với yêu cầu SRS   | Member 1 + Member 5 | Góp ý UI            | Giao diện đủ chức năng, không thiếu flow demo.                    |

### 6.6. Giai đoạn 6 - Phát triển Backend

| **Mã task** | **Task**                       | **Phụ trách**       | **Sản phẩm đầu ra** | **Tiêu chí hoàn thành**                                |
|-------------|--------------------------------|---------------------|---------------------|--------------------------------------------------------|
| P6-T01      | Setup backend project          | Member 3            | Backend project     | Chạy được project rỗng, kết nối DB thành công.         |
| P6-T02      | Tạo entity/model               | Member 3 + Member 2 | Entity classes      | Mapping đúng database schema.                          |
| P6-T03      | Tạo repository/data access     | Member 3            | Repository layer    | Truy vấn được các bảng chính.                          |
| P6-T04      | Xây dựng authentication API    | Member 3            | Auth API            | Login/register/me hoạt động.                           |
| P6-T05      | Xây dựng role/permission logic | Member 3            | Authorization       | Phân quyền Customer/Staff/Kitchen/Admin.               |
| P6-T06      | Xây dựng menu/food API         | Member 3            | Food API            | GET list/detail, admin CRUD food.                      |
| P6-T07      | Xây dựng cart API              | Member 3            | Cart API            | Thêm/sửa/xóa món trong giỏ hàng.                       |
| P6-T08      | Xây dựng order API             | Member 3            | Order API           | Tạo đơn, xem lịch sử, xem chi tiết đơn.                |
| P6-T09      | Xây dựng coupon logic          | Member 3            | Coupon service      | Kiểm tra mã giảm giá, điều kiện, số tiền giảm.         |
| P6-T10      | Xây dựng payment API           | Member 3            | Payment API         | Tạo thanh toán, xác nhận thanh toán.                   |
| P6-T11      | Xây dựng invoice API           | Member 3            | Invoice API         | Tạo hóa đơn sau thanh toán, xem/tải PDF hóa đơn.       |
| P6-T12      | Xây dựng staff API             | Member 3            | Staff API           | Quản lý bàn, tạo đơn tại quầy, xác nhận thanh toán.    |
| P6-T13      | Xây dựng kitchen API           | Member 3            | Kitchen API         | Xem món cần chế biến, cập nhật trạng thái món.         |
| P6-T14      | Xây dựng admin/report API      | Member 3            | Admin API           | Quản lý user/menu/coupon, xem doanh thu, món bán chạy. |
| P6-T15      | Viết API documentation         | Member 3            | Postman/API doc     | Có endpoint, method, request, response, status code.   |

### 6.7. Giai đoạn 7 - Phát triển Frontend

| **Mã task** | **Task**                               | **Phụ trách** | **Sản phẩm đầu ra** | **Tiêu chí hoàn thành**                                      |
|-------------|----------------------------------------|---------------|---------------------|--------------------------------------------------------------|
| P7-T01      | Setup frontend project                 | Member 4      | React/Vite project  | Project chạy được, có routing cơ bản.                        |
| P7-T02      | Tạo layout chính                       | Member 4      | Layout UI           | Header, sidebar, protected route, role-based layout.         |
| P7-T03      | Làm login/register UI                  | Member 4      | Auth screens        | Đăng nhập/đăng ký gọi được API.                              |
| P7-T04      | Làm menu và food detail                | Member 4      | Customer menu UI    | Hiển thị danh sách món, lọc/tìm kiếm, xem chi tiết.          |
| P7-T05      | Làm cart và checkout                   | Member 4      | Cart/Checkout UI    | Thêm/sửa/xóa giỏ hàng, đặt đơn, áp dụng coupon.              |
| P7-T06      | Làm payment và invoice UI              | Member 4      | Payment/Invoice UI  | Thanh toán, xem hóa đơn, tải hóa đơn.                        |
| P7-T07      | Làm order history UI                   | Member 4      | Order history       | Khách xem lịch sử đơn và trạng thái đơn.                     |
| P7-T08      | Làm staff UI                           | Member 4      | Staff screens       | Quản lý bàn, tạo đơn tại quầy, xác nhận thanh toán.          |
| P7-T09      | Làm kitchen UI                         | Member 4      | Kitchen screens     | Danh sách món cần chế biến, cập nhật trạng thái.             |
| P7-T10      | Làm admin UI                           | Member 4      | Admin screens       | Dashboard, user, employee, menu, coupon, report.             |
| P7-T11      | Xử lý form validation và thông báo lỗi | Member 4      | Validation          | Form không để lỗi dữ liệu cơ bản.                            |
| P7-T12      | Tối ưu giao diện demo                  | Member 4      | UI final            | Giao diện rõ ràng, dễ dùng, không lỗi hiển thị nghiêm trọng. |

### 6.8. Giai đoạn 8 - Tích hợp Frontend và Backend

| **Mã task** | **Task**                      | **Phụ trách**       | **Sản phẩm đầu ra** | **Tiêu chí hoàn thành**                                    |
|-------------|-------------------------------|---------------------|---------------------|------------------------------------------------------------|
| P8-T01      | Kết nối login API             | Member 3 + Member 4 | Login flow          | Đăng nhập thành công, lưu token, phân quyền route.         |
| P8-T02      | Kết nối menu API              | Member 4            | Menu integration    | FE hiển thị dữ liệu thật từ BE.                            |
| P8-T03      | Kết nối cart API              | Member 4            | Cart integration    | Thêm/sửa/xóa món hoạt động.                                |
| P8-T04      | Kết nối order API             | Member 3 + Member 4 | Order flow          | Tạo đơn từ giỏ hàng thành công.                            |
| P8-T05      | Kết nối coupon API            | Member 3 + Member 4 | Coupon flow         | Nhập mã giảm giá và tính tổng tiền đúng.                   |
| P8-T06      | Kết nối payment API           | Member 3 + Member 4 | Payment flow        | Thanh toán/xác nhận thanh toán cập nhật trạng thái đúng.   |
| P8-T07      | Kết nối invoice API           | Member 3 + Member 4 | Invoice flow        | Sau thanh toán tạo và xem được hóa đơn.                    |
| P8-T08      | Kết nối kitchen API           | Member 4            | Kitchen integration | Bếp cập nhật trạng thái món, customer/staff thấy thay đổi. |
| P8-T09      | Kết nối admin report API      | Member 4            | Report integration  | Dashboard hiển thị doanh thu/món bán chạy.                 |
| P8-T10      | Kiểm tra flow demo end-to-end | Member 5            | Demo checklist      | Từ login đến order-payment-invoice chạy liên tục.          |

### 6.9. Giai đoạn 9 - Kiểm thử hệ thống

| **Mã task** | **Task**                              | **Phụ trách**       | **Sản phẩm đầu ra** | **Tiêu chí hoàn thành**                                |
|-------------|---------------------------------------|---------------------|---------------------|--------------------------------------------------------|
| P9-T01      | Lập test plan                         | Member 5            | Test plan           | Có phạm vi test, môi trường test, dữ liệu test.        |
| P9-T02      | Viết test case authentication         | Member 5            | TC Auth             | Test login đúng/sai, role redirect.                    |
| P9-T03      | Viết test case menu/cart/order        | Member 5            | TC Customer         | Test xem món, giỏ hàng, đặt đơn.                       |
| P9-T04      | Viết test case coupon/payment/invoice | Member 5            | TC Payment          | Test giảm giá, thanh toán, tạo hóa đơn, tải hóa đơn.   |
| P9-T05      | Viết test case staff/kitchen          | Member 5            | TC Staff/Kitchen    | Test tạo đơn tại quầy, cập nhật món, trạng thái bàn.   |
| P9-T06      | Viết test case admin/report           | Member 5            | TC Admin            | Test CRUD món, coupon, dashboard.                      |
| P9-T07      | Test API bằng Postman                 | Member 5 + Member 3 | Postman result      | API trả đúng status code và dữ liệu.                   |
| P9-T08      | Test UI trên trình duyệt              | Member 5 + Member 4 | UI test result      | Không lỗi nghiêm trọng trong flow demo.                |
| P9-T09      | Lập bug report                        | Member 5            | Bug list            | Mỗi bug có mô tả, bước tái hiện, ảnh minh họa, mức độ. |
| P9-T10      | Retest sau khi fix bug                | Member 5            | Retest result       | Bug quan trọng đã được sửa và xác nhận.                |

### 6.10. Giai đoạn 10 - Báo cáo, slide và demo

| **Mã task** | **Task**                     | **Phụ trách**       | **Sản phẩm đầu ra** | **Tiêu chí hoàn thành**                                      |
|-------------|------------------------------|---------------------|---------------------|--------------------------------------------------------------|
| P10-T01     | Viết chương giới thiệu dự án | Member 1            | Báo cáo phần intro  | Có lý do chọn đề tài, mục tiêu, phạm vi.                     |
| P10-T02     | Viết phần SRS và UCD         | Member 1            | Báo cáo analysis    | Có actor, use case, relationship, use case specification.    |
| P10-T03     | Viết phần ERD/database       | Member 2            | Báo cáo database    | Có ERD, bảng, PK/FK, chuẩn hóa, data flow.                   |
| P10-T04     | Viết phần backend/API        | Member 3            | Báo cáo backend     | Có kiến trúc, API, service logic, security.                  |
| P10-T05     | Viết phần frontend/UI        | Member 4            | Báo cáo frontend    | Có screenshots, luồng màn hình, component.                   |
| P10-T06     | Viết phần testing            | Member 5            | Báo cáo testing     | Có test case, kết quả test, bug report.                      |
| P10-T07     | Tổng hợp và format báo cáo   | Member 1 + Member 5 | File Word final     | Đúng format, mục lục, hình ảnh, bảng biểu.                   |
| P10-T08     | Làm slide thuyết trình       | Member 5            | PowerPoint          | Có intro, member, source management, SRS, UCD, ERD, testing. |
| P10-T09     | Viết demo script             | Member 5 + cả nhóm  | Demo script         | Có thứ tự demo, tài khoản demo, dữ liệu mẫu.                 |
| P10-T10     | Tổng duyệt trước khi nộp     | Cả nhóm             | Checklist final     | Hệ thống chạy, báo cáo/slide hoàn chỉnh, backup sẵn.         |

## 7. Danh sách API đề xuất cho Backend

| **Module**     | **Endpoint**                         | **Mục đích**                      |
|----------------|--------------------------------------|-----------------------------------|
| Authentication | POST /auth/login                     | Đăng nhập hệ thống                |
| Authentication | POST /auth/register                  | Đăng ký tài khoản khách hàng      |
| Authentication | GET /auth/me                         | Lấy thông tin người dùng hiện tại |
| Menu           | GET /foods                           | Lấy danh sách món ăn              |
| Menu           | GET /foods/{id}                      | Lấy chi tiết món ăn               |
| Menu           | POST /admin/foods                    | Admin thêm món ăn                 |
| Menu           | PUT /admin/foods/{id}                | Admin cập nhật món ăn             |
| Cart           | GET /cart                            | Xem giỏ hàng                      |
| Cart           | POST /cart/items                     | Thêm món vào giỏ                  |
| Cart           | PUT /cart/items/{id}                 | Cập nhật số lượng                 |
| Order          | POST /orders                         | Tạo đơn hàng                      |
| Order          | GET /orders                          | Xem lịch sử đơn hàng              |
| Payment        | POST /payments/{orderId}/confirm     | Xác nhận thanh toán               |
| Invoice        | GET /invoices/{orderId}              | Xem hóa đơn                       |
| Invoice        | GET /invoices/{orderId}/pdf          | Tải hóa đơn PDF                   |
| Staff          | GET /staff/tables                    | Xem trạng thái bàn                |
| Staff          | POST /staff/orders                   | Tạo đơn tại quầy                  |
| Kitchen        | GET /kitchen/orders                  | Xem danh sách món cần chế biến    |
| Kitchen        | PUT /kitchen/order-items/{id}/status | Cập nhật trạng thái món           |
| Admin          | GET /admin/reports/revenue           | Xem báo cáo doanh thu             |

## 8. Flow demo bắt buộc

| **Bước** | **Nội dung demo**                     | **Mục tiêu**                              |
|----------|---------------------------------------|-------------------------------------------|
| 1        | Admin đăng nhập                       | Kiểm tra phân quyền admin.                |
| 2        | Admin thêm hoặc cập nhật món ăn       | Chứng minh quản lý thực đơn hoạt động.    |
| 3        | Customer đăng nhập                    | Chuyển sang vai trò khách hàng.           |
| 4        | Customer xem thực đơn và chi tiết món | Chứng minh menu/food API và UI hoạt động. |
| 5        | Customer thêm món vào giỏ hàng        | Kiểm tra cart flow.                       |
| 6        | Customer áp dụng coupon và đặt đơn    | Kiểm tra order + coupon.                  |
| 7        | Customer thanh toán                   | Kiểm tra payment flow.                    |
| 8        | Hệ thống tạo hóa đơn                  | Kiểm tra invoice sau thanh toán.          |
| 9        | Kitchen cập nhật trạng thái món       | Kiểm tra bếp xử lý đơn.                   |
| 10       | Admin xem báo cáo doanh thu           | Chứng minh thống kê sau đơn hàng.         |

## 9. Test case tối thiểu cần có

| **Mã TC** | **Chức năng**               | **Dữ liệu / bước test**            | **Kết quả mong đợi**                                |
|-----------|-----------------------------|------------------------------------|-----------------------------------------------------|
| TC01      | Đăng nhập đúng              | User nhập đúng email/password      | Đăng nhập thành công, vào đúng dashboard theo role. |
| TC02      | Đăng nhập sai               | User nhập sai mật khẩu             | Hệ thống báo lỗi.                                   |
| TC03      | Xem thực đơn                | Customer mở trang menu             | Danh sách món hiển thị đúng.                        |
| TC04      | Thêm vào giỏ hàng           | Customer chọn món và số lượng      | CartItem được tạo, tổng tiền cập nhật.              |
| TC05      | Áp dụng coupon hợp lệ       | Nhập mã coupon hợp lệ              | DiscountAmount được tính đúng.                      |
| TC06      | Áp dụng coupon không hợp lệ | Nhập mã hết hạn hoặc sai điều kiện | Hệ thống báo lỗi.                                   |
| TC07      | Tạo đơn hàng                | Customer checkout                  | Order và OrderItem được lưu.                        |
| TC08      | Xác nhận thanh toán         | Payment được xác nhận              | PaymentStatus = PAID.                               |
| TC09      | Tạo hóa đơn                 | Order đã thanh toán                | Invoice được tạo, có tổng tiền đúng.                |
| TC10      | Tải hóa đơn                 | Customer mở lịch sử đơn            | Tải được file hóa đơn.                              |
| TC11      | Nhân viên tạo đơn tại quầy  | Staff tạo order                    | Order có CreatedByEmployeeID.                       |
| TC12      | Bếp cập nhật món            | Kitchen đổi trạng thái món         | OrderItemStatus cập nhật đúng.                      |
| TC13      | Admin thêm món              | Admin nhập thông tin món           | FoodItem được tạo.                                  |
| TC14      | Admin xem báo cáo           | Admin mở dashboard                 | Doanh thu và món bán chạy hiển thị.                 |
| TC15      | Đặt bàn                     | Customer tạo reservation           | Reservation được lưu và staff có thể xử lý.         |

## 10. Quy định Git workflow

- Nhánh main chỉ chứa phiên bản ổn định, không code trực tiếp trên main.

- Nhánh dev dùng để tích hợp các chức năng đã review.

- Mỗi task tạo một nhánh riêng theo mẫu: feature/task-code-short-name. Ví dụ: feature/P6-T08-order-api.

- Commit message cần ngắn gọn, có mã task. Ví dụ: P6-T08: implement create order API.

- Trước khi push cần pull code mới nhất từ dev để tránh conflict.

- Mỗi pull request cần có mô tả: làm gì, ảnh hoặc video nếu là UI, cách test.

- Member 3 review backend code, Member 4 review frontend code, Member 5 kiểm tra flow test trước khi merge.

## 11. Checklist bàn giao cuối dự án

| **Hạng mục**  | **Tiêu chí hoàn thành**                               | **Phụ trách**       |
|---------------|-------------------------------------------------------|---------------------|
| Database      | Schema chạy được, seed data đầy đủ                    | Member 2            |
| Backend       | API chạy được, không lỗi nghiêm trọng                 | Member 3            |
| Frontend      | UI gọi API thành công, responsive ở mức cơ bản        | Member 4            |
| Testing       | Test case chính đã pass, bug nghiêm trọng đã fix      | Member 5            |
| Documentation | Báo cáo đầy đủ SRS, UCD, ERD, API, UI, testing        | Member 1 + Member 5 |
| Presentation  | Slide rõ ràng, có demo script                         | Member 5            |
| Demo          | Flow demo chạy được từ đầu đến cuối                   | Cả nhóm             |
| Backup        | Có backup source code, database script, slide, report | Cả nhóm             |

## 12. Bảng trách nhiệm cuối cùng theo thành viên

| **Thành viên** | **Vai trò**                | **Phạm vi trách nhiệm**                                                          | **Sản phẩm bàn giao**                                    |
|----------------|----------------------------|----------------------------------------------------------------------------------|----------------------------------------------------------|
| Member 1       | Leader / BA                | SRS, UCD, use case specification, quản lý tiến độ, tổng hợp báo cáo              | SRS final, UCD final, use case spec, project plan        |
| Member 2       | Database Designer          | ERD, schema, seed data, database documentation, normalization                    | ERD final, schema.sql, seed.sql, database design section |
| Member 3       | Backend Developer          | API, service, authentication, order, payment, invoice, staff/kitchen/admin logic | Backend source, API docs, Postman collection             |
| Member 4       | Frontend Developer         | UI, routing, components, integration API, screenshots                            | Frontend source, UI screens, screenshots                 |
| Member 5       | Tester / DevOps / Document | Git workflow, test case, bug report, deployment, slide, demo script              | Test report, bug list, slide, demo guide                 |

## 13. Ghi chú nghiệp vụ quan trọng: Order - Payment - Invoice

Đối với hệ thống nhà hàng, hóa đơn chính thức không nên được tạo ngay khi khách vừa đặt món. Hệ thống chỉ nên hiển thị bill tạm tính trong quá trình khách đang gọi món hoặc chưa thanh toán. Invoice chính thức chỉ được tạo sau khi PaymentStatus chuyển sang PAID.

1.  Customer hoặc Staff tạo Order, trạng thái ban đầu là CREATED hoặc PENDING.

2.  Order có nhiều OrderItem tương ứng với các món đã gọi.

3.  Khách thanh toán bằng tiền mặt, online hoặc QR.

4.  Hệ thống tạo Payment và cập nhật PaymentStatus = PAID.

5.  Sau khi thanh toán thành công, hệ thống tạo Invoice chính thức.

6.  Khách có thể tải hóa đơn PDF hoặc nhận hóa đơn qua email.

7.  Nếu hủy trước thanh toán thì không tạo Invoice. Nếu hoàn tiền sau thanh toán thì cập nhật InvoiceStatus và PaymentStatus tương ứng.
