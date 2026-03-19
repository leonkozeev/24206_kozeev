package ru.nsu.lisp.core.object.interfaces;


import ru.nsu.lisp.core.ParsingFactory;
import ru.nsu.lisp.core.interpreter.Lexer;

public interface ParsingObject extends LispObject {
    ParsingFactory parsingFactory = new ParsingFactory(); // some setup factory (look factory contructor)
    ParsingObject parse(Lexer lex);
}