package ru.nikolay.service.user.service;

public class IllegalLoginException extends RuntimeException{
    public IllegalLoginException() {
        super();
    }

    public IllegalLoginException(String message) {
        super(message);
    }
}
