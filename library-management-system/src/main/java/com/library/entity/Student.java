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
@TableName("student")
public class Student {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String sno;
    private String username;
    private String type;
    private String collage;
    private String major;
    private LocalDate birth;
    private String originPlace;
    private String password;
    private String gender;
    private String className;
    private String idCard;
}