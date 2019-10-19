package logictechcorp.libraryex.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import logictechcorp.libraryex.command.CommandAction;
import net.minecraft.command.CommandSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class CommandActionArgument implements ArgumentType<CommandAction>
{
    private static final Collection<String> EXAMPLES = Arrays.asList("create", "add", "remove");

    public static CommandActionArgument create()
    {
        return new CommandActionArgument();
    }

    public static CommandAction getCommandAction(CommandContext<CommandSource> context, String name)
    {
        return context.getArgument(name, CommandAction.class);
    }

    @Override
    public CommandAction parse(StringReader reader)
    {
        CommandActionParser parser = new CommandActionParser(reader).parse();
        return parser.getCommandAction();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        return new CommandActionParser(reader).parse().getSuggestions(builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }
}
