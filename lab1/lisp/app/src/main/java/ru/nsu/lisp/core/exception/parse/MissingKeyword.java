package ru.nsu.lisp.core.exception.parse;

public class MissingKeyword extends ParseError {
    public MissingKeyword(String after, String then) {
        super("expected `" + then + "` after `" + after + "`");
    }
}
