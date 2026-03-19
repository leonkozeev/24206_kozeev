package ru.nsu.lisp.core.object.math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;
import ru.nsu.lisp.core.exception.execute.ZeroDivisionError;
import ru.nsu.lisp.core.object.interfaces.ExecutableObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;

public class DivObject implements ExecutableObject {
    private final static Logger logger = LogManager.getLogger(DivObject.class);
    public static final String NAME = "/";

    @Override
    public LispObject execute(Environment env, LispObject[] args) {
        if (args.length != 2) {
            logger.info("incorrect numbers of arguments!");
            throw new IncorrectNumberArguments(args.length, NAME, 2);
        }
        if (args[0].eval(env) instanceof LispNumber num1) {} else {
            String t = args[0].getClass().getName();
            logger.info("invalid type `{}` in first argument!", t);
            throw new InvalidArgumentsType(t, NAME);
        }

        if (args[1].eval(env) instanceof LispNumber num2) {} else {
            String t = args[1].getClass().getName();
            logger.info("invalid type `{}` in second argument!", t);
            throw new InvalidArgumentsType(t, NAME);
        }

        int n1 = num1.getNumber();
        int n2 = num2.getNumber();

        if (n2 == 0) {
            logger.info("can't divide by zero: `{} / 0`", n1);
            throw new ZeroDivisionError(n1);
        }

        int res = n1 / n2;

        logger.debug("execute`{}` / `{}` = `{}`", n1, n2, res);
        return new LispNumber(res);
    }

    @Override
    public LispObject eval(Environment env) {
        throw new IsNotEvalError(NAME);
    }
}