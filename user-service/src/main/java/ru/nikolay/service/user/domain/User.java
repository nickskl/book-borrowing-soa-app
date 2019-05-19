package ru.nikolay.service.user.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import ru.nikolay.responses.UserResponse;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id private String id;
    private String login;
    private String password;
    private Role role;

    public UserResponse toResponse() {
        return new UserResponse(id, login);
    }
}
