package com.jamify.uaa.exceptions.auth.custom;

public class RefreshAccessTokenException extends RuntimeException {
    public RefreshAccessTokenException(String message) {
        super(message);
    }
}
