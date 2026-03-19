package ru.nsu.lisp.core.object;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.parse.MissingKeyword;
import ru.nsu.lisp.core.interpreter.Lexer;
import ru.nsu.lisp.core.object.interfaces.BooleanObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.interfaces.ParsingObject;

public class LispString implements ParsingObject, BooleanObject {
    private String str = null;

    public final static String BEGIN_SYMBOL = "\"";
    public final static String END_SYMBOL = "\"";

    @Override
    public ParsingObject parse(Lexer lex) {
        String c;
        StringBuilder sb = new StringBuilder();
        boolean whitespace = false;
        while ((c = lex.next()) != null && !END_SYMBOL.equals(c)) {
            if (whitespace) sb.append(" ");
            sb.append(c);
            whitespace = true;
        }
        if (c == null) {
            throw new MissingKeyword(BEGIN_SYMBOL, END_SYMBOL);
        }
        str = sb.toString();
        return this;
    }

    @Override
    public LispObject eval(Environment env) {
        return this;
    }

    @Override
    public boolean bool() {
        return str != null && !str.isEmpty();
    }
}