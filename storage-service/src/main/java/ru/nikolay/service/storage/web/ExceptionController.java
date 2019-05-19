package ru.nikolay.service.storage.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.nikolay.responses.ErrorResponse;

import java.util.stream.Collectors;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NullPointerException.class)
    public ErrorResponse onNullPointerException(NullPointerException exception) {
        logger.error("Null pointer exception occurred. Exception message: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse onIllegalLoginException(IllegalArgumentException exception) {
        logger.error("Illegal argument exception occurred. Exception message: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse onValidationException(MethodArgumentNotValidException exception) {
        logger.error("An exception occurred. Exception message: " + exception.getMessage());
        return new ErrorResponse(exception.getBindingResult().getFieldErrors().stream().map(x -> x.getField()
                + " " + x.getDefaultMessage())
                .collect(Collectors.toList()).toString());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse onGenericException(Exception exception) {
        logger.error("An exception occurred. Exception message: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }
}
