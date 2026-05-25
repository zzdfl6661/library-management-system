
package com.library.enums;

public enum BookCopyStatus {
    AVAILABLE("可借"),
    BORROWED("已借出"),
    DAMAGED("损坏"),
    WITHDRAWN("下架");

    private final String description;

    BookCopyStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static BookCopyStatus fromString(String value) {
        for (BookCopyStatus status : BookCopyStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown book copy status: " + value);
    }
}
