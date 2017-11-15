package lex.config;

import com.google.gson.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class ConfigExHelper
{
    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void writeConfig(File path, IConfigEx config)
    {
        if(config.isValid())
        {
            String json = GSON.toJson(composeConfig(config));

            try
            {
                FileUtils.write(path, json, Charset.defaultCharset());
            }
            catch(IOException e)
            {
                config.getLogger().warn("The {} config was unable to be written to a file\n" + e.getMessage(), config.getName());
            }
        }
        else
        {
            config.getLogger().warn("The {} config is not valid and it will not be written to a file", config.getName());
        }
    }

    public static JsonElement composeConfig(IConfigEx config)
    {
        JsonObject root = new JsonObject();

        for(Map.Entry<String, JsonElement> entry : config.getAll().entrySet())
        {
            root.add(entry.getKey(), entry.getValue());
        }

        for(Map.Entry<String, JsonElement> entry : config.getAllFallbacks().entrySet())
        {
            if(!root.has(entry.getKey()))
            {
                root.add(entry.getKey(), entry.getValue());
            }
        }

        for(Map.Entry<String, IConfigEx> entry : config.getSubConfigs().entrySet())
        {
            root.add(entry.getKey(), composeConfig(entry.getValue()));
        }

        return root;
    }
}
