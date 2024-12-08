package oop.project.library.parser;

import oop.project.library.parser.ParseException;
import java.util.function.Function;

@FunctionalInterface
public interface Parser<T> {

    T parse(String input) throws ParseException;

    default Parser<T> withConstraint(Function<T, T> constraint) {
        return input -> {
            T result = this.parse(input);
            return constraint.apply(result);
        };
    }

    public static <T> T useParser(Parser<T> parser, String input) throws ParseException {
        return parser.parse(input);
    }

}
