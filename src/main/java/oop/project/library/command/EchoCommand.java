package oop.project.library.command;

import oop.project.library.lexer.Lexer;
import oop.project.library.parser.Parser;
import oop.project.library.parser.StringParser;

import java.util.*;

public class EchoCommand implements Command {

    @Override
    public Map<String, Object> parse(String arguments) throws CommandException {

        var lexer = new Lexer(arguments);
        lexer.lex();

        if (!lexer.getNamed().isEmpty()) {
            throw new CommandException("Named arguments not allowed, found " + lexer.getNamed().size());
        }

        List<String> rawPositional = lexer.getPositional();

        List<Argument> argumentList = new ArrayList<>();
        Parser<String> parser = new StringParser();

        rawPositional.forEach(arg -> {
            argumentList.add(new Argument(Optional.empty(), Optional.of(arg), parser, false));
        });

        Map<String, Object> parsedArgs = new HashMap<>();

        if (argumentList.isEmpty()) {
            parsedArgs.put("message", "Echo, echo, echo!");
        } else {
            argumentList.forEach(arg -> {
                parsedArgs.put("message", arg.parse());
            });
        }

        return parsedArgs;

    }
}
