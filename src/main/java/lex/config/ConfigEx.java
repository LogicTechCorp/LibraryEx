package lex.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ConfigEx implements IConfigEx
{
    public static final JsonParser JSON_PARSER = new JsonParser();

    private final String CONFIG_NAME;
    private final Map<String, JsonElement> JSON_ELEMENTS = new HashMap<>();
    private final Map<String, JsonElement> JSON_FALLBACK_ELEMENTS = new HashMap<>();
    private Logger LOGGER = LogManager.getLogger("LibEx|ConfigEx");

    public ConfigEx(String configNameIn, File configFile)
    {
        CONFIG_NAME = configNameIn.replace(" ", "");
        LOGGER = LogManager.getLogger("LibEx|ConfigEx-" + CONFIG_NAME);

        String json = null;

        if(configFile.exists())
        {
            LOGGER.info("Found the {} config file", CONFIG_NAME);

            try
            {
                json = FileUtils.readFileToString(configFile, Charset.defaultCharset());
            }
            catch(IOException e)
            {
                LOGGER.warn("Error reading the {} config file \n" + e.getMessage(), CONFIG_NAME);
            }
        }
        else
        {
            LOGGER.warn("The {} config file is missing", CONFIG_NAME);
        }

        parse(json);
    }

    @Override
    public void parse(String json)
    {
        if(!Strings.isBlank(json))
        {
            JsonElement element = JSON_PARSER.parse(json);

            if(element != null && element.isJsonObject())
            {
                for(Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet())
                {
                    JSON_ELEMENTS.put(entry.getKey(), entry.getValue());
                }
            }
            else
            {
                LOGGER.warn("The {} config file's root element is null or not a json object", CONFIG_NAME);
            }
        }
        else
        {
            LOGGER.warn("The {} config file is null or empty", CONFIG_NAME);
        }
    }

    @Override
    public String getString(String key, String fallbackValue)
    {
        if(!Strings.isBlank(fallbackValue))
        {
            JSON_FALLBACK_ELEMENTS.put(key, new JsonPrimitive(fallbackValue));
        }
        return getString(key);
    }

    @Override
    public int getInt(String key, int fallbackValue)
    {
        JSON_FALLBACK_ELEMENTS.put(key, new JsonPrimitive(fallbackValue));
        return getInt(key);
    }

    @Override
    public float getFloat(String key, float fallbackValue)
    {
        JSON_FALLBACK_ELEMENTS.put(key, new JsonPrimitive(fallbackValue));
        return getFloat(key);
    }

    @Override
    public boolean getBoolean(String key, boolean fallbackValue)
    {
        JSON_FALLBACK_ELEMENTS.put(key, new JsonPrimitive(fallbackValue));
        return getBoolean(key);
    }

    @Override
    public String getString(String key)
    {
        if(JSON_ELEMENTS.containsKey(key))
        {
            return JSON_ELEMENTS.get(key).getAsString();
        }
        else if(JSON_FALLBACK_ELEMENTS.containsKey(key))
        {
            return JSON_FALLBACK_ELEMENTS.get(key).getAsString();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return "MissingNo";
        }
    }

    @Override
    public int getInt(String key)
    {
        if(JSON_ELEMENTS.containsKey(key))
        {
            return JSON_ELEMENTS.get(key).getAsInt();
        }
        else if(JSON_FALLBACK_ELEMENTS.containsKey(key))
        {
            return JSON_FALLBACK_ELEMENTS.get(key).getAsInt();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return 0;
        }

    }

    @Override
    public float getFloat(String key)
    {
        if(JSON_ELEMENTS.containsKey(key))
        {
            return JSON_ELEMENTS.get(key).getAsFloat();
        }
        else if(JSON_FALLBACK_ELEMENTS.containsKey(key))
        {
            return JSON_FALLBACK_ELEMENTS.get(key).getAsFloat();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return 0.0F;
        }
    }

    @Override
    public boolean getBoolean(String key)
    {
        if(JSON_ELEMENTS.containsKey(key))
        {
            return JSON_ELEMENTS.get(key).getAsBoolean();
        }
        else if(JSON_FALLBACK_ELEMENTS.containsKey(key))
        {
            return JSON_FALLBACK_ELEMENTS.get(key).getAsBoolean();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return false;
        }
    }
}
