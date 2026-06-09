# P6-T05 - Role/Permission Logic

## Mục tiêu
Task **P6-T05 - Xây dựng role/permission logic** thuộc backend. Sản phẩm đầu ra là phần **Authorization**, tiêu chí hoàn thành là hệ thống phân quyền được các vai trò:

- `CUSTOMER`
- `STAFF`
- `KITCHEN`
- `ADMIN`

Folder này được thiết kế để dùng tiếp sau:

- P6-T02: Entity/Model
- P6-T03: Repository/Data Access
- P6-T04: Authentication API

---

## Nội dung chính

```text
src/main/java/com/restaurant/
├── annotation/
│   └── RequireRole.java
├── config/
│   ├── MethodSecurityConfig.java
│   └── SecurityConfig.java
├── constant/
│   ├── AppRole.java
│   └── Permission.java
├── controller/
│   ├── CustomerDemoController.java
│   ├── StaffDemoController.java
│   ├── KitchenDemoController.java
│   ├── AdminDemoController.java
│   └── AccessCheckController.java
├── dto/
│   ├── admin/RoleAssignmentRequest.java
│   └── common/ApiResponse.java
├── security/
│   ├── CustomPermissionEvaluator.java
│   ├── JwtAuthenticationFilter.java
│   └── SecurityUser.java
└── service/
    ├── AuthorizationService.java
    └── RoleManagementService.java
```

---

## Role đề xuất

| Role | Ý nghĩa | Ví dụ quyền |
|---|---|---|
| CUSTOMER | Khách hàng | Xem menu, đặt món, xem hóa đơn cá nhân |
| STAFF | Nhân viên phục vụ/thu ngân | Quản lý bàn, tạo order tại quầy, xác nhận thanh toán |
| KITCHEN | Nhân viên bếp | Xem món cần nấu, cập nhật trạng thái món |
| ADMIN | Quản trị viên | Quản lý user, menu, coupon, báo cáo |

---

## Endpoint demo phân quyền

| Endpoint | Role được phép |
|---|---|
| `GET /api/customer/demo` | CUSTOMER, ADMIN |
| `GET /api/staff/demo` | STAFF, ADMIN |
| `GET /api/kitchen/demo` | KITCHEN, ADMIN |
| `GET /api/admin/demo` | ADMIN |
| `GET /api/access/me` | User đã đăng nhập |

---

## Cách tích hợp vào project

### Bước 1: Copy package vào project Spring Boot

Copy các folder sau vào `src/main/java/com/restaurant/`:

```text
annotation
config
constant
controller
dto
security
service
```

### Bước 2: Kiểm tra entity Role/User

Code này giả định entity `User` có field `Role role`, và `Role` có field `roleName`.

Ví dụ:

```java
@ManyToOne
@JoinColumn(name = "role_id")
private Role role;
```

```java
@Column(name = "role_name")
private String roleName;
```

Role trong DB nên lưu dạng:

```text
CUSTOMER
STAFF
KITCHEN
ADMIN
```

Nếu DB đang lưu dạng `ROLE_CUSTOMER`, `ROLE_ADMIN`, bạn cần sửa hàm normalize trong `AuthorizationService` hoặc `SecurityUser`.

---

## Cách dùng phân quyền trong Controller

### Cách 1: Dùng `@PreAuthorize`

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/reports")
public ResponseEntity<?> getReports() {
    return ResponseEntity.ok("Only admin can access");
}
```

### Cách 2: Dùng nhiều role

```java
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
@GetMapping("/staff/tables")
public ResponseEntity<?> getTables() {
    return ResponseEntity.ok("Staff or Admin can access");
}
```

### Cách 3: Dùng annotation tự tạo

```java
@RequireRole({AppRole.ADMIN})
@GetMapping("/admin/users")
public ResponseEntity<?> getUsers() {
    return ResponseEntity.ok("Admin only");
}
```

---

## Checklist hoàn thành P6-T05

```text
[ ] Có danh sách role rõ ràng: CUSTOMER, STAFF, KITCHEN, ADMIN
[ ] User đăng nhập có role trong JWT hoặc UserDetails
[ ] SecurityConfig phân quyền endpoint đúng
[ ] Customer không vào được trang Staff/Admin
[ ] Staff không vào được Admin
[ ] Kitchen chỉ vào được màn hình bếp
[ ] Admin vào được toàn bộ khu vực quản trị
[ ] API trả 401 khi chưa đăng nhập
[ ] API trả 403 khi đăng nhập nhưng sai quyền
[ ] Có endpoint demo để test nhanh bằng Postman
```

---

## Gợi ý test bằng Postman

1. Login bằng tài khoản `CUSTOMER`.
2. Gọi `GET /api/customer/demo` → phải thành công.
3. Gọi `GET /api/admin/demo` → phải trả 403.
4. Login bằng tài khoản `ADMIN`.
5. Gọi `GET /api/admin/demo` → phải thành công.
6. Login bằng tài khoản `KITCHEN`.
7. Gọi `GET /api/kitchen/demo` → phải thành công.
8. Gọi `GET /api/staff/demo` → phải trả 403, trừ khi nhóm bạn cho Kitchen kiêm Staff.

---

## Ghi chú

Folder này là khung chuẩn để nhóm điều chỉnh theo database/schema thực tế. Nếu tên package, tên entity hoặc field khác với project của nhóm, hãy sửa import và getter tương ứng.
