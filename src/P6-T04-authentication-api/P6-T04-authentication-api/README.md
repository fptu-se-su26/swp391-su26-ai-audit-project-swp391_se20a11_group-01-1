# P6-T04 - Xây dựng Authentication API

Task: **P6-T04 - Xây dựng authentication API**  
Phụ trách: **Member 3**  
Sản phẩm đầu ra: **Auth API**  
Tiêu chí hoàn thành: **Login / Register / Me hoạt động**

## API được tạo

| Method | Endpoint | Mục đích |
|---|---|---|
| POST | `/auth/register` | Đăng ký tài khoản Customer |
| POST | `/auth/login` | Đăng nhập và nhận JWT token |
| GET | `/auth/me` | Lấy thông tin user hiện tại từ token |

## Cấu trúc thư mục

```text
src/main/java/com/restaurant/
├── controller/
│   └── AuthController.java
├── dto/auth/
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── UserProfileResponse.java
├── service/
│   ├── AuthService.java
│   └── CustomUserDetailsService.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   └── JwtService.java
├── config/
│   └── SecurityConfig.java
└── exception/
    └── GlobalExceptionHandler.java
```

## Dependencies cần có trong `pom.xml`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

## Cấu hình trong `application.properties`

```properties
app.jwt.secret=CHANGE_THIS_SECRET_KEY_TO_A_LONG_RANDOM_STRING_AT_LEAST_32_CHARS
app.jwt.expiration-ms=86400000
```

## Test nhanh bằng Postman

### 1. Register

```http
POST /auth/register
Content-Type: application/json

{
  "fullName": "Nguyen Van A",
  "email": "customer1@gmail.com",
  "password": "123456"
}
```

### 2. Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "customer1@gmail.com",
  "password": "123456"
}
```

Response sẽ có token:

```json
{
  "token": "jwt_token_here",
  "tokenType": "Bearer",
  "userId": 1,
  "fullName": "Nguyen Van A",
  "email": "customer1@gmail.com",
  "role": "CUSTOMER"
}
```

### 3. Me

```http
GET /auth/me
Authorization: Bearer jwt_token_here
```

## Lưu ý khi ghép vào project

Code này giả định bạn đã có các file từ P6-T02 và P6-T03:

- `User.java`
- `Role.java`
- `UserRepository.java`
- `RoleRepository.java`

Nếu entity/repository của nhóm bạn đặt tên field khác, hãy sửa lại trong `AuthService` và `CustomUserDetailsService`.
