package ru.nsu.lisp.core.exception;

public abstract class LispException extends RuntimeException {
    public LispException(String message) {
        super(message);
    }
    public LispException(String message, Throwable cause) {
        super(message, cause);
    }
}