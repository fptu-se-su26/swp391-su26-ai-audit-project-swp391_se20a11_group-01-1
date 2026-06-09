# P6-T07 - Cart API

Task: Xây dựng Cart API cho Restaurant Management System.

## Mục tiêu
- Customer xem giỏ hàng hiện tại.
- Customer thêm món vào giỏ.
- Customer cập nhật số lượng món.
- Customer xóa một món khỏi giỏ.
- Customer xóa toàn bộ giỏ hàng.

## API chính

| Method | Endpoint | Mục đích |
|---|---|---|
| GET | `/cart` | Xem giỏ hàng hiện tại |
| POST | `/cart/items` | Thêm món vào giỏ |
| PUT | `/cart/items/{cartItemId}` | Cập nhật số lượng |
| DELETE | `/cart/items/{cartItemId}` | Xóa một món khỏi giỏ |
| DELETE | `/cart` | Xóa toàn bộ giỏ hàng |

## Yêu cầu trước khi dùng
Folder này dùng tiếp với:
- P6-T02 Entity/Model
- P6-T03 Repository/Data Access
- P6-T04 Authentication API
- P6-T05 Role/Permission Logic
- P6-T06 Menu/Food API

## Lưu ý tích hợp
Code đang giả định project có các class/entity/repository sau:
- `User`
- `Cart`
- `CartItem`
- `FoodItem`
- `UserRepository`
- `CartRepository`
- `CartItemRepository`
- `FoodItemRepository`

Nếu tên field trong entity của nhóm khác, sửa lại trong `CartService`.
