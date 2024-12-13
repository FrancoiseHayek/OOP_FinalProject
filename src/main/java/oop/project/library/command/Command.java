package oop.project.library.command;

import oop.project.library.parser.ParseException;
import oop.project.library.parser.Parser;
import oop.project.library.lexer.Lexer;
import oop.project.library.parser.StringParser;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class Command {

    private final String commandName;
    private final List<Argument> requiredPositionalArgs; /// Positional Arguments that a command can, but not must, take in
    private final Map<String, Argument> requiredNamedArgs; /// Named Arguments that command can, but not must, take in
    private final int maxPositionalArgs;
    private final int maxNamedArgsArgs;

    private List<String> lexedPositionalArgs;
    private Map<String, String> lexedNamedArgs;
    private final List<Argument> positionalArgs = new ArrayList<>(); /// Positional arguments given by the user
    private final Map<String, Argument> namedArgs = new HashMap<>(); /// Named arguments given by the user

    Parser<?> defaultParser; /// Parser used to parse extra/optional arguments when expected

    /// Lexes the given user input and splits the input into positional arguments and named ones
    /// Throws a CommandException to the end-user if the given input could not be lexed properly or if the lexed
    /// arguments violate any of the command properties
    private void lex(String arguments) throws CommandException {

        try {
            Lexer lexer = new Lexer(arguments);
            lexer.lex();

            lexedPositionalArgs = lexer.getPositional();
            lexedNamedArgs = lexer.getNamed();
        } catch (UnsupportedOperationException e) {
            throw new CommandException(e.getMessage());
        }

        // Assert less than max number of positional & named args were given
        if (lexedPositionalArgs.size() > maxPositionalArgs) {
            throw new CommandException(commandName + " command accepts up to " + maxPositionalArgs + " positional arguments, but found " + lexedPositionalArgs.size());
        }
        if (lexedNamedArgs.size() > maxNamedArgsArgs) {
            throw new CommandException(commandName + " command accepts up to " + maxNamedArgsArgs + " named arguments, but found " + lexedNamedArgs.size());
        }

    }

    /// Fills in arguments that were not provided by the user but have defaults
    /// Throws CommandExceptions to the user if non-optional/non-default arguments are missing
    private void fillMissingDefaultArgs() throws CommandException {

        if (positionalArgs.size() < requiredPositionalArgs.size()) {
            for (int i = positionalArgs.size(); i < requiredPositionalArgs.size(); i++) {
                var arg = requiredPositionalArgs.get(i);
                if (arg.optional()) {
                    positionalArgs.add(requiredPositionalArgs.get(i));
                } else {
                    throw new CommandException(commandName + " requires at least " + requiredPositionalArgs.size() + " positional arguments, but found only " + positionalArgs.size());
                }
            }
        }

        if (namedArgs.size() < requiredNamedArgs.size()) {
            requiredNamedArgs.forEach( (k, v) -> {
                if (!namedArgs.containsKey(k)) {
                    if (v.optional()) {
                        namedArgs.put(k, v);
                    } else {
                        throw new CommandException("Required argument " + k + " for " + commandName + " command not found");
                    }
                }
            });
        }
    }

    /// Creates Arguments for the Command from the given inputs
    /// Applies each Argument's parser to its value
    /// Returns a Map of names of Arguments and their parsed values
    public Map<String, Object> parse(String arguments) throws CommandException {

        lex(arguments);

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

        fillMissingDefaultArgs();

        // Apply parser to all arguments
        Map<String, Object> parsedArgs = new HashMap<>();
        positionalArgs.forEach(arg -> {
            try {
                parsedArgs.put(arg.name().orElse(""), arg.parse());
            } catch (ParseException e) {
                throw new CommandException(e.getMessage());
            }
        });
        namedArgs.forEach((k, v) -> {
            try {
                parsedArgs.put(k, v.parse());
            } catch (ParseException e) {
                throw new CommandException(e.getMessage());
            }
        });

        return parsedArgs;

    }

    /// Command constructor
    /// Private to enforce the use of the Builder
    private Command(Builder builder) {
        this.commandName = builder.commandName;
        this.requiredPositionalArgs = builder.requiredPositionalArgs;
        this.requiredNamedArgs = builder.requiredNamedArgs;
        this.maxPositionalArgs = builder.maxPositionalArgs;
        this.maxNamedArgsArgs = builder.maxNamedArgs;
        this.defaultParser = builder.defaultParser;
    }

    /// Command Builder to allow for command customization
    public static class Builder {
        private String commandName = null;
        private List<Argument> requiredPositionalArgs = new ArrayList<>();
        private Map<String, Argument> requiredNamedArgs = new HashMap<>();
        private int maxPositionalArgs = 0;
        private int maxNamedArgs = 0;
        Parser<?> defaultParser = new StringParser(); /// StringParser used as a default catch-all

        public Builder withName(String name) {
            this.commandName = name;
            return this;
        }

        /// Ensures optional positional arguments are listed after required positional arguments during command building
        public Builder withRequiredPositionalArgs(List<Argument> requiredPositionalArgs) {

            boolean stillRequired = true;
            for (Argument arg : requiredPositionalArgs) {
                if (arg.optional()) {
                    stillRequired = false;
                } else if (!stillRequired) {
                    throw new CommandException("Required argument " + arg.name() + " for " + commandName + " must come before optional arguments");
                }
            }

            this.requiredPositionalArgs = requiredPositionalArgs;
            return this;
        }

        /// Ensures arguments are uniquely names during command building
        public Builder withRequiredNamedArgs(Map<String, Argument> requiredNamedArgs) {

            int n = requiredNamedArgs.size();
            for (int i = 0; i < n; i++) {
                var arg1 = requiredNamedArgs.get(requiredNamedArgs.keySet().toArray()[i]);
                for (int j = i + 1; j < n; j++) {
                    var arg2 = requiredNamedArgs.get(requiredNamedArgs.keySet().toArray()[j]);
                    if (arg1.name().equals(arg2.name())) {
                        throw new CommandLibraryException("Argument names must be unique: " + arg1.name().orElse("\"\"") + " and " + arg2.name().orElse("\"\""));
                    }
                }
            }
            this.requiredNamedArgs = requiredNamedArgs;
            return this;
        }

        public Builder withMaxPositionalArgs(int maxPositionalArgs) {
            if (maxPositionalArgs < 0) {
                throw new CommandLibraryException("Max positional arguments must be non-negative");
            }
            this.maxPositionalArgs = maxPositionalArgs;
            return this;
        }

        public Builder withMaxNamedArgs(int maxNamedArgs) {
            if (maxNamedArgs < 0) {
                throw new CommandLibraryException("Max named arguments must be non-negative");
            }
            this.maxNamedArgs = maxNamedArgs;
            return this;
        }

        public Builder withDefaultParser(Parser<?> defaultParser) {
            this.defaultParser = defaultParser;
            return this;
        }

        /// Used to build the command after applying all customizations
        /// Ensures the command has a name so that ensuing errors can be properly described/routed
        /// Ensures that the given required positional/named arguments does not exceed the maximum number of
        /// positional/named arguments the command can take
        public Command build() {
            if (this.commandName == null) {
                throw new CommandLibraryException("No command name specified");
            } else if (requiredPositionalArgs.size() > maxPositionalArgs) {
                throw new CommandLibraryException("Required positional args should not exceed the given max: " + requiredPositionalArgs.size() + " required args given and " + maxPositionalArgs + " args allowed for " + commandName + " command");
            } else if (requiredNamedArgs.size() > maxNamedArgs) {
                throw new CommandLibraryException("Required named args should not exceed the given max: " + requiredNamedArgs.size() + " required args given and " + maxNamedArgs + " args allowed for " + commandName + " command");
            }
            return new Command(this);
        }
    }

}
