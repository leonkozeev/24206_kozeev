package ru.nsu.lisp.core.exception.execute;

public class IncorrectNumberArguments extends InvalidArgument {
    public IncorrectNumberArguments(int num, String func, int cor) {
        super("incorrect number `" + String.valueOf(num) + "` giving in `" + func +"`, try give " + String.valueOf(cor));
    }
}
