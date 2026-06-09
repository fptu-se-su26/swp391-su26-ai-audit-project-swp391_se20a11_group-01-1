package com.restaurant.constant;

public enum AppRole {
    CUSTOMER,
    STAFF,
    KITCHEN,
    ADMIN;

    public String asSpringRole() {
        return "ROLE_" + name();
    }
}
