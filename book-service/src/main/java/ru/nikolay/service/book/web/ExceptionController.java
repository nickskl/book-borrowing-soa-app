package ru.nikolay.service.book.web;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.nikolay.apiexception.ApiFieldError;
import ru.nikolay.apiexception.ApiGlobalError;
import ru.nikolay.apiexception.ApiRequestValidationError;
import ru.nikolay.apiexception.ApiRequestValidationException;
import ru.nikolay.responses.ErrorResponse;

import javax.validation.ValidationException;
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
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse onValidationException(MethodArgumentNotValidException exception) {
        logger.error("An exception occurred. Exception message: " + exception.getMessage());
        ApiRequestValidationError error = new ApiRequestValidationError(
                exception.getBindingResult().getFieldErrors().stream()
                        .map(x -> new ApiFieldError(x.getField(), x.getCode(),
                                x.getRejectedValue(), x.getDefaultMessage())).collect(Collectors.toList()),
                exception.getBindingResult().getGlobalErrors().stream()
                        .map(x -> new ApiGlobalError(x.getCode(), x.getDefaultMessage()))
                        .collect(Collectors.toList()));
        String ret = error.toString();
        logger.debug("Error response: " + ret);
        return new ErrorResponse(ret);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse onGenericException(Exception exception) {
        logger.error("An exception occurred. Exception message: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }
}
