package oop.project.library.lexer;

import java.util.*;

public final class Lexer {

    private final CharStream chars;

    public Lexer(String input) {
        chars = new CharStream(input);
    }

    public Map<String, String> lex() {

        Map<String, String> lexedArgs = new HashMap<>();
        List<Token> tokens = new ArrayList<>();

        String backSpace = "\b";
        String newLine = "\n";
        String carriageReturn = "\r";
        String tab = "\t";
        String whiteSpace = " ";

        while(chars.has(0)) {
            if(!(match(backSpace) || match(newLine) || match(carriageReturn) || match(tab) || match(whiteSpace)))
                tokens.add(lexToken());
            else {
                chars.skip();
            }
        }

        for (int i = 0; i < tokens.size(); i++) {

            Token token = tokens.get(i);
            if (token.getType().equals(Token.Type.FLAG)) {
                if (i + 1 == tokens.size()) {
                    throw new UnsupportedOperationException("Missing value for " + token.getLiteral());
                }
                Token nextToken = tokens.get(i + 1);
                if (nextToken == null || !nextToken.getType().equals(Token.Type.ARGUMENT)) {
                    throw new UnsupportedOperationException("Flag " + token.getLiteral() + " must be followed by an argument");
                }

                nextToken.setName(Optional.of(token.getLiteral().substring(2)));
                tokens.remove(token);
                token = nextToken;
            }

            if (token.getName().isPresent()) {
                lexedArgs.put(token.getName().get(), token.getLiteral());
            } else {
                lexedArgs.put(String.valueOf(i), token.getLiteral());
            }
        }

        return lexedArgs;
    }

    public Token lexToken() {

        // String command = "[A-Za-z_]";
        String literal = "[A-Za-z0-9._-]";
        String flag = "[A-Za-z_-]";

        if (peek("-")) {
            match("-");
            if (peek("-")) {
                match("-");
                if (peek("-")) {
                    throw new UnsupportedOperationException("Too many hyphens");
                } else if (peek(flag)) {
                    return lexFlag();
                }
            } else if (peek(literal)) {
                return lexArgument();
            }
        } else if (peek(literal)) {
            return lexArgument();
        }

        throw new UnsupportedOperationException("Could not properly lex token.");
    }

    public Token lexCommand() {
        String validCommand = "[A-Za-z_]";
        while(peek(validCommand)) {
            match(validCommand);
        }

        return chars.emit(Token.Type.COMMAND);
    }

    public Token lexArgument() {
        String validLiteral = "[A-Za-z_0-9-.]";
        while(peek(validLiteral)) {
            match(validLiteral);
        }
        return chars.emit(Token.Type.ARGUMENT);
    }

    public Token lexFlag() {
        String validFlag = "[A-Za-z_-]";
        match("--");
        while (peek(validFlag)) {
            match(validFlag);
        }

        return chars.emit(Token.Type.FLAG);
    }

    public boolean peek(String... patterns) {
        for(int i = 0; i < patterns.length; i++) {
            if ( !chars.has(i) || !String.valueOf(chars.get(i)).matches(patterns[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean match(String... patterns) {
        boolean peek = peek(patterns);
        if(peek) {
            for(int i = 0; i < patterns.length; i++) {
                chars.advance();
            }
        }
        return peek;
    }

    public static final class CharStream {

        private final String input;
        private int index = 0;
        private int length = 0;

        public CharStream(String input) {
            this.input = input;
        }

        public boolean has(int offset) {
            return index + offset < input.length();
        }

        public char get(int offset) {
            return input.charAt(index + offset);
        }

        public void advance() {
            index++;
            length++;
        }

        public void skip() {
            length = 0;
        }

        public Token emit(Token.Type type) {
            int start = index - length;
            skip();
            return new Token(type, Optional.empty(), input.substring(start, index), start);
        }

    }

}