package oop.project.library.command;

import oop.project.library.lexer.Lexer;
import oop.project.library.parser.BooleanParser;
import oop.project.library.parser.Parser;
import oop.project.library.parser.StringParser;

import java.util.*;

public class SearchCommand implements Command {

    @Override
    public Map<String, Object> parse(String arguments) throws CommandException {

        var lexer = new Lexer(arguments);
        lexer.lex();

        if (lexer.getNamed().size() > 1) {
            throw new CommandException("Multiple named arguments not allowed, found " + lexer.getNamed().size());
        }

        if (lexer.getNamed().isEmpty()) {
            lexer.getNamed().put("case-insensitive", "false");
        }

        if (lexer.getPositional().size() != 1) {
            throw new CommandException("Only 1 positional argument is allowed, found " + lexer.getPositional().size());
        }

        List<Argument> argumentList = new ArrayList<>();
        Map<String,Argument> argumentMap = new HashMap<>();
        Parser<String> stringParser = new StringParser();
        Parser<Boolean> booleanParser = new BooleanParser();

        lexer.getPositional().forEach(arg -> {
            argumentList.add(new Argument(Optional.empty(), Optional.of(arg), stringParser, false));
        });

        lexer.getNamed().forEach( (k, v) -> {
            if (!Objects.equals(k, "case-insensitive")) {
                throw new CommandException("Argument " + k + " not allowed");
            }
            argumentMap.put(k, new Argument(Optional.of(k), Optional.of(v), booleanParser, true));
        });

        Map<String, Object> parsedArgs = new HashMap<>();

        argumentMap.forEach( (k,v) -> {
            parsedArgs.put(k, v.parse());
        });

        argumentList.forEach(arg -> {
            parsedArgs.put("term", arg.parse());
        });

        return parsedArgs;

    }
}
