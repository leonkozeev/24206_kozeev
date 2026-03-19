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
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;

class AppendObjectTest {

    @Test void appendAddsItem() {
        var literal = new LispList(new ArrayList<>(Arrays.asList(new LispNumber(10), new LispNumber(15))));
        var quoteExpr = new LispList(Arrays.asList(new LispSymbol("quote"), literal));
        var append = new AppendObject();
        append.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(99), quoteExpr });
        assertEquals(3, literal.data.size());
        assertEquals(99, ((LispNumber) literal.data.get(2)).getNumber());
    }

    @Test void incorrectNumArgsThrows() {
        var append = new AppendObject();
        assertThrows(IncorrectNumberArguments.class, () -> append.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(1) }));
    }

    @Test void invalidListTypeThrows() {
        var append = new AppendObject();
        assertThrows(InvalidArgumentsType.class, () -> append.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(1), new LispNumber(2) }));
    }

    @Test void evalThrows() {
        assertThrows(IsNotEvalError.class, () -> new AppendObject().eval(Interpreter.baseEnv));
    }
}
