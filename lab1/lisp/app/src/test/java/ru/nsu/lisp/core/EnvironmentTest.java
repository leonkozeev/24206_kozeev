package ru.nsu.lisp.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import ru.nsu.lisp.core.object.math.LispNumber;
import ru.nsu.lisp.core.object.interfaces.LispObject;
import ru.nsu.lisp.core.exception.eval.NameError;
import ru.nsu.lisp.core.interpreter.Interpreter;

class EnvironmentTest {
    @Test void constructorWithMapFindsValues() {
        Map<String, LispObject> data = new HashMap<>() {{ put("a", new LispNumber(1)); }};
        Environment env = new Environment(data);
        assertEquals(1, ((LispNumber) env.lookup("a")).getNumber());
    }

    @Test void childLookupFindsParent() {
        Map<String, LispObject> pmap = new HashMap<>() {{ put("p", new LispNumber(2)); }};
        Environment parent = new Environment(pmap);
        Environment child = new Environment(parent);
        assertEquals(2, ((LispNumber) child.lookup("p")).getNumber());
    }

    @Test void registAddsToLocalAndNotParent() {
        Map<String, LispObject> pmap = new HashMap<>() {{ put("x", new LispNumber(1)); }};
        Environment parent = new Environment(pmap);
        Environment child = new Environment(parent);
        child.regist("y", new LispNumber(3));
        assertEquals(3, ((LispNumber) child.lookup("y")).getNumber());
        assertThrows(NameError.class, () -> parent.lookup("y"));
    }

    @Test void shadowingWorks() {
        Map<String, LispObject> pmap = new HashMap<>() {{ put("v", new LispNumber(1)); }};
        Environment parent = new Environment(pmap);
        Environment child = new Environment(parent);
        child.regist("v", new LispNumber(4));
        assertEquals(4, ((LispNumber) child.lookup("v")).getNumber());
        assertEquals(1, ((LispNumber) parent.lookup("v")).getNumber());
    }

    @Test void lookupNotFoundThrows() {
        Environment env = new Environment(new HashMap<>());
        assertThrows(NameError.class, () -> env.lookup("nope"));
    }

    @Test void constructorParentAndDataWorks() {
        Map<String, LispObject> pmap = new HashMap<>() {{ put("p1", new LispNumber(100)); }};
        Environment parent = new Environment(pmap);
        Map<String, LispObject> data = new HashMap<>() {{ put("d1", new LispNumber(200)); }};
        Environment env = new Environment(parent, data);
        assertEquals(200, ((LispNumber) env.lookup("d1")).getNumber());
        assertEquals(100, ((LispNumber) env.lookup("p1")).getNumber());
    }
}
