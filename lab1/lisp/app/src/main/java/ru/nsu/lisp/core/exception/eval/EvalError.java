package ru.nsu.lisp.core.exception.eval;

import ru.nsu.lisp.core.exception.LispException;

public abstract class EvalError extends LispException {
    public EvalError(String message) {
        super(message);
    }
    public EvalError(String message, Throwable cause) {
        super(message, cause);
    }
}
