# P5-T07 – Reusable Base Components

Project: Restaurant Management System  
Member: Member 4  
Product: Component base  
Scope: Button, Table, Modal, Form, Sidebar, Navbar

## 1. Purpose
This package provides reusable UI components for the Restaurant Management System. The components are designed for a Spring Boot + Thymeleaf web project and can be reused across Customer, Staff, Kitchen, and Admin screens.

## 2. Included files

```text
src/main/resources/templates/fragments/base-components.html
src/main/resources/templates/demo/component-demo.html
src/main/resources/static/css/base-components.css
src/main/resources/static/js/base-components.js
```

## 3. How to use in Spring Boot Thymeleaf

Copy the files into your Spring Boot project using the same folder structure.

In a Thymeleaf page, import CSS and JS:

```html
<link rel="stylesheet" th:href="@{/css/base-components.css}">
<script th:src="@{/js/base-components.js}" defer></script>
```

Use a fragment:

```html
<div th:replace="~{fragments/base-components :: navbar('Dashboard', 'Admin User', 'ADMIN')}"></div>
<div th:replace="~{fragments/base-components :: sidebar('dashboard')}"></div>
```

## 4. Component list

| Component | Purpose |
|---|---|
| Button | Reusable action button for add, edit, delete, save, cancel |
| Table | Reusable table layout for users, foods, orders, coupons |
| Modal | Reusable popup for confirmation or form actions |
| Form | Reusable input, select, textarea and form group |
| Sidebar | Reusable left navigation for Customer, Staff, Kitchen, Admin |
| Navbar | Reusable top navigation with title, user name, role and logout |

## 5. Notes

- The word `Manage` is not used in menu labels.
- Labels use clear action names such as Update, Configure, View, Create, Confirm, Print.
- The UI style is neutral and can be customized later.
