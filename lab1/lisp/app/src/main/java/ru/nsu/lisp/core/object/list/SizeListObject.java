package ru.nsu.lisp.core.object.list;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;
import ru.nsu.lisp.core.object.interfaces.ExecutableObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.math.LispNumber;

public class SizeListObject implements ExecutableObject {
    private final static Logger logger = LogManager.getLogger(SizeListObject.class);
    public static final String NAME = "size";

    @Override
    public LispObject execute(Environment env, LispObject[] args) {
        logger.debug("getting list size");
        if (args.length != 1) {
            logger.info("incorrect number of arguments: `{}`", args.length);
            throw new IncorrectNumberArguments(args.length, NAME, 1);
        }
        var obj0 = args[0].eval(env);
        if (obj0 instanceof LispList lst) {
            int size = lst.data.size();
            logger.debug("get list size: `{}`", size);
            return new LispNumber(size);
        } else {
            String t = obj0.getClass().getName();
            logger.info("invalid type to get size: `{}`", t);
            throw new InvalidArgumentsType(t, NAME);
        }
    }
    @Override
    public LispObject eval(Environment env) {
        throw new IsNotEvalError(NAME);
    }
}