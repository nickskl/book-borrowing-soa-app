package ru.nikolay.service.statistics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nikolay.statistics.StorageDetailsViewedEvent;

import java.util.Date;
import java.util.List;

public interface StorageViewedEventRepository extends MongoRepository<StorageDetailsViewedEvent, String> {
    List<StorageDetailsViewedEvent> findAllByTimestampIsGreaterThanEqual(Date data);
}
