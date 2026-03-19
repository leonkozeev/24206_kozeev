package ru.nsu.lisp.core.object.special_symbol;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;
import ru.nsu.lisp.core.object.ClosureObject;
import ru.nsu.lisp.core.object.LispSymbol;
import ru.nsu.lisp.core.object.interfaces.ExecutableObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.list.LispList;

public class LambdaObject implements ExecutableObject {
    private final static Logger logger = LogManager.getLogger(LambdaObject.class);
    public static final String NAME = "lambda";

    @Override
    public LispObject execute(Environment env, LispObject[] args) {
        logger.debug("lambda defining");
        if (args.length < 2) {
            logger.debug("{} need at least two argument, LispList args, *LispObject body", NAME);
            throw new IncorrectNumberArguments(args.length, NAME, 2);
        }

        if (args[0] instanceof LispList arguments) {} else {
            String t = args[0].getClass().getName();
            logger.info("arguments must be LispList!", t);
            throw new InvalidArgumentsType(t, NAME);
        }

        // for (LispObject o: arguments.data) {
        //     if(!(o instanceof LispSymbol)) {
        //         String t = o.getClass().getName();
        //         logger.info("lambda argument must be LispSymbol: `{}` != {}", t, LispSymbol.class.getName());
        //         throw new InvalidArgumentsType(t, NAME);
        //     }
        // }

        try {
            return new ClosureObject(env, arguments.data.toArray(LispSymbol[]::new), Arrays.copyOfRange(args, 1, args.length));
        } catch (ArrayStoreException e) {
                logger.info("lambda argument must be LispSymbol");
                throw new InvalidArgumentsType("arg type", NAME);
        }
    }

    @Override
    public LispObject eval(Environment env) {
        throw new IsNotEvalError(NAME);
    }
}