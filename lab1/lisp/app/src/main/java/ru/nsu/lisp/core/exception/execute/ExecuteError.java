package ru.nsu.lisp.core.exception.execute;

import ru.nsu.lisp.core.exception.LispException;

public abstract class ExecuteError extends LispException {
    public ExecuteError(String message) {
        super(message);
    }
    public ExecuteError(String message, Throwable cause) {
        super(message, cause);
    }
}
