package com.project.backend.exception;

public class NoRefreshAvailable extends RuntimeException {
    public NoRefreshAvailable(String message) {
        super(message);
    }
}
