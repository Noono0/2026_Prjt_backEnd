package com.noonoo.prjtbackend.file.exception;

public class NotAnImageFileException extends IllegalArgumentException {

    public NotAnImageFileException(String message) {
        super(message);
    }
}
