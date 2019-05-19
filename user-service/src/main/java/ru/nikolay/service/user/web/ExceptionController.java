package ru.nikolay.service.user.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.nikolay.responses.ErrorResponse;
import ru.nikolay.service.user.service.IllegalLoginException;

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
    @ExceptionHandler(IllegalLoginException.class)
    public ErrorResponse onIllegalLoginException(IllegalLoginException exception) {
        logger.error("Illegal login exception occurred. Exception message: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse onGenericException(Exception exception) {
        logger.error("An exception occurred. Exception message: " + exception.getMessage());
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
}
