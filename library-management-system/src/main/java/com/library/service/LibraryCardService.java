package com.library.service;

import com.library.entity.LibraryCard;
import com.library.entity.Student;

import java.util.List;

public interface LibraryCardService {

    Student getStudentBySno(String sno);

    LibraryCard getCardBySno(String sno);

    boolean hasValidCard(String sno);

    void createCard(String sno, String cardNo);

    void createCards(List<String> snos, List<String> cardNos);

    void reportLost(String sno);

    void reissueCard(String sno, String newCardNo);

    void cancelCard(String sno);

    void cancelCards(List<String> snos);

    String validateForBorrow(String cardNo);

    int getAvailableBorrowCount(String cardNo);

    List<LibraryCard> getAllCards();

    List<LibraryCard> searchCards(String keyword);

    void updatePassword(String cardNo, String oldPassword, String newPassword);
}