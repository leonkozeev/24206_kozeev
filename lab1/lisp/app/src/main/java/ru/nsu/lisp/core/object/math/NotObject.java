package ru.nsu.lisp.core.object.math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;
import ru.nsu.lisp.core.object.interfaces.BooleanObject;
import ru.nsu.lisp.core.object.interfaces.ExecutableObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;

public class NotObject implements ExecutableObject {
    private final static Logger logger = LogManager.getLogger(NotObject.class);
    public static final String NAME = "not";

    @Override
    public LispObject execute(Environment env, LispObject[] args) {
        if (args.length != 1) {
            logger.info("incorrect numbers of arguments!");
            throw new IncorrectNumberArguments(args.length, NAME, 1);
        }
        if (args[0].eval(env) instanceof BooleanObject num1) {} else {
            String t = args[0].getClass().getName();
            logger.info("invalid type `{}` in first argument!", t);
            throw new InvalidArgumentsType(t, NAME);
        }

        boolean n1 = num1.bool();

        boolean res = !n1;

        logger.debug("execute not `{}` is `{}`", n1, res);
        return new LispNumber(res ? 1 : 0);
    }

    @Override
    public LispObject eval(Environment env) {
        throw new IsNotEvalError(NAME);
    }
}