package ru.nsu.lisp.core.object.interfaces;

import ru.nsu.lisp.core.Environment;

public interface LispObject {
    LispObject eval(Environment env);
}