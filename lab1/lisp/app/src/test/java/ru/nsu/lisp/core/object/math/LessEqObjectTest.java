package ru.nsu.lisp.core.object.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.interfaces.BooleanObject;

class LessEqObjectTest {
    @Test void typeTest() {
        var lessEq = new LessEqObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(15)};
        var res = lessEq.execute(Interpreter.baseEnv, args);
        assertInstanceOf(BooleanObject.class, res);
    }

    @Test void lessEqTest() {
        var lessEq = new LessEqObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(15)};
        var res = (BooleanObject) lessEq.execute(Interpreter.baseEnv, args);
        assertEquals(res.bool(), true);
    }

    @Test void notLessEqTest() {
        var lessEq = new LessEqObject();
        LispObject[] args = {new LispNumber(15), new LispNumber(10)};
        var res = (BooleanObject) lessEq.execute(Interpreter.baseEnv, args);
        assertEquals(res.bool(), false);
    }
}
