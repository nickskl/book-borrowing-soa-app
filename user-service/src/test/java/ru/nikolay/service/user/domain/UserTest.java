package ru.nikolay.service.user.domain;

import org.junit.Test;
import ru.nikolay.responses.UserResponse;
import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void userToResponseTest() {
        UserResponse expectedResponse = new UserResponse("0", "0xBADC0DE");
        UserResponse actualResponse = new User()
                .setId("0")
                .setLogin("0xBADC0DE")
                .toResponse();
        assertEquals(expectedResponse, actualResponse);
    }
}
