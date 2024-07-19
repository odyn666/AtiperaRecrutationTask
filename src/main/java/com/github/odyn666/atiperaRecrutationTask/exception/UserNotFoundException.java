package com.github.odyn666.atiperaRecrutationTask.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final int status;
    private final String message;



    public UserNotFoundException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
