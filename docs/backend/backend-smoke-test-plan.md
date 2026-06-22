# Backend API Smoke Test Plan

This document outlines a manual end-to-end (E2E) testing flow to verify the overall integration of the backend REST APIs. 
This plan targets major business capabilities to ensure modules interact seamlessly.

## Test Flow

### 1. Register/Login
- **Action:** `POST /api/auth/register` with customer details.
- **Action:** `POST /api/auth/login` to retrieve JWT token.
- **Expected:** Token is received, user role is `ROLE_CUSTOMER`.

### 2. Admin list users
- **Action:** Login as Admin, `GET /api/admin/users`.
- **Expected:** List of system users is returned successfully.

### 3. Admin create category/food
- **Action:** `POST /api/admin/categories` to create "Main Course".
- **Action:** `POST /api/admin/foods` with `categoryId` to create "Beef Steak".
- **Expected:** Category and Food return 200 OK and are stored in DB.

### 4. Customer add cart
- **Action:** Using Customer token, `POST /api/cart/items` with `foodId` of "Beef Steak" and quantity.
- **Expected:** Cart is retrieved or created, item added, and updated cart object returned.

### 5. Customer validate coupon
- **Action:** `POST /api/coupons/validate` with a predefined discount code and total cart value.
- **Expected:** Coupon details (discount type, value) are returned.

### 6. Customer checkout order
- **Action:** `POST /api/orders` sending cart details, coupon code (optional), and order type (DINE_IN/TAKEAWAY).
- **Expected:** Cart is cleared. `RestaurantOrder` created with status `PENDING`. `totalAmount` correctly calculated.

### 7. Customer confirm payment
- **Action:** `POST /api/payments/process` with `orderId` and a mock payment provider reference.
- **Expected:** `Payment` created with status `PAID`. Order status transitions from `PENDING` to `CONFIRMED`.

### 8. Customer generate invoice
- **Action:** `GET /api/invoices/payment/{paymentId}`.
- **Expected:** Invoice data is returned representing the paid order. Idempotent check ensures 1 invoice per payment.

### 9. Customer create reservation
- **Action:** `POST /api/reservations` providing `tableId`, time, and guest count.
- **Expected:** Validation ensures the table is available and time does not overlap. `Reservation` created with `PENDING` status.

### 10. Staff create order
- **Action:** Login as Staff, `POST /api/staff/orders` bypassing the cart mechanism completely.
- **Expected:** Order immediately created with `CONFIRMED` status. Prices are correctly snapshotted onto items.

### 11. Kitchen update order
- **Action:** Login as Kitchen, `PATCH /api/kitchen/orders/{id}/status` to change order from `CONFIRMED` to `PREPARING`, and then to `READY`.
- **Expected:** Status transitions validate properly according to Kitchen permissions.

### 12. Admin dashboard report
- **Action:** Login as Admin, `GET /api/admin/dashboard/summary`.
- **Expected:** Returns aggregate counts (total orders, total revenue, available tables). The revenue should precisely reflect the `PAID` payment performed in step 7.
