# P6-T03 - Repository / Data Access Layer

Task: **P6-T03 - Tạo repository/data access**  
Phụ trách: **Member 3**  
Output: **Repository layer**  
Tiêu chí hoàn thành: **Truy vấn được các bảng chính**

## Nội dung folder

```text
src/main/java/com/restaurant/repository/
├── RoleRepository.java
├── UserRepository.java
├── FoodCategoryRepository.java
├── FoodItemRepository.java
├── CartRepository.java
├── CartItemRepository.java
├── CouponRepository.java
├── RestaurantTableRepository.java
├── OrderRepository.java
├── OrderItemRepository.java
├── PaymentRepository.java
├── InvoiceRepository.java
├── ReservationRepository.java
└── InventoryItemRepository.java
```

Có thêm file test:

```text
src/test/java/com/restaurant/repository/RepositorySmokeTest.java
```

## Cách dùng

Copy package `repository` vào project Spring Boot:

```text
src/main/java/com/restaurant/repository/
```

Project cần có sẵn package entity từ task P6-T02:

```text
src/main/java/com/restaurant/entity/
```

## Dependency cần có trong pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

Nếu dùng SQL Server thì thay driver MySQL bằng:

```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <scope>runtime</scope>
</dependency>
```

## Ví dụ dùng trong Service

```java
@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodItemRepository foodItemRepository;

    public List<FoodItem> getAvailableFoods() {
        return foodItemRepository.findByAvailableTrueAndStatus("ACTIVE");
    }
}
```

## Checklist hoàn thành P6-T03

```text
[ ] Repository interface đã tạo cho các bảng chính
[ ] Mỗi repository extends JpaRepository<Entity, Long>
[ ] Có query method cơ bản: findById, findAll, save, deleteById
[ ] Có query nghiệp vụ: tìm user theo email, món theo category, order theo customer
[ ] Backend start không lỗi bean repository
[ ] RepositorySmokeTest pass
[ ] Service/API có thể gọi repository để truy vấn dữ liệu thật
```

## Ghi chú

Các repository này dùng Spring Boot 3.x và entity dùng `jakarta.persistence.*`.
Nếu project dùng Spring Boot 2.x, entity thường dùng `javax.persistence.*`, nhưng repository code hầu như không cần đổi.
