package ru.nsu.lisp.core.object.special_symbol;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.LispSymbol;
import ru.nsu.lisp.core.object.math.LispNumber;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;
import ru.nsu.lisp.core.exception.eval.NameError;

class DefineObjectTest {

    @Test void defineRegistersInOwnEnv() {
        var define = new DefineObject();
        var env = new Environment(Interpreter.baseEnv);

        define.execute(env, new LispObject[]{ new LispSymbol("x"), new LispNumber(123) });

        var res = env.lookup("x");
        assertInstanceOf(LispNumber.class, res);
        assertEquals(123, ((LispNumber) res).getNumber());

        // parent env (base) should not be polluted by define in child env
        assertThrows(NameError.class, () -> Interpreter.baseEnv.lookup("x"));
    }

    @Test void incorrectNumberOfArgumentsThrows() {
        var define = new DefineObject();
        var env = new Environment(Interpreter.baseEnv);
        assertThrows(IncorrectNumberArguments.class, () -> define.execute(env, new LispObject[]{ new LispSymbol("x") }));
    }

    @Test void invalidNameTypeThrows() {
        var define = new DefineObject();
        var env = new Environment(Interpreter.baseEnv);
        assertThrows(InvalidArgumentsType.class, () -> define.execute(env, new LispObject[]{ new LispNumber(1), new LispNumber(2) }));
    }

    @Test void evalThrows() {
        var env = new Environment(Interpreter.baseEnv);
        assertThrows(IsNotEvalError.class, () -> new DefineObject().eval(env));
    }
}
