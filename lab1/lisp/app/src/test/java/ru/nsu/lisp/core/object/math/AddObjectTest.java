package ru.nsu.lisp.core.object.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.interfaces.LispObject;

class AddObjectTest {
    @Test void typeTest() {
        var add = new AddObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(15)};
        var res = add.execute(Interpreter.baseEnv, args);

        assertInstanceOf(LispNumber.class, res);
    }

    @Test void addTest() {
        var add = new AddObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(15)};
        var res = (LispNumber) add.execute(Interpreter.baseEnv, args);

        assertEquals(res.getNumber(), 10 + 15);
    }

    @Test void negativeAddTest() {
        var add = new AddObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(-15)};
        var res = (LispNumber) add.execute(Interpreter.baseEnv, args);

        assertEquals(res.getNumber(), 10 - 15);
    }
}