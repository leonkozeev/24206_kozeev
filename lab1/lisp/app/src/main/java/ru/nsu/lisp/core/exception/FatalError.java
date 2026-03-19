package ru.nsu.lisp.core.exception;

public class FatalError extends LispException {
    public FatalError(String message) {
        super(message);
    }
}
