package ru.nsu.lisp.core.object.math;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.interpreter.Lexer;
import ru.nsu.lisp.core.object.interfaces.BooleanObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.interfaces.ParsingObject;

public class LispNumber implements ParsingObject, BooleanObject {
    public final int num;

    public LispNumber(int num) {
        this.num = num;
    }

    @Override
    public ParsingObject parse(Lexer lex) {
        return this;
    }

    @Override
    public LispObject eval(Environment env) {
        return this;
    }

    @Override
    public boolean bool() {
        return num != 0;
    }

    public int getNumber() {
        return num;
    }
}