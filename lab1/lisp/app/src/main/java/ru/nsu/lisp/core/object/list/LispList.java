package ru.nsu.lisp.core.object.list;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.execute.DoNotExecutableError;
import ru.nsu.lisp.core.exception.parse.MissingKeyword;
import ru.nsu.lisp.core.interpreter.Lexer;
import ru.nsu.lisp.core.object.interfaces.BooleanObject;
import ru.nsu.lisp.core.object.interfaces.ExecutableObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.interfaces.ParsingObject;

public class LispList implements ParsingObject, BooleanObject {
    private final static Logger logger = LogManager.getLogger(LispList.class);
    public final List<LispObject> data;

    public LispList() {
        this.data = new ArrayList<>();
    }

    public LispList(List<LispObject> data) {
        this.data = data;
    }


    public final static String BEGIN_SYMBOL = "(";
    public final static String END_SYMBOL = ")";

    @Override
    public ParsingObject parse(Lexer lex) {
        String c;
        while ((c = lex.next()) != null && !END_SYMBOL.equals(c)) {
            logger.debug("try add `{}` in list", c);
            data.add(parsingFactory.get(c).parse(lex));
        }
        if (c == null) {
            throw new MissingKeyword(BEGIN_SYMBOL, END_SYMBOL);
        }
        return this;
    }

    @Override
    public LispObject eval(Environment env) {
        if (!data.isEmpty()) {
            var obj = data.get(0).eval(env);
            var args = data.subList(1, data.size());
            logger.debug("processing list execute: `{}`", obj.getClass().getName());

            if (obj instanceof ExecutableObject func) {
                logger.debug("list execution");
                return func.execute(env, args.toArray(LispObject[]::new));
            } else {
                String className = obj.getClass().getName();
                logger.warn("unsupported executable type: {}", className);
                throw new DoNotExecutableError(className);
            }
        }
        logger.debug("return empty LispList");
        return this;
    }

    @Override
    public boolean bool() {
        return !data.isEmpty();
    }
}