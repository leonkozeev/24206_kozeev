package ru.nsu.lisp.core.exception.parse;

import ru.nsu.lisp.core.exception.LispException;

public abstract class ParseError extends LispException {
    public ParseError(String message) {
        super(message);
    }
    public ParseError(String message, Throwable cause) {
        super(message, cause);
    }
}
