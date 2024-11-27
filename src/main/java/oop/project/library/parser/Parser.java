package oop.project.library.parser;

import java.text.ParseException;

@FunctionalInterface
public interface Parser<T> {

    T parse(String input) throws ParseException;

    public static <T> T useParser(Parser<T> parser, String input) throws ParseException {
        return parser.parse(input);
    }
}
