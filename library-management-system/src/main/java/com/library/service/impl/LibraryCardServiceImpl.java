package com.library.service.impl;

import com.library.entity.BorrowRecord;
import com.library.entity.LibraryCard;
import com.library.entity.Student;
import com.library.enums.OpType;
import com.library.enums.StudentType;
import com.library.exception.BusinessException;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.CardRecordMapper;
import com.library.mapper.LibraryCardMapper;
import com.library.mapper.StudentMapper;
import com.library.service.CardRecordService;
import com.library.service.LibraryCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LibraryCardServiceImpl implements LibraryCardService {

    private static final BigDecimal MAX_UNPAID_FINE = new BigDecimal("50");

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private LibraryCardMapper libraryCardMapper;

    @Autowired
    private CardRecordMapper cardRecordMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private CardRecordService cardRecordService;

    @Override
    public Student getStudentBySno(String sno) {
        return studentMapper.selectBySno(sno);
    }

    @Override
    public LibraryCard getCardBySno(String sno) {
        return libraryCardMapper.selectBySno(sno);
    }

    @Override
    public boolean hasValidCard(String sno) {
        LibraryCard card = libraryCardMapper.selectBySno(sno);
        return card != null && !"注销".equals(card.getCardStatus());
    }

    @Override
    @Transactional
    public void createCard(String sno, String cardNo) {
        Student student = studentMapper.selectBySno(sno);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        if (hasValidCard(sno)) {
            throw new BusinessException("该学生已有有效的借书证");
        }

        LibraryCard card = new LibraryCard();
        card.setCardNo(cardNo);
        card.setSno(sno);
        card.setSname(student.getUsername());
        card.setType(student.getType());
        card.setCollage(student.getCollage());
        card.setMajor(student.getMajor());
        card.setBirth(student.getBirth());
        card.setOriginPlace(student.getOriginPlace());
        card.setCardStatus("正常");
        card.setTimes(0);
        card.setPassword("123456");
        libraryCardMapper.insert(card);

        cardRecordService.recordOperation(sno, null, cardNo, OpType.NEW);
    }

    @Override
    @Transactional
    public void createCards(List<String> snos, List<String> cardNos) {
        if (snos.size() != cardNos.size()) {
            throw new BusinessException("学号列表和卡号列表数量不一致");
        }
        for (int i = 0; i < snos.size(); i++) {
            createCard(snos.get(i), cardNos.get(i));
        }
    }

    @Override
    @Transactional
    public void reportLost(String sno) {
        LibraryCard card = libraryCardMapper.selectBySno(sno);
        if (card == null) {
            throw new BusinessException("借书证不存在");
        }
        card.setCardStatus("挂失");
        libraryCardMapper.updateByCardNo(card);

        cardRecordService.recordOperation(sno, card.getCardNo(), null, OpType.LOST);
    }

    @Override
    @Transactional
    public void reissueCard(String sno, String newCardNo) {
        LibraryCard oldCard = libraryCardMapper.selectBySno(sno);
        if (oldCard == null) {
            throw new BusinessException("借书证不存在");
        }

        oldCard.setCardStatus("注销");
        libraryCardMapper.updateByCardNo(oldCard);

        Student student = studentMapper.selectBySno(sno);
        LibraryCard newCard = new LibraryCard();
        newCard.setCardNo(newCardNo);
        newCard.setSno(sno);
        newCard.setSname(student.getUsername());
        newCard.setType(student.getType());
        newCard.setCollage(student.getCollage());
        newCard.setMajor(student.getMajor());
        newCard.setBirth(student.getBirth());
        newCard.setOriginPlace(student.getOriginPlace());
        newCard.setCardStatus("正常");
        newCard.setTimes(0);
        newCard.setPassword("123456");
        libraryCardMapper.insert(newCard);

        cardRecordService.recordOperation(sno, oldCard.getCardNo(), newCardNo, OpType.REISSUE);
    }

    @Override
    @Transactional
    public void cancelCard(String sno) {
        LibraryCard card = libraryCardMapper.selectBySno(sno);
        if (card == null) {
            throw new BusinessException("借书证不存在");
        }
        card.setCardStatus("注销");
        libraryCardMapper.updateByCardNo(card);

        cardRecordService.recordOperation(sno, card.getCardNo(), null, OpType.CANCEL);
    }

    @Override
    @Transactional
    public void cancelCards(List<String> snos) {
        for (String sno : snos) {
            cancelCard(sno);
        }
    }

    @Override
    public String validateForBorrow(String cardNo) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            return "借书证不存在";
        }
        if (!"正常".equals(card.getCardStatus())) {
            return "借书证状态异常，当前状态：" + card.getCardStatus();
        }

        int availableCount = getAvailableBorrowCount(cardNo);
        if (availableCount <= 0) {
            return "已达到借阅上限";
        }

        List<BorrowRecord> unpaidRecords = borrowRecordMapper.selectUnpaidBySno(card.getSno());
        BigDecimal totalUnpaidFine = BigDecimal.ZERO;
        for (BorrowRecord record : unpaidRecords) {
            if (record.getFineMoney() != null) {
                totalUnpaidFine = totalUnpaidFine.add(record.getFineMoney());
            }
        }
        if (totalUnpaidFine.compareTo(MAX_UNPAID_FINE) > 0) {
            return "有未缴罚款，超过允许额度";
        }

        return null;
    }

    @Override
    public int getAvailableBorrowCount(String cardNo) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            return 0;
        }

        Integer maxBorrowCount = getMaxBorrowCount(card.getType());
        if (maxBorrowCount == null) {
            return 0;
        }

        int borrowedCount = borrowRecordMapper.countBySno(card.getSno());
        return maxBorrowCount - borrowedCount;
    }

    @Override
    public List<LibraryCard> getAllCards() {
        return libraryCardMapper.selectAll();
    }

    @Override
    public List<LibraryCard> searchCards(String keyword) {
        return libraryCardMapper.searchByKeyword(keyword);
    }

    private Integer getMaxBorrowCount(String studentType) {
        if (studentType == null) {
            return null;
        }
        for (StudentType type : StudentType.values()) {
            if (type.getType().equals(studentType)) {
                return type.getMaxBorrowCount();
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void updatePassword(String cardNo, String oldPassword, String newPassword) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            throw new BusinessException("借书证不存在");
        }
        if (!oldPassword.equals(card.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        card.setPassword(newPassword);
        libraryCardMapper.updateByCardNo(card);
    }
}