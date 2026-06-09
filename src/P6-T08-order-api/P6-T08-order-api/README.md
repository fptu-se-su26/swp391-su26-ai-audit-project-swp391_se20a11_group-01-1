# P6-T08 - Xây dựng Order API

Task: Tạo đơn, xem lịch sử, xem chi tiết đơn.

Phụ trách: Member 3  
Output: Order API

## API có trong package

| Method | Endpoint | Mục đích |
|---|---|---|
| POST | `/orders` | Customer tạo đơn từ giỏ hàng |
| GET | `/orders` | Customer xem lịch sử đơn hàng của mình |
| GET | `/orders/{orderId}` | Customer xem chi tiết đơn hàng |
| GET | `/orders/code/{orderCode}` | Xem chi tiết theo mã đơn |
| GET | `/admin/orders` | Admin/Staff xem toàn bộ đơn |
| PATCH | `/admin/orders/{orderId}/status` | Admin/Staff cập nhật trạng thái đơn |

## Cách dùng

Copy các package sau vào project Spring Boot:

```text
src/main/java/com/restaurant/controller/OrderController.java
src/main/java/com/restaurant/dto/order/*.java
src/main/java/com/restaurant/service/OrderService.java
src/main/java/com/restaurant/constant/OrderStatus.java
src/main/java/com/restaurant/exception/*.java
```

Nếu repository hiện tại chưa có method cần thiết, xem folder:

```text
src/main/java/com/restaurant/repository-snippets/
```

## Phụ thuộc từ task trước

Package này dùng tiếp:

- P6-T02 Entity/Model
- P6-T03 Repository/Data Access
- P6-T04 Authentication API
- P6-T05 Role/Permission logic
- P6-T07 Cart API

## Luồng tạo đơn

1. Lấy user hiện tại từ JWT token.
2. Lấy cart của user.
3. Kiểm tra cart không rỗng.
4. Tạo `Order` với trạng thái `PENDING`.
5. Convert `CartItem` thành `OrderItem`.
6. Tính `totalAmount`, `discountAmount`, `finalAmount`.
7. Lưu order và order item.
8. Xóa cart sau khi tạo đơn thành công.

## Lưu ý nghiệp vụ

Task này **chỉ tạo Order**. Không tạo Invoice ngay tại đây. Invoice chỉ nên tạo sau khi PaymentStatus = PAID ở task Payment/Invoice.
