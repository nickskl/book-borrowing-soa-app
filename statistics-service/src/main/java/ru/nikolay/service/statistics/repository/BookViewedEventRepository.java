package ru.nikolay.service.statistics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nikolay.statistics.BookDetailsViewedEvent;

import java.util.Date;
import java.util.List;

public interface BookViewedEventRepository extends MongoRepository<BookDetailsViewedEvent, String> {
    List<BookDetailsViewedEvent> findAllByTimestampIsGreaterThanEqual(Date data);
}
