package ru.nikolay.service.bookBorrowing.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nikolay.service.bookBorrowing.domain.BorrowingInfo;

import java.util.List;

public interface BorrowingInfoRepository extends MongoRepository<BorrowingInfo, String> {
    List<BorrowingInfo> findAllByBookId(String bookId);
    List<BorrowingInfo> findAllByUserId(String userId);
}
