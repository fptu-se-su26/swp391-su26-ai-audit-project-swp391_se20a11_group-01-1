# P6-T06 - Menu/Food API

Task: Xây dựng menu/food API  
Phụ trách: Member 3  
Sản phẩm đầu ra: Food API  
Tiêu chí hoàn thành: GET list/detail hoạt động, Admin CRUD food hoạt động.

## API public

| Method | Endpoint | Mục đích |
|---|---|---|
| GET | `/foods` | Lấy danh sách món ăn |
| GET | `/foods/{id}` | Lấy chi tiết món ăn |
| GET | `/foods/categories/{categoryId}` | Lấy món theo danh mục |
| GET | `/foods/search?keyword=...` | Tìm kiếm món ăn |

## API admin

| Method | Endpoint | Mục đích |
|---|---|---|
| POST | `/admin/foods` | Thêm món ăn |
| PUT | `/admin/foods/{id}` | Cập nhật món ăn |
| DELETE | `/admin/foods/{id}` | Xóa/ẩn món ăn |
| PATCH | `/admin/foods/{id}/availability` | Cập nhật trạng thái còn/hết |

## Yêu cầu trước khi copy

Folder này dùng tiếp với các task trước:

- P6-T02 Entity/Model
- P6-T03 Repository/Data Access
- P6-T04 Authentication API
- P6-T05 Role/Permission Logic

Cần có sẵn entity và repository:

```text
FoodItem
FoodCategory
FoodItemRepository
FoodCategoryRepository
```

Nếu entity của nhóm bạn đặt tên field khác, hãy sửa lại trong `FoodService`.

## Gợi ý test nhanh bằng Postman

### 1. Lấy danh sách món

```http
GET http://localhost:8080/foods
```

### 2. Lấy chi tiết món

```http
GET http://localhost:8080/foods/1
```

### 3. Admin thêm món

```http
POST http://localhost:8080/admin/foods
Authorization: Bearer <ADMIN_TOKEN>
Content-Type: application/json

{
  "name": "Phở bò",
  "description": "Phở bò truyền thống",
  "price": 55000,
  "imageUrl": "https://example.com/pho.jpg",
  "available": true,
  "categoryId": 1
}
```

### 4. Admin cập nhật món

```http
PUT http://localhost:8080/admin/foods/1
Authorization: Bearer <ADMIN_TOKEN>
Content-Type: application/json

{
  "name": "Phở bò đặc biệt",
  "description": "Phở bò nhiều topping",
  "price": 70000,
  "imageUrl": "https://example.com/pho-special.jpg",
  "available": true,
  "categoryId": 1
}
```

### 5. Admin cập nhật trạng thái còn/hết món

```http
PATCH http://localhost:8080/admin/foods/1/availability?available=false
Authorization: Bearer <ADMIN_TOKEN>
```

## Checklist hoàn thành

- [ ] `GET /foods` trả về danh sách món.
- [ ] `GET /foods/{id}` trả về chi tiết món.
- [ ] `POST /admin/foods` chỉ Admin gọi được.
- [ ] `PUT /admin/foods/{id}` chỉ Admin gọi được.
- [ ] `DELETE /admin/foods/{id}` chỉ Admin gọi được.
- [ ] Có validate dữ liệu đầu vào.
- [ ] Có xử lý lỗi khi không tìm thấy món hoặc danh mục.
