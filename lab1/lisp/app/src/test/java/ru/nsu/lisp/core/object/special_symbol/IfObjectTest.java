package ru.nsu.lisp.core.object.special_symbol;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.math.LispNumber;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.LispSymbol;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;

class IfObjectTest {

    @Test void trueConditionEvaluatesThenBranch() {
        var ifo = new IfObject();
        var env = new Environment(Interpreter.baseEnv);
        var res = (LispNumber) ifo.execute(env, new LispObject[]{ new LispNumber(1), new LispNumber(10), new LispNumber(20) });
        assertEquals(10, res.getNumber());
    }

    @Test void falseConditionEvaluatesElseBranch() {
        var ifo = new IfObject();
        var env = new Environment(Interpreter.baseEnv);
        var res = (LispNumber) ifo.execute(env, new LispObject[]{ new LispNumber(0), new LispNumber(11), new LispNumber(22) });
        assertEquals(22, res.getNumber());
    }

    @Test void invalidConditionTypeThrows() {
        var ifo = new IfObject();
        var env = new Environment(Interpreter.baseEnv);
        assertThrows(InvalidArgumentsType.class, () -> ifo.execute(env, new LispObject[]{ new LispSymbol("+"), new LispNumber(1), new LispNumber(2) }));
    }

    @Test void incorrectNumberOfArgumentsThrows() {
        var ifo = new IfObject();
        var env = new Environment(Interpreter.baseEnv);
        assertThrows(IncorrectNumberArguments.class, () -> ifo.execute(env, new LispObject[]{ new LispNumber(1), new LispNumber(2) }));
    }

    @Test void lazyEvaluationOfBranches() {
        var ifo = new IfObject();
        var env = new Environment(Interpreter.baseEnv);
        // second argument refers to undefined symbol; since condition is false, second should not be evaluated
        var res = (LispNumber) ifo.execute(env, new LispObject[]{ new LispNumber(0), new LispSymbol("undefined_symbol_for_test"), new LispNumber(42) });
        assertEquals(42, res.getNumber());
    }
}
