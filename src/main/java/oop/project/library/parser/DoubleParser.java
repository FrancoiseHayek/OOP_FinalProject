package oop.project.library.parser;

public class DoubleParser implements Parser<Double> {

    /// Parses user input into a Double
    /// Throws a parse exception if the parsing fails due to a NumberFormatException
    @Override
    public Double parse(String input) {

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage());
        }
    }
}
