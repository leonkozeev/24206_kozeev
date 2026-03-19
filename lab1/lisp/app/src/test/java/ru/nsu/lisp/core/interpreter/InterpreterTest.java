package ru.nsu.lisp.core.interpreter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.object.list.LispList;
import ru.nsu.lisp.core.object.math.LispNumber;

class InterpreterTest {

    private LispObject runProgram(String code) {
        return runProgram(code, Interpreter.baseEnv);
    }

    private LispObject runProgram(String code, Environment env) {
        var lex = new Lexer(code);
        LispObject last = null;
        String tok;
        while ((tok = lex.next()) != null) {
            if (!LispList.BEGIN_SYMBOL.equals(tok)) {
                throw new RuntimeException("unexpected token: " + tok);
            }
            var list = new LispList();
            var parsed = list.parse(lex);
            last = parsed.eval(env);
        }
        return last;
    }

    @Test void simpleAddition() {
        var res = runProgram("(+ 1 2)");
        assertInstanceOf(LispNumber.class, res);
        assertEquals(3, ((LispNumber) res).getNumber());
    }

    @Test void nestedArithmetic() {
        var res = runProgram("(* (+ 1 2) (- 5 3))");
        assertInstanceOf(LispNumber.class, res);
        assertEquals(6, ((LispNumber) res).getNumber());
    }

    @Test void defineAndUseVariable() {
        var env = new Environment(Interpreter.baseEnv);
        var res = runProgram("(define a1 10) (+ a1 5)", env);
        assertInstanceOf(LispNumber.class, res);
        assertEquals(15, ((LispNumber) res).getNumber());
        assertEquals(10, ((LispNumber) env.lookup("a1")).getNumber());
    }

    @Test void lambdaAndCall() {
        var res = runProgram("(define inc1 (lambda (n) (+ n 1))) (inc1 5)");
        assertInstanceOf(LispNumber.class, res);
        assertEquals(6, ((LispNumber) res).getNumber());
    }

    @Test void recursionFactorial() {
        var code = "(define fact1 (lambda (n) (if (< n 2) 1 (* n (fact1 (- n 1)))))) (fact1 5)";
        var res = runProgram(code);
        assertInstanceOf(LispNumber.class, res);
        assertEquals(120, ((LispNumber) res).getNumber());
    }

    @Test void closureCapturesParentEnv() {
        var parent = new Environment(Interpreter.baseEnv);
        parent.regist("z", new LispNumber(7));
        var res = runProgram("(define addz (lambda (x) (+ x z))) (addz 3)", parent);
        assertInstanceOf(LispNumber.class, res);
        assertEquals(10, ((LispNumber) res).getNumber());
    }

    @Test void listOperationsAppendSize() {
        var env = new Environment(Interpreter.baseEnv);
        var res = runProgram("(define lst1 (list 1 2 3)) (append 4 lst1) (size lst1)", env);
        assertInstanceOf(LispNumber.class, res);
        assertEquals(4, ((LispNumber) res).getNumber());
        var lst = env.lookup("lst1");
        assertInstanceOf(LispList.class, lst);
        assertEquals(4, ((LispList) lst).data.size());
        assertEquals(4, ((LispNumber) ((LispList) lst).data.get(3)).getNumber());
    }

    @Test void getAndPopOperations() {
        var env = new Environment(Interpreter.baseEnv);
        var res = runProgram("(define lst2 (list 10 20 30)) (get 1 lst2) (pop lst2) (get 1 lst2)", env);
        assertInstanceOf(LispNumber.class, res);
        assertEquals(20, ((LispNumber) res).getNumber());
    }

    @Test void nestedLambdaMakeAdder() {
        var env = new Environment(Interpreter.baseEnv);
        var code = "(define make_adder (lambda (x) (lambda (y) (+ x y)))) (define add5 (make_adder 5)) (add5 3)";
        var res = runProgram(code, env);
        assertInstanceOf(LispNumber.class, res);
        assertEquals(8, ((LispNumber) res).getNumber());
    }

    @Test void quoteCreatesLiteralList() {
        var env = new Environment(Interpreter.baseEnv);
        var res = runProgram("(define q1 (quote (1 2 3))) (size q1)", env);
        assertInstanceOf(LispNumber.class, res);
        assertEquals(3, ((LispNumber) res).getNumber());
    }
}
