package ru.nsu.lisp.core.object.list;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.IndexError;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;
import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.LispSymbol;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.math.LispNumber;

class GetListObjectTest {

    @Test void getReturnsItem() {
        var literal = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(10), new LispNumber(15))));
        var quoteExpr = new LispList(Arrays.asList(new LispSymbol("quote"), literal));
        var get = new GetListObject();
        var res = (LispNumber) get.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(1), quoteExpr });
        assertEquals(15, res.getNumber());
    }

    @Test void incorrectNumArgsThrows() {
        var get = new GetListObject();
        assertThrows(IncorrectNumberArguments.class, () -> get.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(1) }));
    }

    @Test void invalidIndexTypeThrows() {
        var literalIndex = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(1))));
        var quoteIndex = new LispList(Arrays.asList(new LispSymbol("quote"), literalIndex));
        var literal = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(10), new LispNumber(20))));
        var quoteList = new LispList(Arrays.asList(new LispSymbol("quote"), literal));
        var get = new GetListObject();
        assertThrows(InvalidArgumentsType.class, () -> get.execute(Interpreter.baseEnv, new LispObject[]{ quoteIndex, quoteList }));
    }

    @Test void negativeIndexThrows() {
        var literal = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(10))));
        var quoteList = new LispList(Arrays.asList(new LispSymbol("quote"), literal));
        var get = new GetListObject();
        assertThrows(IndexError.class, () -> get.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(-1), quoteList }));
    }

    @Test void indexOutOfRangeThrows() {
        var literal = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(10))));
        var quoteList = new LispList(Arrays.asList(new LispSymbol("quote"), literal));
        var get = new GetListObject();
        assertThrows(IndexError.class, () -> get.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(5), quoteList }));
    }

    @Test void invalidListTypeThrows() {
        var get = new GetListObject();
        assertThrows(InvalidArgumentsType.class, () -> get.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(0), new LispNumber(1) }));
    }
}
