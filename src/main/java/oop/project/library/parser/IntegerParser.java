package oop.project.library.parser;

public class IntegerParser implements Parser<Integer> {

    @Override
    public Integer parse(String input) throws ParseException {

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage());
        }
    }

    public static Parser<Integer> withRange(int min, int max) {
            return new IntegerParser().withConstraint(value -> {
                try {
                    if (value < min || value > max) {
                        throw new IllegalArgumentException("Value " + value + " is out of range, must be between " + min + " and " + max + " (inclusive)");
                    }
                return value;
                } catch (IllegalArgumentException e) {
                    throw new ParseException(e.getMessage());
                }
            });
    }

}
