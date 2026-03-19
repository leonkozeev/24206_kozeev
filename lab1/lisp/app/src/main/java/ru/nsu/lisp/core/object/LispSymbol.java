package ru.nsu.lisp.core.object;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.interpreter.Lexer;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.interfaces.ParsingObject;

public class LispSymbol implements ParsingObject {
    private final static Logger logger = LogManager.getLogger(LispSymbol.class);
    private final String lit;

    public LispSymbol(String name) {
        lit = name;
    }

    public String getSymbol() {
        return lit;
    }

    @Override
    public LispObject eval(Environment env) {
        logger.debug("lookup symbol `{}`", lit);
        return env.lookup(lit);
    }

    @Override
    public ParsingObject parse(Lexer let) {
        return this;
    }
}