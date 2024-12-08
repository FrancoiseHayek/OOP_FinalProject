package oop.project.library.parser;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StringParser implements Parser<String> {

    @Override
    public String parse(String input) { return input; }

    public static <E extends Enum<E>> Parser<String> withChoices(Class<E> enumType) {
        Set<String> validValues =
                EnumSet.allOf(enumType).stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet());

        return new StringParser().withConstraint(value -> {
            try {
                if (!validValues.contains(value)) {
                    throw new IllegalArgumentException("Value: " + value + " is not one of the listed choices: " + validValues);
                }
                return value;
            } catch (IllegalArgumentException e) {
                throw new ParseException(e.getMessage());
            }
        });
    }

}
