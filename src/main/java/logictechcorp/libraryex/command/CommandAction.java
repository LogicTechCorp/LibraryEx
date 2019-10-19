package logictechcorp.libraryex.command;

import java.util.stream.Stream;

public enum CommandAction
{
    UNKNOWN,
    CREATE,
    ADD,
    REMOVE;

    public static CommandAction fromString(String string)
    {
        return Stream.of(values()).filter(value -> value.toString().equalsIgnoreCase(string)).findFirst().orElse(UNKNOWN);
    }
}
