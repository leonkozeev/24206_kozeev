package ru.nsu.lisp.core.object.list;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.IndexError;
import ru.nsu.lisp.core.exception.execute.InvalidArgument;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;
import ru.nsu.lisp.core.object.interfaces.ExecutableObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.math.LispNumber;

public class GetListObject implements ExecutableObject {
    private final static Logger logger = LogManager.getLogger(SizeListObject.class);
    public static final String NAME = "get";

    @Override
    public LispObject execute(Environment env, LispObject[] args) {
        logger.debug("getting item from list");
        if (args.length != 2) {
            logger.info("incorrect number of arguments: `{}`, use ({} `index`, `list`)", NAME, args.length);
            throw new IncorrectNumberArguments(args.length, NAME, 2);
        }
        int i;
        var obj0 = args[0].eval(env);
        if (obj0 instanceof LispNumber idx) {
            i = idx.getNumber();
            logger.debug("get index `{}`", i);
            if (i < 0) {
                logger.info("get negative index: `{}`", i);
                throw new IndexError("index must be positive!");
            }
        } else {
            String t = obj0.getClass().getName();
            logger.info("invalid index type to get: `{}`", t);
            throw new InvalidArgumentsType(t, NAME);
        }

        var obj1 = args[1].eval(env);
        if (obj1 instanceof LispList lst) {
            var l = lst.data;
            logger.debug("get list `{}`", lst.toString());
            if (i >= l.size()) {
                logger.info("index out of range: `{}`", i);
                throw new IndexError("index out of range: `" + String.valueOf(i) +  " >= " + String.valueOf(l.size()) + "`");
            }
            logger.debug("get item");
            return l.get(i);
        } else {
            String t = obj1.getClass().getName();
            logger.info("invalid list type to get item: `{}`", t);
            throw new InvalidArgumentsType(t, NAME);
        }

    }
    @Override
    public LispObject eval(Environment env) {
        throw new IsNotEvalError(NAME);
    }
}