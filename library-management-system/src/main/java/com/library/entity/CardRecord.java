package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.enums.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("cardrec")
public class CardRecord {
    @TableId(type = IdType.AUTO)
    private Integer serNum;
    private String sno;
    private String originCardNo;
    private String newCardNo;
    private OpType opType;
}