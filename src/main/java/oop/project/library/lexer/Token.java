package oop.project.library.lexer;

import java.util.Optional;

public final class Token {

    public enum Type {
        COMMAND,
        FLAG,
        ARGUMENT
    }

    private final Type type;
    private Optional<String> name;
    private final String literal;
    private final int index;

    public Token(Type type, Optional<String> name, String literal, int index) {
        this.type = type;
        this.name = name;
        this.literal = literal;
        this.index = index;
    }

    public Type getType() {
        return type;
    }

    public Optional<String> getName() { return name; }

    public void setName(Optional<String> name) { this.name = name; }

    public String getLiteral() {
        return literal;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Token
                && type == ((Token) obj).type
                && name == ((Token) obj).name
                && literal.equals(((Token) obj).literal)
                && index == ((Token) obj).index;
    }

    @Override
    public String toString() {
        return type + " " + literal;
    }

}
