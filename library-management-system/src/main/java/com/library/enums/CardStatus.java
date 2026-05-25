
package com.library.enums;

public enum CardStatus {
    NORMAL("正常"),
    LOST("挂失"),
    CANCELLED("注销");

    private final String description;

    CardStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static CardStatus fromString(String value) {
        for (CardStatus status : CardStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown card status: " + value);
    }
}
