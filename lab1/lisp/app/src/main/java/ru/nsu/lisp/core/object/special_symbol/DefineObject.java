package ru.nsu.lisp.core.object.special_symbol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;
import ru.nsu.lisp.core.object.LispSymbol;
import ru.nsu.lisp.core.object.interfaces.ExecutableObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;

public class DefineObject implements ExecutableObject {
    private final static Logger logger = LogManager.getLogger(DefineObject.class);
    public static final String NAME = "define";

    @Override
    public LispObject execute(Environment env, LispObject[] args) {
        logger.debug("defining");

        if (args.length != 2) {
            throw new IncorrectNumberArguments(args.length, NAME, 2);
        }

        if (args[0] instanceof LispSymbol symbol_name) {
            String name = symbol_name.getSymbol();
            var obj = args[1].eval(env);
            logger.debug("define `{}` to `{}`", name, obj.toString());
            env.regist(name, obj);
        } else {
            String t = args[0].getClass().getName();
            logger.info("invalid define name type: `{}`, need LispSymbol", t);
            throw new InvalidArgumentsType(t, NAME);
        }

        return this;
    }

    @Override
    public LispObject eval(Environment env) {
        throw new IsNotEvalError(NAME);
    }
}