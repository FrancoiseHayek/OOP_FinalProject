package oop.project.library.command;

import oop.project.library.parser.ParseException;
import oop.project.library.parser.Parser;
import oop.project.library.command.CommandException;


import java.util.Optional;

public record Argument (
        Optional<String> name,
        String value,
        Parser<?> parser,
        boolean optional
) {

    public Object parse() throws CommandException {
        try {
            return parser.parse(value);
        } catch (ParseException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
