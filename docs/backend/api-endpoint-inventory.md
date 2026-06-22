# API Endpoint Inventory

This document provides a comprehensive inventory of all REST API endpoints implemented in the Restaurant Management System backend.

## 1. Public API
These endpoints are accessible to any user without authentication.

| Method | Path | Role | Controller | Purpose |
|---|---|---|---|---|
| POST | `/api/auth/register` | `permitAll` | `AuthController` | Đăng ký tài khoản khách hàng mới |
| POST | `/api/auth/login` | `permitAll` | `AuthController` | Đăng nhập và nhận JWT token |
| GET | `/api/categories` | `permitAll` | `CategoryController` | Lấy danh sách danh mục món ăn |
| GET | `/api/foods` | `permitAll` | `FoodController` | Lấy danh sách món ăn (có thể filter theo danh mục/tìm kiếm) |
| GET | `/api/foods/{id}` | `permitAll` | `FoodController` | Lấy chi tiết một món ăn |
| GET | `/api/tables` | `permitAll` | `TableController` | Lấy danh sách bàn (xem trạng thái trống/đã đặt) |

## 2. Authenticated Customer
These endpoints require authentication (`ROLE_CUSTOMER`).

| Method | Path | Role | Controller | Purpose |
|---|---|---|---|---|
| GET | `/api/cart` | `ROLE_CUSTOMER` | `CartController` | Lấy giỏ hàng của user |
| POST | `/api/cart/items` | `ROLE_CUSTOMER` | `CartController` | Thêm món vào giỏ hàng |
| PUT | `/api/cart/items/{id}` | `ROLE_CUSTOMER` | `CartController` | Cập nhật số lượng món trong giỏ hàng |
| DELETE | `/api/cart/items/{id}` | `ROLE_CUSTOMER` | `CartController` | Xóa món khỏi giỏ hàng |
| DELETE | `/api/cart` | `ROLE_CUSTOMER` | `CartController` | Làm sạch giỏ hàng |
| POST | `/api/coupons/validate` | `ROLE_CUSTOMER` | `CouponController` | Kiểm tra tính hợp lệ của mã giảm giá |
| POST | `/api/orders` | `ROLE_CUSTOMER` | `OrderController` | Checkout giỏ hàng để tạo đơn hàng |
| GET | `/api/orders` | `ROLE_CUSTOMER` | `OrderController` | Xem lịch sử đơn hàng của bản thân |
| GET | `/api/orders/{id}` | `ROLE_CUSTOMER` | `OrderController` | Xem chi tiết một đơn hàng của bản thân |
| POST | `/api/payments/process` | `ROLE_CUSTOMER` | `PaymentController` | Xử lý thanh toán cho đơn hàng |
| GET | `/api/payments/{id}` | `ROLE_CUSTOMER` | `PaymentController` | Xem chi tiết thanh toán |
| GET | `/api/invoices/payment/{paymentId}` | `ROLE_CUSTOMER` | `InvoiceController` | Lấy thông tin hóa đơn sau khi thanh toán thành công |
| GET | `/api/invoices/{id}` | `ROLE_CUSTOMER` | `InvoiceController` | Xem chi tiết hóa đơn bằng ID |
| POST | `/api/reservations` | `ROLE_CUSTOMER` | `ReservationController` | Đặt bàn nhà hàng |
| GET | `/api/reservations` | `ROLE_CUSTOMER` | `ReservationController` | Lấy danh sách các lượt đặt bàn của user |
| GET | `/api/reservations/{id}` | `ROLE_CUSTOMER` | `ReservationController` | Xem chi tiết đặt bàn |
| PATCH | `/api/reservations/{id}/cancel` | `ROLE_CUSTOMER` | `ReservationController` | Hủy đặt bàn (nếu còn hợp lệ) |

## 3. Staff API
These endpoints are for staff members processing orders at the counter.

| Method | Path | Role | Controller | Purpose |
|---|---|---|---|---|
| POST | `/api/staff/orders` | `ROLE_STAFF`, `ROLE_ADMIN` | `StaffOrderController` | Nhân viên tạo đơn hàng trực tiếp tại quầy (không qua giỏ hàng) |
| GET | `/api/staff/orders` | `ROLE_STAFF`, `ROLE_ADMIN` | `StaffOrderController` | Lấy danh sách các đơn do nhân viên tạo |
| GET | `/api/staff/orders/{id}` | `ROLE_STAFF`, `ROLE_ADMIN` | `StaffOrderController` | Chi tiết đơn tại quầy |
| PATCH | `/api/staff/orders/{id}/status` | `ROLE_STAFF`, `ROLE_ADMIN` | `StaffOrderController` | Cập nhật trạng thái đơn hàng (CONFIRMED -> PREPARING, READY -> COMPLETED) |

## 4. Kitchen API
These endpoints are for kitchen staff managing the preparation of food.

| Method | Path | Role | Controller | Purpose |
|---|---|---|---|---|
| GET | `/api/kitchen/orders` | `ROLE_KITCHEN`, `ROLE_ADMIN` | `KitchenOrderController` | Bếp lấy danh sách đơn hàng cần chuẩn bị (CONFIRMED, PREPARING) |
| GET | `/api/kitchen/orders/{id}` | `ROLE_KITCHEN`, `ROLE_ADMIN` | `KitchenOrderController` | Chi tiết đơn cần nấu |
| PATCH | `/api/kitchen/orders/{id}/status` | `ROLE_KITCHEN`, `ROLE_ADMIN` | `KitchenOrderController` | Cập nhật trạng thái chuẩn bị (CONFIRMED -> PREPARING -> READY) |

## 5. Admin API
These endpoints are strictly for the system administrator.

| Method | Path | Role | Controller | Purpose |
|---|---|---|---|---|
| GET | `/api/admin/users` | `ROLE_ADMIN` | `UserManagementController` | Lấy danh sách người dùng |
| POST | `/api/admin/users` | `ROLE_ADMIN` | `UserManagementController` | Tạo người dùng/nhân viên mới |
| PUT | `/api/admin/users/{id}` | `ROLE_ADMIN` | `UserManagementController` | Cập nhật thông tin/quyền của user |
| DELETE | `/api/admin/users/{id}` | `ROLE_ADMIN` | `UserManagementController` | Khóa/Vô hiệu hóa user |
| POST | `/api/admin/categories` | `ROLE_ADMIN` | `AdminCategoryController` | Thêm mới danh mục |
| PUT | `/api/admin/categories/{id}` | `ROLE_ADMIN` | `AdminCategoryController` | Sửa danh mục |
| DELETE | `/api/admin/categories/{id}` | `ROLE_ADMIN` | `AdminCategoryController` | Xóa (soft-delete) danh mục |
| POST | `/api/admin/foods` | `ROLE_ADMIN` | `AdminFoodController` | Thêm mới món ăn |
| PUT | `/api/admin/foods/{id}` | `ROLE_ADMIN` | `AdminFoodController` | Sửa món ăn |
| DELETE | `/api/admin/foods/{id}` | `ROLE_ADMIN` | `AdminFoodController` | Xóa (soft-delete) món ăn |
| GET | `/api/admin/coupons` | `ROLE_ADMIN` | `AdminCouponController` | Danh sách mã giảm giá |
| POST | `/api/admin/coupons` | `ROLE_ADMIN` | `AdminCouponController` | Tạo mã giảm giá mới |
| PUT | `/api/admin/coupons/{id}` | `ROLE_ADMIN` | `AdminCouponController` | Cập nhật mã giảm giá |
| DELETE | `/api/admin/coupons/{id}` | `ROLE_ADMIN` | `AdminCouponController` | Xóa mã giảm giá |
| GET | `/api/admin/tables` | `ROLE_ADMIN` | `AdminTableController` | Quản lý danh sách bàn |
| POST | `/api/admin/tables` | `ROLE_ADMIN` | `AdminTableController` | Thêm bàn mới |
| PUT | `/api/admin/tables/{id}` | `ROLE_ADMIN` | `AdminTableController` | Sửa bàn |
| PATCH | `/api/admin/tables/{id}/status` | `ROLE_ADMIN` | `AdminTableController` | Cập nhật status của bàn |
| GET | `/api/admin/reservations` | `ROLE_ADMIN` | `AdminReservationController` | Lấy danh sách toàn bộ đặt bàn |
| PATCH | `/api/admin/reservations/{id}/status` | `ROLE_ADMIN` | `AdminReservationController` | Cập nhật status đặt bàn |
| GET | `/api/admin/dashboard/summary` | `ROLE_ADMIN` | `AdminDashboardController` | Lấy dữ liệu tóm tắt Dashboard (tổng quan doanh thu, đơn) |
| GET | `/api/admin/reports/revenue` | `ROLE_ADMIN` | `AdminReportController` | Báo cáo doanh thu theo ngày |
| GET | `/api/admin/reports/orders` | `ROLE_ADMIN` | `AdminReportController` | Báo cáo số lượng/doanh thu đơn hàng |
| GET | `/api/admin/reports/top-foods` | `ROLE_ADMIN` | `AdminReportController` | Báo cáo top món bán chạy |
| GET | `/api/admin/reports/reservations` | `ROLE_ADMIN` | `AdminReportController` | Báo cáo tổng số lượt đặt bàn |
