package ru.nsu.lisp.core.object.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.exception.execute.ZeroDivisionError;
import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.interfaces.LispObject;

class DivObjectTest {
    @Test void typeTest() {
        var div = new DivObject();
        LispObject[] args = {new LispNumber(20), new LispNumber(10)};
        var res = div.execute(Interpreter.baseEnv, args);

        assertInstanceOf(LispNumber.class, res);
    }

    @Test void divTest() {
        var div = new DivObject();
        LispObject[] args = {new LispNumber(20), new LispNumber(10)};
        var res = (LispNumber) div.execute(Interpreter.baseEnv, args);

        assertEquals(res.getNumber(), 20 / 10);
    }

    @Test void negativeDivTest() {
        var div = new DivObject();
        LispObject[] args = {new LispNumber(20), new LispNumber(-10)};
        var res = (LispNumber) div.execute(Interpreter.baseEnv, args);

        assertEquals(res.getNumber(), 20 / (-10));
    }

    @Test void floatDivTest() {
        var div = new DivObject();
        LispObject[] args = {new LispNumber(43), new LispNumber(5)};
        var res = (LispNumber) div.execute(Interpreter.baseEnv, args);

        assertEquals(res.getNumber(), 43 / 5);
    }

    @Test void zeroDivTest() {
        var div = new DivObject();
        LispObject[] args = {new LispNumber(1), new LispNumber(0)};

        assertThrows(ZeroDivisionError.class, () -> div.execute(Interpreter.baseEnv, args));
    }
}