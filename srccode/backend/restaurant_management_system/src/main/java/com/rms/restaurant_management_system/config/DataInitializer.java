package com.rms.restaurant_management_system.config;

import com.rms.restaurant_management_system.entity.Category;
import com.rms.restaurant_management_system.entity.Food;
import com.rms.restaurant_management_system.entity.RestaurantTable;
import com.rms.restaurant_management_system.entity.Role;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.enums.TableStatus;
import com.rms.restaurant_management_system.repository.CategoryRepository;
import com.rms.restaurant_management_system.repository.FoodRepository;
import com.rms.restaurant_management_system.repository.RestaurantTableRepository;
import com.rms.restaurant_management_system.repository.RoleRepository;
import com.rms.restaurant_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final FoodRepository foodRepository;
    private final RestaurantTableRepository tableRepository;

    @Override
    public void run(String... args) {
        // ── Roles ──────────────────────────────────────────
        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("CUSTOMER");
        createRoleIfNotExists("STAFF");
        createRoleIfNotExists("KITCHEN");

        // ── Users ──────────────────────────────────────────
        createUserIfNotExists("admin",    "admin@gmail.com",    "123456", "ADMIN");
        createUserIfNotExists("staff",    "staff@gmail.com",    "123456", "STAFF");
        createUserIfNotExists("kitchen",  "kitchen@gmail.com",  "123456", "KITCHEN");
        createUserIfNotExists("customer", "customer@gmail.com", "123456", "CUSTOMER");

        // ── Categories ─────────────────────────────────────
        createCategoryIfNotExists("Khai vi",     "Cac mon khai vi");
        createCategoryIfNotExists("Mon chinh",   "Cac mon chinh");
        createCategoryIfNotExists("Trang mieng", "Cac mon trang mieng");
        createCategoryIfNotExists("Do uong",     "Cac loai do uong");
        createCategoryIfNotExists("Sup & Chao",  "Cac mon sup va chao");

        // ── Foods ──────────────────────────────────────────
        // Khai vi
        createFoodIfNotExists("Sup bao ngu vi ca",       "Sup bao ngu vi ca thuong hang",              185000, "https://images.unsplash.com/photo-1547592180-85f173990554?w=400", "Khai vi");
        createFoodIfNotExists("Goi tom hum xoai xanh",   "Goi tom hum tuoi voi xoai xanh chua ngot",   220000, "https://images.unsplash.com/photo-1559847844-5315695dadae?w=400", "Khai vi");
        createFoodIfNotExists("Cha gio hai san",          "Cha gio gion nhan hai san",                  120000, "https://images.unsplash.com/photo-1562802378-063ec186a863?w=400", "Khai vi");

        // Mon chinh
        createFoodIfNotExists("Bo Wagyu nuong than hoa",   "Bo Wagyu A5 nuong than hoa thom lung",        580000, "https://images.unsplash.com/photo-1558030006-450675393462?w=400", "Mon chinh");
        createFoodIfNotExists("Tom hum hap bia",            "Tom hum tuoi hap bia dac trung",              750000, "https://images.unsplash.com/photo-1553361371-9b22f78e8b1d?w=400", "Mon chinh");
        createFoodIfNotExists("Ca hoi ap chao sot chanh",  "Ca hoi Na Uy ap chao sot chanh thom ngon",    320000, "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=400", "Mon chinh");
        createFoodIfNotExists("Vit quay Bac Kinh",          "Vit quay Bac Kinh da gion vang dam",          420000, "https://images.unsplash.com/photo-1611599537845-1c7aca0091c0?w=400", "Mon chinh");
        createFoodIfNotExists("Suon bo ham ruou vang",      "Suon bo ham ruou vang do Phap mem tan",       380000, "https://images.unsplash.com/photo-1544025162-d76594e1c2de?w=400", "Mon chinh");
        createFoodIfNotExists("Khoai tay nghien truffle",   "Khoai tay nghien bo voi nam truffle den",      95000, "https://images.unsplash.com/photo-1600891964092-4316c288032e?w=400", "Mon chinh");
        createFoodIfNotExists("Rau cu nuong thao moc",      "Rau cu nuong lo voi cac loai thao moc tuoi",   85000, "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400", "Mon chinh");

        // Trang mieng
        createFoodIfNotExists("Banh souffle socola",   "Banh souffle socola nong chay trong",    125000, "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400", "Trang mieng");
        createFoodIfNotExists("Creme brulee vani",     "Kem creme brulee vani Madagascar",         95000, "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400", "Trang mieng");
        createFoodIfNotExists("Banh tart chanh leo",   "Banh tart chanh leo chua ngot thanh mat",  85000, "https://images.unsplash.com/photo-1519915028121-7d3463d20b13?w=400", "Trang mieng");

        // Do uong
        createFoodIfNotExists("Cocktail Signature",     "Cocktail dac trung cua nha hang",          145000, "https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?w=400", "Do uong");
        createFoodIfNotExists("Ruou vang do Phap",      "Ly ruou vang do Bordeaux thuong hang",     280000, "https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?w=400", "Do uong");
        createFoodIfNotExists("Nuoc ep trai cay tuoi",  "Nuoc ep trai cay tuoi theo mua",            65000, "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400", "Do uong");
        createFoodIfNotExists("Tra thao moc huu co",    "Tra thao moc organic imported",             55000, "https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=400", "Do uong");

        // Sup & Chao
        createFoodIfNotExists("Sup kem nam rung", "Sup kem nam rung tuoi thom ngon", 110000, "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400", "Sup & Chao");

        // ── Tables ─────────────────────────────────────────
        createTableIfNotExists("Ban 1",  2);
        createTableIfNotExists("Ban 2",  4);
        createTableIfNotExists("Ban 3",  4);
        createTableIfNotExists("Ban 4",  6);
        createTableIfNotExists("Ban 5",  2);
        createTableIfNotExists("Ban 6",  4);
        createTableIfNotExists("Ban 7",  8);
        createTableIfNotExists("Ban 8",  6);
        createTableIfNotExists("Ban 9",  4);
        createTableIfNotExists("Ban 10", 2);
        createTableIfNotExists("Ban 11", 4);
        createTableIfNotExists("Ban 12", 6);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void createRoleIfNotExists(String roleName) {
        if (roleRepository.findByRoleName(roleName).isEmpty()) {
            roleRepository.save(Role.builder()
                    .roleName(roleName)
                    .isActive(true)
                    .build());
        }
    }

    private void createUserIfNotExists(String username, String email,
                                        String password, String roleName) {
        if (userRepository.existsByEmail(email)) return;

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        userRepository.save(User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .isActive(true)
                .build());
    }

    private Category createCategoryIfNotExists(String name, String description) {
        return categoryRepository.findByCategoryName(name).orElseGet(() ->
                categoryRepository.save(Category.builder()
                        .categoryName(name)
                        .description(description)
                        .isActive(true)
                        .build())
        );
    }

    private void createFoodIfNotExists(String name, String description,
                                        int price, String imageUrl,
                                        String categoryName) {
        if (foodRepository.existsByFoodName(name)) return;

        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryName));

        foodRepository.save(Food.builder()
                .foodName(name)
                .description(description)
                .price(BigDecimal.valueOf(price))
                .imageUrl(imageUrl)
                .rating(4.5)
                .orders(0)
                .isAvailable(true)
                .category(category)
                .build());
    }

    private void createTableIfNotExists(String tableName, int capacity) {
        if (!tableRepository.existsByTableName(tableName)) {
            tableRepository.save(RestaurantTable.builder()
                    .tableName(tableName)
                    .capacity(capacity)
                    .status(TableStatus.EMPTY)
                    .isActive(true)
                    .build());
        }
    }
}
