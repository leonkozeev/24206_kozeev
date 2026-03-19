package ru.nsu.lisp.core.object.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.interfaces.LispObject;

class MulObjectTest {
    @Test void typeTest() {
        var mul = new MulObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(15)};
        var res = mul.execute(Interpreter.baseEnv, args);

        assertInstanceOf(LispNumber.class, res);
    }

    @Test void mulTest() {
        var mul = new MulObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(15)};
        var res = (LispNumber) mul.execute(Interpreter.baseEnv, args);

        assertEquals(res.getNumber(), 10 * 15);
    }

    @Test void negativeMulTest() {
        var mul = new MulObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(-15)};
        var res = (LispNumber) mul.execute(Interpreter.baseEnv, args);

        assertEquals(res.getNumber(), 10 * (-15));
    }
}