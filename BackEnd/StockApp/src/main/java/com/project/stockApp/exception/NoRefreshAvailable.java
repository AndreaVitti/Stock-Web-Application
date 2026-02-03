package com.project.stockApp.exception;

public class NoRefreshAvailable extends RuntimeException {
    public NoRefreshAvailable(String message) {
        super(message);
    }
}
