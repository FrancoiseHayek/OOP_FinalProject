package oop.project.library.command;

import java.util.Map;

public interface Command {

    String name = "";

    Map<String, Object> parse(String arguments) throws CommandException;

    public static Map<String, Object> useCustomCommand(Command custom, String arguments) throws CommandException {
        return custom.parse(arguments);
    }

}
