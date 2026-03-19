package ru.nsu.lisp.core.interpreter;

import java.util.HashMap;

import ru.nsu.lisp.core.Environment;
import ru.nsu.lisp.core.exception.execute.DoNotExecutableError;
import ru.nsu.lisp.core.object.list.AppendObject;
import ru.nsu.lisp.core.object.list.GetListObject;
import ru.nsu.lisp.core.object.list.LispList;
import ru.nsu.lisp.core.object.list.NewListObject;
import ru.nsu.lisp.core.object.list.PopListObject;
import ru.nsu.lisp.core.object.list.SizeListObject;
import ru.nsu.lisp.core.object.math.AddObject;
import ru.nsu.lisp.core.object.math.AndObject;
import ru.nsu.lisp.core.object.math.DivObject;
import ru.nsu.lisp.core.object.math.EqualObject;
import ru.nsu.lisp.core.object.math.GreatEqObject;
import ru.nsu.lisp.core.object.math.GreatObject;
import ru.nsu.lisp.core.object.math.LessEqObject;
import ru.nsu.lisp.core.object.math.LessObject;
import ru.nsu.lisp.core.object.math.ModObject;
import ru.nsu.lisp.core.object.math.MulObject;
import ru.nsu.lisp.core.object.math.NotObject;
import ru.nsu.lisp.core.object.math.OrObject;
import ru.nsu.lisp.core.object.math.SubObject;
import ru.nsu.lisp.core.object.special_symbol.DefineObject;
import ru.nsu.lisp.core.object.special_symbol.IfObject;
import ru.nsu.lisp.core.object.special_symbol.LambdaObject;
import ru.nsu.lisp.core.object.special_symbol.QuoteObject;

public class Interpreter {
    private final Lexer lex;

    /*
     * baseEnv is the global environment that contains interpreter builtins.
     * It is initialized as a static final so all Interpreter instances share
     * the same set of primitive functions and special-forms. The double-brace
     * initializer is used here to populate the backing Map inline; that is a
     * concise but slightly unconventional pattern—it's only used for test/demo
     * simplicity. Important: entries are stored under their NAME constants
     * and later looked up by LispSymbol.eval() through Environment.lookup.
     */
    public static final Environment baseEnv = new Environment(new HashMap<>() {{
        put(AddObject.NAME, new AddObject());
        put(SubObject.NAME, new SubObject());
        put(MulObject.NAME, new MulObject());
        put(DivObject.NAME, new DivObject());
        put(ModObject.NAME, new ModObject());

        put(AndObject.NAME, new AndObject());
        put(OrObject.NAME, new OrObject());
        put(NotObject.NAME, new NotObject());
        put(LessObject.NAME, new LessObject());
        put(LessEqObject.NAME, new LessEqObject());
        put(GreatObject.NAME, new GreatObject());
        put(GreatEqObject.NAME, new GreatEqObject());
        put(EqualObject.NAME, new EqualObject());

        put(NewListObject.NAME, new NewListObject());
        put(SizeListObject.NAME, new SizeListObject());
        put(GetListObject.NAME, new GetListObject());
        put(AppendObject.NAME, new AppendObject());
        put(PopListObject.NAME, new PopListObject());

        put(DefineObject.NAME, new DefineObject());
        put(QuoteObject.NAME, new QuoteObject());
        put(IfObject.NAME, new IfObject());
        put(LambdaObject.NAME, new LambdaObject());
    }});

    public Interpreter(String code) {
        lex = new Lexer(code);
    }
    
    public void repl() {
        String tok;
        while ((tok = lex.next()) != null) {
            if (!tok.equals(LispList.BEGIN_SYMBOL)) {
                throw new DoNotExecutableError(tok);
            }
            new LispList().parse(lex).eval(baseEnv);
        }
    }

    
}