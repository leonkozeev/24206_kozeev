package ru.nsu.lisp.core.object.special_symbol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.object.interfaces.ExecutableObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;

public class QuoteObject implements ExecutableObject {
    private final static Logger logger = LogManager.getLogger(QuoteObject.class);
    public static final String NAME = "quote";
    
    @Override
    public LispObject execute(Environment env, LispObject[] args) {
        logger.debug("quoting");
        if (args.length != 1) {
            logger.debug("{} need only one argument", NAME);
            throw new IncorrectNumberArguments(args.length, NAME, 1);
        }
        return args[0];

    }

    @Override
    public LispObject eval(Environment env) {
        throw new IsNotEvalError(NAME);
    }
}