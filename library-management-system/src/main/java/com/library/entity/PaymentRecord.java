package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("payrec")
public class PaymentRecord {
    @TableId(type = IdType.AUTO)
    private Integer serNum;
    private String sno;
    private BigDecimal payAmount;
    private LocalDate payDate;
}