package com.library.dto.response;

import com.library.entity.LibraryCard;
import com.library.entity.Student;
import lombok.Data;

@Data
public class StudentCardInfo {
    private Student student;
    private LibraryCard card;
    private Boolean hasValidCard;
    private Integer availableBorrowCount;
}