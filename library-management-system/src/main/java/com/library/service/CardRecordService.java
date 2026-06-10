package com.library.service;

import com.library.entity.CardRecord;
import com.library.enums.OpType;

import java.util.List;

public interface CardRecordService {

    void recordOperation(String sno, String originCardNo, String newCardNo, OpType opType);

    List<CardRecord> getRecordsBySno(String sno);
}