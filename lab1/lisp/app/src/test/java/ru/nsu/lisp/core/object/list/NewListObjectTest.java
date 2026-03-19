package ru.nsu.lisp.core.object.list;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.math.LispNumber;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;

class NewListObjectTest {
    @Test void executeCreatesList() {
        var obj = new NewListObject();
        LispObject[] args = { new LispNumber(1), new LispNumber(2) };
        var res = obj.execute(Interpreter.baseEnv, args);
        assertInstanceOf(LispList.class, res);
        var list = (LispList) res;
        assertEquals(2, list.data.size());
        assertEquals(1, ((LispNumber) list.data.get(0)).getNumber());
        assertEquals(2, ((LispNumber) list.data.get(1)).getNumber());
    }

    @Test void evalThrows() {
        assertThrows(IsNotEvalError.class, () -> new NewListObject().eval(Interpreter.baseEnv));
    }
}
