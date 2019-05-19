package ru.nikolay.service.user.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.nikolay.requests.UserRequest;
import ru.nikolay.responses.UserResponse;
import ru.nikolay.service.user.domain.User;
import ru.nikolay.service.user.service.IllegalLoginException;
import ru.nikolay.service.user.service.UserService;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserRestController.class)
public class UserRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void getExistingUserTest() throws Exception {
        User expectedUserToFind = new User()
                .setId("0")
                .setLogin("0xBADC0DE");
        UserResponse response = expectedUserToFind.toResponse();
        given(userService.getById("0")).willReturn(expectedUserToFind);

        mvc.perform(get("/user/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUserToFind.getId())))
                .andExpect(jsonPath("$.login", is(expectedUserToFind.getLogin())));
    }

    @Test
    public void getNonexistentUserReturnsErrorTest() throws Exception {
        given(userService.getById("0")).willThrow(new NullPointerException("User not found"));

        mvc.perform(get("/user/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addNewUserTest() throws Exception {
        User expectedUserToAdd = new User().setId("AWAWA").setLogin("0xBADC0DE");
        UserRequest request = new UserRequest("0xBADC0DE");
        given(userService.add(request)).willReturn(expectedUserToAdd);

        mvc.perform(post("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/user/AWAWA"));
    }

    @Test
    public void addNewUserWithAlreadyExistingLoginReturnsError() throws Exception {
        UserRequest request = new UserRequest("0xBADC0DE");
        given(userService.add(request)).willThrow(new IllegalLoginException("Login already exists"));

        mvc.perform(post("/user/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUserTest() throws Exception {
        mvc.perform(delete("/user/0")).andExpect(status().isNoContent());
    }

    @Test
    public void onGenericExceptionTest() throws Exception {
        given(userService.getById(anyString())).willThrow(new RuntimeException("Some kind of error"));

        mvc.perform(get("/user/12345").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
