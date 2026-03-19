package ru.nsu.lisp.core.object.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.interfaces.BooleanObject;

class GreatEqObjectTest {
    @Test void typeTest() {
        var greatEq = new GreatEqObject();
        LispObject[] args = {new LispNumber(15), new LispNumber(10)};
        var res = greatEq.execute(Interpreter.baseEnv, args);
        assertInstanceOf(BooleanObject.class, res);
    }

    @Test void greatEqTest() {
        var greatEq = new GreatEqObject();
        LispObject[] args = {new LispNumber(15), new LispNumber(10)};
        var res = (BooleanObject) greatEq.execute(Interpreter.baseEnv, args);
        assertEquals(res.bool(), true);
    }

    @Test void notGreatEqTest() {
        var greatEq = new GreatEqObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(15)};
        var res = (BooleanObject) greatEq.execute(Interpreter.baseEnv, args);
        assertEquals(res.bool(), false);
    }
}
