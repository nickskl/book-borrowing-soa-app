package ru.nikolay.service.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nikolay.service.user.domain.Role;
import ru.nikolay.service.user.domain.User;
import ru.nikolay.service.user.repository.UserRepository;
import ru.nikolay.requests.UserRequest;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenStore tokenStore;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(String id) {
        User user = userRepository.findOne(id);
        if(user == null) {
            throw new NullPointerException("User[" + id + "] not found in the database");
        }
        return user;
    }

    @Override
    @Transactional
    public User add(UserRequest userRequest) {
        if ((userRequest.getLogin() == null)||
                (userRequest.getLogin().isEmpty())) {
            throw new IllegalLoginException("Login must not be empty");
        }
        if (userRequest.getLogin().contains(" ")) {
            throw new IllegalLoginException("Login contains illegal characters");
        }

        User user = userRepository.findFirstByLogin(userRequest.getLogin());

        if(user != null) {
            throw new IllegalLoginException("User[" + userRequest.getLogin() + "] already exists in the database");
        }

        return userRepository.save(new User()
                .setLogin(userRequest.getLogin()));
    }

    @Override
    @Transactional
    public void delete(String id) {
        userRepository.delete(id);
    }

    @Override
    public boolean isAdmin(String token) {
        Authentication authentication = tokenStore.readAuthentication(token);
        String username = authentication.getName();
        User user = userRepository.findFirstByLogin(username);
        return user != null && user.getRole() == Role.ADMIN;
    }
}
