package oop.project.library.parser;

public class DoubleParser implements Parser<Double> {

    @Override
    public Double parse(String input) {

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage());
        }
    }
}
