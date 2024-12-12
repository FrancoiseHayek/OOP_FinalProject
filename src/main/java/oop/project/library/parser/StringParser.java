package oop.project.library.parser;


import java.util.Set;

public class StringParser implements Parser<String> {

    @Override
    public String parse(String input) { return input; }

    public static <E extends Enum<E>> Parser<String> withChoices(Set<String> validValues) {

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
