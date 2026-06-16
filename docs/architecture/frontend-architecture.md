# Frontend Architecture

## 1. Tech Stack
* **Framework**: React 18+ (SPA)
* **Language**: TypeScript
* **Build Tool**: Vite (cho tốc độ build nhanh)
* **Routing**: React Router DOM (v6)
* **HTTP Client**: Axios (với interceptors)
* **Form Validation**: React Hook Form kết hợp với Zod
* **State Management**: Zustand (nhẹ, thay thế Redux)
* **Styling**: TailwindCSS (Utility-first CSS framework)

## 2. Folder Structure
```text
src/
  ├── api/          # Cấu hình Axios instance và các API services
  ├── assets/       # Hình ảnh, font, css tĩnh
  ├── components/   # Các UI components dùng chung (Button, Input, Modal)
  ├── features/     # Logic nghiệp vụ chia theo domain (auth, order, cart)
  ├── hooks/        # Custom hooks (useAuth, useCart)
  ├── layouts/      # Layout wrapper (AdminLayout, CustomerLayout)
  ├── pages/        # Các trang màn hình chính (Home, Login, Dashboard)
  ├── routes/       # Cấu hình routing cho ứng dụng
  ├── store/        # Zustand stores
  ├── types/        # TypeScript interfaces & types
  ├── utils/        # Hàm helper dùng chung (formatCurrency, formatDate)
  └── validation/   # Các Zod schema dùng cho form validation
```

## 3. Routing Strategy
Hệ thống chia làm 3 loại route chính:
* **Public Route**: Bất kỳ ai cũng truy cập được (`/`, `/menu`, `/login`, `/register`).
* **Protected Route**: Cần đăng nhập mới truy cập được (`/profile`, `/cart`, `/orders`).
* **Role-based Route**: Cần đúng quyền (Role):
  - `/admin/*` (Quyền ADMIN)
  - `/staff/*` (Quyền STAFF)
  - `/kitchen/*` (Quyền KITCHEN)

## 4. Layouts
Hệ thống cung cấp trải nghiệm chuyên biệt qua nhiều layout:
* **Customer Layout**: Giao diện B2C, gồm Header (có giỏ hàng, user menu), Main Content, Footer.
* **Staff Layout**: Giao diện tối ưu cho máy tính bảng/mobile nội bộ, tập trung vào sơ đồ bàn và đặt món nhanh.
* **Kitchen Layout**: Giao diện bảng (Kanban) tự động cập nhật, chữ to, nút chạm lớn, tối giản.
* **Admin Layout**: Giao diện B2B (Dashboard), có Sidebar điều hướng, Header chứa thông tin tài khoản, Main Workspace hiện bảng biểu, biểu đồ.

## 5. State Management (Zustand)
Quản lý state toàn cục được giới hạn ở các trường hợp cần thiết để tránh overhead:
* **Auth Store**: Lưu JWT token, thông tin User cơ bản, Role (persist qua localStorage).
* **Cart Store**: Lưu danh sách món, số lượng khách đang chọn (persist qua localStorage để khách không mất giỏ hàng khi F5).
* **UI Store**: Quản lý trạng thái mở/đóng Sidebar, Modal thông báo lỗi chung, Loading state (global spinner).

## 6. API Layer (Axios)
Tất cả call API đều thông qua một instance Axios dùng chung.
* **Interceptors - Request**: Tự động đính kèm `Authorization: Bearer <token>` vào mọi request nếu có trong store.
* **Interceptors - Response**: 
  - Bắt lỗi 401 (Unauthorized): Tự động xóa token và redirect ra trang Login.
  - Bắt lỗi 403 (Forbidden): Hiển thị màn hình "Access Denied".
  - Xử lý chung các lỗi HTTP (500, 400) để map thông báo lỗi thống nhất ra UI.

## 7. Screen and SDS Traceability
* Mỗi screen cần có screen name.
* Role được phép truy cập.
* API sử dụng.
* Validation chính.
* Mapping tới use case.
