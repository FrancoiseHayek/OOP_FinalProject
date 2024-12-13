package oop.project.library.parser;

public class BooleanParser implements Parser<Boolean> {

    /// Parses user input into a boolean
    /// Throws a parse exception if input does not conform to standard Java boolean types
    @Override
    public Boolean parse(String input) {

        return switch (input) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new ParseException("Invalid boolean value");
        };
    }
}
