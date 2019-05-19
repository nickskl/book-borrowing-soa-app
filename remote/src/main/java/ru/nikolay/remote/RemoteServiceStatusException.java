package ru.nikolay.remote;

import org.springframework.http.HttpStatus;

public class RemoteServiceStatusException extends RemoteServiceException {
    private HttpStatus status;

    public RemoteServiceStatusException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
