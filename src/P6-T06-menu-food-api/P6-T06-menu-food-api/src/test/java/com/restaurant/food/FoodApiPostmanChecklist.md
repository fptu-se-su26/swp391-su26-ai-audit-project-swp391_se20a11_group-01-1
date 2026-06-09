# Food API Test Checklist

## Public API

- [ ] GET `/foods` trả 200.
- [ ] GET `/foods?availableOnly=true` chỉ trả món còn bán.
- [ ] GET `/foods/{id}` trả đúng món.
- [ ] GET `/foods/{id}` với id không tồn tại trả 404.
- [ ] GET `/foods/categories/{categoryId}` trả món theo danh mục.
- [ ] GET `/foods/search?keyword=pho` trả món có keyword tương ứng.

## Admin API

- [ ] POST `/admin/foods` với token ADMIN tạo được món.
- [ ] POST `/admin/foods` không có token trả 401/403.
- [ ] POST `/admin/foods` với token CUSTOMER trả 403.
- [ ] PUT `/admin/foods/{id}` cập nhật được món.
- [ ] PATCH `/admin/foods/{id}/availability` đổi được trạng thái còn/hết.
- [ ] DELETE `/admin/foods/{id}` ẩn món bằng cách set `available = false`.
