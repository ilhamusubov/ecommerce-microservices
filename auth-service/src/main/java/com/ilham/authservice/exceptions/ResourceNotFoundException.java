package com.ilham.authservice.exceptions;

import com.ilham.authservice.enums.ErrorMessage;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
