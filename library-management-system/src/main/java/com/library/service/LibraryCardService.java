
package com.library.service;

import com.library.dto.request.CardCreateRequest;
import com.library.entity.LibraryCard;

import java.util.List;

import com.library.dto.response.LibraryCardResponse;

public interface LibraryCardService {
    LibraryCard createCard(CardCreateRequest request);
    void reportLost(String cardNo);
    LibraryCard reissueCard(String cardNo);
    void cancelCard(String cardNo);
    LibraryCardResponse getCardInfo(String cardNo);
    List<LibraryCard> getAllCards();
}
