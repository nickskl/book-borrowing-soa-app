package ru.nikolay.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UserRequest {
    @NotNull
    @Size(min=3,max=32)
    private String login;
}
