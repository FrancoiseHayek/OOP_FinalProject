package oop.project.library.scenarios;

import oop.project.library.command.*;
import oop.project.library.lexer.Lexer;
import oop.project.library.parser.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.Set;

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
            case "distance" -> distance(arguments);
            case "typeExtraction" -> typeExtraction(arguments);
            case "misusingCommandBuilder" -> misusingCommandBuilder(arguments);
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

            Parser<Integer> parser = new IntegerParser();
            Argument left = new Argument(Optional.of("left"), Optional.empty(), parser, false);
            Argument right = new Argument(Optional.of("right"), Optional.empty(), parser, false);

            Command add = new Command.Builder()
                    .withName("add")
                    .withMaxPositionalArgs(2)
                    .withMaxNamedArgs(0)
                    .withRequiredPositionalArgs(List.of(left, right))
                    .build();

            var parsedArgs = add.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> sub(String arguments) {

        try {

            Parser<Double> parser = new DoubleParser();
            Argument left = new Argument(Optional.of("left"), Optional.empty(), parser, false);
            Argument right = new Argument(Optional.of("right"), Optional.empty(), parser, false);
            Command sub = new Command.Builder()
                    .withName("sub")
                    .withMaxPositionalArgs(0)
                    .withMaxNamedArgs(2)
                    .withRequiredNamedArgs(Map.of(left.name().orElse(""), left, right.name().orElse(""), right))
                    .build();

            var parsedArgs = sub.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
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

            Parser<Integer> parser = IntegerParser.withInclusiveRange(1, 100);
            Argument value = new Argument(Optional.of("number"), Optional.empty(), parser, false);

            Command fizzBuzz = new Command.Builder()
                    .withName("fizzbuzz")
                    .withMaxNamedArgs(0)
                    .withMaxPositionalArgs(1)
                    .withRequiredPositionalArgs(List.of(value))
                    .build();

            var parsedArgs = fizzBuzz.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> difficulty(String arguments) {

        try {

            Parser<String> parser = StringParser.withChoices(Set.of("easy", "normal", "hard", "peaceful"));
            Argument difficulty = new Argument(Optional.of("difficulty"), Optional.empty(), parser, false);

            Command mode = new Command.Builder()
                    .withName("mode")
                    .withMaxNamedArgs(0)
                    .withMaxPositionalArgs(1)
                    .withRequiredPositionalArgs(List.of(difficulty))
                    .build();

            var parsedArgs = mode.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> echo(String arguments) {

        try {

            Parser<String> parser = new StringParser();
            Argument message = new Argument(Optional.of("message"), Optional.of("Echo, echo, echo!"), parser, true);

            Command echo = new Command.Builder()
                    .withName("echo")
                    .withMaxNamedArgs(0)
                    .withMaxPositionalArgs(1)
                    .withRequiredPositionalArgs(List.of(message))
                    .build();

            var parsedArgs = echo.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> search(String arguments) {

        try {

            Parser<String> stringParser = new StringParser();
            Parser<Boolean> booleanParser = new BooleanParser();
            Argument term = new Argument(Optional.of("term"), Optional.empty(), stringParser, false);
            Argument caseInsensitive = new Argument(Optional.of("case-insensitive"), Optional.of("false"), booleanParser, true);

            Command search = new Command.Builder()
                    .withName("search")
                    .withMaxNamedArgs(1)
                    .withMaxPositionalArgs(1)
                    .withRequiredNamedArgs(Map.of("case-insensitive", caseInsensitive))
                    .withRequiredPositionalArgs(List.of(term))
                    .build();

            var parsedArgs = search.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> weekday(String arguments) {

        try {

            Parser<?> parser = new LocalDateParser();
            Argument date = new Argument(Optional.of("date"), Optional.empty(), parser, false);

            Command localDate = new Command.Builder()
                    .withName("localDate")
                    .withMaxNamedArgs(0)
                    .withMaxPositionalArgs(1)
                    .withRequiredPositionalArgs(List.of(date))
                    .build();

            var parsedArgs = localDate.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> distance(String arguments) {

        try {

            Parser<Integer> parser = new IntegerParser();
            Argument x = new Argument(Optional.of("x"), Optional.of("0"), parser, true);
            Argument y = new Argument(Optional.of("y"), Optional.of("0"), parser, true);

            Command distance = new Command.Builder()
                    .withName("distance")
                    .withRequiredNamedArgs(Map.of("x", x, "y", y))
                    .withMaxPositionalArgs(0)
                    .withMaxNamedArgs(2)
                    .build();

            var parsedArgs = distance.parse(arguments);

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }

    }

    private static Result<Map<String, Object>> typeExtraction(String arguments) {

        try {

            Parser<Integer> parser = new IntegerParser();
            Argument x = new Argument(Optional.of("x"), Optional.of("0"), parser, true);
            Argument y = new Argument(Optional.of("y"), Optional.of("0"), parser, true);

            Command test = new Command.Builder()
                    .withName("test")
                    .withRequiredNamedArgs(Map.of("x", x, "y", y))
                    .withMaxPositionalArgs(0)
                    .withMaxNamedArgs(2)
                    .build();

            var parsedArgs = test.parse(arguments);

            int parsedX = (int) parsedArgs.get("x");
            int parsedY = (int) parsedArgs.get("y");

            var addition = parsedX + parsedY;
            parsedArgs.put("result", addition);

            return new Result.Success<>(parsedArgs);

        } catch (CommandException e) {
            return new Result.Failure<>(e.toString());
        }

    }

    private static Result<Map<String, Object>> misusingCommandBuilder(String arguments) {

        try {

            Parser<Integer> parser = new IntegerParser();
            Argument x = new Argument(Optional.of("x"), Optional.of("0"), parser, true);
            Argument y = new Argument(Optional.of("y"), Optional.of("0"), parser, true);

            Command test = new Command.Builder()
                    .withName("test")
                    .withRequiredNamedArgs(Map.of("x", x, "y", y))
                    .withMaxPositionalArgs(0)
                    .withMaxNamedArgs(0) // ERROR!
                    .build();

            var parsedArgs = test.parse(arguments);

            return new Result.Success<>(parsedArgs);

        } catch (CommandException | CommandLibraryException e) {
            return new Result.Failure<>(e.toString());
        }

    }

}
