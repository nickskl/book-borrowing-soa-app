package ru.nikolay.remote;

public interface RemoteUserService {
    boolean isAuthenticated(String token);

    boolean isAdmin(String token);

    TokenPair refreshToken(String refreshToken);
}
