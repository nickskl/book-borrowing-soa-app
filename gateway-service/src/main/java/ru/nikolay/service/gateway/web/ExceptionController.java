package ru.nikolay.service.gateway.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.nikolay.apiexception.ApiRequestValidationException;
import ru.nikolay.remote.RemoteServiceAccessException;
import ru.nikolay.remote.RemoteServiceException;
import ru.nikolay.remote.RemoteServiceStatusException;
import ru.nikolay.responses.ErrorResponse;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RemoteServiceStatusException.class)
    public ErrorResponse onRemoteServiceStatusException(RemoteServiceStatusException exception) {
        logger.error("Remote service exception occurred. Exception message: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RemoteServiceAccessException.class)
    public ErrorResponse onRemoteServiceAccessException(RemoteServiceAccessException exception) {
        logger.error("Remote service exception occurred. Exception message: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse onGenericException(Exception exception) {
        logger.error("An exception occurred. Exception message: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApiRequestValidationException.class)
    public ErrorResponse onApiRequestValidationException(ApiRequestValidationException exception) {
        logger.error("Api request validation exception occurred. Exception message: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }
}
