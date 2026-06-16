# Backend Architecture

## 1. Tech Stack
* **Ngôn ngữ**: Java 21
* **Framework chính**: Spring Boot 3.5
* **Security**: Spring Security + JWT (JSON Web Token), BCrypt hashing.
* **Data Access**: Spring Data JPA
* **Database**: MySQL
* **Migration**: Flyway
* **API Documentation**: Swagger/OpenAPI 3
* **Testing**: JUnit 5, Mockito

## 2. Package Structure
Áp dụng kiến trúc phân lớp chuẩn:
```text
com.restaurant
├── config       # Cấu hình Spring, Swagger, CORS
├── controller   # REST API Controllers
├── dto          # Data Transfer Objects (Request/Response)
├── entity       # JPA Entities
├── exception    # GlobalExceptionHandler, Custom Exceptions
├── mapper       # MapStruct hoặc custom mapper (Entity <-> DTO)
├── repository   # Spring Data JPA Interfaces
├── security     # JWT Filter, UserDetailsService, Auth logic
├── service      # Business Interfaces
│   └── impl     # Business Implementations
├── util         # Tiện ích dùng chung (DateUtils, StringUtils)
└── validation   # Custom Bean Validators
```

## 3. API Response Format
Mọi API trả về cấu trúc thống nhất (`ApiResponse<T>`):
```json
{
  "success": true,
  "message": "Success message",
  "data": {
     // Dữ liệu payload
  },
  "timestamp": "2026-06-15T10:00:00Z"
}
```

## 4. Error Response Format
Dành cho các lỗi validation hoặc exception:
```json
{
  "success": false,
  "errorCode": "VALIDATION_FAILED",
  "message": "Invalid input data",
  "fieldErrors": [
    {
      "field": "email",
      "message": "Email format is invalid"
    }
  ],
  "timestamp": "2026-06-15T10:00:00Z"
}
```

## 5. Security Strategy
* **JWT Authentication**: Client gửi token qua header `Authorization: Bearer <token>`.
* **Password Hashing**: BCryptPasswordEncoder.
* **Role-Based Access Control**: Các quyền cơ bản bao gồm `CUSTOMER`, `STAFF`, `KITCHEN`, `ADMIN`.
* **Public Endpoints**: Các API cho menu, đăng nhập, đăng ký, quên mật khẩu không yêu cầu auth.
* **Protected Endpoints**: Yêu cầu xác thực JWT. Các API nhạy cảm yêu cầu role cụ thể qua `@PreAuthorize("hasRole('ADMIN')")`.

## 6. Transaction Boundary
Sử dụng `@Transactional` ở mức Service cho các nghiệp vụ:
* **Checkout**: Xử lý tạo Order, trừ số lượng tồn kho (nếu có), sử dụng Coupon (cập nhật trạng thái) trong cùng một transaction.
* **Payment**: Cập nhật trạng thái Payment, Order, bàn (nếu có) và sinh Invoice trong một transaction.
* **Invoice**: Đảm bảo Invoice chỉ được tạo khi Order đã thanh toán thành công.
* **Reservation**: Tạo đặt bàn và giữ chỗ không bị conflict.
* **Kitchen Update**: Cập nhật trạng thái món ăn đồng bộ với lịch sử.

## 7. Domain Model (Entities)
1. **User**: Quản lý tài khoản (id, email, password, active).
2. **Role**: Vai trò hệ thống.
3. **UserRole**: Bảng trung gian (N-N).
4. **CustomerProfile**: Thông tin khách hàng chi tiết.
5. **Employee**: Thông tin nhân sự.
6. **FoodCategory**: Danh mục món ăn.
7. **FoodItem**: Món ăn.
8. **Cart & CartItem**: Giỏ hàng của khách hàng.
9. **Coupon & CouponUsage**: Mã giảm giá và lịch sử sử dụng để tránh dùng quá giới hạn.
10. **RestaurantTable**: Bàn ăn tại nhà hàng.
11. **Reservation**: Đơn đặt bàn.
12. **Order & OrderItem**: Đơn hàng chính và danh sách món (lưu giá tại thời điểm mua).
13. **Payment**: Giao dịch thanh toán.
14. **Invoice**: Hóa đơn đỏ/chứng từ sau khi thanh toán.

## 8. Business Rules
* **Order**: Giá của món ăn trong OrderItem phải là giá được snapshot tại thời điểm đặt hàng. Order không được phép xóa (soft delete hoặc chuyển trạng thái CANCELLED).
* **Coupon**: Chỉ áp dụng nếu order thỏa mãn min/max value, thời hạn và số lượng còn lại. Một order chỉ áp dụng 1 coupon.
* **Cart**: Giỏ hàng của khách lưu theo session/user. Xóa khi đặt hàng thành công.
* **Payment**: Hệ thống cho phép nhiều phương thức (CASH, BANK). Khi tạo order tại bàn, payment ở trạng thái PENDING.
* **Invoice**: Chỉ sinh ra tự động hoặc thủ công KHI Payment = PAID. 1 Payment -> 1 Invoice.
* **Reservation**: Bàn chuyển sang RESERVED. Tránh overbooking bằng cách check thời gian trùng lặp.
* **Kitchen Status Transition**: `PENDING -> ACCEPTED -> PREPARING -> READY -> SERVED`. Không được nhảy cóc sai nghiệp vụ.
* **Revenue Report**: Tính toán doanh thu chỉ dựa trên các Order đã hoàn tất thanh toán (PAID/COMPLETED). Không tính PENDING hoặc CANCELLED. Dùng BigDecimal cho tiền.

## 9. Java Coding Standards
* 4 spaces indentation.
* Class name PascalCase.
* Method/variable camelCase.
* Constants UPPER_SNAKE_CASE.
* One public class per file.
* Braces luôn dùng cho if/for/while.
* Không dùng public field tùy tiện.
* Không hard-code magic number.
* Không dùng `double` cho money.
