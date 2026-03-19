package ru.nsu.lisp.core.object.interfaces;

import ru.nsu.lisp.core.Environment;

public interface ExecutableObject extends LispObject {
    LispObject execute(Environment env, LispObject[] args);
}