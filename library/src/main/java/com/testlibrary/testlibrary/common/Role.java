package com.testlibrary.testlibrary.common;

import lombok.Getter;

@Getter
public enum Role {
    CUSTOMER("Customer"),
    EMPLOYEE("Employee");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
