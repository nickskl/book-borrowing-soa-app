package ru.nikolay.service.storage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nikolay.service.storage.domain.Storage;

import java.util.List;

public interface StorageRepository extends MongoRepository<Storage, String>{
    List<Storage> findAllByBookInformationResponseIdsIn(List<String> bookInformationResponseIds);
}
