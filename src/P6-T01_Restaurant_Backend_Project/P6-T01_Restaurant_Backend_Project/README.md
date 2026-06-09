# P6-T01 - Restaurant Backend Project

## 1. Mục tiêu

Task: **P6-T01 - Setup backend project**  
Người phụ trách: **Member 3**  
Sản phẩm đầu ra: **Backend project**  
Tiêu chí hoàn thành: **Chạy được project rỗng, kết nối DB thành công**

Project này dùng:

- Java 21
- Spring Boot 3.2.5
- Maven
- Spring Web
- Spring Data JPA
- SQL Server JDBC Driver
- SQL Server / SQL Server Management Studio 21

---

## 2. Cấu trúc project

```text
restaurant-backend/
├── pom.xml
├── README.md
├── docs/
│   └── create-database.sql
└── src/
    └── main/
        ├── java/com/restaurant/
        │   ├── RestaurantBackendApplication.java
        │   ├── common/api/ApiResponse.java
        │   ├── config/DatabaseConnectionChecker.java
        │   └── health/
        │       ├── HealthController.java
        │       └── DatabaseHealthController.java
        └── resources/
            ├── application.properties
            └── application-dev.properties
```

---

## 3. Tạo database trong SSMS

Mở SQL Server Management Studio, chạy file:

```text
docs/create-database.sql
```

Hoặc chạy trực tiếp:

```sql
CREATE DATABASE RestaurantDB;
```

Sau đó chạy tiếp `schema.sql` và `seed.sql` nếu nhóm đã có 2 file đó.

---

## 4. Cấu hình kết nối SQL Server

Mở file:

```text
src/main/resources/application.properties
```

Sửa lại thông tin tài khoản SQL Server:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=RestaurantDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourStrongPassword123
```

Nếu máy bạn dùng Windows Authentication thì cấu hình sẽ khác. Bản này đang dùng tài khoản SQL Server Authentication.

---

## 5. Chạy project

Chạy bằng terminal trong thư mục project:

```bash
mvn spring-boot:run
```

Nếu kết nối DB thành công, console sẽ có dòng:

```text
SQL Server connection successful. Test query result = 1
```

---

## 6. API kiểm tra

### Kiểm tra backend chạy

```http
GET http://localhost:8080/api/health
```

Kết quả mong đợi:

```json
{
  "success": true,
  "message": "Application health check successful",
  "data": {
    "application": "Restaurant Backend",
    "status": "UP",
    "message": "Backend project is running successfully"
  }
}
```

### Kiểm tra kết nối database

```http
GET http://localhost:8080/api/db-check
```

Kết quả mong đợi:

```json
{
  "success": true,
  "message": "Database connection successful",
  "data": {
    "database": "SQL Server",
    "query": "SELECT 1",
    "result": 1,
    "status": "CONNECTED"
  }
}
```

---

## 7. Lỗi thường gặp

### Lỗi sai password

Kiểm tra lại:

```properties
spring.datasource.username=sa
spring.datasource.password=...
```

### Lỗi SQL Server chưa bật TCP/IP

Mở **SQL Server Configuration Manager**:

```text
SQL Server Network Configuration
→ Protocols for MSSQLSERVER
→ Enable TCP/IP
→ Restart SQL Server Service
```

### Lỗi database không tồn tại

Tạo database trước:

```sql
CREATE DATABASE RestaurantDB;
```

### Muốn chạy tạm không cần kiểm tra DB khi startup

Đổi trong `application.properties`:

```properties
app.database-check.enabled=false
```

Nhưng khi nộp P6-T01, nên để `true` để chứng minh project kết nối DB thành công.
