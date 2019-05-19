package ru.nikolay.service.book.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.nikolay.service.book.domain.Book;

import java.util.List;

public interface BookRepository extends MongoRepository<Book, String>{
}
