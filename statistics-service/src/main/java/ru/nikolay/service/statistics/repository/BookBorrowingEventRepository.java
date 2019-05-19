package ru.nikolay.service.statistics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nikolay.statistics.BookBorrowingEvent;

public interface BookBorrowingEventRepository extends MongoRepository<BookBorrowingEvent, String>{
}
