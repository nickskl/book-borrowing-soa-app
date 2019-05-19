package ru.nikolay.service.bookBorrowing.service;

public class BookCurrentlyNotAvailableException extends RuntimeException {
    public BookCurrentlyNotAvailableException() {
        super();
    }

    public BookCurrentlyNotAvailableException(String message) {
        super(message);
    }
}
