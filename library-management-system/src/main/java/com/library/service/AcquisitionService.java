
package com.library.service;

import com.library.dto.request.BookCreateRequest;
import com.library.entity.Book;

public interface AcquisitionService {
    Book importNewBook(BookCreateRequest request);
    void discardBook(String barcode);
    void withdrawBook(Long bookId);
}
