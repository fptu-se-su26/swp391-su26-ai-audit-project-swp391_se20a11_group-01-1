# Security Route Review

This document reviews the security configuration and role requirements for all routes in the application based on `SecurityConfig.java` and `@PreAuthorize` annotations on controllers.

## Review Results

### 1. `/api/auth/**`
- **Protection:** `permitAll()` explicitly configured in `SecurityConfig.java`.
- **Finding:** Correct. Registration and Login must be publicly accessible.

### 2. `/api/categories/**`
- **Protection:** `permitAll()` explicitly configured in `SecurityConfig.java` for GET methods.
- **Admin routes:** `/api/admin/categories/**` protected by `@PreAuthorize("hasAuthority('ROLE_ADMIN')")`.
- **Finding:** Correct. Public can view, only admin can manage.

### 3. `/api/foods/**`
- **Protection:** `permitAll()` explicitly configured in `SecurityConfig.java` for GET methods.
- **Admin routes:** `/api/admin/foods/**` protected by `@PreAuthorize("hasAuthority('ROLE_ADMIN')")`.
- **Finding:** Correct. Public can view, only admin can manage.

### 4. `/api/cart/**`
- **Protection:** Authenticated request required (handled by standard Spring Security `anyRequest().authenticated()`).
- **Controller Annotation:** `@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")` is present on the controller.
- **Finding:** Correct. Only CUSTOMER can access the cart.

### 5. `/api/coupons/**`
- **Protection:** `/api/coupons/validate` is protected by `@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")`.
- **Admin routes:** `/api/admin/coupons/**` protected by `@PreAuthorize("hasAuthority('ROLE_ADMIN')")`.
- **Finding:** Correct. Customers validate coupons; Admins manage coupons.

### 6. `/api/orders/**`
- **Protection:** Protected by `@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")` on `OrderController`.
- **Finding:** Correct. Customers can checkout and view their own orders. Staff and Kitchen have their own separate endpoints.

### 7. `/api/payments/**`
- **Protection:** Protected by `@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")` on `PaymentController`.
- **Finding:** Correct. Only Customers can trigger or view their payment transactions.

### 8. `/api/invoices/**`
- **Protection:** Protected by `@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")` on `InvoiceController`.
- **Finding:** Correct. Invoices are generated automatically by the system and viewed securely by the paying Customer.

### 9. `/api/reservations/**`
- **Protection:** Protected by `@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")` on `ReservationController`.
- **Admin routes:** `/api/admin/reservations/**` protected by `@PreAuthorize("hasAuthority('ROLE_ADMIN')")`.
- **Tables:** `/api/tables` is `permitAll()` configured in `SecurityConfig.java` (GET).
- **Finding:** Correct. Tables can be viewed publicly. Reservation requires Customer login. Admin can manage them.

### 10. `/api/admin/**`
- **Protection:** All controllers starting with `Admin...` are protected by `@PreAuthorize("hasAuthority('ROLE_ADMIN')")`.
- **Finding:** Correct. Access is restricted to System Administrators only.

### 11. `/api/staff/**`
- **Protection:** Protected by `@PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")` on `StaffOrderController`.
- **Finding:** Correct. Staff members and Admins can create and process counter orders.

### 12. `/api/kitchen/**`
- **Protection:** Protected by `@PreAuthorize("hasAnyAuthority('ROLE_KITCHEN', 'ROLE_ADMIN')")` on `KitchenOrderController`.
- **Finding:** Correct. Kitchen staff and Admins can view and update preparation statuses.

## Summary
The security model relies on a combination of global configuration (`SecurityConfig.java`) for `permitAll` rules and method-level security (`@PreAuthorize`) for role-based access control (RBAC). 
- All routes are properly secured.
- No exposed protected routes.
- **No security fixes required.**
