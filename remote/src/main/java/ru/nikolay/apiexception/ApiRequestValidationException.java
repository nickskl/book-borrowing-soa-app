package ru.nikolay.apiexception;

public class ApiRequestValidationException extends RuntimeException {
    private ApiRequestValidationError error;

    public ApiRequestValidationException(ApiRequestValidationError error) {
        this.error = error;
    }

    @Override
    public String getMessage() {
        return error.toString();
    }

    public ApiRequestValidationError getError() {
        return error;
    }
}
