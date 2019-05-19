package ru.nikolay.remote;

import lombok.Data;

@Data
public class TokenPair {
    private String accessToken;
    private String refreshToken;
}
