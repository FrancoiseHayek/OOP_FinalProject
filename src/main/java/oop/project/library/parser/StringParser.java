package oop.project.library.parser;


import oop.project.library.command.CommandLibraryException;

import java.util.Set;

public class StringParser implements Parser<String> {

    /// Simply returns the input as it was provided as a String
    @Override
    public String parse(String input) { return input; }

    /// Creates a 'choices' constraint and applies to the given input
    /// Developers can provide a list of choices that input from end-users must conform to
    /// Throws a CommandLibraryException if the developer provided choices is empty
    /// Throws a CommandException if the end-user provided input does not conform to the developer provided choices
    public static <E extends Enum<E>> Parser<String> withChoices(Set<String> validValues) {
        if (validValues.isEmpty()) {
            throw new CommandLibraryException("String choices should be non-empty");
        }
        return new StringParser().withConstraint(value -> {
            try {
                if (!validValues.contains(value)) {
                    throw new IllegalArgumentException("Value: '" + value + "' is not one of the listed choices: " + validValues);
                }
                return value;
            } catch (IllegalArgumentException e) {
                throw new ParseException(e.getMessage());
            }
        });
    }

}
