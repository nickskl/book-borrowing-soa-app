package ru.nikolay.service.gateway.web.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.nikolay.responses.UserResponse;

@Data
@Accessors(chain = true)
public class UserComposite {
    private String userId;
    private String login;

    public static UserComposite fromResponse(UserResponse userResponse) {
        return new UserComposite()
                .setUserId(userResponse.getId())
                .setLogin(userResponse.getLogin());
    }
}
