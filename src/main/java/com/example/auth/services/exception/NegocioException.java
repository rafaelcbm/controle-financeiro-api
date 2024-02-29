package com.example.auth.services.exception;

public class NegocioException extends RuntimeException {

    public NegocioException(String message){
        super(message);
    }
}
