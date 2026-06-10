package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("libcard")
public class LibraryCard {
    @TableId(type = IdType.INPUT)
    private String cardNo;
    private String sno;
    private String sname;
    private String type;
    private String collage;
    private String major;
    private LocalDate birth;
    private String originPlace;
    private String cardStatus;
    private Integer times;
    private String password;
}