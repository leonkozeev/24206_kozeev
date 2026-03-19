package ru.nsu.lisp.core.interpreter;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Lexer {
    private static final Logger logger = LogManager.getLogger(Lexer.class);
    private static final Pattern TOKEN = Pattern.compile("[\\p{L}\\p{N}_]+|[^\\s\\p{L}\\p{N}_]");

    private final List<String> code;
    private int pointer = 0;

    public Lexer(String text) {
        code = tokenize(text);
    }

    private List<String> tokenize(String text) {
        logger.debug("spliting code");
        return TOKEN.matcher(text).results().map(m -> m.group()).toList();
    }

    public String next() {
        if (pointer < code.size()) {
            return code.get(pointer++);
        } 
        return null;
    }

    public String peek() {
        if (pointer < code.size()) {
            return code.get(pointer);
        } 
        return null;
    }
}