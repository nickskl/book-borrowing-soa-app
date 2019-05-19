package ru.nikolay.service.user.service;

import ru.nikolay.service.user.domain.User;
import ru.nikolay.requests.UserRequest;

public interface UserService {
    User getById(String id);

    User add(UserRequest userRequest);

    void delete(String id);

    boolean isAdmin(String token);
}
