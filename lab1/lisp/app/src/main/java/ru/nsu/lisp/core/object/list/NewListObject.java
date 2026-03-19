package ru.nsu.lisp.core.object.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.object.interfaces.ExecutableObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;

public class NewListObject implements ExecutableObject {
    private final static Logger logger = LogManager.getLogger(NewListObject.class);
    public static final String NAME = "list";

    @Override
    public LispObject execute(Environment env, LispObject[] args) {
        logger.debug("create list `{}`", args.toString());
        return new LispList(Arrays.stream(args).map(o -> o.eval(env)).collect(Collectors.toCollection(ArrayList::new)));
    }
    @Override
    public LispObject eval(Environment env) {
        throw new IsNotEvalError(NAME);
    }
}