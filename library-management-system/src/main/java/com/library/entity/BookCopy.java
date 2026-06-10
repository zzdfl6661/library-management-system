package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.enums.BookCopyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("bookcopy")
public class BookCopy {
    @TableId(type = IdType.INPUT)
    private String barCode;
    private String ISBN;
    private String place;
    private BookCopyStatus status;
    private Integer oldStatus;
}