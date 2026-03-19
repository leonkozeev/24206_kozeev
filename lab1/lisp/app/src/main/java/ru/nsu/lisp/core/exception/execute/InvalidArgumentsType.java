package ru.nsu.lisp.core.exception.execute;

public class InvalidArgumentsType extends InvalidArgument {
    public InvalidArgumentsType(String type, String func) {
        super("type `" + type + "` is invalid in `" + func +"`");
    }
}
