# Những phần đã sửa cho khớp dự án

- Đổi entity `FoodItem` thành `Food` mapping đúng bảng `foods`.
- Đổi `FoodCategory` thành `Category` mapping đúng bảng `categories`.
- Đổi `User.id` thành `userId`, `password` thành `passwordHash`.
- Đổi `Role.id` thành `roleId`, thêm many-to-many user_roles và role_permissions.
- Sửa Order theo bảng `orders`: `orderType`, `orderStatus`, `subtotalAmount`, `discountAmount`, `totalAmount`.
- Sửa OrderItem theo bảng `order_items`: `itemStatus`, `kitchenNote`.
- Sửa Payment theo bảng `payments`: `paymentMethod`, `paymentStatus`, `transactionCode`, `paidAt`.
- Sửa Invoice theo bảng `invoices`, unique theo `order_id`.
- Thêm rule: không tạo invoice nếu payment chưa PAID.
- Sửa endpoint và service theo đúng use case Customer/Menu/Cart/Order/Coupon/Payment/Invoice.


## P6-T13 Kitchen API
- Added KitchenTask entity/repository mapping `kitchen_tasks`.
- Added KitchenService and KitchenController.
- Added endpoints to view cooking orders and update item cooking status.

## P6-T14 Admin/Report API
- Added AdminUserService/AdminUserController for account and role update.
- Added AdminReportService/AdminReportController for dashboard, revenue and best-selling food reports.
- Reports follow project rule: revenue is calculated from paid payments only.
