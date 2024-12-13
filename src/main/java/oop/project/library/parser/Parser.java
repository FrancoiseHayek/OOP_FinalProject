package oop.project.library.parser;

import oop.project.library.command.CommandLibraryException;

import java.util.function.Function;

/// Parser interface to allow for flexibility in parsable types (including ones not known by this library)
/// Parsers to be used by Arguments for Commands created by this library should implement this interface
@FunctionalInterface
public interface Parser<T> {

    /// Parse function that takes in the String representation of a value and should return the value parsed to type T
    /// Throws ParseException to denote end-user errors (e.g., attempting to parse "one" into the Integer 1)
    /// Throws CommandLibraryException to denote errors to the developer creating Commands (e.g., attempting to apply a
    /// min/max constraint where min > max)
    T parse(String input) throws ParseException, CommandLibraryException;

    /// Method to create a constraint on a given parser, yielding a more specialized parser. Takes in a function to be
    /// applied to the parsed input (e.g., An Integer Parser that has added min/max constraints, see IntegerParser for
    /// a clearer representation)
    default Parser<T> withConstraint(Function<T, T> constraint) {
        return input -> {
            T result = this.parse(input);
            return constraint.apply(result);
        };
    }

}
