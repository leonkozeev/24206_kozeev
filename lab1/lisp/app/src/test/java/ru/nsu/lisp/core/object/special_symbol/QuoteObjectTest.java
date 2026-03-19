package ru.nsu.lisp.core.object.special_symbol;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.LispSymbol;
import ru.nsu.lisp.core.object.list.LispList;
import ru.nsu.lisp.core.object.math.LispNumber;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;

class QuoteObjectTest {

    @Test void returnsArgumentUnchanged() {
        var quote = new QuoteObject();
        var env = new Environment(Interpreter.baseEnv);

        var sym = new LispSymbol("abc");
        var res = quote.execute(env, new LispObject[]{ sym });
        assertSame(sym, res);

        var list = new LispList(Arrays.asList(new LispSymbol("+"), new LispNumber(1), new LispNumber(2)));
        var res2 = quote.execute(env, new LispObject[]{ list });
        assertSame(list, res2);
    }

    @Test void incorrectNumberOfArgumentsThrows() {
        var quote = new QuoteObject();
        var env = new Environment(Interpreter.baseEnv);
        assertThrows(IncorrectNumberArguments.class, () -> quote.execute(env, new LispObject[]{}));
    }

    @Test void evalThrows() {
        assertThrows(IsNotEvalError.class, () -> new QuoteObject().eval(new Environment(Interpreter.baseEnv)));
    }
}
