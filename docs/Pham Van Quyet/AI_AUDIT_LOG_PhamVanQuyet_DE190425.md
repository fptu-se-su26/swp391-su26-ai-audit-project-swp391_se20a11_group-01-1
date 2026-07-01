# AI AUDIT LOG – SWP391

> **Phạm vi:** Nhật ký cá nhân của **Phạm Văn Quyết (DE190425)** trong dự án nhóm SWP391.
---

## 1. Thông tin chung

| Thông tin | Nội dung |
|---|---|
| Môn học | Software Development Project |
| Mã môn học | SWP391 |
| Lớp | SE20A11 |
| Học kỳ | Summer 2026 |
| Tên Project | Restaurant Management System |
| Nhóm | Group 01 |
| Sinh viên thực hiện | Phạm Văn Quyết |
| MSSV | DE190425 |
| Git username | `quyetpham2k5` |
| Vai trò hiện tại | Member – Backend & Security Lead |
| Giảng viên hướng dẫn |QuangLTN3|
| Ngày bắt đầu ghi nhận | 18/05/2026|
| Ngày cập nhật gần nhất | 02/07/2026 |
| Repository baseline đã đối chiếu | `main`, HEAD `3188f436` – `Update AI_AUDIT_LOG.md` |
| Nhánh cá nhân | `origin/QuyetPV_DE190425` |

### Thành viên nhóm

| STT | MSSV | Họ và tên | Git username | Vai trò hiện tại |
|---:|---|---|---|---|
| 1 | DE191019 | Phan Nguyễn | `Nguyendeptraibodoi` | Leader – Database & Payment Lead |
| 2 | DE190425 | Phạm Văn Quyết | `quyetpham2k5` | Backend & Security Lead |
| 3 | DE190986 | Nguyễn Tiến Lộc | `tienloc1234` | Order & Restaurant Workflow Backend |
| 4 | DE190096 | Nguyễn Đức Thương | `thuong1703n` | Frontend Lead |
| 5 | DE180571 | Trần Thanh Gia Huy | `huyttde12` | QA, Documentation & Project Tracking |

---

## 2. Công cụ AI đã sử dụng

- [x] ChatGPT
- [ ] Gemini
- [ ] Claude
- [ ] GitHub Copilot
- [ ] Cursor
- [x] Antigravity / AI Agent trong IDE
- [ ] Perplexity
- [ ] Microsoft Copilot
- [x] Công cụ khác: Git và Git log được dùng để kiểm chứng đầu ra AI, nhưng không phải công cụ AI.

Em chỉ đánh dấu những công cụ có bằng chứng đã sử dụng trong phạm vi audit này.

---

## 3. Mục tiêu sử dụng AI

Em sử dụng AI như công cụ hỗ trợ đọc dự án, phân tích và lập kế hoạch, không xem đầu ra AI là kết quả cuối cùng. Các mục tiêu chính:

- Đọc và lập bản đồ cấu trúc repository sau khi clone.
- Phân tích sự khác nhau giữa `main`, nhánh cá nhân và nhánh chức năng.
- Xác định code cũ, code trùng, cấu hình chưa an toàn và rủi ro merge.
- Gợi ý kiến trúc backend, frontend, API convention và quy chuẩn tài liệu.
- Phân tích domain và hỗ trợ thiết kế database.
- Chia task nhỏ cho AI Agent, dừng sau từng task để em kiểm tra.
- Hỗ trợ lập báo cáo hiện trạng và kế hoạch phát triển một tuần.
- Hỗ trợ soạn AI Audit Log, sau đó em kiểm tra bằng Git log và tài liệu thật.

AI giúp giảm thời gian đọc một repository lớn, còn quyết định giữ/xóa code, cấu trúc database, phân công và trạng thái hoàn thành đều do em kiểm tra lại.

---

## 4. Nhật ký sử dụng AI chi tiết

### Lần sử dụng AI số 1 – Repository audit ban đầu

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 15/06/2026 |
| Công cụ AI | ChatGPT và Antigravity AI Agent |
| Mục đích | Đọc toàn bộ source, xác định cấu trúc, công nghệ, module hiện có và vấn đề của repository |
| Phần việc | Requirement / Design / Backend / Frontend / Database / Debug |
| Mức độ sử dụng | Hỗ trợ nhiều |

#### 4.1. Prompt đã sử dụng

```text
Tôi có một dự án nhóm môn SWP đang làm. Tôi vừa clone nhánh main từ GitHub về để xem và làm tiếp.
Đây là toàn bộ source code tôi đã zip lại. Hãy mở và đọc hiểu dự án sâu nhất có thể.

Yêu cầu:
1. Không sửa code ngay.
2. Đọc cấu trúc thư mục, công nghệ, luồng chạy, database và các module hiện có.
3. Kiểm tra Git status, branch hiện tại và lịch sử commit.
4. Chỉ ra phần đã hoàn thành, phần còn thiếu, code trùng lặp, lỗi cấu hình và rủi ro.
5. Viết repository audit có dẫn chứng theo file và branch.
6. Không tự push code.
```

#### 4.2. Kết quả AI gợi ý

AI tạo bản phân tích repository, nhận diện cấu trúc backend/frontend, tình trạng branch và các điểm cần kiểm tra. AI đề xuất tách giai đoạn “đọc và audit” khỏi giai đoạn “sửa code”.

#### 4.3. Phần em sử dụng

- Khung kiểm tra repository theo source, cấu hình, database, security, test và tài liệu.
- Danh sách lệnh kiểm tra branch/working tree.
- Cấu trúc báo cáo audit theo mức độ ưu tiên.
- Đề xuất lưu quyết định kỹ thuật trong `docs/`.

#### 4.4. Phần em tự chỉnh sửa/cải tiến

- Yêu cầu AI không sửa và không push.
- Xác nhận những file bị xóa là do em chủ động xóa.
- Loại bỏ kết luận không có file hoặc Git log làm bằng chứng.
- Chỉ giữ vấn đề có thể đối chiếu trong source.

#### 4.5. Minh chứng

| Loại | Nội dung |
|---|---|
| Commit | `ebbe85b0` – P0-01 Repository audit |
| Branch | `QuyetPV_DE190425` |
| File | `docs/agent/repository-audit.md` |
| Kiểm tra | Branch, working tree và cấu trúc repository |
| Screenshot | Cần bổ sung ảnh prompt, response và terminal Git trước khi nộp |
| Ghi chú | Không có thao tác push tự động |

#### 4.6. Nhận xét cá nhân

AI có thể đọc nhanh nhiều file nhưng không tự biết thay đổi nào là chủ ý của lập trình viên. Việc em xác nhận file đã xóa giúp tránh khôi phục nhầm code cũ.

---

### Lần sử dụng AI số 2 – Xác lập baseline sau khi loại bỏ source legacy

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 15/06/2026 |
| Công cụ AI | Antigravity AI Agent |
| Mục đích | Xác nhận trạng thái repository và tạo task plan tiếp theo |
| Phần việc | Design / Project Management / Repository Management |
| Mức độ sử dụng | Hỗ trợ một phần |

#### 4.1. Prompt đã sử dụng

```text
Các file đã xóa là tôi xóa, hãy làm tiếp từ trạng thái hiện tại.
Không khôi phục source cũ. Kiểm tra git diff, xác nhận baseline, ghi lý do loại bỏ legacy,
lập task plan cho các bước tiếp theo, dừng sau task hiện tại và không push.
```

#### 4.2. Kết quả AI gợi ý

AI ghi nhận 127 file legacy bị loại bỏ theo quyết định của em, tạo baseline và đề xuất thứ tự phát triển: kiến trúc, database, migration, backend, frontend, test và tài liệu.

#### 4.3. Phần em sử dụng

- Cách mô tả baseline.
- Cách chia công việc thành task nhỏ có đầu vào, đầu ra, tiêu chí dừng.
- Quy tắc không trộn quá nhiều thay đổi vào một commit.

#### 4.4. Phần em tự chỉnh sửa/cải tiến

- Kiểm tra lại toàn bộ phần xóa bằng `git diff`.
- Giữ nguyên nguyên tắc không push để tự review.
- Điều chỉnh task plan theo scope SWP391 của nhóm.

#### 4.5. Minh chứng

| Loại | Nội dung |
|---|---|
| Commit | `275e0cb0` – BASELINE-01 Remove legacy source + task plan |
| Branch | `QuyetPV_DE190425` |
| File | Các file legacy bị xóa và task plan trong commit |
| Kiểm tra | `git diff` và phạm vi file |
| Screenshot | Cần bổ sung ảnh diff/stat và commit |
| Ghi chú | Quyết định xóa file thuộc về em |

#### 4.6. Nhận xét cá nhân

Commit baseline giúp em có điểm quay lại và phân biệt lỗi cũ với lỗi phát sinh sau này.

---

### Lần sử dụng AI số 3 – Thiết kế kiến trúc hệ thống

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 15–16/06/2026 |
| Công cụ AI | ChatGPT và Antigravity AI Agent |
| Mục đích | Xây dựng tài liệu kiến trúc trước khi code |
| Phần việc | Design / Backend / Frontend / API / Documentation |
| Mức độ sử dụng | Hỗ trợ nhiều |

#### 4.1. Prompt đã sử dụng

```text
Hãy thiết kế kiến trúc tổng thể cho Restaurant Management System từ trạng thái hiện tại.
Phù hợp SWP391, nhóm 5 người, Java 21, Spring Boot 3.5, JPA, Security, Validation.
Có JWT, role authorization, error format, transaction strategy.
Frontend có page, component, service, routing, state management.
Viết API conventions, documentation standards và Git workflow.
Không code nghiệp vụ ở task này.
```

#### 4.2. Kết quả AI gợi ý

AI đề xuất kiến trúc nhiều tầng, package backend, cấu trúc frontend, format API, security, transaction và quy trình tài liệu/Git.

#### 4.3. Phần em sử dụng

- `docs/architecture/system-architecture.md`
- `docs/architecture/backend-architecture.md`
- `docs/architecture/frontend-architecture.md`
- `docs/architecture/api-conventions.md`
- `docs/architecture/documentation-standards.md`

#### 4.4. Phần em tự chỉnh sửa/cải tiến

- Điều chỉnh package, module và role theo domain nhà hàng.
- Bổ sung JWT, transaction, response/error convention.
- Bỏ thành phần vượt phạm vi iteration.
- Kiểm tra phù hợp deliverable SWP391.

#### 4.5. Minh chứng

| Loại | Nội dung |
|---|---|
| Commit | `0743b2a8` – P0-02 System architecture + SWP391 alignment |
| Branch | `QuyetPV_DE190425` |
| File | 5 tài liệu trong `docs/architecture/` |
| Kiểm tra | Review tài liệu và commit diff |
| Screenshot | Cần bổ sung ảnh prompt, response và diff |
| Ghi chú | Tài liệu thiết kế không đồng nghĩa chức năng đã code xong |

#### 4.6. Nhận xét cá nhân

AI tạo khung nhanh nhưng dễ chung chung. Em phải điều chỉnh theo domain, công nghệ và khả năng thực hiện của nhóm.

---

### Lần sử dụng AI số 4 – Phân tích domain và thiết kế database

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 16–17/06/2026 |
| Công cụ AI | ChatGPT và Antigravity AI Agent |
| Mục đích | Phân tích entity, business rule, khóa và quan hệ |
| Phần việc | Requirement / Design / Database |
| Mức độ sử dụng | Hỗ trợ nhiều |

#### 4.1. Prompt đã sử dụng

```text
Dựa trên yêu cầu Restaurant Management System, hãy thiết kế database theo task nhỏ:
1. Domain analysis và data dictionary, chưa tạo SQL.
2. Logical design: PK, FK, cardinality và business constraints.
3. Physical design/schema dùng cho migration.
Phải xử lý unique constraint, trạng thái, tiền tệ, thời gian, concurrency và audit fields.
Dừng sau mỗi task để tôi review.
```

#### 4.2. Kết quả AI gợi ý

AI gợi ý 18 entity, các enum trạng thái, quan hệ User/Role/Food/Category/Table/Reservation/Order/OrderItem/Coupon/Payment/Invoice và các bảng hỗ trợ; đồng thời gợi ý constraint, versioning và thứ tự migration.

#### 4.3. Phần em sử dụng

- Danh sách domain entity và data dictionary.
- Logical relationship.
- Tên bảng `restaurant_orders`.
- Kiểu tiền tệ/thời gian thống nhất.
- Thứ tự migration.

#### 4.4. Phần em tự chỉnh sửa/cải tiến

- Điều chỉnh entity/field theo scope.
- Bổ sung lifecycle refresh token.
- Bổ sung unique constraint cho token hash và payment/invoice.
- Sửa thứ tự tạo bảng để tránh lỗi FK.
- Không chấp nhận schema trước khi review constraint.

#### 4.5. Minh chứng

| Loại | Nội dung |
|---|---|
| Commit 1 | `9688ceda` – DB-01A Domain analysis & data dictionary |
| Commit 2 | `8e057c24` – DB-01B Logical database design |
| File | `docs/database/domain-analysis.md` |
| File | `docs/database/data-dictionary.md` |
| File | `docs/database/logical-database-design.md` |
| File chuẩn bị | `docs/database/physical-database-design.md`, `docs/database/schema.sql` |
| Ghi chú | Không ghi hash DB-01C vì chưa có bằng chứng hash chắc chắn |

#### 4.6. Nhận xét cá nhân

Phần khó không phải để AI liệt kê bảng, mà là kiểm tra business rule, constraint và concurrency. Mô hình nhìn hợp lý vẫn có thể sai nghiệp vụ.

---

### Lần sử dụng AI số 5 – Đối chiếu `main` và lập báo cáo hiện trạng

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 01/07/2026 |
| Công cụ AI | ChatGPT |
| Mục đích | Đánh giá khả năng dùng `main` làm nền tảng |
| Phần việc | Repository Audit / Debug / Security / Report / Planning |
| Mức độ sử dụng | Hỗ trợ nhiều |

#### 4.1. Prompt đã sử dụng

```text
Bây giờ nhóm tôi muốn dùng nền tảng từ main làm lên.
Hãy viết báo cáo tình trạng hiện tại, các lỗi và thiếu sót,
sau đó viết kế hoạch phát triển và sửa lỗi trong một tuần cho 5 người.
Mỗi task phải nói làm gì, làm thế nào và kết quả ra sao.
Phải dựa trên source code và Git log, không tự đoán phần đã hoàn thành.
```

#### 4.2. Kết quả AI gợi ý

AI phân tích cấu trúc song song, database config, security, mock data, thiếu workflow end-to-end, thiếu test/tài liệu và tạo kế hoạch 7 ngày.

#### 4.3. Phần em sử dụng

- Phân loại lỗi Blocker/Critical/High/Medium.
- Kế hoạch 7 ngày và Definition of Done.
- Thứ tự merge và nguyên tắc không merge trực tiếp nhánh order.

#### 4.4. Phần em tự chỉnh sửa/cải tiến

- Xác nhận `main` HEAD `3188f436`.
- Ghi riêng `origin/QuyetPV_DE190425` và `origin/Loc-Branch-order`.
- Nhận diện khác biệt cấu trúc nên không chấp nhận merge mù.
- Sửa đúng danh sách 5 thành viên.
- Đổi vai trò: Quyết phụ trách Backend & Security, Thương phụ trách Frontend.
- Tách báo cáo hiện trạng và kế hoạch thành hai file.

#### 4.5. Minh chứng

| Loại | Nội dung |
|---|---|
| Baseline | `main` – HEAD `3188f436` |
| Branch | `origin/QuyetPV_DE190425` |
| Branch | `origin/Loc-Branch-order` |
| File | `Bao_cao_hien_trang_du_an_SWP391.docx` |
| File | `Ke_hoach_phat_trien_1_tuan_SWP391.docx` |
| Kiểm tra | Git log, branch, cấu trúc thư mục và phân công |
| Ghi chú | Lỗi trong báo cáo là kết quả audit, không có nghĩa đã được sửa |

#### 4.6. Nhận xét cá nhân

AI có thể tạo kế hoạch hợp lý nhưng vẫn gán sai người, sai branch hoặc giả định code đã xong. Đối chiếu Git log là bắt buộc.

---

### Lần sử dụng AI số 6 – Soạn AI Audit Log

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 02/07/2026 |
| Công cụ AI | ChatGPT |
| Mục đích | Tổng hợp lịch sử dùng AI theo biểu mẫu |
| Phần việc | Report / AI Governance / Academic Integrity |
| Mức độ sử dụng | Hỗ trợ nhiều |

#### 4.1. Prompt đã sử dụng

```text
Dựa trên yêu cầu trong AI_AUDIT_LOG.md, dùng thông tin của tôi và Git log đã đọc được
để viết AI Audit Log dưới vai trò sinh viên Phạm Văn Quyết.
Không bịa commit, link, kết quả test hoặc phần việc không có minh chứng.
Phải nói rõ AI gợi ý gì, em dùng gì, em tự sửa gì và kiểm chứng thế nào.
```

#### 4.2. Kết quả AI gợi ý

AI tạo bản nháp đầy đủ các mục: thông tin, công cụ, từng lần dùng, minh chứng, hạn chế, kiểm chứng, đóng góp, reflection và cam kết.

#### 4.3. Phần em sử dụng

- Cấu trúc trình bày.
- Tổng hợp các mốc commit đã xác nhận.
- Phân biệt AI output, phần sử dụng và phần tự sửa.
- Cách ghi trung thực mục chưa đủ minh chứng.

#### 4.4. Phần em tự chỉnh sửa/cải tiến

- Kiểm tra tên, MSSV, username, vai trò và nhóm.
- Loại bỏ hash không chắc chắn.
- Không tạo link repository/Drive giả.
- Đánh dấu tên giảng viên và link evidence cần bổ sung.
- Đọc lại và chịu trách nhiệm giải thích.

#### 4.5. Minh chứng

| Loại | Nội dung |
|---|---|
| File | `AI_AUDIT_LOG_PhamVanQuyet_DE190425.md` |
| Commit | Chưa tạo tại thời điểm soạn |
| Kiểm tra | Đối chiếu Git log và các báo cáo |
| Screenshot | Cần chụp phiên trao đổi và lưu theo quy ước nhóm |
| Ghi chú | Việc AI hỗ trợ viết báo cáo được khai báo trực tiếp |

#### 4.6. Nhận xét cá nhân

AI Audit Log phải phản ánh đúng vai trò AI và trách nhiệm của sinh viên, không phóng đại cũng không che giấu.

---

## 5. Bảng tổng hợp mức độ sử dụng AI

| Hạng mục | Không dùng AI | AI hỗ trợ ít | AI hỗ trợ nhiều | AI sinh chính | Ghi chú |
|---|:---:|:---:|:---:|:---:|---|
| Phân tích yêu cầu |  |  | **X** |  | AI hỗ trợ phạm vi/domain; em chọn lại |
| Viết user story/use case |  | **X** |  |  | Chỉ gợi ý cấu trúc, chưa có commit UC cá nhân xác nhận |
| Thiết kế database |  |  | **X** |  | Em review PK/FK, lifecycle và dependency |
| Thiết kế kiến trúc |  |  | **X** |  | AI tạo khung; em điều chỉnh theo SWP391 |
| Thiết kế giao diện | **X** |  |  |  | Không có minh chứng cá nhân trong audit này |
| Code frontend | **X** |  |  |  | Frontend hiện do Nguyễn Đức Thương phụ trách |
| Code backend |  | **X** |  |  | Mới có hỗ trợ đọc/lập kế hoạch; chưa ghi nhận commit code backend cá nhân |
| Debug lỗi |  |  | **X** |  | Khoanh vùng config, security và merge risk |
| Viết test case |  | **X** |  |  | Có test plan, chưa có test commit cá nhân xác nhận |
| Kiểm thử sản phẩm |  | **X** |  |  | Kiểm chứng mức repository, chưa tuyên bố full pass |
| Tối ưu code |  | **X** |  |  | Gợi ý coding convention/refactor |
| Viết báo cáo |  |  | **X** |  | Em kiểm tra và chỉnh theo Git evidence |
| Slide thuyết trình | **X** |  |  |  | Không thuộc phạm vi audit này |

---

## 6. Các lỗi hoặc hạn chế từ AI

| STT | Lỗi/hạn chế | Cách phát hiện | Cách xử lý |
|---:|---|---|---|
| 1 | Coi file bị xóa là lỗi và đề xuất khôi phục | Em biết đây là phần chủ động xóa, kiểm tra `git diff` | Xác nhận rõ, tạo baseline, không cho tự phục hồi |
| 2 | Trộn trạng thái `main` với nhánh cá nhân | So sánh branch, cây thư mục và Git history | Ghi riêng từng branch |
| 3 | Đề xuất merge toàn bộ branch dù kiến trúc khác | Kiểm tra ahead/behind và `git diff` | Chỉ lấy phần phù hợp sau review |
| 4 | Database AI sinh thiếu constraint/lifecycle | Review business rule, PK/FK, trạng thái | Bổ sung lifecycle, uniqueness, concurrency |
| 5 | Mô tả test pass dù chưa có output | Không có log terminal tương ứng | Chỉ ghi test plan hoặc cần chạy |
| 6 | Gán sai thành viên/vai trò | Đối chiếu danh sách nhóm | Sửa đúng danh sách và vai trò |

---

## 7. Kiểm chứng kết quả AI

Em kiểm tra bằng:

1. **Git:** `git status --short`, `git branch --show-current`, `git branch -a`, `git log --oneline --decorate --all`, `git diff`.
2. **File thật:** mở trực tiếp tài liệu và source; kiểm tra package, entity, bảng và dependency có tồn tại.
3. **Build/test:** chỉ ghi thành công khi có output terminal; không ghi “đã test đầy đủ” nếu mới có test plan.
4. **Tài liệu môn học:** đối chiếu Student Guide, SRS/SDS template, Project Tracking và Java Coding Standards.
5. **Review con người:** tự đọc diff trước commit; không cho Agent tự push; quyết định merge/phân công phải được nhóm thống nhất.
6. **Bằng chứng:** chỉ dùng hash đã xác nhận; không tạo URL, screenshot hay kết quả test giả.

---

## 8. Đóng góp cá nhân và nhóm

### 8.1. Đóng góp cá nhân của em

Trong phạm vi Git history đã đối chiếu, em đã:

- Thực hiện repository audit.
- Xác lập baseline sau khi loại bỏ legacy.
- Xây dựng tài liệu kiến trúc.
- Phân tích domain, data dictionary và logical database.
- Đối chiếu `main`, nhánh cá nhân và nhánh order.
- Xây dựng báo cáo hiện trạng và kế hoạch một tuần.
- Nhận vai trò Backend & Security Lead.
- Kiểm tra và chịu trách nhiệm đối với nội dung AI hỗ trợ.

AI hỗ trợ đọc nhanh và tạo khung. Em quyết định phạm vi, xác nhận file bị xóa, kiểm tra Git, chỉnh kiến trúc/database/phân công và loại bỏ kết luận không có bằng chứng.

### 8.2. Đóng góp nhóm

> Đây là audit cá nhân. Em chỉ xác nhận việc dùng AI của bản thân. Thành viên khác cần khai báo trong log riêng hoặc cung cấp evidence.

| Thành viên | MSSV | Nhiệm vụ chính | Sử dụng AI? | Minh chứng cần đối chiếu |
|---|---|---|---|---|
| Phan Nguyễn | DE191019 | Leader; Database, Coupon, Payment, Invoice | Chưa xác nhận | Git log `Nguyendeptraibodoi`, issue/MR |
| Phạm Văn Quyết | DE190425 | Backend & Security; architecture/database groundwork; audit | **Có** | `ebbe85b0`, `275e0cb0`, `0743b2a8`, `9688ceda`, `8e057c24` |
| Nguyễn Tiến Lộc | DE190986 | Table, Reservation, Order, Kitchen | Chưa xác nhận | Git log `tienloc1234`, branch order |
| Nguyễn Đức Thương | DE190096 | Frontend Lead | Chưa xác nhận | Git log `thuong1703n`, ảnh UI |
| Trần Thanh Gia Huy | DE180571 | QA, Postman, SRS/SDS, release evidence | Chưa xác nhận | Git log `huyttde12`, test/document evidence |

---

## 9. Reflection cuối bài

### 9.1. AI hỗ trợ ở điểm nào?

AI giúp em đọc repository nhanh, chia hệ thống thành phần dễ kiểm tra, gợi ý kiến trúc/database và biến yêu cầu lớn thành task nhỏ.

### 9.2. Phần nào không sử dụng? Vì sao?

Em không dùng gợi ý khôi phục legacy, merge toàn bộ branch, khẳng định chức năng hoàn thành chỉ vì có file, ghi test pass khi chưa chạy, dùng nguyên database còn thiếu constraint, hoặc tạo evidence giả. Các gợi ý này có thể làm sai repository và báo cáo.

### 9.3. Kiểm tra tính đúng đắn thế nào?

Em dùng Git log/diff/branch, file source, tài liệu và output terminal; kiểm tra PK/FK/constraint; sửa phân công theo thông tin nhóm xác nhận.

### 9.4. Nếu không có AI, phần nào khó nhất?

Khó nhất là đọc nhanh repository nhiều nhánh/cấu trúc rồi tổng hợp thành tài liệu có logic. AI rút ngắn thời gian tổng hợp, còn quyết định vẫn do em.

### 9.5. Học được gì về môn học?

Một chức năng không hoàn thành chỉ vì có code. Nó cần requirement, design, database, test, commit truy vết và tài liệu cập nhật.

### 9.6. Học được gì về AI có trách nhiệm?

- Cung cấp đủ ngữ cảnh nhưng không giao toàn quyền.
- Chia task nhỏ và review sau từng task.
- Không cho AI tự push/merge.
- Lưu prompt, response, diff, commit và test.
- Ghi rõ phần AI hỗ trợ và phần tự sửa.
- Không báo cáo điều bản thân không giải thích được.

---

## 10. Cam kết học thuật

Em cam kết:

- Nội dung AI hỗ trợ được ghi nhận trung thực.
- Không nộp nguyên văn đầu ra AI mà không kiểm tra.
- Có khả năng giải thích quyết định, tài liệu và commit.
- Không tự tạo hash, link, screenshot hoặc kết quả test.
- Mục thiếu evidence đã được đánh dấu.
- Chịu trách nhiệm về sản phẩm và báo cáo.
- Hiểu rằng sử dụng AI không khai báo có thể ảnh hưởng kết quả đánh giá.

| Đại diện sinh viên | MSSV | Ngày xác nhận |
|---|---|---|
| Phạm Văn Quyết | DE190425 | 02/07/2026 |

---

## 11. Checklist trước khi nộp

- [ ] Điền tên giảng viên.
- [ ] Kiểm tra ngày thật từng commit bằng `git log --format=fuller`.
- [ ] Gắn URL repository/commit nếu giảng viên truy cập được.
- [ ] Chụp từng phiên AI có prompt, response và phần follow-up.
- [ ] Gắn ảnh `git log`, `git show --stat <commit>` và build/test.
- [ ] Upload evidence lên Google Drive theo yêu cầu.
- [ ] Kiểm tra tác giả commit nhánh order.
- [ ] Commit file audit và bổ sung hash cuối cùng vào lần số 6.
- [ ] Nhờ Leader hoặc thành viên review chéo.
