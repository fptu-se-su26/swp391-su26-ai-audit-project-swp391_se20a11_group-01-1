# AI Audit Log

## 1. Thông tin chung

| Thông tin | Nội dung |
|---|---|
| Môn học | 	Software development project |
| Mã môn học | SWP391 |
| Lớp | SE20A11 |
| Học kỳ | 5 |
| Tên bài tập / Project | Office Maintenance Manage System |
| Tên sinh viên / Nhóm | Nguyễn Đức Thương / 1 |
| MSSV / Danh sách MSSV | DE190096 |
| Giảng viên hướng dẫn | 	QuangLTN3 |
| Ngày bắt đầu | 11/5/2026 |
| Ngày hoàn thành |  |

---

## 2. Công cụ AI đã sử dụng

Đánh dấu các công cụ AI đã sử dụng trong quá trình thực hiện bài tập/project.

- [  ] ChatGPT
- [ ] Gemini
- [  ] Claude
- [ ] GitHub Copilot
- [ ] Cursor
- [ ] Antigravity
- [ ] Perplexity
- [ ] Microsoft Copilot
- [ ] Công cụ khác: ....................................

---

## 3. Mục tiêu sử dụng AI

Mô tả ngắn gọn sinh viên/nhóm đã sử dụng AI để hỗ trợ những công việc nào.

Ví dụ:

- Phân tích yêu cầu bài toán
- Gợi ý ý tưởng giải pháp
- Thiết kế database
- Thiết kế giao diện
- Viết code mẫu
- Debug lỗi
- Tối ưu code
- Viết test case
- Kiểm tra bảo mật
- Viết báo cáo
- Chuẩn bị slide thuyết trình
- Tìm hiểu công nghệ mới

### Mô tả mục tiêu sử dụng AI

```text
- Phân tích yêu cầu bài toán và xác định Use Case của hệ thống
- Gợi ý ý tưởng cải tiến quy trình nghiệp vụ (Dining Session, QR Check-in)
- Hỗ trợ xây dựng giao diện các trang Admin bằng ReactJS
- Debug lỗi và tối ưu code frontend
```

## 4. Nhật ký sử dụng AI chi tiết

> Mỗi lần sử dụng AI cho một phần quan trọng của bài tập/project, sinh viên cần ghi lại theo mẫu bên dưới.  
> Sinh viên/nhóm có thể nhân bản mẫu “Lần sử dụng AI” nhiều lần tùy theo số lần sử dụng AI thực tế.

---

### Lần sử dụng AI số 1

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 20/05/2026 |
| Công cụ AI | ChatGPT |
| Mục đích sử dụng | Phân tích yêu cầu hệ thống và xác định các Actor, Use Case chính của Restaurant Management System |
| Phần việc liên quan | Requirement |
| Mức độ sử dụng | Hỗ trợ ý tưởng |

#### 4.1. Prompt đã sử dụng

```text
Tôi đang làm dự án Restaurant Management System. Hãy giúp tôi xác định các actor và use case chính của hệ thống.
```

#### 4.2. Kết quả AI gợi ý

```text
ChatGPT đề xuất 4 actor chính:
- Customer: đặt bàn, gọi món, thanh toán
- Staff: xác nhận đặt bàn, phục vụ, hỗ trợ thanh toán
- Kitchen Staff: nhận và xử lý đơn gọi món
- Admin: quản lý tài khoản, menu, báo cáo doanh thu
Đồng thời gợi ý danh sách các Use Case chính tương ứng với từng actor.
```

#### 4.3. Phần sinh viên/nhóm đã sử dụng từ AI

```text
Sử dụng danh sách 4 Actor và các Use Case tương ứng để xây dựng Use Case Diagram và tài liệu SRS của hệ thống.
```

#### 4.4. Phần sinh viên/nhóm tự chỉnh sửa hoặc cải tiến

```text
Sau khi trao đổi với giảng viên, nhóm đã chỉnh sửa lại một số Use Case cho phù hợp với thực tế hơn. Cụ thể: điều chỉnh vai trò của Kitchen Staff, bổ sung luồng QR check-in và Dining Session vào quy trình nghiệp vụ.
```

#### 4.5. Minh chứng

| Loại minh chứng | Nội dung |
|---|---|
| Link commit | https://github.com/fptu-se-su26/swp391-su26-ai-audit-project-swp391_se20a11_group-01-1/commit/0a070623 |
| File liên quan | docs/Nguyen Duc Thuong/AI_AUDIT_LOG.md |
| Screenshot |  |
| Kết quả chạy/test | Use Case Diagram và SRS đã được hoàn thiện dựa trên gợi ý của AI |
| Link video demo |  |
| Ghi chú khác |  |

#### 4.6. Nhận xét cá nhân/nhóm

```text
AI giúp định hướng nhanh về phạm vi hệ thống ngay từ đầu, tiết kiệm nhiều thời gian brainstorm. Tuy nhiên, gợi ý của AI còn mang tính chung chung, cần phải đối chiếu kỹ với yêu cầu thực tế của giảng viên và đặc thù nghiệp vụ nhà hàng để điều chỉnh cho phù hợp.
```

---

### Lần sử dụng AI số 2

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 16/06/2026 |
| Công cụ AI | Claude |
| Mục đích sử dụng | Hỗ trợ xây dựng giao diện các trang quản trị Admin (Quản lý tài khoản, Voucher, Đặt bàn, Feedback) bằng ReactJS |
| Phần việc liên quan | Frontend |
| Mức độ sử dụng | Hỗ trợ nhiều |

#### 4.1. Prompt đã sử dụng

```text
Tôi đang xây dựng trang Admin cho hệ thống Restaurant Management System bằng ReactJS. Hãy giúp tôi tạo component AdminAccounts.js có chức năng: hiển thị danh sách tài khoản người dùng, tìm kiếm theo tên/email, lọc theo role (Customer, Staff, Kitchen, Admin), khóa/mở tài khoản và thay đổi role. Sử dụng CSS module tách riêng file AdminAccounts.css.
```

#### 4.2. Kết quả AI gợi ý

```text
Claude đề xuất cấu trúc component gồm:
- State quản lý: accounts, searchTerm, filterRole, loading
- Gọi API GET /api/admin/accounts để lấy danh sách tài khoản
- Hàm handleSearch để lọc theo tên/email phía client
- Hàm handleToggleStatus để gọi API khóa/mở tài khoản
- Hàm handleChangeRole để cập nhật role người dùng
- Render bảng danh sách với các action button tương ứng
- CSS styling cho bảng, badge role, button action
Tương tự, Claude cũng gợi ý cấu trúc cho AdminVouchers.js, AdminReservations.js và AdminFeedback.js.
```

#### 4.3. Phần sinh viên/nhóm đã sử dụng từ AI

```text
Sử dụng cấu trúc state và logic gọi API do Claude đề xuất làm nền tảng cho các component AdminAccounts.js, AdminVouchers.js, AdminReservations.js, AdminFeedback.js. Áp dụng gợi ý về cách tổ chức CSS riêng biệt theo từng trang.
```

#### 4.4. Phần sinh viên/nhóm tự chỉnh sửa hoặc cải tiến

```text
Điều chỉnh lại endpoint API cho khớp với backend Spring Boot thực tế của nhóm. Chỉnh sửa giao diện CSS theo design system chung của toàn project. Bổ sung thêm xử lý lỗi (error handling) và thông báo (toast notification) khi thao tác thành công/thất bại. Kiểm tra lại logic phân quyền để đảm bảo chỉ Admin mới truy cập được các trang này.
```

#### 4.5. Minh chứng

| Loại minh chứng | Nội dung |
|---|---|
| Link commit | https://github.com/fptu-se-su26/swp391-su26-ai-audit-project-swp391_se20a11_group-01-1/commit/5ea4e37b |
| File liên quan | srccode/frontend/src/pages/admin/AdminAccounts.js, AdminVouchers.js, AdminReservations.js, AdminFeedback.js |
| Screenshot |  |
| Kết quả chạy/test | Các trang Admin hiển thị và hoạt động đúng sau khi tích hợp với backend |
| Link video demo |  |
| Ghi chú khác |  |

#### 4.6. Nhận xét cá nhân/nhóm

```text
Sau lần sử dụng Claude này, em nhận thấy AI rất hữu ích trong việc gợi ý cấu trúc component React có thể tái sử dụng cho nhiều trang Admin khác nhau. Tuy nhiên, AI chưa biết được endpoint API cụ thể của backend nhóm, nên phần tích hợp vẫn cần tự làm. Quan trọng là phải kiểm tra kỹ logic phân quyền trước khi deploy.
```

---

### Lần sử dụng AI số 3

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 22/06/2026 |
| Công cụ AI | ChatGPT |
| Mục đích sử dụng | Cải tiến quy trình nghiệp vụ sau phản biện: giải quyết vấn đề nhiều người quét QR và vai trò Kitchen Staff không hợp lý |
| Phần việc liên quan | Requirement |
| Mức độ sử dụng | Hỗ trợ ý tưởng |

#### 4.1. Prompt đã sử dụng

```text
Giảng viên góp ý rằng Kitchen Staff không hợp lý và nhiều người cùng quét QR sẽ xử lý như thế nào?
```

#### 4.2. Kết quả AI gợi ý

```text
ChatGPT đề xuất giải pháp:
- Dining Session: mỗi bàn có một phiên ăn uống riêng
- Staff xác nhận trước khi mở phiên gọi món, tránh nhiều người quét QR tùy tiện
- Shared Cart: nhiều khách cùng bàn có thể cùng thêm món vào giỏ hàng chung
- Reservation Check-in: khách check-in qua đặt bàn trước, QR chỉ kích hoạt khi Staff xác nhận
```

#### 4.3. Phần sinh viên/nhóm đã sử dụng từ AI

```text
Nhóm sử dụng các ý tưởng Dining Session và Staff-confirmed QR để chỉnh sửa lại quy trình nghiệp vụ của hệ thống sau buổi phản biện.
```

#### 4.4. Phần sinh viên/nhóm tự chỉnh sửa hoặc cải tiến

```text
Nhóm tiếp tục thảo luận nội bộ và đơn giản hoá luồng so với đề xuất của AI: bỏ Shared Cart phức tạp, giữ lại luồng Staff xác nhận QR và Reservation Check-in là cốt lõi. Cập nhật lại Use Case Diagram và SRS cho phù hợp với phạm vi triển khai thực tế trong thời gian còn lại.
```

#### 4.5. Minh chứng

| Loại minh chứng | Nội dung |
|---|---|
| Link commit | https://github.com/fptu-se-su26/swp391-su26-ai-audit-project-swp391_se20a11_group-01-1/commit/5ea4e37b |
| File liên quan | docs/Nguyen Duc Thuong/AI_AUDIT_LOG.md |
| Screenshot |  |
| Kết quả chạy/test | Quy trình nghiệp vụ được cập nhật và trình bày lại với giảng viên |
| Link video demo |  |
| Ghi chú khác |  |

#### 4.6. Nhận xét cá nhân/nhóm

```text
AI rất hữu ích khi cần brainstorm nhanh các giải pháp sau khi nhận feedback từ giảng viên. Tuy nhiên, không phải giải pháp nào AI đề xuất cũng khả thi trong phạm vi project sinh viên — cần chọn lọc và đơn giản hoá cho phù hợp với thời gian và kỹ năng của nhóm.
```

---

## 5. Bảng tổng hợp mức độ sử dụng AI

Đánh dấu mức độ AI hỗ trợ ở từng hạng mục.

| Hạng mục | Không dùng AI | AI hỗ trợ ít | AI hỗ trợ nhiều | AI sinh chính | Ghi chú |
|---|:---:|:---:|:---:|:---:|---|
| Phân tích yêu cầu |  |  |  |  |  |
| Viết user story/use case |  |  |  |  |  |
| Thiết kế database |  |  |  |  |  |
| Thiết kế kiến trúc hệ thống |  |  |  |  |  |
| Thiết kế giao diện |  |  |  |  |  |
| Code frontend |  |  |  |  |  |
| Code backend |  |  |  |  |  |
| Debug lỗi |  |  |  |  |  |
| Viết test case |  |  |  |  |  |
| Kiểm thử sản phẩm |  |  |  |  |  |
| Tối ưu code |  |  |  |  |  |
| Viết báo cáo |  |  |  |  |  |
| Làm slide thuyết trình |  |  |  |  |  |

---

## 6. Các lỗi hoặc hạn chế từ AI

Ghi lại các trường hợp AI trả lời sai, thiếu, chưa phù hợp hoặc sinh code không chạy.

| STT | Lỗi/hạn chế từ AI | Cách phát hiện | Cách xử lý/cải tiến |
|---:|---|---|---|
| 1 |  |  |  |
| 2 |  |  |  |
| 3 |  |  |  |

---

## 7. Kiểm chứng kết quả AI

Mô tả cách sinh viên/nhóm kiểm tra lại kết quả do AI gợi ý.

Có thể bao gồm:

- Chạy thử chương trình
- Viết test case
- So sánh với yêu cầu đề bài
- Kiểm tra output
- Đối chiếu tài liệu môn học
- Hỏi lại giảng viên
- Review cùng thành viên nhóm
- Kiểm tra lỗi bảo mật
- Kiểm tra bằng dữ liệu mẫu
- So sánh trước và sau khi dùng AI

### Nội dung kiểm chứng

```text
Viết tại đây...
```

---

## 8. Đóng góp cá nhân hoặc đóng góp nhóm

### 8.1. Đối với bài cá nhân

Mô tả phần sinh viên tự làm, phần AI hỗ trợ và phần đã tự cải tiến.

```text
Viết tại đây...
```

### 8.2. Đối với bài nhóm

| Thành viên | MSSV | Nhiệm vụ chính | Có sử dụng AI không? | Minh chứng đóng góp |
|---|---|---|---|---|
|  |  |  | Có / Không |  |
|  |  |  | Có / Không |  |
|  |  |  | Có / Không |  |
|  |  |  | Có / Không |  |

---

## 9. Reflection cuối bài

### 9.1. AI đã hỗ trợ em/nhóm ở điểm nào?

```text
Viết tại đây...
```

### 9.2. Phần nào em/nhóm không sử dụng theo gợi ý của AI? Vì sao?

```text
Viết tại đây...
```

### 9.3. Em/nhóm đã kiểm tra tính đúng đắn của kết quả AI như thế nào?

```text
Viết tại đây...
```

### 9.4. Nếu không có AI, phần nào sẽ khó khăn nhất?

```text
Viết tại đây...
```

### 9.5. Sau bài tập/project này, em/nhóm học được gì về môn học?

```text
Viết tại đây...
```

### 9.6. Sau bài tập/project này, em/nhóm học được gì về cách sử dụng AI có trách nhiệm?

```text
Viết tại đây...
```

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
