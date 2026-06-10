package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.enums.FineStatus;
import com.library.enums.RetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("borrowrec")
public class BorrowRecord {
    @TableId(type = IdType.AUTO)
    private Integer serNum;
    private String sno;
    private String barCode;
    private LocalDate borDate;
    private LocalDate retDate;
    private LocalDate realRetDate;
    private RetStatus retStatus;
    private Integer ovdDays;
    private BigDecimal fineMoney;
    private FineStatus fineStatus;
    private Integer paySerNum;
}