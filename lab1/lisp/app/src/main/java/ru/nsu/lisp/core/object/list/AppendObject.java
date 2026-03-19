package ru.nsu.lisp.core.object.list;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;
import ru.nsu.lisp.core.object.interfaces.ExecutableObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;

public class AppendObject implements ExecutableObject {
    private final static Logger logger = LogManager.getLogger(AppendObject.class);
    public static final String NAME = "append";

    @Override
    public LispObject execute(Environment env, LispObject[] args) {
        logger.debug("appending item to list");
        if (args.length != 2) {
            logger.info("incorrect number of arguments: `{}`, use ({} `item`, `list`)", NAME, args.length);
            throw new IncorrectNumberArguments(args.length, NAME, 2);
        }
        var obj1 = args[1].eval(env);
        if (obj1 instanceof LispList lst) {
            var item = args[0].eval(env);
            logger.debug("append `{}` in list `{}`", item.toString(), lst.toString());
            lst.data.add(item);
        } else {
            String t = obj1.getClass().getName();
            logger.info("invalid list type to get item: `{}`", t);
            throw new InvalidArgumentsType(t, NAME);
        }

        return this;

    }
    @Override
    public LispObject eval(Environment env) {
        throw new IsNotEvalError(NAME);
    }
}