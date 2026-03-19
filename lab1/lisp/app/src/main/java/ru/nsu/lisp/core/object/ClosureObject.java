package ru.nsu.lisp.core.object;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.FatalError;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.object.interfaces.ExecutableObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;

public class ClosureObject implements ExecutableObject {
    private final static Logger logger = LogManager.getLogger(ClosureObject.class);
    public static final String NAME = "lambda func";
    private final Environment parent;
    private final LispSymbol[] arguments;
    private final LispObject[] body;
    
    public ClosureObject(Environment parent, LispSymbol[] arguments, LispObject[] body) {
        this.parent = parent;
        this.arguments = arguments;
        assert body.length != 0;
        this.body = body;
    }

    @Override
    public LispObject execute(Environment env, LispObject[] args) {
        logger.debug("executing closure");
        if (args.length != arguments.length) {
            logger.info("arguments must have same number of giving args: `{} != {}`", arguments.length, args.length);
            throw new IncorrectNumberArguments(args.length, "lambda func", arguments.length);
        }

        logger.debug("create environment");
        var cenv = new Environment(parent);
        
        for (int i = 0; i < arguments.length; i++) {
            String name = arguments[i].getSymbol();
            logger.debug("regist `{}` to environment", name);
            cenv.regist(name, args[i].eval(env));
        }

        LispObject res = null;
        logger.debug("computing `{}` commands", body.length);
        for (LispObject b: body) {
            res = b.eval(cenv);
            logger.debug("eval body `{}`, get result: `{}`", b.getClass().getName(), res.getClass().getName());
        }

        if (res == null) {
            logger.error("get `null` result!");
            throw new FatalError("get `null` result!");
        }

        return res;
    }

    @Override
    public LispObject eval(Environment env) {
        throw new IsNotEvalError(NAME);
    }
}