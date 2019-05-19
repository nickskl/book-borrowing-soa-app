package ru.nikolay.service.user.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nikolay.service.user.domain.User;
import ru.nikolay.service.user.service.UserService;
import ru.nikolay.requests.UserRequest;
import ru.nikolay.responses.UserResponse;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@RestController
@RequestMapping("/user")
public class UserRestController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public UserResponse getUserById(@PathVariable String id) {
        logger.debug("User: getting user with id[" + id + "]");
        return userService.getById(id).toResponse();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public UserResponse addUser(@Valid @RequestBody UserRequest userRequest, HttpServletResponse response) {
        logger.debug("User: creating user with request[" + userRequest + "]");
        User user = userService.add(userRequest);
        response.addHeader(HttpHeaders.LOCATION, "/user/" + user.getId());
        return user.toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable String id) {
        logger.debug("User: deleting user with id[" + id + "]");
        userService.delete(id);
    }

    @GetMapping(value = "/isAdmin/{token}")
    public boolean isAdmin(@PathVariable String token) {
        return userService.isAdmin(token);
    }
}