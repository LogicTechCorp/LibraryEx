package lex.config;

import com.google.gson.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.WordUtils;
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

    public ConfigEx(String configNameIn, String json)
    {
        CONFIG_NAME = configNameIn.replace(" ", "");
        LOGGER = LogManager.getLogger("LibEx|ConfigEx-" + CONFIG_NAME);
        parse(json);
    }

    @Override
    public void parse(String json)
    {
        if(!Strings.isBlank(json))
        {
            JsonElement rootElement = JSON_PARSER.parse(json);

            if(rootElement != null)
            {
                if(rootElement.isJsonObject())
                {
                    for(Map.Entry<String, JsonElement> entry : rootElement.getAsJsonObject().entrySet())
                    {
                        JSON_ELEMENTS.put(entry.getKey(), entry.getValue());
                    }
                }
                else if(rootElement.isJsonArray())
                {
                    for(int i = 0; i < rootElement.getAsJsonArray().size(); i++)
                    {
                        JSON_ELEMENTS.put(Integer.toString(i), rootElement.getAsJsonArray().get(i));
                    }
                }
                else
                {
                    LOGGER.warn("The {} config file's root element is not a json object or array", CONFIG_NAME);
                }
            }
            else
            {
                LOGGER.warn("The {} config file's root element is null", CONFIG_NAME);
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
            addFallbackElement(key, new JsonPrimitive(fallbackValue));
        }
        return getString(key);
    }

    @Override
    public int getInt(String key, int fallbackValue)
    {
        addFallbackElement(key, new JsonPrimitive(fallbackValue));
        return getInt(key);
    }

    @Override
    public float getFloat(String key, float fallbackValue)
    {
        addFallbackElement(key, new JsonPrimitive(fallbackValue));
        return getFloat(key);
    }

    @Override
    public boolean getBoolean(String key, boolean fallbackValue)
    {
        addFallbackElement(key, new JsonPrimitive(fallbackValue));
        return getBoolean(key);
    }

    @Override
    public JsonObject getJsonObject(String key, JsonObject fallbackValue)
    {
        addFallbackElement(key, fallbackValue);
        return getJsonObject(key);
    }

    @Override
    public JsonArray getJsonArray(String key, JsonArray fallbackValue)
    {
        addFallbackElement(key, fallbackValue);
        return getJsonArray(key);
    }

    @Override
    public String getString(String key)
    {
        if(jsonElementsHave(key) && getElement(key).isJsonPrimitive() && getElement(key).getAsJsonPrimitive().isString())
        {
            return getElement(key).getAsString();
        }
        else if(jsonFallbackElementsHave(key) && getFallbackElement(key).isJsonPrimitive() && getFallbackElement(key).getAsJsonPrimitive().isString())
        {
            return getFallbackElement(key).getAsString();
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
        if(jsonElementsHave(key) && getElement(key).isJsonPrimitive() && getElement(key).getAsJsonPrimitive().isNumber())
        {
            return getElement(key).getAsInt();
        }
        else if(jsonFallbackElementsHave(key) && getFallbackElement(key).isJsonPrimitive() && getFallbackElement(key).getAsJsonPrimitive().isNumber())
        {
            return getFallbackElement(key).getAsInt();
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
        if(jsonElementsHave(key) && getElement(key).isJsonPrimitive() && getElement(key).getAsJsonPrimitive().isNumber())
        {
            return getElement(key).getAsFloat();
        }
        else if(jsonFallbackElementsHave(key) && getFallbackElement(key).isJsonPrimitive() && getFallbackElement(key).getAsJsonPrimitive().isNumber())
        {
            return getFallbackElement(key).getAsFloat();
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
        if(jsonElementsHave(key) && getElement(key).isJsonPrimitive() && getElement(key).getAsJsonPrimitive().isBoolean())
        {
            return getElement(key).getAsBoolean();
        }
        else if(jsonFallbackElementsHave(key) && getFallbackElement(key).isJsonPrimitive() && getFallbackElement(key).getAsJsonPrimitive().isBoolean())
        {
            return getFallbackElement(key).getAsBoolean();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return false;
        }
    }

    @Override
    public JsonObject getJsonObject(String key)
    {
        if(jsonElementsHave(key) && getElement(key).isJsonObject())
        {
            return getElement(key).getAsJsonObject();
        }
        else if(jsonFallbackElementsHave(key) && getFallbackElement(key).isJsonObject())
        {
            return getFallbackElement(key).getAsJsonObject();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return new JsonObject();
        }
    }

    @Override
    public JsonArray getJsonArray(String key)
    {
        if(jsonElementsHave(key) && getElement(key).isJsonArray())
        {
            return getElement(key).getAsJsonArray();
        }
        else if(jsonFallbackElementsHave(key) && getFallbackElement(key).isJsonArray())
        {
            return getFallbackElement(key).getAsJsonArray();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return new JsonArray();
        }
    }

    @Override
    public IConfigEx getJsonObjectAsConfig(String configName, String key)
    {
        JsonElement element;

        if(jsonElementsHave(key) && getElement(key).isJsonObject())
        {
            element = getElement(key).getAsJsonObject();
        }
        else if(jsonFallbackElementsHave(key) && getFallbackElement(key).isJsonObject())
        {
            element = getFallbackElement(key).getAsJsonObject();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return null;
        }
        return new ConfigEx(configName, element.toString());
    }

    @Override
    public IConfigEx getJsonArrayAsConfig(String configName, String key)
    {
        JsonArray array;

        if(jsonElementsHave(key) && getElement(key).isJsonArray())
        {
            array = getElement(key).getAsJsonArray();
        }
        else if(jsonFallbackElementsHave(key) && getFallbackElement(key).isJsonArray())
        {
            array = getFallbackElement(key).getAsJsonArray();
        }
        else
        {
            LOGGER.warn("The {} config file is missing the {} value and it does not have a fallback value", CONFIG_NAME, key);
            return null;
        }
        return new ConfigEx(configName, array.toString());
    }

    @Override
    public IConfigEx getJsonObjectAsConfig(String key)
    {
        return getJsonObjectAsConfig(WordUtils.capitalize(key), key);
    }

    @Override
    public IConfigEx getJsonArrayAsConfig(String key)
    {
        return getJsonArrayAsConfig(WordUtils.capitalize(key), key);
    }

    public void addFallbackElement(String key, JsonElement element)
    {
        JSON_FALLBACK_ELEMENTS.put(key, element);
    }

    public boolean jsonElementsHave(String key)
    {
        return JSON_ELEMENTS.containsKey(key);
    }

    public boolean jsonFallbackElementsHave(String key)
    {
        return JSON_FALLBACK_ELEMENTS.containsKey(key);
    }

    public JsonElement getElement(String key)
    {
        return JSON_ELEMENTS.get(key);
    }

    public JsonElement getFallbackElement(String key)
    {
        return JSON_FALLBACK_ELEMENTS.get(key);
    }
}
