package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String ISBN;
    private String bname;
    private String author;
    private String publisher;
    private String introduction;
    private LocalDate pubDate;
    private String clcNum;
    private BookStatus bookStatus;
}