package oop.project.library.command;

import oop.project.library.lexer.Lexer;
import oop.project.library.parser.DoubleParser;
import oop.project.library.parser.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SubCommand implements Command {

    @Override
    public Map<String, Object> parse(String arguments) throws CommandException {

        var lexer = new Lexer(arguments);
        lexer.lex();

        if (!lexer.getPositional().isEmpty()) {
            throw new CommandException("Positional arguments not allowed, found " + lexer.getPositional().size());
        }

        Map<String, String> rawNamed = lexer.getNamed();

        if (rawNamed.size() != 2) {
            throw new CommandException("Invalid number of named arguments, should be 2 but found " + rawNamed.size());
        }

        if (!rawNamed.containsKey("left")) {
            throw new CommandException("Missing 'left' argument");
        }

        if (!rawNamed.containsKey("right")) {
            throw new CommandException("Missing 'right' argument");
        }

        Map<String, Argument> argumentsMap = new HashMap<>();
        Parser<Double> parser = new DoubleParser();

        rawNamed.forEach( (k, v) -> {
            argumentsMap.put(k, new Argument(Optional.of(k), Optional.of(v), parser, false));
        });

        Map<String, Object> parsedArgs = new HashMap<>();

        parsedArgs.put("left", argumentsMap.get("left").parse());
        parsedArgs.put("right", argumentsMap.get("right").parse());

        return parsedArgs;

    }
}
