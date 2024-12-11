package oop.project.library.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Command {

    String name = "";
    List<Argument> positional = new ArrayList<>();
    Map<String, Argument> named = new HashMap<>();

    Map<String, Object> parse(String arguments) throws CommandException;

    static Map<String, Object> useCustomCommand(Command custom, String arguments) throws CommandException {
        return custom.parse(arguments);
    }

}
