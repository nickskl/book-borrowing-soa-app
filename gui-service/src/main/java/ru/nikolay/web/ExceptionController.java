package ru.nikolay.web;

import org.springframework.http.HttpStatus;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.nikolay.apiexception.ApiRequestValidationException;
import ru.nikolay.remote.RemoteServiceAccessException;
import ru.nikolay.remote.RemoteServiceException;
import ru.nikolay.remote.RemoteServiceStatusException;

@ControllerAdvice
public class ExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String onNoHandlerFoundException(NoHandlerFoundException exception) {
        return "exceptions/404";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String onGenericException(Exception exception) {
        return "exceptions/SomethingWentWrong";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RemoteServiceAccessException.class)
    public String onRemoteServiceAccessException(RemoteServiceAccessException exception) {
        return "exceptions/RemoteServiceFailed";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RemoteServiceStatusException.class)
    public String onRemoteServiceStatusException(RemoteServiceStatusException exception) {
        if (exception.getStatus() == HttpStatus.BAD_REQUEST) {
            return "exceptions/ValidationFailed";
        }
        else {
            return "exceptions/RemoteServiceFailed";
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpStatusCodeException.class)
    public String onHttpStatusCodeException(HttpStatusCodeException ex) {
        if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return "exceptions/ValidationFailed";
        }
        else {
            return "exceptions/SomethingWentWrong";
        }
    }
}
