# Domain Analysis

## 1. User Domain
**Entities**: User, Role, UserRole, CustomerProfile, Employee
* **Purpose**: Quản lý thông tin định danh, phân quyền và hồ sơ cá nhân của tất cả người dùng trong hệ thống.
* **Main actors**: Mọi người dùng (Customer, Staff, Kitchen, Admin).
* **Main business rules**: 
  - Một user có thể có nhiều role (tùy nghiệp vụ, nhưng thường Customer có role CUSTOMER, nhân viên có thể có role STAFF, ADMIN).
  - Khách hàng vãng lai có thể không cần CustomerProfile chi tiết cho đến khi đặt hàng/đăng ký.
  - Employee liên kết chặt chẽ với User account để quản lý lương/ca làm.
* **Lifecycle**: Đăng ký/Tạo mới -> Xác thực (Active) -> Bị khóa (Banned/Inactive).
* **Dependencies**: Độc lập (là nền tảng cho mọi domain khác).

## 2. Menu Domain
**Entities**: FoodCategory, FoodItem
* **Purpose**: Quản lý danh mục và thực đơn các món ăn, đồ uống của nhà hàng.
* **Main actors**: Admin (quản lý), Customer/Staff (xem và chọn món).
* **Main business rules**:
  - Mỗi FoodItem thuộc về ít nhất một FoodCategory.
  - FoodItem có thể bị ẩn (không bán nữa) nhưng không được xóa cứng nếu đã có OrderItem tham chiếu tới nó.
  - Giá bán (Price) là bắt buộc và phải >= 0.
* **Lifecycle**: Khởi tạo -> Hiển thị (Available) -> Hết hàng (Out_of_stock) -> Dừng bán (Unavailable).
* **Dependencies**: Độc lập. Được Cart và Order tham chiếu tới.

## 3. Cart Domain
**Entities**: Cart, CartItem
* **Purpose**: Lưu trữ tạm thời các món ăn mà khách hàng (hoặc nhân viên tạo dùm khách) chọn trước khi thanh toán.
* **Main actors**: Customer, Staff.
* **Main business rules**:
  - Cart liên kết với User (nếu đã đăng nhập) hoặc Session.
  - Khi Order được tạo thành công, Cart phải được làm trống (xóa CartItem).
* **Lifecycle**: Khởi tạo (trống) -> Thêm món (Active) -> Chuyển thành Order -> Làm sạch.
* **Dependencies**: Phụ thuộc vào User (owner) và Menu Domain (món ăn).

## 4. Coupon Domain
**Entities**: Coupon, CouponUsage
* **Purpose**: Quản lý mã giảm giá và lịch sử sử dụng để kích cầu.
* **Main actors**: Admin (tạo coupon), Customer/Staff (sử dụng).
* **Main business rules**:
  - Coupon có thể giới hạn số lượt dùng tổng, lượt dùng mỗi user, thời gian hiệu lực và giá trị đơn hàng tối thiểu.
  - CouponUsage ghi nhận thời điểm và đơn hàng đã dùng để chống gian lận.
* **Lifecycle**: Draft -> Active -> Expired/Exhausted.
* **Dependencies**: Phụ thuộc vào User, Order.

## 5. Reservation Domain
**Entities**: RestaurantTable, Reservation
* **Purpose**: Quản lý sơ đồ bàn và việc đặt chỗ trước.
* **Main actors**: Customer, Staff.
* **Main business rules**:
  - Tránh overbooking: Không cho phép 2 Reservation trùng bàn trong cùng một khung giờ.
  - Bàn có sức chứa tối đa.
* **Lifecycle**:
  - Table: Available -> Reserved -> Occupied -> Out_of_service -> Available.
  - Reservation: Pending -> Confirmed -> Seated -> Cancelled/Completed.
* **Dependencies**: Phụ thuộc vào User.

## 6. Order Domain
**Entities**: Order, OrderItem
* **Purpose**: Ghi nhận và theo dõi các đơn hàng ăn tại quán hoặc mang đi/giao hàng.
* **Main actors**: Customer, Staff, Kitchen.
* **Main business rules**:
  - Giá món ăn trong OrderItem là giá *snapshot* tại thời điểm tạo Order, không thay đổi ngay cả khi FoodItem đổi giá.
  - Nếu khách dùng tại quán, Order được gán với RestaurantTable.
* **Lifecycle**: Pending -> Pending_Payment/Preparing -> Ready -> Served/Completed (hoặc Cancelled).
* **Dependencies**: Phụ thuộc Menu, User, Table, Coupon.

## 7. Payment Domain
**Entities**: Payment
* **Purpose**: Ghi nhận các giao dịch thanh toán cho đơn hàng.
* **Main actors**: Customer (online), Staff (nhận tiền mặt).
* **Main business rules**:
  - Payment có thể bằng nhiều hình thức (Cash, VNPay, Credit Card).
  - Số tiền thanh toán phải khớp hoặc lớn hơn/bằng giá trị Order.
* **Lifecycle**: Pending -> Paid -> Failed/Refunded.
* **Dependencies**: Phụ thuộc Order.

## 8. Invoice Domain
**Entities**: Invoice
* **Purpose**: Sinh hóa đơn tài chính/hóa đơn bán lẻ sau khi thanh toán thành công.
* **Main actors**: Staff, Admin.
* **Main business rules**:
  - Invoice chỉ được tạo khi Payment = PAID.
  - Invoice lưu trữ thông tin thuế (Tax) nếu có.
* **Lifecycle**: Draft (lúc thanh toán) -> Issued -> Cancelled (nếu hoàn tiền/hủy đơn).
* **Dependencies**: Phụ thuộc mạnh vào Payment và Order.
