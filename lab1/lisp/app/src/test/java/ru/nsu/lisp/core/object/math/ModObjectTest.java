package ru.nsu.lisp.core.object.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.exception.execute.ZeroDivisionError;
import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.interfaces.LispObject;

class ModObjectTest {
    @Test void typeTest() {
        var mod = new ModObject();
        LispObject[] args = {new LispNumber(20), new LispNumber(10)};
        var res = mod.execute(Interpreter.baseEnv, args);

        assertInstanceOf(LispNumber.class, res);
    }

    @Test void modTest() {
        var mod = new ModObject();
        LispObject[] args = {new LispNumber(20), new LispNumber(10)};
        var res = (LispNumber) mod.execute(Interpreter.baseEnv, args);

        assertEquals(res.getNumber(), 20 % 10);
    }

    @Test void negativeModTest() {
        var mod = new ModObject();
        LispObject[] args = {new LispNumber(20), new LispNumber(-10)};
        var res = (LispNumber) mod.execute(Interpreter.baseEnv, args);

        assertEquals(res.getNumber(), 20 % (-10));
    }

    @Test void floatModTest() {
        var mod = new ModObject();
        LispObject[] args = {new LispNumber(43), new LispNumber(5)};
        var res = (LispNumber) mod.execute(Interpreter.baseEnv, args);

        assertEquals(res.getNumber(), 43 % 5);
    }

    @Test void zeroModTest() {
        var mod = new ModObject();
        LispObject[] args = {new LispNumber(1), new LispNumber(0)};

        assertThrows(ZeroDivisionError.class, () -> mod.execute(Interpreter.baseEnv, args));
    }
}