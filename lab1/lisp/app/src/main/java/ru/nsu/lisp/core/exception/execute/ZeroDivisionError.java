package ru.nsu.lisp.core.exception.execute;

public class ZeroDivisionError extends ExecuteError {
    public ZeroDivisionError(int n) {
        super("can`t division `" + String.valueOf(n) + "` by zero!");
    }
}
