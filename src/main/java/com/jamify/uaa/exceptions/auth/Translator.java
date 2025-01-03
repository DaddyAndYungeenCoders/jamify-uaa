package com.jamify.uaa.exceptions.auth;

import com.jamify.uaa.exceptions.auth.custom.InvalidApiKeyException;
import com.jamify.uaa.exceptions.auth.custom.RefreshAccessTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class Translator extends ResponseEntityExceptionHandler {

    @ExceptionHandler({InvalidApiKeyException.class})
    public ResponseEntity<Object> handleInvalidApiKeyException(final Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({RefreshAccessTokenException.class})
    public ResponseEntity<Object> refreshAccessTokenException(final Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
