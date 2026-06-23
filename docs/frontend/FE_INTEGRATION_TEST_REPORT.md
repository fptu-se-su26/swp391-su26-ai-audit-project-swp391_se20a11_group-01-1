# FE-10 Frontend Integration Test Report

## 1. Scope
Kiểm thử tích hợp toàn bộ frontend flow từ FE-01 đến FE-09:
- Public (Menu, Auth)
- Customer (Cart, Checkout, Reservation, Orders, Profile)
- Admin (Dashboard, Reports, Users, Categories, Foods, Coupons)
- Staff (Orders, Dashboard)
- Kitchen (Queue)

## 2. Environment
- Frontend: React + Vite + TypeScript (cổng 5173 / mặc định)
- Backend: Spring Boot + MySQL (port 8080)
- Testing time: Cập nhật gần nhất theo nhánh `QuyetPV_DE190425`.

## 3. Route matrix
| Route | Page | Public/Protected | Allowed role | Navigation source | Result |
|---|---|---|---|---|---|
| `/` | `Navigate to /menu` | Public | All | Root | PASS |
| `/login` | `LoginPage` | Public (Unauth only) | All | Header | PASS |
| `/register` | `RegisterPage` | Public (Unauth only) | All | Header | PASS |
| `/menu` | `MenuPage` | Public | All | Header | PASS |
| `/customer` | `Navigate to /customer/menu` | Protected | CUSTOMER | System | PASS |
| `/customer/menu` | `MenuPage` | Protected | CUSTOMER | CustomerLayout | PASS |
| `/customer/cart` | `CartPage` | Protected | CUSTOMER | CustomerLayout | PASS |
| `/customer/checkout` | `CheckoutPage` | Protected | CUSTOMER | CartPage | PASS |
| `/customer/orders` | `OrderListPage` | Protected | CUSTOMER | CustomerLayout | PASS |
| `/customer/reservations` | `ReservationListPage` | Protected | CUSTOMER | CustomerLayout | PASS |
| `/customer/profile` | `ProfilePage` | Protected | CUSTOMER | CustomerLayout | PASS |
| `/admin` | `AdminDashboardPage` | Protected | ADMIN | AdminLayout | PASS |
| `/admin/users` | `AdminUsersPage` | Protected | ADMIN | AdminLayout | PASS |
| `/admin/foods` | `AdminFoodsPage` | Protected | ADMIN | AdminLayout | PASS |
| `/admin/coupons` | `AdminCouponsPage` | Protected | ADMIN | AdminLayout | PASS |
| `/admin/reports` | `AdminReportsPage` | Protected | ADMIN | AdminLayout | PASS |
| `/staff` | `StaffDashboardPage` | Protected | STAFF | StaffLayout | PASS |
| `/staff/orders` | `StaffOrdersPage` | Protected | STAFF | StaffLayout | PASS |
| `/staff/orders/new` | `StaffOrderCreatePage`| Protected | STAFF | StaffOrdersPage | PASS |
| `/kitchen` | `Navigate to /kitchen/queue`| Protected | KITCHEN | System | PASS |
| `/kitchen/queue` | `KitchenQueuePage` | Protected | KITCHEN | KitchenLayout | PASS |

## 4. Role access matrix
| Role combination | Expected landing route | Result |
|---|---|---|
| `ROLE_ADMIN` | `/admin` | PASS |
| `ROLE_STAFF` | `/staff` | PASS |
| `ROLE_KITCHEN` | `/kitchen` (redirects to `/kitchen/queue`) | PASS |
| `ROLE_CUSTOMER` | `/customer/menu` | PASS |
| Đa role (Ví dụ ADMIN + STAFF) | `/admin` (Ưu tiên Admin) | PASS |
| Không role hợp lệ / Lỗi role | `/unauthorized` | PASS |

## 5. API integration summary
Tất cả endpoint frontend cấu hình trong `src/api` khớp với Spring Boot Controllers (theo `api-endpoint-inventory.md`).
- Path chuẩn (`/api/auth`, `/api/public`, `/api/customer`, `/api/admin`, `/api/staff`, `/api/kitchen`).
- Params và body chuẩn khớp DTO.
- Không phát hiện mismatch qua static review.

## 6. Public/Auth results
- Không có automatic token refresh (Backend không cung cấp endpoint refresh token).
- Frontend restore session từ token/current user hiện có lưu tại `localStorage`. Khi API trả 401, session bị xóa hoặc người dùng phải đăng nhập lại theo implementation thực tế.
- PASS — static code and API contract review.

## 7. Customer results
- Cập nhật số lượng, review mã giảm giá (coupon preview reset khi cập nhật giỏ).
- Thanh toán (`DINE_IN`, `TAKEAWAY`), truyền `tableId` có điều kiện.
- Đặt bàn (Reservation), kiểm tra sức chứa và hiển thị thời gian quá khứ.
- Quản lý lịch sử đơn, xem trạng thái, theo dõi thanh toán.
- PASS WITH LIMITATION — browser runtime not executed.

## 8. Admin results
- Dashboard/Report, quản lý Users (Toggle status lock/unlock).
- Quản lý Món ăn/Danh mục (List, Add, Update status, image URL placeholder).
- Quản lý Coupon (Giới hạn `%` <= 100, valid date ranges).
- PASS WITH LIMITATION — browser runtime not executed.

## 9. Staff results
- Staff tạo đơn tại quầy, lọc type (`DINE_IN`, `TAKEAWAY`, `DELIVERY`), tableId tự xóa nếu đổi khỏi DINE_IN.
- Quản lý trạng thái (`CONFIRMED -> PREPARING` và `READY -> COMPLETED` hoặc `CANCELLED`).
- PASS WITH LIMITATION — browser runtime not executed.

## 10. Kitchen results
- Hàng đợi lấy tự động và sort order theo thời gian `createdAt` cũ trước.
- Bếp báo đang làm (`PREPARING`) hoặc báo xong (`READY`).
- PASS WITH LIMITATION — browser runtime not executed.

## 11. Backend test results
- Chạy qua Maven Surefire Plugin.
- Kết quả từ Maven summary:
  - Tests run: 74
  - Failures: 0
  - Errors: 0
  - Skipped: 0
  - BUILD SUCCESS

## 12. Frontend lint/build results
- **LINT**: 0 error, 0 warning.
- **BUILD**: Hoàn thành, không warning biên dịch, TypeScript validation PASS.
- **Frontend automated tests**: NOT AVAILABLE (không có script `test` trong package.json).

## 13. Runtime tests executed
- Backend automated tests (JUnit): Executed
- Build verification & Static Contract Analysis: Executed

## 14. Runtime tests not executed
- Browser/manual runtime flow: NOT EXECUTED
- End-to-end FE–BE runtime: NOT EXECUTED

## 15. Known backend limitations
1. Backend không cung cấp endpoint `/api/admin/categories` dùng để hiển thị toàn bộ danh mục (kể cả bị inactive). Trang quản lý danh mục Frontend đang tái sử dụng public endpoint `/api/public/categories`, dẫn tới danh mục bị vô hiệu hóa sẽ mất khỏi danh sách quản lý.
2. Backend bảo vệ strict `/api/admin/tables` bằng thẻ Spring Security `hasAuthority("ROLE_ADMIN")` trên HTTP matcher, do đó `@PreAuthorize` của Staff không có tác dụng. Buộc Staff không thể thao tác chọn bàn qua API này.

## 16. Known frontend limitations
1. Các danh mục đã bị inactive không thể phục hồi do (15.1).
2. Tạm thời chỉ hỗ trợ URL ảnh thực đơn, chưa hỗ trợ upload multipart.

## 17. Remaining risks
- Quản trị viên tự lock account của mình sẽ bị gián đoạn session ở login kế tiếp.
- Concurrency issue có thể tồn tại ở quy trình Staff/Kitchen nếu update cùng 1 đơn.

## 18. Final readiness
**READY WITH LIMITATIONS** (Hoàn toàn đáp ứng static verification, browser runtime chưa được thực hiện).
