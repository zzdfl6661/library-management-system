
package com.library.controller;

import com.library.dto.request.BookCreateRequest;
import com.library.dto.response.ApiResponse;
import com.library.entity.Book;
import com.library.service.AcquisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/acquisition")
@CrossOrigin(origins = "http://localhost:5173")
public class AcquisitionController {

    @Autowired
    private AcquisitionService acquisitionService;

    @PostMapping("/books")
    public ApiResponse<Book> importNewBook(@RequestBody BookCreateRequest request) {
        Book book = acquisitionService.importNewBook(request);
        return ApiResponse.success("新书入库成功", book);
    }

    @PutMapping("/books/{barcode}/discard")
    public ApiResponse<Void> discardBook(@PathVariable String barcode) {
        acquisitionService.discardBook(barcode);
        return ApiResponse.success("报废成功");
    }

    @PutMapping("/books/{bookId}/withdraw")
    public ApiResponse<Void> withdrawBook(@PathVariable Long bookId) {
        acquisitionService.withdrawBook(bookId);
        return ApiResponse.success("下架成功");
    }
}
