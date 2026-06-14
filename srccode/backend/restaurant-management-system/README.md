# Restaurant Management System - Backend

Dự án Hệ thống Quản lý Nhà hàng (Restaurant Management System) xây dựng bằng Spring Boot 3 và Java 21.

## Công nghệ sử dụng
- Java 21
- Spring Boot 3.5.x
- MySQL & Spring Data JPA
- Spring Security & JWT
- Lombok
- Jakarta Validation
- Swagger/OpenAPI (springdoc)

## Cấu trúc Module

Dự án được chia thành 2 phần chính:

### Phần Người 1 (Core & User Management)
- **Role & User**: Quản lý tài khoản, phân quyền (ADMIN, STAFF, CUSTOMER).
- **JWT & Auth**: Đăng ký, đăng nhập và xác thực qua token JWT.
- **Category & Food**: Quản lý thực đơn nhà hàng (danh mục và món ăn).
- **Review**: Khách hàng đánh giá món ăn.

### Phần Người 2 (Ordering & Payment System)
- **RestaurantTable**: Quản lý sơ đồ bàn ăn, thông tin sức chứa và trạng thái.
- **Reservation**: Đặt bàn trực tuyến, tự động kiểm tra trùng lặp lịch (chênh lệch 2 tiếng).
- **Cart & CartItem**: Giỏ hàng lưu trữ tạm thời các món ăn người dùng muốn đặt.
- **Coupon**: Hệ thống mã giảm giá theo % hoặc số tiền cố định, giới hạn số lần dùng.
- **Order & OrderDetail**: Xử lý tạo đơn hàng từ giỏ hàng, áp dụng mã giảm giá và quản lý trạng thái luân chuyển (PENDING -> PREPARING -> SERVED -> CANCELLED).
- **Payment**: Thanh toán đơn hàng, chặn Double-payment, tự động cập nhật trạng thái đơn.

## Hướng dẫn chạy dự án

### 1. Cài đặt Cơ sở dữ liệu (MySQL)
Mở file `src/main/resources/application.properties` để cấu hình thông tin kết nối DB.
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant_db
spring.datasource.username=root
spring.datasource.password=123456
```
Tạo schema cơ sở dữ liệu có tên tương ứng (`restaurant_db`).

### 2. Chạy ứng dụng
Sử dụng Maven Wrapper có sẵn trong thư mục gốc của backend:

**Trên Windows:**
```bash
.\mvnw.cmd clean spring-boot:run
```

**Trên Linux/macOS:**
```bash
./mvnw clean spring-boot:run
```

### 3. Xem API Documentation (Swagger)
Khi server đã chạy (mặc định ở cổng 8080), hãy truy cập:
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

Tại Swagger UI, bạn có thể dễ dàng test các endpoint. Để gọi các API yêu cầu xác thực, hãy gọi API Login lấy JWT Token, sau đó bấm vào nút "Authorize" và nhập token vào.

## Chạy Unit Test
Để chạy toàn bộ Unit test và xác minh hệ thống hoạt động đúng chuẩn:
```bash
.\mvnw.cmd clean test
```
*(Ghi chú: Lệnh test có thể yêu cầu kết nối DB thực tế ở một số contextLoad test)*
