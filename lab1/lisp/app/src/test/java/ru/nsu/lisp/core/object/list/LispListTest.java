package ru.nsu.lisp.core.object.list;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.interpreter.Lexer;
import ru.nsu.lisp.core.object.LispSymbol;
import ru.nsu.lisp.core.object.math.LispNumber;
import ru.nsu.lisp.core.exception.parse.MissingKeyword;
import ru.nsu.lisp.core.exception.execute.DoNotExecutableError;

class LispListTest {
    @Test void parseMissingEndThrows() {
        var lex = new Lexer("( 1 2");
        assertThrows(MissingKeyword.class, () -> new LispList().parse(lex));
    }

    @Test void evalEmptyReturnsSelf() {
        var lst = new LispList();
        var res = lst.eval(Interpreter.baseEnv);
        assertSame(lst, res);
        assertInstanceOf(LispList.class, res);
        assertEquals(0, lst.data.size());
    }

    @Test void evalNonExecutableThrows() {
        var lst = new LispList(Arrays.asList(new LispNumber(1)));
        assertThrows(DoNotExecutableError.class, () -> lst.eval(Interpreter.baseEnv));
    }

    @Test void evalExecuteWithFunction() {
        var lst = new LispList(Arrays.asList(new LispSymbol("+"), new LispNumber(3), new LispNumber(4)));
        var res = lst.eval(Interpreter.baseEnv);
        assertInstanceOf(LispNumber.class, res);
        assertEquals(7, ((LispNumber) res).getNumber());
    }
}
