package ru.nikolay.service.user.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.nikolay.requests.UserRequest;
import ru.nikolay.service.user.domain.User;
import ru.nikolay.service.user.repository.UserRepository;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceImplTest {
    @MockBean
    private UserRepository mockedUserRepository;

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void getUserByIdTest() {
        User expectedUserToFind = new User()
                .setId("0")
                .setLogin("0xBADC0DE");
        given(mockedUserRepository.findOne("0")).willReturn(expectedUserToFind);

        User actualUserFound = userService.getById("0");

        assertEquals(expectedUserToFind, actualUserFound);
    }

    @Test(expected = NullPointerException.class)
    public void getUserByIdThrowsOnNonexistentUserTest() {
        given(mockedUserRepository.findOne(anyString())).willReturn(null);
        userService.getById("0");
    }

    @Test
    public void addUserTest() {
        User expectedUserToBeAdded = new User().setLogin("0xBADC0DE");
        UserRequest request = new UserRequest("0xBADC0DE");
        given(mockedUserRepository.findFirstByLogin("0xBADC0DE")).willReturn(null);
        given(mockedUserRepository.save(expectedUserToBeAdded)).willReturn(expectedUserToBeAdded);

        User actualUserAdded = userService.add(request);

        assertEquals("0xBADC0DE", actualUserAdded.getLogin());
        verify(mockedUserRepository, times(1)).save(expectedUserToBeAdded);
    }

    @Test(expected = IllegalLoginException.class)
    public void addUserThrowsOnEmptyLoginTest() {
        UserRequest request = new UserRequest("");

        userService.add(request);
    }

    @Test(expected = IllegalLoginException.class)
    public void addUserThrowsOnNullLoginTest() {
        UserRequest request = new UserRequest(null);

        userService.add(request);
    }

    @Test(expected = IllegalLoginException.class)
    public void addUserThrowsOnIllegalCharactersInLoginTest() {
        UserRequest request = new UserRequest("HELLO WORLD!");

        userService.add(request);
    }

    @Test(expected = IllegalLoginException.class)
    public void addUserThrowsOnAlreadyExistingLoginTest() {
        User existingUser = new User().setLogin("0xBADC0DE");
        UserRequest request = new UserRequest("0xBADC0DE");

        given(mockedUserRepository.findFirstByLogin("0xBADC0DE")).willReturn(existingUser);

        userService.add(request);
    }

    @Test
    public void deleteUserTest() {
        userService.delete("Hello world!");
        verify(mockedUserRepository, times(1)).delete("Hello world!");
    }
}
