package oop.project.library.scenarios;

import oop.project.library.command.Argument;
import oop.project.library.command.Command;
import oop.project.library.command.CommandException;
import oop.project.library.lexer.Lexer;
import oop.project.library.parser.ParseException;

import java.util.*;

public class LocalDateCommand implements Command {

    @Override
    public Map<String, Object> parse(String arguments) {

        var lexer = new Lexer(arguments);
        lexer.lex();

        if (!lexer.getNamed().isEmpty()) {
            throw new CommandException("Named arguments not supported, found " + lexer.getNamed().size());
        }

        if (lexer.getPositional().size() != 1) {
            throw new CommandException("Only 1 positional argument is allowed, found" + lexer.getPositional().size());
        }

        Map<String, Object> parsedArgs = new HashMap<>();
        LocalDateParser parser = new LocalDateParser();

        List<Argument> argumentList = new ArrayList<>();

        lexer.getPositional().forEach(arg -> {
            argumentList.add(new Argument(Optional.empty(), Optional.of(arg), parser, false));
        });

        argumentList.forEach(arg -> {
            try {
                parsedArgs.put("date", arg.parse());
            } catch (LocalDateException e) {
                throw new CommandException(e.getMessage());
            }
        });

        return parsedArgs;
    }
}
