package oop.project.library.scenarios;

import oop.project.library.lexer.Lexer;
import oop.project.library.parser.*;

import java.util.HashMap;
import java.util.Map;

import static oop.project.library.parser.Parser.useParser;

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
            var lexedArgs = lexer.lex();
            return new Result.Success<>(new HashMap<>(lexedArgs));
        } catch (UnsupportedOperationException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> add(String arguments) {

        try {
            Lexer lexer = new Lexer(arguments);
            var lexedArgs = lexer.lex();

            if (lexedArgs.size() != 2) {
                throw new ParseException("Invalid number of arguments, should be 2 but found: " + lexedArgs.size());
            }

            Map<String, Integer> parsedArgs = new HashMap<>();
            Parser<Integer> parser = new IntegerParser();
            parsedArgs.put("left", parser.parse(lexedArgs.get("0")));
            parsedArgs.put("right", parser.parse(lexedArgs.get("1")));

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (ParseException | java.text.ParseException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> sub(String arguments) {

        try {
            Lexer lexer = new Lexer(arguments);
            var lexedArgs = lexer.lex();

            if (lexedArgs.size() != 2) {
                throw new ParseException("Invalid number of arguments, should be 2 but found: " + lexedArgs.size());
            }

            Map<String, Double> parsedArgs = new HashMap<>();
            Parser<Double> parser = new DoubleParser();

            parsedArgs.put("left", parser.parse(lexedArgs.get("left")));
            parsedArgs.put("right", parser.parse(lexedArgs.get("right")));

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (ParseException | java.text.ParseException e) {
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
            Lexer lexer = new Lexer(arguments);
            var lexedArgs = lexer.lex();

            if (lexedArgs.size() != 1) {
                throw new ParseException("Invalid number of arguments, should be 1 but found: " + lexedArgs.size());
            }

            Map<String, Integer> parsedArgs = new HashMap<>();
            Parser<Integer> parser = new IntegerParser();

            parsedArgs.put("number", parser.parse(lexedArgs.get("0")));

            var number = parsedArgs.get("number");
            if (number < 1 || number > 100) {
                throw new ParseException("Number must be between 1 and 100 (inclusive) but found: " + number);
            }

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (ParseException | java.text.ParseException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> difficulty(String arguments) {

        try {
            Lexer lexer = new Lexer(arguments);
            var lexedArgs = lexer.lex();

            if (lexedArgs.size() != 1) {
                throw new ParseException("Invalid number of arguments, should be 1 but found: " + lexedArgs.size());
            }

            Map<String, String> parsedArgs = new HashMap<>();
            Parser<String> parser = new StringParser();

            parsedArgs.put("difficulty", parser.parse(lexedArgs.get("0")));

            String difficulty = parsedArgs.get("difficulty");

            return switch (difficulty) {
                case "easy", "normal", "hard", "peaceful" -> new Result.Success<>(new HashMap<>(parsedArgs));
                default -> throw new ParseException("Invalid difficulty: " + difficulty);
            };

        } catch (ParseException | java.text.ParseException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> echo(String arguments) {

        try {
            Lexer lexer = new Lexer(arguments);
            var lexedArgs = lexer.lex();

            Map<String, String> parsedArgs = new HashMap<>();
            Parser<String> parser = new StringParser();

            if (lexedArgs.isEmpty()) {
                parsedArgs.put("message", "Echo, echo, echo!");
            } else {
                lexedArgs.forEach( (k,v) -> {
                    try {
                        parsedArgs.put("message", parser.parse(v));
                    } catch (java.text.ParseException e) {
                        throw new ParseException(e.getMessage());
                    }
                });
            }

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (ParseException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> search(String arguments) {

        try {
            Lexer lexer = new Lexer(arguments);
            var lexedArgs = lexer.lex();

            if (lexedArgs.isEmpty() || lexedArgs.size() > 2) {
                throw new ParseException("Invalid number of arguments, should be 1 or 2 but found: " + lexedArgs.size());
            } else if (lexedArgs.size() == 2 && !lexedArgs.containsKey("case-insensitive")) {
                throw new ParseException("Extraneous argument" + lexedArgs.get("1"));
            }

            Map<String, Object> parsedArgs = new HashMap<>();
            Parser<String> stringParser = new StringParser();

            parsedArgs.put("case-insensitive", false);

            lexedArgs.forEach( (k,v) -> {
               try {
                   if (k.equals("case-insensitive")) {
                       Parser<Boolean> booleanParser = new BooleanParser();
                       parsedArgs.put("case-insensitive", booleanParser.parse(v));
                   } else {

                       parsedArgs.put("term", stringParser.parse(v));
                   }

               } catch (ParseException | java.text.ParseException e) {
                   throw new ParseException(e.getMessage());
               }
            });

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (ParseException e) {
            return new Result.Failure<>(e.toString());
        }
    }

    private static Result<Map<String, Object>> weekday(String arguments) {

        try {
            Lexer lexer = new Lexer(arguments);
            var lexedArgs = lexer.lex();

            if (lexedArgs.size() != 1) {
                throw new LocalDateException("Invalid number of arguments, should be 1 but found: " + lexedArgs.size());
            }

            Map<String, Object> parsedArgs = new HashMap<>();
            LocalDateParser parser = new LocalDateParser();

            parsedArgs.put("date", parser.parse(lexedArgs.get("0")));

            return new Result.Success<>(new HashMap<>(parsedArgs));

        } catch (ParseException | LocalDateException e) {
            return new Result.Failure<>(e.toString());
        }
    }

}
