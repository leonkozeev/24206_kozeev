package ru.nsu.lisp.core.object.list;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.ArrayList;

import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.math.LispNumber;
import ru.nsu.lisp.core.object.LispSymbol;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;

class SizeListObjectTest {

    @Test void sizeReturnsCorrect() {
        var size = new SizeListObject();
        var literal = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(10), new LispNumber(20))));
        var quoteExpr = new LispList(Arrays.asList(new LispSymbol("quote"), literal));
        var res = (LispNumber) size.execute(Interpreter.baseEnv, new LispObject[]{ quoteExpr });
        assertEquals(2, res.getNumber());
    }

    @Test void incorrectNumArgsThrows() {
        var size = new SizeListObject();
        assertThrows(IncorrectNumberArguments.class, () -> size.execute(Interpreter.baseEnv, new LispObject[]{}));
    }

    @Test void invalidTypeThrows() {
        var size = new SizeListObject();
        assertThrows(InvalidArgumentsType.class, () -> size.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(1) }));
    }
}
