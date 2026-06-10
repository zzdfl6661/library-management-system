package com.library.service.impl;

import com.library.entity.CardRecord;
import com.library.enums.OpType;
import com.library.mapper.CardRecordMapper;
import com.library.service.CardRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardRecordServiceImpl implements CardRecordService {

    @Autowired
    private CardRecordMapper cardRecordMapper;

    @Override
    public void recordOperation(String sno, String originCardNo, String newCardNo, OpType opType) {
        CardRecord record = new CardRecord();
        record.setSno(sno);
        record.setOriginCardNo(originCardNo);
        record.setNewCardNo(newCardNo);
        record.setOpType(opType);
        cardRecordMapper.insert(record);
    }

    @Override
    public List<CardRecord> getRecordsBySno(String sno) {
        return cardRecordMapper.selectBySno(sno);
    }
}