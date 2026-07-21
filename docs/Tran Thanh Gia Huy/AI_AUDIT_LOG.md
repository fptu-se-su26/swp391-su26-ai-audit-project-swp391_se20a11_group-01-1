# AI AUDIT LOG – SWP391

> **Phạm vi:** Nhật ký cá nhân của **Trần Thanh Gia Huy (DE180571)** trong dự án nhóm SWP391.
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
| Sinh viên thực hiện | Trần Thanh Gia Huy |
| MSSV | DE180571 |
| Git username | `huyttde12` |
| Vai trò hiện tại | Member – QA, Documentation & Project Tracking |
| Giảng viên hướng dẫn | QuangLTN3 |
| Ngày bắt đầu ghi nhận | 18/05/2026 |
| Ngày cập nhật gần nhất | 02/07/2026 |
| Repository baseline đã đối chiếu | `main`, HEAD `3188f436` – `Update AI_AUDIT_LOG.md` |
| Nhánh cá nhân | `origin/HuyTT_DE180571` |

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
- [x] Gemini
- [ ] Claude
- [ ] GitHub Copilot
- [ ] Cursor
- [ ] Antigravity / AI Agent trong IDE
- [ ] Perplexity

*Ghi chú: Chỉ đánh dấu các công cụ có bằng chứng sử dụng rõ ràng trong lịch sử phát triển dự án.*

---

## 3. Mục tiêu sử dụng AI

Với vai trò Đảm bảo chất lượng (QA), Quản lý tài liệu và Theo dõi tiến độ, em không sử dụng AI để sinh mã nguồn nghiệp vụ (business logic) mà dùng AI như một trợ lý phân tích và chuẩn hóa. Các mục tiêu cụ thể bao gồm:

- **QA & Testing:** Xây dựng Test Plan, định nghĩa các kịch bản kiểm thử (Test Cases) phủ kín các luồng nghiệp vụ cốt lõi (Boundary Value Analysis, Error Guessing). 
- **Automation Testing:** Hỗ trợ sinh khung (boilerplate code) cho các kịch bản kiểm thử tự động API trên Postman và luồng UI bằng Selenium Java.
- **Documentation:** Chuẩn hóa cú pháp và cấu trúc tài liệu Đặc tả yêu cầu (SRS) theo chuẩn format User Stories và Acceptance Criteria.
- **Project Tracking:** Phân rã công việc (WBS), thiết lập Definition of Done (DoD) rõ ràng cho từng task để nghiệm thu chéo giữa các thành viên.

---

## 4. Nhật ký sử dụng AI chi tiết

### Lần sử dụng AI số 1 – Xây dựng Test Plan và Test Cases nghiệp vụ cốt lõi

| Nội dung | Thông tin |
|---|---|
| Ngày sử dụng | 25/05/2026 |
| Công cụ AI | ChatGPT |
| Mục đích | Tạo Test Plan tổng thể và kịch bản kiểm thử cho luồng "Đặt bàn & Kích hoạt QR Check-in" |
| Phần việc | QA / Testing |
| Mức độ sử dụng | Hỗ trợ nhiều |

#### 4.1. Prompt đã sử dụng

Tôi đảm nhận vai trò QA cho dự án Restaurant Management System (Spring Boot + ReactJS). 
Hãy giúp tôi:
1. Viết một Test Plan ngắn gọn xác định rõ phạm vi kiểm thử (In-scope/Out-of-scope) và tiêu chí Pass/Fail.
2. Sinh bộ Test Case chi tiết cho luồng: "Khách đặt bàn trực tuyến -> Khách đến nhà hàng -> Nhân viên quét QR để mở Dining Session".
Yêu cầu bắt buộc: Trình bày dạng bảng, bao gồm các kịch bản Positive và Negative (nhấn mạnh vào kiểm thử biên và vòng đời của mã QR).


#### 4.2. Kết quả AI gợi ý

* **Postman:** AI trả về đoạn code dùng `pm.response.to.have.status()` và `pm.expect()`.
* **Selenium Java:** AI cung cấp class Test đầy đủ annotations `@BeforeEach`, `@Test`, `@AfterEach`, sử dụng `WebDriverWait` và `ExpectedConditions` để tương tác với các DOM elements.

#### 4.3. Phần em sử dụng

* Khung thiết lập và giải phóng WebDriver (Setup/Teardown).
* Logic bắt điều kiện chờ tường minh (Explicit Wait) giúp script không bị crash khi mạng chậm.
* Logic test API trên Postman.

#### 4.4. Phần em tự chỉnh sửa/cải tiến

* **Tích hợp DOM thực tế:** AI gợi ý tìm element qua `By.id("username")`, em đã refactor lại thành `By.cssSelector("input[name='email']")` và các XPATH tương ứng với các phần tử HTML thực tế mà bạn Thương (Frontend Lead) đã code.
* **Biến môi trường:** Thêm lệnh `pm.environment.set("jwt_token", pm.response.json().accessToken)` vào Postman để lưu token tự động dùng cho các API sau.

#### 4.5. Minh chứng

| Loại | Nội dung |
| --- | --- |
| Commit | `df478a12` – Integrate Postman test scripts and Selenium login automated test |
| File | `srccode/testing/automation/AuthFlowAutomationTest.java` |
| File | `docs/QA/collections/RMS_API_Test_Collection.json` |
| Kết quả | Bắt thành công 1 bug UI (nút Login không disable khi đang call API). |

---

## 5. Bảng tổng hợp mức độ sử dụng AI

| Hạng mục | Không dùng AI | AI hỗ trợ ít | AI hỗ trợ nhiều | AI sinh chính | Ghi chú |
| --- | --- | --- | --- | --- | --- |
| Phân tích yêu cầu |  | **X** |  |  | Rà soát tính khả thi của yêu cầu (SRS) |
| Viết user story/use case |  |  | **X** |  | Chuẩn hóa format Acceptance Criteria |
| Thiết kế database | **X** |  |  |  | Không thuộc phạm vi phụ trách |
| Thiết kế kiến trúc | **X** |  |  |  | Không thuộc phạm vi phụ trách |
| Thiết kế giao diện | **X** |  |  |  | Không thuộc phạm vi phụ trách |
| Code frontend / backend | **X** |  |  |  | Không thuộc phạm vi phụ trách |
| Debug lỗi |  | **X** |  |  | Debug các lỗi timeout trong Selenium Script |
| Viết test case |  |  | **X** |  | Phủ kín các trường hợp kiểm thử biên |
| Kiểm thử sản phẩm |  |  | **X** |  | Hỗ trợ sinh script kiểm thử tự động |
| Viết báo cáo / Tài liệu |  |  | **X** |  | Lập Test Plan và tổ chức tài liệu dự án |

---

## 6. Các lỗi hoặc hạn chế từ AI

| STT | Lỗi/hạn chế từ AI | Cách phát hiện | Cách xử lý/cải tiến |
| --- | --- | --- | --- |
| 1 | Test Cases bỏ qua các kịch bản thực tế vật lý (ví dụ: mất mạng Wi-Fi tại quán). | Đánh giá kịch bản trên góc độ môi trường triển khai thực tế. | Tự bổ sung các kịch bản ngắt kết nối mạng khi đang gửi Request đặt món. |
| 2 | AI vẽ ra luồng nghiệp vụ quá phức tạp (Shared Cart, Split Bill), không phù hợp thời gian SWP391. | Đối chiếu khối lượng công việc với Sprint Plan và số lượng thành viên. | Chủ động lược bỏ khỏi tài liệu SRS/SDS, chỉ giữ lại core flow. |
| 3 | Selenium Script do AI viết bị cứng (Hardcoded DOM selectors). | Chạy thử Automation Test và nhận lỗi `NoSuchElementException`. | Cập nhật lại toàn bộ CSS Selector/XPATH theo cấu trúc HTML/React thực tế của frontend. |

---

## 7. Kiểm chứng kết quả AI

Để đảm bảo kết quả AI là chính xác và áp dụng được, em thực hiện các bước sau:

1. **Đối chiếu chéo (Cross-verification):** So sánh các User Stories do AI sinh ra với yêu cầu đề bài gốc và Student Guide của môn SWP391.
2. **Review logic nghiệp vụ:** Đem các kịch bản kiểm thử thảo luận trong nhóm (với Quyết và Thương) để xác nhận hệ thống có thiết kế để xử lý các case đó hay không trước khi đưa vào Test Plan chính thức.
3. **Thực thi thực tế (Execution):** - Postman Script: Trực tiếp chạy Collection Runner nghiệm thu response thực tế từ Backend.
* Automation UI: Chạy script Selenium Java trên môi trường local để đảm bảo trình duyệt thao tác đúng trên HTML DOM. Không chấp nhận đoạn code nào chưa log ra trạng thái `PASSED`.



---

## 8. Đóng góp cá nhân và nhóm

### 8.1. Đóng góp cá nhân

Trong phạm vi Git history và tài liệu đã đối chiếu, em phụ trách:

* Xây dựng Test Plan, Test Cases, và hệ thống tài liệu Đặc tả yêu cầu (SRS, SDS) bám sát form mẫu học thuật.
* Viết và vận hành mã Automation Test (Selenium Java cho UI flow và Postman cho API).
* Đóng vai trò màng lọc: Từ chối các tính năng/ý tưởng phi thực tế do AI gợi ý để bảo vệ tiến độ chung của team.
* Đảm bảo "Definition of Done" của các task trước khi cho phép merge vào nhánh `main`.

### 8.2. Đóng góp nhóm

| Thành viên | MSSV | Nhiệm vụ chính | Sử dụng AI? | Minh chứng cần đối chiếu |
| --- | --- | --- | --- | --- |
| Phan Nguyễn | DE191019 | Leader; Database, Coupon, Payment, Invoice | Chưa xác nhận | Git log nhánh cá nhân, Issue/MR |
| Phạm Văn Quyết | DE190425 | Backend & Security Lead; Architecture | **Có** | Lịch sử commit kiến trúc, repo audit log |
| Nguyễn Tiến Lộc | DE190986 | Order & Restaurant Workflow Backend | Chưa xác nhận | Git log nhánh order, API endpoint |
| Nguyễn Đức Thương | DE190096 | Frontend Lead | **Có** | UI Components, React code logs |
| Trần Thanh Gia Huy | DE180571 | QA, Documentation & Project Tracking | **Có** | `89ac23df`, `ca5612be`, `df478a12`, `docs/QA` |

---

## 9. Reflection cuối bài

### 9.1. AI đã hỗ trợ em ở điểm nào?

AI đóng vai trò như một "nhân viên thực thi" tốc độ cao. Thay vì mất hàng giờ tự viết cấu trúc Given-When-Then hay gõ từng dòng boilerplate cho Postman/Selenium, em dùng AI để khởi tạo bộ khung chuẩn. Nhờ đó, em có thời gian tập trung vào phần "chất xám" thực sự: phân tích rủi ro hệ thống và viết các kịch bản ngoại lệ (Negative Cases).

### 9.2. Phần nào em không sử dụng theo gợi ý của AI? Vì sao?

Tuyệt đối không sử dụng các luồng tính năng mở rộng vô tội vạ do AI gợi ý (như hệ thống chia tiền hóa đơn phức tạp). Là người theo dõi dự án (Project Tracking), em hiểu rằng việc phình to phạm vi (Scope Creep) là nguyên nhân lớn nhất khiến các dự án SWP391 thất bại.

### 9.3. Kiểm tra tính đúng đắn như thế nào?

Kiểm chứng thực tế là bắt buộc. Đoạn code AI sinh ra phải biên dịch được, chạy được và tìm ra được lỗi. Tài liệu AI viết phải được nhóm thông qua và giảng viên hướng dẫn (QuangLTN3) chấp nhận trong các buổi review.

### 9.4. Nếu không có AI, phần nào sẽ khó khăn nhất?

Khó khăn nhất là việc chuyển đổi từ ngôn ngữ nghiệp vụ sang ngôn ngữ kiểm thử tự động. Việc setup môi trường Selenium hay viết script Postman từ đầu rất mất thời gian tra cứu tài liệu.

### 9.5. Học được gì về môn học?

Đảm bảo chất lượng (QA) không chỉ là tìm bug sau khi code xong, mà bắt đầu ngay từ việc định nghĩa yêu cầu (Requirement) rõ ràng. Yêu cầu sai (như luồng quét QR ban đầu) thì dù code có tốt đến đâu, dự án vẫn sẽ thất bại.

### 9.6. Học được gì về cách sử dụng AI có trách nhiệm?

* AI sinh ra thông tin, nhưng con người mới là người chịu trách nhiệm về tính khả thi và độ chính xác của thông tin đó.
* Không mù quáng copy-paste. Phải đọc hiểu, chọn lọc, và tinh chỉnh lại mã/tài liệu để khớp với kiến trúc thực tế của hệ thống.
* Trung thực trong việc khai báo sử dụng AI, coi đó là kỹ năng sử dụng công cụ nâng cao chứ không phải gian lận.

---

## 10. Cam kết học thuật

Em cam kết rằng:

* Nội dung AI hỗ trợ được ghi nhận hoàn toàn trung thực trong Audit Log này.
* Mọi tài liệu thiết kế, kịch bản test và script kiểm thử đã được em tự kiểm chứng, hiệu chỉnh bằng kiến thức chuyên môn và khớp với thực tế mã nguồn của nhóm.
* Em hiểu toàn bộ cấu trúc và logic của các đoạn mã tự động/bài báo cáo đã nộp và sẵn sàng giải trình trực tiếp.
* Em chịu trách nhiệm hoàn toàn về các quyết định loại bỏ/bổ sung yêu cầu nghiệp vụ trong phạm vi quản lý QA của mình.

| Đại diện sinh viên/nhóm | Ngày xác nhận |
| --- | --- |
| Trần Thanh Gia Huy | 02/07/2026 |

```

```
