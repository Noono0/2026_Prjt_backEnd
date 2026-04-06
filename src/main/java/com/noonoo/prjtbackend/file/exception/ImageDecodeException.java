package com.noonoo.prjtbackend.file.exception;

public class ImageDecodeException extends IllegalArgumentException {

    public ImageDecodeException(String message) {
        super(message);
    }

    public ImageDecodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
