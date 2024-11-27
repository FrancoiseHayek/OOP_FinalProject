package oop.project.library.parser;

public class BooleanParser implements Parser<Boolean> {

    @Override
    public Boolean parse(String input) {

        return switch (input) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new ParseException("Invalid boolean value");
        };
    }
}
