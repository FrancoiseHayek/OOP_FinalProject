package oop.project.library.command;

import oop.project.library.parser.ParseException;
import oop.project.library.parser.Parser;
import oop.project.library.command.CommandException;


import java.util.Optional;

/// Argument class used by Command
/// Takes an optional name for the Argument
/// Takes an optional value (if the value must be given by the end-user)
/// Takes a parser to be applied to the Argument
/// Takes a boolean to represent whether this Argument is optional for the user to give
public record Argument (
        Optional<String> name,
        Optional<?> value,
        Parser<?> parser,
        boolean optional
) {

    /// Applies the given parser to the given value
    /// Throws a command exception to the end-user if no value was given to parse
    public Object parse() throws CommandException {
        if (value.isPresent()) {
            return parser.parse((String) value.get());
        } else {
            throw new CommandException("No value given to this Argument for parsing");
        }
    }
}
