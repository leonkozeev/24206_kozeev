package ru.nsu.lisp.core.exception.eval;

public class NameError extends EvalError {
    public NameError(String name) {
        super("name `" + name + "` is not defined!");
    }
}
