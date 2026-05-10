package com.cts.demo.exception;

public class InvalidInventoryDataException extends RuntimeException {
    public InvalidInventoryDataException(String message) {
        super(message);
    }
}
