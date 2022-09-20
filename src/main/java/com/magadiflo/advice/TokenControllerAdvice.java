package com.magadiflo.advice;

import com.magadiflo.exceptions.TokenRefreshException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

/**
 * @RestControllerAdvice. Permitirá que esta clase sea un controlador para manejar las excepciones. Es decir,
 * apenas se produzca una excepción esta clase va a realizar el tratamiento según el tipo de excepción producida.
 * */
@RestControllerAdvice
public class TokenControllerAdvice {

    @ExceptionHandler(value = TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        return new ErrorMessage(HttpStatus.FORBIDDEN.value(), new Date(), ex.getMessage(), request.getDescription(false));
    }

}
