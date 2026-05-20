package com.ilham.authservice.exceptions;

import com.ilham.authservice.enums.ErrorMessage;

public class CustomException extends RuntimeException{

    public CustomException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
