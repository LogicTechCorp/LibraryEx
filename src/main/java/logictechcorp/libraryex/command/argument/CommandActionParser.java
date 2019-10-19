package logictechcorp.libraryex.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import logictechcorp.libraryex.command.CommandAction;
import net.minecraft.command.ISuggestionProvider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public class CommandActionParser
{
    private final StringReader reader;
    private CommandAction commandAction;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions;

    public CommandActionParser(StringReader reader)
    {
        this.reader = reader;
        this.suggestions = SuggestionsBuilder::buildFuture;
    }

    public CommandActionParser parse()
    {
        int i = this.reader.getCursor();

        while(this.reader.canRead() && this.isValidCharacter(this.reader.peek()))
        {
            this.reader.skip();
        }

        this.commandAction = CommandAction.fromString(this.reader.getString().substring(i, this.reader.getCursor()));
        this.suggestions = this::getAllSuggestions;
        return this;
    }

    private boolean isValidCharacter(char character)
    {
        return character >= 'a' && character <= 'z' || character >= 'A' && character <= 'Z';
    }

    public CommandAction getCommandAction()
    {
        return this.commandAction;
    }

    private CompletableFuture<Suggestions> getAllSuggestions(SuggestionsBuilder builder)
    {
        return ISuggestionProvider.suggest(Stream.of(CommandAction.values()).filter(action -> action != CommandAction.UNKNOWN).map(action -> action.toString().toLowerCase()), builder);
    }

    public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder)
    {
        return this.suggestions.apply(builder.createOffset(this.reader.getCursor()));
    }
}
