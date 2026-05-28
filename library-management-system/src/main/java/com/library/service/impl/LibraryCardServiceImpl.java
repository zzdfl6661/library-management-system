
package com.library.service.impl;

import com.library.dto.request.CardCreateRequest;
import com.library.entity.LibraryCard;
import com.library.entity.Student;
import com.library.enums.CardStatus;
import com.library.exception.BusinessException;
import com.library.mapper.LibraryCardMapper;
import com.library.mapper.StudentMapper;
import com.library.service.LibraryCardService;
import com.library.util.BarcodeGenerator;
import com.library.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LibraryCardServiceImpl implements LibraryCardService {

    @Autowired
    private LibraryCardMapper libraryCardMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    @Transactional
    public LibraryCard createCard(CardCreateRequest request) {
        Student student = studentMapper.selectByStudentNo(request.getStudentNo());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        List<LibraryCard> existingCards = libraryCardMapper.selectByStudentId(student.getId());
        for (LibraryCard card : existingCards) {
            if (!CardStatus.CANCELLED.name().equals(card.getStatus())) {
                throw new BusinessException("该学生已有有效的借书证");
            }
        }
        LibraryCard card = new LibraryCard();
        card.setCardNo(BarcodeGenerator.generateCardNo());
        card.setStudentId(student.getId());
        card.setStatus(CardStatus.NORMAL.name());
        card.setIssueDate(LocalDate.now());
        card.setCreateTime(LocalDateTime.now());
        libraryCardMapper.insert(card);
        return card;
    }

    @Override
    @Transactional
    public void reportLost(String cardNo) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            throw new BusinessException("借书证不存在");
        }
        if (CardStatus.CANCELLED.name().equals(card.getStatus())) {
            throw new BusinessException("借书证已注销");
        }
        card.setStatus(CardStatus.LOST.name());
        libraryCardMapper.update(card);
    }

    @Override
    @Transactional
    public LibraryCard reissueCard(String cardNo) {
        LibraryCard oldCard = libraryCardMapper.selectByCardNo(cardNo);
        if (oldCard == null) {
            throw new BusinessException("借书证不存在");
        }
        if (!CardStatus.LOST.name().equals(oldCard.getStatus())) {
            throw new BusinessException("只有挂失的借书证才能补办");
        }
        oldCard.setStatus(CardStatus.CANCELLED.name());
        libraryCardMapper.update(oldCard);
        LibraryCard newCard = new LibraryCard();
        newCard.setCardNo(BarcodeGenerator.generateCardNo());
        newCard.setStudentId(oldCard.getStudentId());
        newCard.setStatus(CardStatus.NORMAL.name());
        newCard.setIssueDate(LocalDate.now());
        newCard.setCreateTime(LocalDateTime.now());
        libraryCardMapper.insert(newCard);
        return newCard;
    }

    @Override
    @Transactional
    public void cancelCard(String cardNo) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            throw new BusinessException("借书证不存在");
        }
        card.setStatus(CardStatus.CANCELLED.name());
        libraryCardMapper.update(card);
    }

    @Override
    public LibraryCard getByCardNo(String cardNo) {
        return libraryCardMapper.selectByCardNo(cardNo);
    }

    @Override
    public List<LibraryCard> getAllCards() {
        return libraryCardMapper.selectAll();
    }
}
