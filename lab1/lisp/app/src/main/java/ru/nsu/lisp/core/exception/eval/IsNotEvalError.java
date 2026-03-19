package ru.nsu.lisp.core.exception.eval;

public class IsNotEvalError extends EvalError {
    public IsNotEvalError(String name) {
        super("object `" + name + "` can't eval, maybe execute?");
    }
}
