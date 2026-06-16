# API Conventions

## 1. URL Pattern
Sử dụng chuẩn RESTful, danh từ số nhiều, lowercase chữ thường:
* `/api/v1/auth`: Đăng nhập, đăng ký
* `/api/v1/foods`: Thực đơn món ăn
* `/api/v1/categories`: Danh mục
* `/api/v1/orders`: Đơn hàng
* `/api/v1/payments`: Thanh toán
* `/api/v1/invoices`: Hóa đơn
* `/api/v1/tables`: Quản lý bàn
* `/api/v1/kitchen/tasks`: Quản lý công việc bếp
* `/api/v1/admin/revenue`: API riêng cho admin báo cáo

## 2. Pagination
Các API danh sách cần hỗ trợ phân trang chuẩn hóa:
**Request query**: `?page=0&size=10` (page bắt đầu từ 0).
**Response format**:
```json
{
  "success": true,
  "data": {
    "content": [
      { "id": 1, "name": "Item 1" }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```

## 3. Sorting & Filtering
**Sorting**: Dùng tham số `sort` dạng `field,direction`. Ví dụ: `?sort=createdAt,desc`.
**Filtering**: Truyền tham số trực tiếp hoặc dùng object. Ví dụ: `?status=PENDING&categoryId=1`.

## 4. Status Code Convention
Tuân thủ chuẩn HTTP Status Code:
* `200 OK`: Request thành công (GET, PUT, PATCH).
* `201 Created`: Tạo mới tài nguyên thành công (POST).
* `204 No Content`: Xóa thành công (DELETE) hoặc thao tác không cần trả data.
* `400 Bad Request`: Lỗi validation đầu vào.
* `401 Unauthorized`: Lỗi xác thực (sai token, mất token).
* `403 Forbidden`: Không có quyền thao tác.
* `404 Not Found`: Không tìm thấy tài nguyên (id không tồn tại).
* `409 Conflict`: Xung đột dữ liệu (ví dụ: tạo email đã tồn tại).
* `422 Unprocessable Entity`: Business rule bị vi phạm.
* `500 Internal Server Error`: Lỗi server (cần ghi log ẩn stacktrace).

## 5. Naming Convention
* **Endpoints**: Kebab-case (`/api/v1/food-categories`).
* **JSON Properties**: CamelCase (`firstName`, `totalAmount`).
* **DTO**: Tên Class hậu tố `Request` hoặc `Response` (`CreateOrderRequest`, `FoodResponse`).
* **Entity**: PascalCase, dạng số ít (`FoodItem`, `Order`).
* **Enum**: UPPER_SNAKE_CASE (ví dụ `PENDING_PAYMENT`, `CREDIT_CARD`).

## 6. Date Format
Mọi API nhận và trả ngày giờ dưới định dạng ISO 8601 (UTC).
* Ví dụ: `2026-06-15T10:00:00Z`
* Tránh định dạng ngày `dd/MM/yyyy` ở API, việc hiển thị để Frontend tự localize.

## 7. Decimal Format
Các trường tiền tệ, giá cả:
* Back-end: Dùng `BigDecimal`.
* Front-end/JSON: Trả về kiểu số thực (`number`) với quy ước đơn vị chuẩn (VND). Ví dụ: `50000.00`.

## 8. Enum Strategy
* Back-end: Ánh xạ enum vào database thông qua kiểu `String` thay vì `Ordinal` (Index). Ví dụ `@Enumerated(EnumType.STRING)`.
* Front-end: Gọi API nhận giá trị String (`"PENDING"`, `"COMPLETED"`) giúp dễ debug và tránh sai lệch.

## 9. Documentation Traceability
* Mỗi API chính phải map được tới use case/SRS.
* Mỗi chức năng backend/frontend phải có liên kết tới SDS hoặc design note.
* Bug/defect phải đưa vào Issues Report.
