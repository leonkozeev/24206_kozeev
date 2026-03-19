package ru.nsu.lisp.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.lisp.core.exception.eval.NameError;
import ru.nsu.lisp.core.object.interfaces.LispObject;

public class Environment {
    private final static Logger logger = LogManager.getLogger(Environment.class);
    private final Environment parent; 
    private final Map<String, LispObject> data;

    /**
     * Create an environment backed by the provided map. This constructor
     * is useful for creating the global/static environments where bindings
     * are pre-populated (see Interpreter.baseEnv).
     * The parent is set to null so lookups only consult this map.
     */
    public Environment(Map<String, LispObject> data) {
        logger.debug("create environment from data");
        parent = null;
        this.data = data;
    }

    /**
     * Create a child environment that inherits from the provided parent.
     * The child has its own empty map for local bindings; lookup() will
     * consult the child's map first, then walk up to the parent if not found.
     * This pattern is used to implement lexical scoping for closures and
     * local definitions.
     */
    public Environment(Environment parent) {
        logger.debug("create environment");
        this.parent = parent;
        data = new HashMap<>();
    }

    /**
     * Create an environment with both a parent and an initial data map.
     * This is a convenience for tests or when a snapshot of bindings needs
     * to be exposed with a specific parent chain.
     */
    public Environment(Environment parent, Map<String, LispObject> data) {
        logger.debug("create environment from data and parent");
        this.parent = parent;
        this.data = data;
    }

    /**
     * Register a binding in the current environment only. This does not
     * modify parent environments. Use this to create/override local symbols.
     */
    public void regist(String name, LispObject obj) {
        logger.debug("registering Object: `{}`", name);
        data.put(name, obj);
    }

    /**
     * Lookup a name in the environment chain. Behavior:
     * - If the name exists in the current environment's map, return it.
     * - Otherwise, if a parent exists, delegate lookup to parent.
     * - If not found in the whole chain, throw NameError.
     *
     * Important subtlety: registrements always affect only the current
     * Environment instance; lookup walking ensures closures capture the
     * environment they were created in but calls evaluate argument expressions
     * against the call-site environment when executing closures.
     */
    public LispObject lookup(String name) {
        LispObject obj = data.get(name);
        if (obj != null) {
            logger.debug("find Object `{}`", name);
            return obj;
        } else if (parent != null) {
            logger.debug("don't find Object `{}`, going up", name);
            return parent.lookup(name);
        } else {
            logger.info("don't find Object `{}` if tree!", name);
            throw new NameError(name);
        }
    }
}
