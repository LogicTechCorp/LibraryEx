package lex.config;

import com.google.gson.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import static lex.util.JsonUtils.*;

public class ConfigFactory
{
    private static final JsonParser JSON_PARSER = new JsonParser();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static Config parseFile(File configFile)
    {
        String jsonString = new JsonObject().toString();

        if(configFile.exists())
        {
            try
            {
                jsonString = FileUtils.readFileToString(configFile, Charset.defaultCharset());
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        Config config = new Config(configFile, false);
        JsonElement element = JSON_PARSER.parse(jsonString);

        if(isObject(element))
        {
            for(Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet())
            {
                config.getElements().put(entry.getKey(), entry.getValue());
            }
        }

        return config;
    }

    protected static Config parseString(String jsonString)
    {
        Config config = new Config(null, true);
        JsonElement element = JSON_PARSER.parse(jsonString);

        if(isObject(element))
        {
            for(Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet())
            {
                config.getElements().put(entry.getKey(), entry.getValue());
            }
        }

        return config;
    }

    protected static JsonElement composeRootObject(Config config)
    {
        JsonObject rootObject = new JsonObject();

        for(Map.Entry<String, Config> entry : config.getInnerConfigs().entrySet())
        {
            if(config.has(entry.getKey()))
            {
                config.add(entry.getKey(), composeRootObject(entry.getValue()));
            }
            else if(config.hasDefault(entry.getKey()))
            {
                config.addDefault(entry.getKey(), composeRootObject(entry.getValue()));
            }
        }

        for(Map.Entry<String, JsonElement> entry : config.getElements().entrySet())
        {
            rootObject.add(entry.getKey(), entry.getValue());
        }

        for(Map.Entry<String, JsonElement> entry : config.getDefaultElements().entrySet())
        {
            if(!rootObject.has(entry.getKey()))
            {
                rootObject.add(entry.getKey(), entry.getValue());
            }
        }

        return rootObject;
    }

    public static void saveConfig(Config config)
    {
        if(config.getFile() != null && !config.isInnerConfig())
        {
            String jsonString = GSON.toJson(composeRootObject(config));

            try
            {
                FileUtils.write(config.getFile(), jsonString, Charset.defaultCharset());
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
