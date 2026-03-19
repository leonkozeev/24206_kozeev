package ru.nsu.lisp.core;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.exception.FatalError;
import ru.nsu.lisp.core.object.LispString;
import ru.nsu.lisp.core.object.LispSymbol;
import ru.nsu.lisp.core.object.interfaces.ParsingObject;
import ru.nsu.lisp.core.object.list.LispList;
import ru.nsu.lisp.core.object.math.LispNumber;

public class ParsingFactory {
    private final static Logger logger = LogManager.getLogger(ParsingFactory.class);
    private final Map<String, Class<?>> data = Map.of(
        LispList.BEGIN_SYMBOL, LispList.class,
        LispString.BEGIN_SYMBOL, LispString.class
    );

    public ParsingObject get(String name) {
        logger.debug("parsing `{}`", name);
        try {
            int num = Integer.parseInt(name);
            logger.debug("parse number `{}`", num);
            return new LispNumber(num);
        } catch (NumberFormatException e) {}

        Class<?> clazz = data.get(name);
        if (clazz == null) {
            logger.debug("parse symbol `{}`", name);
            return new LispSymbol(name);
        }

        try {
            logger.debug("parse object `{}`", clazz.getName());
            return (ParsingObject) clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            logger.error("cannot create Object `{}` object!", name, e);
            throw new FatalError("cannot create `" + name + "` parse object!");
        }
    }
}
