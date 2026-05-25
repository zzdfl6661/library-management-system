
package com.library.enums;

public enum StudentType {
    UNDERGRADUATE("本科生", 5),
    GRADUATE("研究生", 10),
    TEACHER("教师", 15);

    private final String description;
    private final int maxBorrowCount;

    StudentType(String description, int maxBorrowCount) {
        this.description = description;
        this.maxBorrowCount = maxBorrowCount;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxBorrowCount() {
        return maxBorrowCount;
    }

    public static StudentType fromString(String value) {
        for (StudentType type : StudentType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown student type: " + value);
    }
}
