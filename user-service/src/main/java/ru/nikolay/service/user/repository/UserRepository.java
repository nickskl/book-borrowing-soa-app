package ru.nikolay.service.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nikolay.service.user.domain.User;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    User findFirstByLogin(String login);
}
