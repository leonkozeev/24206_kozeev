package ru.nsu.lisp.core.object;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.eval.NameError;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.list.LispList;
import ru.nsu.lisp.core.object.math.LispNumber;

class ClosureObjectTest {
    @Test void returnsLastBodyEval() {
        var closure = new ClosureObject(Interpreter.baseEnv, new LispSymbol[]{ new LispSymbol("a") }, new LispObject[]{ new LispNumber(5), new LispNumber(7) });
        var res = (LispNumber) closure.execute(new Environment(Interpreter.baseEnv), new LispObject[]{ new LispNumber(1) });
        assertEquals(7, res.getNumber());
    }

    @Test void incorrectNumberOfArgsThrows() {
        var closure = new ClosureObject(Interpreter.baseEnv, new LispSymbol[]{ new LispSymbol("a"), new LispSymbol("b") }, new LispObject[]{ new LispNumber(1) });
        assertThrows(IncorrectNumberArguments.class, () -> closure.execute(new Environment(Interpreter.baseEnv), new LispObject[]{ new LispNumber(1) }));
    }

    @Test void BodyEmptyThrows() {
        assertThrows(AssertionError.class, () -> new ClosureObject(Interpreter.baseEnv, new LispSymbol[]{ new LispSymbol("a") }, new LispObject[0]));
    }

    @Test void evalThrows() {
        var closure = new ClosureObject(Interpreter.baseEnv, new LispSymbol[0], new LispObject[]{ new LispNumber(1) });
        assertThrows(IsNotEvalError.class, () -> closure.eval(new Environment(Interpreter.baseEnv)));
    }

    @Test void capturesParentEnv() {
        var parent = new Environment(Interpreter.baseEnv);
        parent.regist("z", new LispNumber(7));
        var closure = new ClosureObject(parent, new LispSymbol[]{ new LispSymbol("x") }, new LispObject[]{ new LispList(Arrays.asList(new LispSymbol("+"), new LispSymbol("x"), new LispSymbol("z"))) });
        var res = (LispNumber) closure.execute(new Environment(Interpreter.baseEnv), new LispObject[]{ new LispNumber(3) });
        assertEquals(10, res.getNumber());
    }

    @Test void argsEvaluationUsesCallEnv() {
        var closure = new ClosureObject(Interpreter.baseEnv, new LispSymbol[]{ new LispSymbol("x") }, new LispObject[]{ new LispSymbol("x") });
        var callEnv = new Environment(Interpreter.baseEnv);
        callEnv.regist("v", new LispNumber(99));
        var res = (LispNumber) closure.execute(callEnv, new LispObject[]{ new LispSymbol("v") });
        assertEquals(99, res.getNumber());
    }

    @Test void argsEvaluationMissingNameThrows() {
        var closure = new ClosureObject(Interpreter.baseEnv, new LispSymbol[]{ new LispSymbol("x") }, new LispObject[]{ new LispSymbol("x") });
        var callEnv = new Environment(Interpreter.baseEnv);
        assertThrows(NameError.class, () -> closure.execute(callEnv, new LispObject[]{ new LispSymbol("not_exist") }));
    }
}
