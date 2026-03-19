package ru.nsu.lisp.core.object.special_symbol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;
import ru.nsu.lisp.core.object.interfaces.BooleanObject;
import ru.nsu.lisp.core.object.interfaces.ExecutableObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;

public class IfObject implements ExecutableObject {
    private final static Logger logger = LogManager.getLogger(IfObject.class);
    public static final String NAME = "if";
    
    @Override
    public LispObject execute(Environment env, LispObject[] args) {
        logger.debug("if condition");
        if (args.length != 3) {
            logger.debug("{} need only three argument", NAME);
            throw new IncorrectNumberArguments(args.length, NAME, 3);
        }
        
        var obj0 = args[0].eval(env);
        if (obj0 instanceof BooleanObject cond) {
            logger.debug("get condition: `{}`", cond.bool());
        } else {
            String t = obj0.getClass().getName();
            logger.info("type `{}` must be Boolean", t);
            throw new InvalidArgumentsType(t, NAME);
        }

        return cond.bool() ? args[1].eval(env) : args[2].eval(env);
    }

    @Override
    public LispObject eval(Environment env) {
        throw new IsNotEvalError(NAME);
    }
}