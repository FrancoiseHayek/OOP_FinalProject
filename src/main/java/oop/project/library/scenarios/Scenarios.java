package oop.project.library.scenarios;

import oop.project.library.command.*;
import oop.project.library.lexer.Lexer;
import oop.project.library.parser.*;

import java.util.HashMap;
import java.util.Map;

public class Scenarios {

    public static Result<Map<String, Object>> parse(String command) {
        //Note: Unlike argparse4j, our library will contain a lexer than can
        //support an arbitrary String (instead of requiring a String[] array).
        //We still need to split the base command from the actual arguments
        //string to know which scenario (aka command) we're trying to parse
        //arguments for. This sounds like something a library should handle...
        var split = command.split(" ", 2);
        var base = split[0];
        var arguments = split.length == 2 ? split[1] : "";
        return switch (base) {
            case "lex" -> lex(arguments);
            case "add" -> add(arguments);
            case "sub" -> sub(arguments);
            case "fizzbuzz" -> fizzbuzz(arguments);
            case "difficulty" -> difficulty(arguments);
            case "echo" -> echo(arguments);
            case "search" -> search(arguments);
            case "weekday" -> weekday(arguments);
            default -> throw new AssertionError("Undefined command " + base + ".");
        };
    }

    private static Result<Map<String, Object>> lex(String arguments) {

        try {
            Lexer lexer = new Lexer(arguments);
            lexer.lex();

            Map<String, Object> lexedArgs = new HashMap<>();

            lexer.getPositional().forEach(arg -> {
                lexedArgs.put("0", arg);
            });

            lexedArgs.putAll(lexer.getNamed());
            
            return new Result.Success<>(new HashMap<>(lexedArgs));
        } catch (UnsupportedOperationException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> add(String arguments) {

        try {

            Command add = new AddCommand();
            var parsedArgs = add.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> sub(String arguments) {

        try {

            Command sub = new SubCommand();
            var parsedArgs = sub.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (ParseException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> fizzbuzz(String arguments) {
        //Note: This is the first command your library may not support all the
        //functionality to implement yet. This is fine - parse the number like
        //normal, then check the range manually. The goal is to get a feel for
        //the validation involved even if it's not in the library yet.
        //var number = IntegerParser.parse(lexedArguments.get("number"));
        //if (number < 1 || number > 100) ...

        try {

            Command fizzBuzz = new FizzBuzzCommand();
            var parsedArgs = fizzBuzz.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> difficulty(String arguments) {

        try {

            Command difficulty = new DifficultyCommand();
            var parsedArgs = difficulty.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> echo(String arguments) {

        try {

            Command echoCommand = new EchoCommand();
            var parsedArgs = echoCommand.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> search(String arguments) {

        try {

            Command search = new SearchCommand();
            var parsedArgs = search.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> weekday(String arguments) {

        try {

            Command localDate = new LocalDateCommand();
            var parsedArgs = localDate.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }
    }

}
