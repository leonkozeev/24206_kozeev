package ru.nsu.lisp.core.exception.execute;

public class DoNotExecutableError extends ExecuteError {
    public DoNotExecutableError(String name) {
        super("object `" + name + "` is not executable!");
    }
}
