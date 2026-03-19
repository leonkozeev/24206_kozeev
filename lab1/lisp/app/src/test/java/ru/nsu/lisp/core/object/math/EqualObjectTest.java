package ru.nsu.lisp.core.object.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.interfaces.BooleanObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.interfaces.BooleanObject;

class EqualObjectTest {
    @Test void typeTest() {
        var equal = new EqualObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(10)};
        var res = equal.execute(Interpreter.baseEnv, args);
        assertInstanceOf(BooleanObject.class, res);
    }

    @Test void equalTest() {
        var equal = new EqualObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(10)};
        var res = (BooleanObject) equal.execute(Interpreter.baseEnv, args);
        assertEquals(res.bool(), true);
    }

    @Test void notEqualTest() {
        var equal = new EqualObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(15)};
        var res = (BooleanObject) equal.execute(Interpreter.baseEnv, args);
        assertEquals(res.bool(), false);
    }
}
