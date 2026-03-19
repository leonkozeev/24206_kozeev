package ru.nsu.lisp.core.object.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.interfaces.LispObject;

class SubObjectTest {
    @Test void typeTest() {
        var sub = new SubObject();
        LispObject[] args = {new LispNumber(15), new LispNumber(10)};
        var res = sub.execute(Interpreter.baseEnv, args);

        assertInstanceOf(LispNumber.class, res);
    }

    @Test void subTest() {
        var sub = new SubObject();
        LispObject[] args = {new LispNumber(15), new LispNumber(10)};
        var res = (LispNumber) sub.execute(Interpreter.baseEnv, args);

        assertEquals(res.getNumber(), 15 - 10);
    }

    @Test void negativeSubTest() {
        var sub = new SubObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(-15)};
        var res = (LispNumber) sub.execute(Interpreter.baseEnv, args);

        assertEquals(res.getNumber(), 10 + 15);
    }
}