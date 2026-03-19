package ru.nsu.lisp.core.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.parse.MissingKeyword;
import ru.nsu.lisp.core.interpreter.Interpreter;
import ru.nsu.lisp.core.interpreter.Lexer;

class LispStringTest {
    @Test void parseSimpleString() {
        var lex = new Lexer("\"hello\"");
        assertEquals("\"", lex.next()); // consume begin symbol
        var s = new LispString();
        s.parse(lex);
        assertTrue(s.bool());
        assertNull(lex.next());
    }

    @Test void parseStringWithSpaces() {
        var lex = new Lexer("\"hello world\"");
        assertEquals("\"", lex.next());
        var s = new LispString();
        s.parse(lex);
        assertTrue(s.bool());
        assertNull(lex.next());
    }

    @Test void emptyStringParse() {
        var lex = new Lexer("\"\"");
        assertEquals("\"", lex.next());
        var s = new LispString();
        s.parse(lex);
        assertFalse(s.bool());
    }

    @Test void unterminatedStringParse() {
        var lex = new Lexer("\"unterminated");
        assertEquals("\"", lex.next());
        assertThrows(MissingKeyword.class, () -> new LispString().parse(lex));
    }

    @Test void evalReturnsEmptyString() {
        var s = new LispString();
        assertFalse(s.bool());
        assertSame(s, s.eval(new Environment(Interpreter.baseEnv)));
    }
}
