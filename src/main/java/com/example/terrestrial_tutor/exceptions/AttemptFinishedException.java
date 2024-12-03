package com.example.terrestrial_tutor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AttemptFinishedException extends RuntimeException {
    public AttemptFinishedException(String message) {
        super(message);
    }
}
