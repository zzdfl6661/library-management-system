
package com.library.enums;

public enum UserRole {
    OFFICE("办公室"),
    CIRCULATION("流通部"),
    ACQUISITION("采编部");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
