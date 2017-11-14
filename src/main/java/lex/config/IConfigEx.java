package lex.config;

public interface IConfigEx
{
    void parse(String json);

    String getString(String key, String fallbackValue);

    int getInt(String key, int fallbackValue);

    float getFloat(String key, float fallbackValue);

    boolean getBoolean(String key, boolean fallbackValue);

    String getString(String key);

    int getInt(String key);

    float getFloat(String key);

    boolean getBoolean(String key);
}
