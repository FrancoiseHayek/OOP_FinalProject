package oop.project.library.command;

import oop.project.library.lexer.Lexer;
import oop.project.library.parser.Parser;
import oop.project.library.parser.StringParser;

import java.util.*;

public class DifficultyCommand implements Command {

    @Override
    public Map<String, Object> parse(String arguments) throws CommandException {

        var lexer = new Lexer(arguments);
        lexer.lex();

        if (!lexer.getNamed().isEmpty()) {
            throw new CommandException("Named arguments not allowed, found " + lexer.getNamed().size());
        }

        List<String> rawPositional = lexer.getPositional();

        List<Argument> argumentList = new ArrayList<>();
        Parser<String> parser = StringParser.withChoices(Difficulty.class);

        rawPositional.forEach(arg -> {
            argumentList.add(new Argument(Optional.empty(), arg, parser, false));
        });

        Map<String, Object> parsedArgs = new HashMap<>();

        argumentList.forEach(arg -> {
            parsedArgs.put("difficulty", arg.parse());
        });

        return parsedArgs;

    }
}
