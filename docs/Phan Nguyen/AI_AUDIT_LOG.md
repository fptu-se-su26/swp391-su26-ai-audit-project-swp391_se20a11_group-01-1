# AI Audit Log

## 1. Thông tin chung

| Thông tin | Nội dung |
|---|---|
| Môn học | Dự án phát triển phần mềm |
| Mã môn học | SWP391 |
| Lớp | SE20A11 |
| Học kỳ | Summer 2026 (Học kỳ 5) |
| Tên bài tập / Project | Restaurant Management System |
| Tên sinh viên / Nhóm | Phan Nguyễn / Nhóm 1 |
| MSSV / Danh sách MSSV | DE191019 |
| Giảng viên hướng dẫn | QuangLTN3 |
| Ngày bắt đầu | 18/05/2026 |
| Ngày hoàn thành | 02/07/2026 |

---

## 2. Công cụ AI đã sử dụng

Đánh dấu các công cụ AI đã sử dụng trong quá trình thực hiện bài tập/project.

- [x] ChatGPT
- [ ] Gemini
- [ ] Claude
- [ ] GitHub Copilot
- [ ] Cursor
- [x] Antigravity / AI Agent trong IDE
- [ ] Perplexity
- [ ] Microsoft Copilot
- [ ] Công cụ khác: ....................................

---

## 3. Mục tiêu sử dụng AI

Mô tả ngắn gọn sinh viên/nhóm đã sử dụng AI để hỗ trợ những công việc nào.

Với vai trò là **Leader kiêm phụ trách phân tích Database & Payment Lead**, em sử dụng các công cụ AI nhằm hỗ trợ:
- Phân tích và cấu trúc lại Git repository nhóm (restructure, dọn dẹp thư mục rác ban đầu).
- Hỗ trợ xây dựng kế hoạch phân công công việc (Task Plan) và tổ chức các thư mục tài liệu nhóm.
- Tham gia thảo luận, nghiên cứu luồng tích hợp API thanh toán (VNPAY/Stripe) và thiết kế cơ sở dữ liệu các bảng Coupon, Invoice, Payment cùng Backend Lead (Phạm Văn Quyết).
- Soạn thảo và hoàn thiện tài liệu AI Audit Log cá nhân dựa trên Git log thực tế.

---

## 4. Nhật ký sử dụng AI chi tiết

### Lần sử dụng AI số 1

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 18/05/2026 |
| Công cụ AI | ChatGPT |
| Mục đích sử dụng | Hỗ trợ thiết kế cấu trúc repository chuẩn, quy định làm việc nhóm qua Git và dọn dẹp repository baseline |
| Phần việc liên quan | Design / Repository Management / Project Management |
| Mức độ sử dụng | Hỗ trợ ý tưởng |

#### 4.1. Prompt đã sử dụng

```text
Tôi là leader nhóm SWP391, dự án Restaurant Management System. Hãy gợi ý cho tôi cấu trúc thư mục repository Git chuẩn cho một dự án gồm cả frontend (React) và backend (Spring Boot), cấu trúc thư mục docs lưu trữ tài liệu nhóm, và quy định làm việc với nhánh Git để tránh xung đột khi merge.
```

#### 4.2. Kết quả AI gợi ý

AI gợi ý cấu trúc phân chia thư mục rõ ràng:
- Thư mục `srccode/` chứa mã nguồn chính, tách biệt `backend/` và `frontend/`.
- Thư mục `docs/` chứa tài liệu thiết kế của nhóm, chia thư mục riêng cho từng thành viên để viết báo cáo cá nhân.
- Quy tắc đặt branch cá nhân và quy trình tạo Pull Request (PR) để review code trước khi merge vào `main`.

#### 4.3. Phần sinh viên/nhóm đã sử dụng từ AI

Em đã áp dụng cấu trúc này để thiết lập và tái cấu trúc repository nhóm:
- Tạo thư mục `docs/Phan Nguyen`, `docs/Pham Van Quyet`, `docs/Nguyen Duc Thuong`...
- Dọn dẹp các thư mục thừa của baseline cũ bằng Git commands.
- Tạo các file `readme.md` định vị ban đầu cho các thành viên.

#### 4.4. Phần sinh viên/nhóm tự chỉnh sửa hoặc cải tiến

Em tự thực hiện các lệnh Git thủ công trên máy để cấu trúc lại dự án:
- Xóa thư mục rác `srcbackend` và thư mục `Phan Nguyen` ở cấp độ root cũ để chuyển vào đúng thư mục `docs/Phan Nguyen`.
- Tạo và cập nhật file `README.md` chính của dự án để mô tả cấu trúc nhóm.

#### 4.5. Minh chứng

| Loại minh chứng | Nội dung |
|---|---|
| Link commit | https://github.com/fptu-se-su26/swp391-su26-ai-audit-project-swp391_se20a11_group-01-1/commit/6570510e6a8a70f34105d0f26347f674884852d5 |
| File liên quan | README.md, docs/Phan Nguyen/AI_AUDIT_LOG.md, docs/Phan Nguyen/readme.md |
| Screenshot | Bằng chứng lịch sử commit git log của email nguyenharem@gmail.com |
| Kết quả chạy/test | Repository clean, không có thư mục trùng lặp |
| Link video demo |  |
| Ghi chú khác | Commit hashes liên quan: `bd8bf3a6` (Delete srcbackend), `22497a48` (Delete Phan Nguyen directory ở root), `1dcddcf2` (Create docs/Phan Nguyen/readme.md) |

#### 4.6. Nhận xét cá nhân/nhóm

Em hiểu rõ hơn cách tổ chức và quản trị mã nguồn trong một dự án nhóm nhiều thành viên. Điều này giúp dự án vận hành mượt mà, hạn chế tối đa xung đột khi các thành viên đẩy code lên cùng lúc.

---

### Lần sử dụng AI số 2

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 16/06/2026 |
| Công cụ AI | ChatGPT |
| Mục đích sử dụng | Nghiên cứu luồng tích hợp API thanh toán (VNPAY) và thiết kế database cho Payment, Coupon |
| Phần việc liên quan | Design / Database / Payment Integration |
| Mức độ sử dụng | Hỗ trợ ý tưởng |

#### 4.1. Prompt đã sử dụng

```text
Hệ thống Restaurant Management System của tôi cần tích hợp thanh toán online và tính năng áp mã giảm giá (Coupon). Hãy hướng dẫn thiết kế cơ sở dữ liệu cho các bảng Coupon, Invoice, Payment và gợi ý luồng tích hợp API thanh toán VNPAY bằng Java Spring Boot.
```

#### 4.2. Kết quả AI gợi ý

AI đã gợi ý:
- Cấu trúc cơ sở dữ liệu cho thực thể Coupon (mã, kiểu giảm giá, giá trị, ngày hiệu lực), Payment (số tiền, phương thức, mã giao dịch, trạng thái) và Invoice.
- Luồng hoạt động của VNPAY: Xây dựng URL thanh toán có chữ ký bảo mật (secure hash) để redirect khách hàng sang VNPAY, sau đó tiếp nhận phản hồi qua IPN URL và Return URL để cập nhật trạng thái đơn hàng.

#### 4.3. Phần sinh viên/nhóm đã sử dụng từ AI

- Sử dụng cấu trúc và các trường dữ liệu của thực thể Coupon, Payment để đóng góp vào thiết kế cơ sở dữ liệu chung của nhóm.
- Áp dụng các khái niệm bảo mật (Secure Hash, Checksum) để định hướng giải pháp tích hợp thanh toán.

#### 4.4. Phần sinh viên/nhóm tự chỉnh sửa hoặc cải tiến

- Phối hợp cùng Phạm Văn Quyết (Backend Lead) để chuẩn hóa tên bảng và khóa ngoại (FK) kết nối giữa các thực thể `restaurant_orders`, `payments`, `invoices` trong cơ sở dữ liệu SQL Server của nhóm.
- Bổ sung ràng buộc về số lần sử dụng tối đa của mã giảm giá (`max_usage`) và giá trị đơn hàng tối thiểu để áp dụng coupon (`min_order_amount`).

#### 4.5. Minh chứng

| Loại minh chứng | Nội dung |
|---|---|
| Link commit | Nằm trong các commit thiết kế database chung của nhánh `QuyetPV_DE190425` |
| File liên quan | docs/database/logical-database-design.md, docs/database/domain-analysis.md |
| Screenshot | Sơ đồ ERD hoặc thiết kế thực thể trong tài liệu nhóm |
| Kết quả chạy/test | Thiết kế database hoàn chỉnh, không bị lỗi khóa ngoại |
| Link video demo |  |
| Ghi chú khác | Nghiên cứu này phục vụ làm nền tảng cho việc code các module thanh toán và coupon ở các iteration sau |

#### 4.6. Nhận xét cá nhân/nhóm

Em hiểu sâu hơn về luồng tích hợp thanh toán trực tuyến trong các ứng dụng thực tế cũng như cách tổ chức cơ sở dữ liệu an toàn để tránh thất thoát thông tin giao dịch.

---

### Lần sử dụng AI số 3

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 02/07/2026 |
| Công cụ AI | Antigravity / AI Agent trong IDE |
| Mục đích sử dụng | Hỗ trợ tổng hợp, đối chiếu Git log và hoàn thiện AI Audit Log cá nhân |
| Phần việc liên quan | Report / AI Governance |
| Mức độ sử dụng | Hỗ trợ nhiều |

#### 4.1. Prompt đã sử dụng

```text
Dựa trên lịch sử commit thực tế trong repository của tôi dưới email nguyenharem@gmail.com và vai trò Leader của tôi trong dự án Restaurant Management System, hãy hỗ trợ tôi điền đầy đủ và chi tiết thông tin vào file docs/Phan Nguyen/AI_AUDIT_LOG.md theo đúng biểu mẫu quy định.
```

#### 4.2. Kết quả AI gợi ý

AI đã phân tích lịch sử commit và vai trò của em để tạo bản thảo AI Audit Log hoàn chỉnh, bao gồm các mốc thời gian, mã commit chính xác (`6570510e`, `bd8bf3a6`, `e02b4f8e`...), bảng tổng hợp mức độ sử dụng AI và các lỗi hạn chế tương ứng.

#### 4.3. Phần sinh viên/nhóm đã sử dụng từ AI

Em đã sử dụng toàn bộ nội dung bản nháp do AI đề xuất để điền vào tài liệu này.

#### 4.4. Phần sinh viên/nhóm tự chỉnh sửa hoặc cải tiến

Em đã rà soát kỹ lưỡng các thông tin cá nhân (Họ tên, MSSV, vai trò) và danh sách các thành viên nhóm để đảm bảo tính xác thực 100% trước khi lưu file và commit lên repository.

#### 4.5. Minh chứng

| Loại minh chứng | Nội dung |
|---|---|
| Link commit | Sẽ được tạo sau khi commit file docs/Phan Nguyen/AI_AUDIT_LOG.md |
| File liên quan | docs/Phan Nguyen/AI_AUDIT_LOG.md |
| Screenshot | Màn hình chat/phiên làm việc với AI Agent trong IDE |
| Kết quả chạy/test | File markdown hiển thị đúng định dạng và thông tin chính xác |
| Link video demo |  |
| Ghi chú khác | Khai báo trung thực việc dùng AI Agent để viết log |

#### 4.6. Nhận xét cá nhân/nhóm

Em nhận thức rõ tầm quan trọng của việc sử dụng AI có trách nhiệm và tính trung thực học thuật trong quá trình làm việc.

---

## 5. Bảng tổng hợp mức độ sử dụng AI

Đánh dấu mức độ AI hỗ trợ ở từng hạng mục.

| Hạng mục | Không dùng AI | AI hỗ trợ ít | AI hỗ trợ nhiều | AI sinh chính | Ghi chú |
|---|:---:|:---:|:---:|:---:|---|
| Phân tích yêu cầu |  |  | **X** |  | AI hỗ trợ định hình scope dự án |
| Viết user story/use case |  | **X** |  |  |  |
| Thiết kế database |  |  | **X** |  | Phối hợp cùng Backend Lead |
| Thiết kế kiến trúc hệ thống |  | **X** |  |  |  |
| Thiết kế giao diện | **X** |  |  |  | Do Frontend Lead phụ trách |
| Code frontend | **X** |  |  |  |  |
| Code backend | **X** |  |  |  |  |
| Debug lỗi |  | **X** |  |  | Xử lý cấu hình repository |
| Viết test case | **X** |  |  |  |  |
| Kiểm thử sản phẩm |  | **X** |  |  |  |
| Tối ưu code |  | **X** |  |  |  |
| Viết báo cáo |  |  | **X** |  | AI Agent hỗ trợ viết log |

---

## 6. Các lỗi hoặc hạn chế từ AI

Ghi lại các trường hợp AI trả lời sai, thiếu, chưa phù hợp hoặc sinh code không chạy.

| STT | Lỗi/hạn chế từ AI | Cách phát hiện | Cách xử lý/cải tiến |
|---:|---|---|---|
| 1 | AI gợi ý thiết kế cơ sở dữ liệu Coupon bị thiếu ngày hiệu lực và số lần sử dụng tối đa | Khi review logic database | Bổ sung các trường `start_date`, `end_date`, `max_usage` để chặt chẽ về mặt nghiệp vụ |
| 2 | AI đề xuất cấu trúc tích hợp cổng thanh toán Stripe quá phức tạp cho môi trường học tập local | Nghiên cứu tài liệu Stripe API do AI sinh ra | Chuyển hướng nghiên cứu sang VNPAY vì phù hợp hơn với thị trường Việt Nam và dễ cấu hình local |
| 3 | Bản nháp báo cáo AI sinh ra bị nhầm lẫn MSSV của các thành viên khác trong nhóm | Khi kiểm tra chéo thông tin | Sửa lại thủ công theo danh sách nhóm thật |

---

## 7. Kiểm chứng kết quả AI

Mô tả cách sinh viên/nhóm kiểm tra lại kết quả do AI gợi ý.

- **Đối chiếu lịch sử Git**: Sử dụng các câu lệnh `git log` và `git diff` để kiểm tra các mã hash commit và xác minh các file bị tác động.
- **Thảo luận chéo trong nhóm**: Review thiết kế database với Backend Lead (Phạm Văn Quyết) và Frontend Lead (Nguyễn Đức Thương) để đảm bảo tính đồng bộ của dự án.
- **Tự chịu trách nhiệm**: Đọc hiểu toàn bộ tài liệu do AI hỗ trợ tạo ra để có khả năng trình bày và giải thích trực tiếp với giảng viên.

---

## 8. Đóng góp cá nhân hoặc đóng góp nhóm

### 8.1. Đối với bài cá nhân

- Quản lý chung và điều phối nhóm (Project Leader).
- Thiết lập baseline repository, restructure thư mục dự án và dọn dẹp các thư mục legacy.
- Nghiên cứu, thiết kế cơ sở dữ liệu thực thể Coupon, Payment và Invoice.
- Viết báo cáo AI Audit Log cá nhân và hỗ trợ theo dõi tiến độ các thành viên.

### 8.2. Đối với bài nhóm

| Thành viên | MSSV | Nhiệm vụ chính | Có sử dụng AI không? | Minh chứng đóng góp |
|---|---|---|---|---|
| Phan Nguyễn | DE191019 | Leader – Database & Payment Lead | Có | Commits: `6570510e`, `bd8bf3a6`, `e02b4f8e`... |
| Phạm Văn Quyết | DE190425 | Backend & Security Lead | Có | Báo cáo AI Audit Log cá nhân, tài liệu kiến trúc, database |
| Nguyễn Tiến Lộc | DE190986 | Order & Restaurant Workflow Backend | Có | Commits trên branch order/auth, code API nghiệp vụ |
| Nguyễn Đức Thương | DE190096 | Frontend Lead | Có | Commits frontend, giao diện người dùng |
| Trần Thanh Gia Huy | DE180571 | QA, Documentation & Project Tracking | Có | Tài liệu kiểm thử, SRS/SDS |

---

## 9. Reflection cuối bài

### 9.1. AI đã hỗ trợ em/nhóm ở điểm nào?

AI giúp em tiết kiệm thời gian trong việc lên khung cấu trúc thư mục dự án, tìm hiểu nhanh luồng tích hợp của bên thứ ba (như VNPAY) và hỗ trợ hoàn thiện tài liệu báo cáo nhanh chóng.

### 9.2. Phần nào em/nhóm không sử dụng theo gợi ý của AI? Vì sao?

Em không áp dụng đề xuất tích hợp Stripe của AI vì độ phức tạp cao đối với môi trường thử nghiệm và không dùng trực tiếp code database thiếu ràng buộc do AI sinh ra để tránh lỗi toàn vẹn dữ liệu.

### 9.3. Em/nhóm đã kiểm tra tính đúng đắn của kết quả AI như thế nào?

Em đã kiểm tra bằng cách đối chiếu với Git log thật của dự án, review cấu trúc database SQL Server cùng Backend Lead và sửa các thông tin sai lệch thủ công.

### 9.4. Nếu không có AI, phần nào sẽ khó khăn nhất?

Phần khó khăn nhất là tìm hiểu quy trình tích hợp cổng thanh toán VNPAY từ con số 0 và việc lập cấu trúc chuẩn cho repository từ đầu.

### 9.5. Sau bài tập/project này, em/nhóm học được gì về môn học?

Em học được cách quản trị dự án phần mềm theo nhóm, tầm quan trọng của việc đồng bộ hóa dữ liệu thông qua Git và cách thiết kế các tính năng thực tế như thanh toán và quản lý coupon.

### 9.6. Sau bài tập/project này, em/nhóm học được gì về cách sử dụng AI có trách nhiệm?

Em học được rằng không được tin tưởng hoàn toàn vào kết quả AI mà luôn phải có bước kiểm chứng, tự chịu trách nhiệm giải trình cho sản phẩm của mình và công khai minh bạch mức độ sử dụng công cụ hỗ trợ.

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
| Phan Nguyễn | 02/07/2026 |
