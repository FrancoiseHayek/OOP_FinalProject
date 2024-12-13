package oop.project.library.parser;

import oop.project.library.command.CommandLibraryException;

public class IntegerParser implements Parser<Integer> {

    /// Overridden parse function specific to Integers
    /// Takes in the String representation of a value to be parsed, returns the Integer representation of the value if
    /// possible.
    /// Throws a ParseException if the value could not be parsed successfully
    @Override
    public Integer parse(String input) throws ParseException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage());
        }
    }

    /// Method to apply a range constraint to an IntegerParser
    /// Takes min and max values (inclusive) and applies the constraint on the parsed input during parsing
    /// Throws a CommandLibraryException to the command creator if the range does not make sense
    /// Throws a ParseException (which then gets routed as a CommandException) to the end-user if the given input failed
    /// to meet the constraint
    ///
    /// Can be easily duplicated to create additional constraints for exclusive ranges, mins but no maxes, maxes but no mins, etc.
    /// Simply make use of the 'withConstraint()' method when creating the constraint as shown below
    public static Parser<Integer> withInclusiveRange(int min, int max) {
        if (min > max) {
            throw new CommandLibraryException("min should be less than max: min: " + min + ", max: " + max);
        }
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
