package oop.project.library.command;

import oop.project.library.lexer.Lexer;
import oop.project.library.parser.IntegerParser;
import oop.project.library.parser.Parser;

import java.util.*;

public class AddCommand implements Command {

    @Override
    public Map<String, Object> parse(String arguments) throws CommandException {

        var lexer = new Lexer(arguments);
        lexer.lex();

        if (!lexer.getNamed().isEmpty()) {
            throw new CommandException("Named arguments not allowed, found " + lexer.getNamed().size());
        }

        List<String> rawPositional = lexer.getPositional();

        if (rawPositional.size() != 2) {
            throw new CommandException("Invalid number of arguments, should be 2 but found: " + rawPositional.size());
        }

        List<Argument> argumentList = new ArrayList<>();
        Parser<Integer> parser = new IntegerParser();

        rawPositional.forEach(arg -> {
            argumentList.add(new Argument(Optional.empty(), Optional.of(arg), parser, false));
        });

        Map<String, Object> parsedArgs = new HashMap<>();

        parsedArgs.put("left", argumentList.getFirst().parse());
        parsedArgs.put("right", argumentList.getLast().parse());

        return parsedArgs;

    }
}
