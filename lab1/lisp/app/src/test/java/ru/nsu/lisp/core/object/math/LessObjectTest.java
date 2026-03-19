package ru.nsu.lisp.core.object.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.interfaces.BooleanObject;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.interfaces.BooleanObject;

class LessObjectTest {
    @Test void typeTest() {
        var less = new LessObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(15)};
        var res = less.execute(Interpreter.baseEnv, args);
        assertInstanceOf(BooleanObject.class, res);
    }

    @Test void lessTest() {
        var less = new LessObject();
        LispObject[] args = {new LispNumber(10), new LispNumber(15)};
        var res = (BooleanObject) less.execute(Interpreter.baseEnv, args);
        assertEquals(res.bool(), true);
    }

    @Test void notLessTest() {
        var less = new LessObject();
        LispObject[] args = {new LispNumber(15), new LispNumber(10)};
        var res = (BooleanObject) less.execute(Interpreter.baseEnv, args);
        assertEquals(res.bool(), false);
    }
}
