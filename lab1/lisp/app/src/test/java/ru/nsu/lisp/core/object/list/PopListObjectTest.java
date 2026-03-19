package ru.nsu.lisp.core.object.list;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.IndexError;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;
import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.LispSymbol;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.math.LispNumber;

class PopListObjectTest {

    @Test void popLastRemovesItem() {
        var literal = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(10), new LispNumber(15), new LispNumber(20))));
        var quoteExpr = new LispList(Arrays.asList(new LispSymbol("quote"), literal));
        var pop = new PopListObject();
        pop.execute(Interpreter.baseEnv, new LispObject[]{ quoteExpr });
        assertEquals(2, literal.data.size());
        assertEquals(15, ((LispNumber) literal.data.get(literal.data.size()-1)).getNumber());
    }

    @Test void popIndexRemovesCorrectItem() {
        var literal = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(10), new LispNumber(15), new LispNumber(20))));
        var quoteExpr = new LispList(Arrays.asList(new LispSymbol("quote"), literal));
        var pop = new PopListObject();
        pop.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(0), quoteExpr });
        assertEquals(2, literal.data.size());
        assertEquals(15, ((LispNumber) literal.data.get(0)).getNumber());
    }

    @Test void incorrectNumArgsThrows() {
        var pop = new PopListObject();
        assertThrows(IncorrectNumberArguments.class, () -> pop.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(1), new LispNumber(2), new LispNumber(3) }));
    }

    @Test void invalidIndexTypeThrows() {
        var literalIdx = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(1))));
        var quoteIdx = new LispList(Arrays.asList(new LispSymbol("quote"), literalIdx));
        var literal = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(10))));
        var quoteList = new LispList(Arrays.asList(new LispSymbol("quote"), literal));
        var pop = new PopListObject();
        assertThrows(InvalidArgumentsType.class, () -> pop.execute(Interpreter.baseEnv, new LispObject[]{ quoteIdx, quoteList }));
    }

    @Test void negativeIndexThrows() {
        var literal = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(10))));
        var quoteList = new LispList(Arrays.asList(new LispSymbol("quote"), literal));
        var pop = new PopListObject();
        assertThrows(IndexError.class, () -> pop.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(-1), quoteList }));
    }

    @Test void indexOutOfRangeThrows() {
        var literal = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(10))));
        var quoteList = new LispList(Arrays.asList(new LispSymbol("quote"), literal));
        var pop = new PopListObject();
        assertThrows(IndexError.class, () -> pop.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(5), quoteList }));
    }

    @Test void PopEmptyListThrows() {
        var pop = new PopListObject();
        assertThrows(IndexError.class, () -> pop.execute(Interpreter.baseEnv, new LispObject[]{ new LispList()}));
    }
    @Test void invalidListTypeThrows() {
        var pop = new PopListObject();
        assertThrows(InvalidArgumentsType.class, () -> pop.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(0), new LispNumber(1) }));
    }

    @Test void singleArgInvalidListTypeThrows() {
        var pop = new PopListObject();
        assertThrows(InvalidArgumentsType.class, () -> pop.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(1) }));
    }

    @Test void evalThrows() {
        assertThrows(IsNotEvalError.class, () -> new PopListObject().eval(Interpreter.baseEnv));
    }
}
