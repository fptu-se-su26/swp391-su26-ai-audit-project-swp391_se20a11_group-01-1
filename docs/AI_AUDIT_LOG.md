# AI Audit Log

## 1. Thông tin chung

| Thông tin | Nội dung |
|---|---|
| Môn học | Software Project / SWP391 |
| Mã môn học | SWP391 |
| Lớp | SE20A11 |
| Học kỳ | Summer 2026 |
| Tên bài tập / Project | Restaurant Management System - Cái Gì Cũng Không Có |
| Tên sinh viên | Nguyễn Tiến Lộc |
| MSSV | DE190986 |
| Giảng viên hướng dẫn | QuangLTN3 |
| Ngày bắt đầu | 18/05/2026 |
| Ngày hoàn thành | In Progress |

---

## 2. Công cụ AI đã sử dụng

Đánh dấu các công cụ AI đã sử dụng trong quá trình thực hiện bài tập/project.

- [x] ChatGPT
- [ ] Gemini
- [x] Claude
- [ ] GitHub Copilot
- [ ] Cursor
- [ ] Antigravity
- [ ] Perplexity
- [ ] Microsoft Copilot
- [ ] Công cụ khác: ....................................

---

## 3. Mục tiêu sử dụng AI

### Mô tả mục tiêu sử dụng AI

Em sử dụng ChatGPT để hỗ trợ trong quá trình phát triển project Restaurant Management System.

AI được sử dụng chủ yếu để:
- Phân tích hướng phát triển module sau khi hoàn thành Auth.
- Gợi ý thiết kế backend cho các module Auth, Category, Food và Order.
- Hỗ trợ viết code mẫu cho Spring Boot backend và React frontend.
- Debug lỗi khi chạy backend, frontend, API và database.
- Hướng dẫn cấu hình gửi OTP qua email bằng Gmail SMTP.
- Hướng dẫn xử lý lỗi React khi backend trả về object lỗi.
- Hỗ trợ xây dựng flow đặt món thực tế gồm Customer, Staff và Kitchen.
- Hướng dẫn kiểm thử API bằng Thunder Client/Postman.
- Hướng dẫn kiểm tra dữ liệu trong SQL Server.

### Mô tả mục tiêu sử dụng AI

```text
Viết tại đây...

## 4. Nhật ký sử dụng AI chi tiết

> Mỗi lần sử dụng AI cho một phần quan trọng của bài tập/project, sinh viên cần ghi lại theo mẫu bên dưới.  
> Sinh viên/nhóm có thể nhân bản mẫu “Lần sử dụng AI” nhiều lần tùy theo số lần sử dụng AI thực tế.

---

### Lần sử dụng AI số 1

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 20/05/2026 |
| Công cụ AI | ChatGPT / Claude |
| Mục đích sử dụng | Hoàn thiện Auth module và Forgot Password bằng OTP email |
| Phần việc liên quan | Requirement Backend / Frontend / Debug / Testing |
| Mức độ sử dụng | Hỗ trợ nhiều |

#### 4.1. Prompt đã sử dụng

Tôi muốn làm chức năng forgot password, gửi OTP qua email luôn.
Chỉ tôi cấu hình email và sửa frontend để nhập OTP, reset password.
Sau đó nếu login sai mật khẩu thì phải hiện lỗi đúng chứ không được crash React.

#### 4.2. Kết quả AI gợi ý

AI đã gợi ý cách xây dựng chức năng Forgot Password gồm:
- Tạo ForgotPasswordRequest và ResetPasswordRequest.
- Thêm API /api/auth/forgot-password và /api/auth/reset-password.
- Tạo EmailService và EmailServiceImpl để gửi OTP qua Gmail SMTP.
- Cấu hình spring-boot-starter-mail trong pom.xml.
- Cấu hình application-local.properties để lưu mail username và app password.
- Không push application-local.properties lên GitHub vì có chứa thông tin nhạy cảm.
- Sửa ForgotPassword.js để gửi email, nhập OTP và đặt lại mật khẩu.
- Sửa lỗi ô OTP bị browser autofill nhầm email.
- Sửa AuthContext.js để khi login sai mật khẩu thì frontend hiển thị lỗi “Email hoặc mật khẩu không đúng”.

#### 4.3. Phần sinh viên/nhóm đã sử dụng từ AI

Em đã sử dụng các gợi ý sau:
- Tạo DTO ForgotPasswordRequest và ResetPasswordRequest.
- Tạo EmailService và EmailServiceImpl.
- Thêm dependency spring-boot-starter-mail vào pom.xml.
- Thêm API forgot password và reset password trong AuthController.
- Cập nhật AuthService và AuthServiceImpl để tạo OTP, lưu OTP tạm thời và reset password.
- Cập nhật ForgotPassword.js để gọi API thật từ backend.
- Cập nhật AuthContext.js để xử lý lỗi login sai mật khẩu an toàn hơn.
- Thêm application-local.properties vào .gitignore để tránh lộ App Password.

#### 4.4. Phần sinh viên/nhóm tự chỉnh sửa hoặc cải tiến

Em tự kiểm tra lại luồng hoạt động trên trình duyệt và backend:
- Chạy backend bằng Maven.
- Test gửi OTP bằng giao diện frontend.
- Sửa lại input OTP khi trình duyệt tự động điền email vào ô OTP.
- Kiểm tra lỗi 500 khi cấu hình mail chưa đúng.
- Phân biệt giữa mật khẩu user trong hệ thống và Gmail App Password.
- Kiểm tra việc login sai mật khẩu không làm React crash nữa.
- Chỉ push những file code cần thiết, không push file chứa mật khẩu mail thật.

#### 4.5. Minh chứng

| Loại minh chứng | Nội dung |
| Link commit | https://github.com/fptu-se-su26/swp391-su26-ai-audit-project-swp391_se20a11_group-01-1/commits/Loc-Branch-auth/ |
| File liên quan | AuthController.java, AuthService.java, AuthServiceImpl.java, EmailService.java, EmailServiceImpl.java, ForgotPasswordRequest.java, ResetPasswordRequest.java, ForgotPassword.js, AuthContext.js |
| Screenshot |  |
| Kết quả chạy/test | Forgot Password gửi OTP thành công, Reset Password thành công, Login sai mật khẩu hiển thị lỗi đúng |
| Link video demo |  |
| Ghi chú khác |  |

#### 4.6. Nhận xét cá nhân/nhóm

Qua phần này, em hiểu rõ hơn cách xây dựng chức năng xác thực người dùng trong ứng dụng thực tế. Em học được cách gửi OTP qua email, cách cấu hình Gmail SMTP trong Spring Boot, cách xử lý lỗi từ backend trả về frontend, và đặc biệt là cách bảo vệ thông tin nhạy cảm như App Password khi push code lên GitHub.

---

### Lần sử dụng AI số 2

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 01/07/2026 |
| Công cụ AI | ChatGPT / Claude |
| Mục đích sử dụng | Xây dựng backend Order module chuyên nghiệp |
| Phần việc liên quan | Requirement / Design / Database / Backend / Testing |
| Mức độ sử dụng | Hỗ trợ nhiều |

#### 4.1. Prompt đã sử dụng

Backend của tôi đã có Auth, Category, Food rồi. Bây giờ tôi muốn làm chức năng mới chuyên nghiệp giống thực tế nhất có thể. Tôi muốn làm Order module, lưu order vào database và có flow Customer, Staff, Kitchen.

#### 4.2. Kết quả AI gợi ý

AI đã đề xuất xây dựng Order Management Flow gồm:
- Customer chọn món và đặt hàng từ Cart.
- Backend lưu Order và OrderItem vào database.
- Staff xác nhận đơn từ PENDING sang CONFIRMED.
- Kitchen nhận đơn CONFIRMED, chuyển sang PREPARING và READY.
- Staff hoàn thành đơn từ READY sang COMPLETED.
- Customer xem lịch sử đơn hàng thật từ database.

AI cũng gợi ý thiết kế backend gồm:
- OrderStatus enum.
- Order.java.
- OrderItem.java.
- OrderRepository.java.
- OrderItemRepository.java.
- OrderRequest.java.
- OrderItemRequest.java.
- UpdateOrderStatusRequest.java.
- OrderResponse.java.
- OrderItemResponse.java.
- OrderService.java.
- OrderServiceImpl.java.
- OrderController.java.

AI cũng nhấn mạnh backend không nên tin giá từ frontend mà phải tự lấy giá từ bảng foods để tính totalAmount.

#### 4.3. Phần sinh viên/nhóm đã sử dụng từ AI

Em đã sử dụng cấu trúc và code gợi ý để tạo Order module:
- Tạo OrderStatus enum gồm PENDING, CONFIRMED, PREPARING, READY, COMPLETED, CANCELLED.
- Tạo entity Order và OrderItem.
- Tạo repository cho Order và OrderItem.
- Tạo DTO request/response cho Order.
- Tạo OrderService và OrderServiceImpl.
- Tạo OrderController với các API:
  + POST /api/orders
  + GET /api/orders
  + GET /api/orders/{orderId}
  + GET /api/orders/customer/{userId}
  + GET /api/orders/status/{status}
  + PUT /api/orders/{orderId}/status
  + DELETE /api/orders/{orderId}
- Backend tự tính tổng tiền dựa trên Food trong database.
- OrderItem lưu snapshot gồm foodId, foodName, unitPrice, quantity, subtotal, imageUrl, emoji.

#### 4.4. Phần sinh viên/nhóm tự chỉnh sửa hoặc cải tiến

Em đã tự kiểm tra và điều chỉnh theo project hiện tại:
- Gửi cho AI các file Food.java, FoodRepository.java, User.java, UserRepository.java để code Order khớp với project.
- Chạy Maven compile để kiểm tra lỗi.
- Xử lý lỗi thiếu memory/paging file khi chạy Java bằng cách chạy lại compile và giảm tải hệ thống.
- Test API bằng Thunder Client.
- Kiểm tra response trả về có orderId, orderCode, status, totalAmount và items.
- Kiểm tra dữ liệu được insert vào bảng orders và order_items trong SQL Server.
- Test luồng update status từ PENDING → CONFIRMED → PREPARING → READY → COMPLETED.

#### 4.5. Minh chứng

| Loại minh chứng | Nội dung |
| Link commit | https://github.com/fptu-se-su26/swp391-su26-ai-audit-project-swp391_se20a11_group-01-1/commits/Loc-Branch-order |
| File liên quan | OrderStatus.java, Order.java, OrderItem.java, OrderRepository.java, OrderItemRepository.java, OrderRequest.java, OrderItemRequest.java, UpdateOrderStatusRequest.java, OrderResponse.java, OrderItemResponse.java, OrderService.java, OrderServiceImpl.java, OrderController.java |
| Screenshot |  |
| Kết quả chạy/test | POST /api/orders trả về orderId, orderCode, status PENDING và lưu thành công vào database |
| Link video demo |  |
| Ghi chú khác | Backend compile BUILD SUCCESS trước khi test API |

#### 4.6. Nhận xét cá nhân/nhóm

Qua phần này, em hiểu rõ hơn cách thiết kế một module backend có quan hệ dữ liệu thực tế. Em học được cách chia entity Order và OrderItem, cách lưu snapshot tên món và giá món trong order item để lịch sử đơn hàng không bị sai khi giá món thay đổi. Em cũng hiểu hơn về cách validate trạng thái đơn hàng và cách backend tự tính tổng tiền thay vì tin dữ liệu từ frontend.

---

### Lần sử dụng AI số 3

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 01/07/2026 |
| Công cụ AI | ChatGPT / Claude |
| Mục đích sử dụng | Nối frontend Cart, CustomerOrders, StaffOrders, KitchenQueue, KitchenHistory với backend Order API |
| Phần việc liên quan | Requirement Frontend / Backend Integration / Testing / Debug |
| Mức độ sử dụng | Hỗ trợ ý tưởng / Hỗ trợ một phần / Hỗ trợ nhiều / Sinh chính nội dung |

#### 4.1. Prompt đã sử dụng

Tôi đã lưu được order vào database rồi. Bây giờ giúp tôi nối frontend: Cart, CustomerOrders, StaffOrders, KitchenQueue và KitchenHistory với API thật.

#### 4.2. Kết quả AI gợi ý

AI đã hướng dẫn nối frontend với backend theo từng bước:
- Sửa CartContext.js để hỗ trợ foodId, id, qty, quantity và lưu cart vào localStorage.
- Sửa Cart.js để khi bấm xác nhận đặt hàng thì gọi POST /api/orders.
- Sửa CustomerOrders.js để gọi GET /api/orders/customer/{userId}.
- Sửa StaffOrders.js để gọi GET /api/orders và cập nhật trạng thái PENDING → CONFIRMED, READY → COMPLETED.
- Sửa KitchenQueue.js để lấy đơn CONFIRMED và PREPARING, rồi cập nhật CONFIRMED → PREPARING → READY.
- Sửa KitchenHistory.js để lấy đơn READY và COMPLETED từ database.
- Bổ sung CSS cho KitchenHistory để giao diện filter, nút làm mới và badge trạng thái hiển thị đẹp hơn.

#### 4.3. Phần sinh viên/nhóm đã sử dụng từ AI

Em đã sử dụng code và hướng dẫn để cập nhật các file frontend:
- CartContext.js
- Cart.js
- CustomerOrders.js
- StaffOrders.js
- KitchenQueue.js
- KitchenHistory.js
- KitchenHistory.css

Các chức năng đã nối thành công:
- Customer đặt món từ Cart và lưu order vào database.
- Customer xem lịch sử đơn hàng thật trong CustomerOrders.
- Staff xem đơn thật và xác nhận đơn.
- Kitchen xem hàng đợi bếp, chuyển đơn sang đang nấu và sẵn sàng phục vụ.
- Kitchen xem lịch sử đơn đã sẵn sàng hoặc hoàn thành.

#### 4.4. Phần sinh viên/nhóm tự chỉnh sửa hoặc cải tiến

Em đã tự test toàn bộ flow:
- Login bằng tài khoản customer.
- Thêm món vào giỏ hàng.
- Đặt món từ Cart.
- Kiểm tra order được lưu vào database.
- Mở CustomerOrders để xem đơn thật.
- Login bằng staff và xác nhận đơn.
- Login bằng kitchen và xử lý đơn.
- Kiểm tra đơn READY xuất hiện ở StaffOrders.
- Staff hoàn thành đơn.
- Kiểm tra KitchenHistory hiển thị đơn READY/COMPLETED.
- Sửa thêm CSS KitchenHistory vì ban đầu nút làm mới và filter hiển thị chưa đẹp.

#### 4.5. Minh chứng

| Loại minh chứng | Nội dung |
|---|---|
| Link commit | https://github.com/fptu-se-su26/swp391-su26-ai-audit-project-swp391_se20a11_group-01-1/commits/Loc-Branch-order |
| File liên quan | CartContext.js, Cart.js, CustomerOrders.js, StaffOrders.js, KitchenQueue.js, KitchenHistory.js, KitchenHistory.css |
| Screenshot |  |
| Kết quả chạy/test | Flow Customer → Staff → Kitchen → Staff hoàn thành chạy ổn |
| Link video demo |  |
| Ghi chú khác | Frontend đã bỏ data fake cho các màn hình chính liên quan Order |

#### 4.6. Nhận xét cá nhân/nhóm

Qua phần này, em hiểu rõ hơn cách nối frontend React với backend Spring Boot. Em học được cách thay data fake bằng API thật, cách quản lý loading/error state, cách xử lý response từ backend và cách tổ chức flow nhiều vai trò trong hệ thống nhà hàng. Em cũng hiểu hơn về quy trình kiểm thử end-to-end từ customer đặt món đến staff và kitchen xử lý đơn.

---

## 5. Bảng tổng hợp mức độ sử dụng AI

Đánh dấu mức độ AI hỗ trợ ở từng hạng mục.

| Hạng mục | Không dùng AI | AI hỗ trợ ít | AI hỗ trợ nhiều | AI sinh chính | Ghi chú |
| Phân tích yêu cầu |  |  | x |  |  |
| Viết user story/use case |  | x | x |  |  |
| Thiết kế database |  |  | x |  |  |
| Thiết kế kiến trúc hệ thống |  | x |  |  |  |
| Thiết kế giao diện |  |  | x |  |  |
| Code frontend |  |  | x |  |  |
| Code backend |  |  | x |  |  |
| Debug lỗi |  |  |  | x |  |
| Viết test case |  | x |  |  |  |
| Kiểm thử sản phẩm |  | x |  |  |  |
| Tối ưu code |  | x |  |  |  |
| Viết báo cáo |  | x |  |  |  |
| Làm slide thuyết trình |  |  |  |  |  |

---

## 6. Các lỗi hoặc hạn chế từ AI

Ghi lại các trường hợp AI trả lời sai, thiếu, chưa phù hợp hoặc sinh code không chạy.

| STT | Lỗi/hạn chế từ AI | Cách phát hiện | Cách xử lý/cải tiến |

| 1 | Code ban đầu chưa xử lý đầy đủ lỗi backend trả về object nên React có thể crash khi render object lỗi | Khi cố tình login sai mật khẩu thì React báo “Objects are not valid as a React child” | Sửa AuthContext.js và các file frontend để dùng hàm getApiMessage, riêng login sai thì luôn trả message “Email hoặc mật khẩu không đúng” |
| 2 | Forgot Password ban đầu bị trình duyệt autofill email vào ô OTP | Khi mở màn hình nhập OTP, ô OTP tự hiện email thay vì để trống | Sửa input OTP với name riêng, autoComplete="one-time-code", maxLength={6}, inputMode="numeric" và reset OTP khi gửi mã |
| 3 | Backend spring-boot:run lỗi không phải do code mà do máy thiếu memory/paging file | Terminal báo “The paging file is too small” và Java không đủ memory | Tắt bớt app nặng, chạy lại Maven compile, có thể giới hạn MAVEN_OPTS hoặc tăng Virtual Memory Windows |

---

## 7. Kiểm chứng kết quả AI

Em đã kiểm chứng kết quả AI bằng nhiều cách:

1. Kiểm tra compile backend:
- Chạy .\mvnw.cmd clean compile.
- Đảm bảo kết quả BUILD SUCCESS trước khi chạy backend.

2. Chạy backend và frontend local:
- Backend chạy ở http://localhost:8080.
- Frontend chạy ở http://localhost:3000 hoặc 3001.

3. Test API bằng Thunder Client:
- POST /api/orders để tạo order.
- GET /api/orders để lấy toàn bộ order.
- GET /api/orders/customer/{userId} để lấy order theo customer.
- GET /api/orders/status/PENDING, CONFIRMED, PREPARING, READY, COMPLETED.
- PUT /api/orders/{orderId}/status để cập nhật trạng thái đơn.
- DELETE /api/orders/{orderId} để hủy đơn.

4. Kiểm tra database SQL Server:
- Kiểm tra bảng orders.
- Kiểm tra bảng order_items.
- Đảm bảo order được lưu thật vào database sau khi đặt món từ frontend.

5. Test flow người dùng:
- Customer login.
- Customer thêm món vào cart.
- Customer đặt món.
- CustomerOrders hiển thị đơn thật.
- StaffOrders hiển thị đơn PENDING.
- Staff xác nhận đơn PENDING sang CONFIRMED.
- KitchenQueue nhận đơn CONFIRMED.
- Kitchen chuyển đơn CONFIRMED sang PREPARING rồi READY.
- Staff hoàn thành đơn READY sang COMPLETED.
- KitchenHistory hiển thị đơn READY/COMPLETED.

6. Kiểm tra bảo mật cơ bản:
- Không push application-local.properties chứa Gmail App Password.
- Không push backend/.vscode, target, file log, hs_err_pid.
- Thêm file chứa thông tin nhạy cảm vào .gitignore.

7. Kiểm tra Git:
- Dùng git status để kiểm tra file trước khi add/commit.
- Chỉ add các file code liên quan.
- Commit và push lên branch riêng để dễ review.

---

## 8. Đóng góp cá nhân hoặc đóng góp nhóm

### 8.1. Đối với bài cá nhân

Đây là phần em thực hiện cá nhân trong project.

Phần em tự làm:
- Chạy project frontend và backend trên máy local.
- Cấu hình database SQL Server.
- Kiểm tra các file backend/frontend hiện có.
- Copy và điều chỉnh code theo cấu trúc project.
- Chạy Maven compile và xử lý lỗi phát sinh.
- Test API bằng Thunder Client.
- Kiểm tra dữ liệu lưu trong SQL Server.
- Test flow end-to-end trên giao diện web.
- Quản lý Git branch, commit và push code.
- Không push file chứa thông tin nhạy cảm.

Phần AI hỗ trợ:
- Gợi ý thiết kế module Auth, Forgot Password OTP và Order Management.
- Gợi ý code mẫu cho backend Spring Boot.
- Gợi ý code mẫu cho frontend React.
- Hướng dẫn debug lỗi frontend/backend.
- Hướng dẫn test API và kiểm tra database.
- Hướng dẫn Git workflow.
- Hỗ trợ viết AI Audit Log.

Phần em tự cải tiến và kiểm chứng:
- Điều chỉnh code để khớp với entity Food, User, repository và cấu trúc project hiện tại.
- Test từng API trước khi nối frontend.
- Chỉnh CSS KitchenHistory để giao diện hiển thị tốt hơn.
- Test nhiều vai trò customer, staff, kitchen.
- Đảm bảo code chạy được trước khi push lên GitHub.

### 8.2. Đối với bài nhóm

Không áp dụng. Đây là AI Audit Log cho phần đóng góp cá nhân của em.

## 9. Reflection cuối bài

### 9.1. AI đã hỗ trợ em/nhóm ở điểm nào?

AI hỗ trợ em rất nhiều trong việc phân tích hướng phát triển module, thiết kế flow nghiệp vụ, viết code mẫu và debug lỗi. Đặc biệt, AI giúp em hiểu cách xây dựng flow Order Management thực tế gồm Customer, Staff và Kitchen. AI cũng giúp em xử lý các lỗi như backend trả 500, React crash khi render object, lỗi cấu hình email OTP và lỗi thiếu memory khi chạy Java.

### 9.2. Phần nào em/nhóm không sử dụng theo gợi ý của AI? Vì sao?

Em không sử dụng hoàn toàn mọi gợi ý của AI một cách máy móc. Một số phần em điều chỉnh lại theo project hiện có, ví dụ:
- Dùng field có sẵn trong Food.java như foodId, foodName, price, imageUrl, emoji, isAvailable.
- Dùng User hiện tại với userId, username, email và role.
- Giữ lại một số CSS/giao diện cũ để không phá layout frontend.
- Tạm thời chưa thêm GlobalExceptionHandler vì muốn làm cách đơn giản hơn để demo login sai mật khẩu.
- Tạm thời chưa tách riêng paymentMethod, serviceFee, discountAmount thành cột database mà lưu một phần trong note trước.

### 9.3. Em/nhóm đã kiểm tra tính đúng đắn của kết quả AI như thế nào?

Em kiểm tra bằng cách:
- Chạy Maven compile để đảm bảo backend không lỗi cú pháp.
- Chạy Spring Boot backend và React frontend.
- Test API bằng Thunder Client.
- Kiểm tra response trả về có đúng dữ liệu không.
- Kiểm tra dữ liệu trong SQL Server.
- Test flow thật trên giao diện với nhiều vai trò.
- Cố tình nhập sai mật khẩu, sai OTP hoặc trạng thái không hợp lệ để kiểm tra lỗi.
- Quan sát console browser và terminal backend để phát hiện lỗi.

### 9.4. Nếu không có AI, phần nào sẽ khó khăn nhất?

Nếu không có AI, phần khó nhất với em là thiết kế flow Order Management đầy đủ và thực tế. Em sẽ mất nhiều thời gian để tự nghĩ cách chia Order và OrderItem, cách update status theo vai trò Staff/Kitchen, cách nối frontend với backend và cách debug các lỗi phát sinh giữa React, Spring Boot và SQL Server.

### 9.5. Sau bài tập/project này, em/nhóm học được gì về môn học?

Sau phần này, em học được cách xây dựng một chức năng web application hoàn chỉnh từ backend đến frontend. Em hiểu rõ hơn về mô hình Controller - Service - Repository, DTO request/response, entity relationship, cách gọi API từ React, cách lưu dữ liệu vào database và cách kiểm thử luồng nghiệp vụ nhiều vai trò trong hệ thống.

### 9.6. Sau bài tập/project này, em/nhóm học được gì về cách sử dụng AI có trách nhiệm?

Em học được rằng AI chỉ nên được dùng như công cụ hỗ trợ, không nên copy kết quả mà không hiểu. Em cần kiểm tra, chạy thử, debug và điều chỉnh code theo project thật. Em cũng cần ghi nhận rõ phần nào có AI hỗ trợ, không che giấu việc dùng AI, và phải đảm bảo mình có thể giải thích các phần đã nộp. Ngoài ra, em học được việc không đưa hoặc không push thông tin nhạy cảm như mật khẩu email, App Password hay file cấu hình local lên GitHub.

---

## 10. Cam kết học thuật

Sinh viên/nhóm cam kết rằng:

- Nội dung AI hỗ trợ đã được ghi nhận trung thực.
- Không nộp nguyên văn kết quả AI mà không kiểm tra.
- Có khả năng giải thích các phần đã nộp.
- Chịu trách nhiệm về tính đúng đắn của sản phẩm cuối cùng.
- Hiểu rằng việc sử dụng AI không khai báo có thể ảnh hưởng đến kết quả đánh giá.

| Đại diện sinh viên/nhóm | Ngày xác nhận |
|---|---|
|  |  |
