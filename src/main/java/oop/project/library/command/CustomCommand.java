package oop.project.library.command;

import oop.project.library.parser.ParseException;
import oop.project.library.parser.Parser;
import oop.project.library.lexer.Lexer;

import java.util.*;

public class CustomCommand {


    private final List<Argument> requiredPositionalArgs;
    private final Map<String, Argument> requiredNamedArgs;
    private final int maxPositionalArgs;
    private final int maxNamedArgsArgs;

    private final List<Argument> positionalArgs = new ArrayList<>();
    private final Map<String, Argument> namedArgs = new HashMap<>();

    Parser<?> defaultParser = null; // Parser used to parse extra/optional arguments when expected

    public Map<String, Object> parse(String arguments) {

        try {
            Lexer lexer = new Lexer(arguments);
            lexer.lex();

            var lexedPositionalArgs = lexer.getPositional();
            var lexedNamedArgs = lexer.getNamed();

            // Assert max number of positional & named args were given
            if (lexedPositionalArgs.size() > maxPositionalArgs) {
                throw new CommandException("Only " + maxPositionalArgs + " positional arguments allowed, but found " + lexedPositionalArgs.size());
            }
            if (lexedNamedArgs.size() > maxNamedArgsArgs) {
                throw new CommandException("Only " + maxNamedArgsArgs + " named arguments allowed, but found " + lexedNamedArgs.size());
            }

            int index = 0;
            for (int i = 0; i < lexedPositionalArgs.size(); i++) {
                String value = lexedPositionalArgs.get(i);
                if (index < requiredPositionalArgs.size()) {
                    var arg = requiredPositionalArgs.get(index++);
                    positionalArgs.add(new Argument(arg.name(), Optional.of(value), arg.parser(), arg.optional()));
                } else {
                    positionalArgs.add(new Argument(Optional.of(String.valueOf(i)), Optional.of(value), defaultParser, true));
                }
            }

            lexedNamedArgs.forEach( (k, v) -> {

                if (requiredNamedArgs.containsKey(k)) {
                    var arg = requiredNamedArgs.get(k);
                    namedArgs.put(k, new Argument(arg.name(), Optional.of(v), arg.parser(), arg.optional()));
                } else {
                    namedArgs.put(k, new Argument(Optional.of(k), Optional.of(v), defaultParser, true));
                }
            });


            if (positionalArgs.size() < requiredPositionalArgs.size()) {
                for (int i = positionalArgs.size(); i < requiredPositionalArgs.size(); i++) {
                    var arg = requiredPositionalArgs.get(i);
                    if (arg.optional()) {
                        positionalArgs.add(requiredPositionalArgs.get(i));
                    } else {
                        throw new CommandException(requiredPositionalArgs.size() + " positional arguments required, but found " + positionalArgs.size());
                    }
                }
            }

            if (namedArgs.size() < requiredNamedArgs.size()) {
                requiredNamedArgs.forEach( (k, v) -> {
                    if (!namedArgs.containsKey(k)) {
                        if (v.optional()) {
                            namedArgs.put(k, v);
                        } else {
                            throw new CommandException("Required argument " + k + " not found");
                        }
                    }
                });
            }

            // Apply the parser to all the arguments
            Map<String, Object> parsedArgs = new HashMap<>();

            positionalArgs.forEach( arg -> {
                parsedArgs.put(arg.name().orElse(""), arg.parse());
            });

            namedArgs.forEach( (k, v) -> {
                parsedArgs.put(k, v.parse());
            });

            return parsedArgs;

        } catch (CommandException e) {
            throw new CommandException(e.getMessage());
        }

    }

    private CustomCommand(Builder builder) {
        String commandName = builder.commandName;
        this.requiredPositionalArgs = builder.requiredPositionalArgs;
        this.requiredNamedArgs = builder.requiredNamedArgs;
        this.maxPositionalArgs = builder.maxPositionalArgs;
        this.maxNamedArgsArgs = builder.maxNamedArgs;
    }

    public static class Builder {
        private String commandName = null;
        private List<Argument> requiredPositionalArgs = new ArrayList<>();
        private Map<String, Argument> requiredNamedArgs = new HashMap<>();
        private int maxPositionalArgs = 0;
        private int maxNamedArgs = 0;

        public Builder withName(String name) {
            this.commandName = name;
            return this;
        }

        public Builder withRequiredPositionalArgs(List<Argument> requiredPositionalArgs) {
            this.requiredPositionalArgs = requiredPositionalArgs;
            return this;
        }

        public Builder withRequiredNamedArgs(Map<String, Argument> requiredNamedArgs) {
            this.requiredNamedArgs = requiredNamedArgs;
            return this;
        }

        public Builder withMaxPositionalArgs(int maxPositionalArgs) {
            this.maxPositionalArgs = maxPositionalArgs;
            return this;
        }

        public Builder withMaxNamedArgs(int maxNamedArgs) {
            this.maxNamedArgs = maxNamedArgs;
            return this;
        }

        public CustomCommand build() {
            return new CustomCommand(this);
        }
    }

}
