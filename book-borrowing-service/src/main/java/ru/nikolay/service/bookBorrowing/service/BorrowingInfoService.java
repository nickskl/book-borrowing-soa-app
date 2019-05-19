package ru.nikolay.service.bookBorrowing.service;

import ru.nikolay.service.bookBorrowing.domain.BorrowingInfo;
import ru.nikolay.requests.BorrowingInfoRequest;

import java.util.List;

public interface BorrowingInfoService {
    BorrowingInfo getById(String id);
    List<BorrowingInfo> getByUserId(String userId);
    List<BorrowingInfo> getByBookId(String bookId);
    BorrowingInfo borrowBook(BorrowingInfoRequest borrowingInfoRequest);
    void returnBook(String borrowingId);
}
