package ru.nsu.lisp.core.object.special_symbol;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.object.ClosureObject;
import ru.nsu.lisp.core.object.LispSymbol;
import ru.nsu.lisp.core.object.list.LispList;
import ru.nsu.lisp.core.object.math.LispNumber;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.exception.execute.IncorrectNumberArguments;
import ru.nsu.lisp.core.exception.execute.InvalidArgumentsType;
import ru.nsu.lisp.core.exception.eval.IsNotEvalError;

class LambdaObjectTest {

    @Test void createClosureAndExecuteSimpleAdd() {
        var lambda = new LambdaObject();
        var argsList = new LispList(Arrays.asList(new LispSymbol("a"), new LispSymbol("b")));
        var body = new LispList(Arrays.asList(new LispSymbol("+"), new LispSymbol("a"), new LispSymbol("b")));

        var closureObj = lambda.execute(Interpreter.baseEnv, new LispObject[]{ argsList, body });
        assertInstanceOf(ClosureObject.class, closureObj);

        var closure = (ClosureObject) closureObj;
        var res = (LispNumber) closure.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(4), new LispNumber(5) });
        assertEquals(9, res.getNumber());
    }

    @Test void closureCapturesParentEnv() {
        var parent = new Environment(Interpreter.baseEnv);
        parent.regist("z", new LispNumber(7));

        var lambda = new LambdaObject();
        var argsList = new LispList(Arrays.asList(new LispSymbol("x")));
        var body = new LispList(Arrays.asList(new LispSymbol("+"), new LispSymbol("x"), new LispSymbol("z")));

        var closure = (ClosureObject) lambda.execute(parent, new LispObject[]{ argsList, body });
        var res = (LispNumber) closure.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(3) });
        assertEquals(10, res.getNumber());
    }

    @Test void invalidNumberOfArgumentsThrows() {
        var lambda = new LambdaObject();
        var argsList = new LispList(Arrays.asList(new LispSymbol("a")));
        assertThrows(IncorrectNumberArguments.class, () -> lambda.execute(Interpreter.baseEnv, new LispObject[]{ argsList }));
    }

    @Test void invalidArgsListTypeThrows() {
        var lambda = new LambdaObject();
        var body = new LispList(Arrays.asList(new LispSymbol("+"), new LispSymbol("a"), new LispSymbol("b")));
        assertThrows(InvalidArgumentsType.class, () -> lambda.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(1), body }));
    }

    @Test void argsContainNonSymbolThrows() {
        var lambda = new LambdaObject();
        var badArgsList = new LispList(Arrays.asList(new LispNumber(1)));
        var body = new LispList(Arrays.asList(new LispSymbol("+"), new LispSymbol("a"), new LispSymbol("b")));
        assertThrows(InvalidArgumentsType.class, () -> lambda.execute(Interpreter.baseEnv, new LispObject[]{ badArgsList, body }));
    }

    @Test void evalThrows() {
        assertThrows(IsNotEvalError.class, () -> new LambdaObject().eval(new Environment(Interpreter.baseEnv)));
    }

    @Test void closureArgCountMismatchThrows() {
        var lambda = new LambdaObject();
        var argsList = new LispList(Arrays.asList(new LispSymbol("a"), new LispSymbol("b")));
        var body = new LispList(Arrays.asList(new LispSymbol("+"), new LispSymbol("a"), new LispSymbol("b")));
        var closure = (ClosureObject) lambda.execute(Interpreter.baseEnv, new LispObject[]{ argsList, body });
        assertThrows(IncorrectNumberArguments.class, () -> closure.execute(Interpreter.baseEnv, new LispObject[]{ new LispNumber(1) }));
    }

    @Test void closureArgsEvaluatedInCallEnv() {
        var lambda = new LambdaObject();
        var argsList = new LispList(Arrays.asList(new LispSymbol("a")));
        var body = new LispSymbol("a");
        var closure = (ClosureObject) lambda.execute(Interpreter.baseEnv, new LispObject[]{ argsList, body });

        var callEnv = new Environment(Interpreter.baseEnv);
        callEnv.regist("v", new LispNumber(99));

        var res = (LispNumber) closure.execute(callEnv, new LispObject[]{ new LispSymbol("v") });
        assertEquals(99, res.getNumber());
    }
}
