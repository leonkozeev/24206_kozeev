package ru.nsu.lisp;

import ru.nsu.lisp.core.interpreter.Interpreter;

public class App {
    public static void main(String[] args) {
        // Interpreter it = new Interpreter(
        //     """
        //     (define make_adder (lambda (x)  (lambda (y) (+ x y))))
        //     (define add_five (make_adder 5))
        //     (define add_ten (make_adder 10))
        //     (add_five 3) 
        //     (add_ten 3)
        //     """
        // );
        Interpreter it = new Interpreter(
            """
            (define fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))
            (fib 20)
            """
        );
        it.repl();
    }
}
