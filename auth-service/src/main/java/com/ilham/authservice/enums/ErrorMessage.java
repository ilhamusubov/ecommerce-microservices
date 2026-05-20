package com.ilham.authservice.enums;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    USER_NOT_FOUND("User not found"),
    EMAIL_ALREADY_EXISTS("Email already exists"),
    USER_NOT_VERIFIED("User not verified. Please verify OTP first."),
    REFRESH_TOKEN_NOT_FOUND("Refresh token not found"),
    REFRESH_TOKEN_EXPIRED("Refresh token expired"),
    USER_NOT_ACTIVE("User is not active"),
    INVALID_HEADER("Invalid authorization header"),
    INVALID_REQUEST("Invalid request"),
    INVALID_OTP("OTP expired or not found");


    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
