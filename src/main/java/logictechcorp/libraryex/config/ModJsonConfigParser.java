/*
 * LibraryEx
 * Copyright (c) 2017-2019 by LogicTechCorp
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package logictechcorp.libraryex.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.io.*;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A json config parser that uses LibraryEx's custom json config format
 * <p>
 * Based on code written by TheElectronWill here:
 * https://github.com/TheElectronWill/Night-Config/blob/3bf426a86994acfe650ab17919c27043090bb7b6/json/src/main/java/com/electronwill/nightconfig/json/JsonParser.java
 */
public class ModJsonConfigParser implements ConfigParser<Config>
{
    private static final char[] SPACES = {' ', '\t', '\n', '\r'};
    private static final char[] TRUE_TAIL = {'r', 'u', 'e'};
    private static final char[] FALSE_TAIL = {'a', 'l', 's', 'e'};
    private static final char[] NULL_TAIL = {'u', 'l', 'l'};
    private static final char[] NUMBER_END = {',', '}', ']', ' ', '\t', '\n', '\r'};

    @Override
    public Config parse(Reader reader)
    {
        Config config = ModJsonConfigFormat.newConfig();
        this.parse(reader, config, ParsingMode.MERGE);
        return config;
    }

    @Override
    public void parse(Reader reader, Config output, ParsingMode parsingMode)
    {
        CharacterInput input = new ReaderInput(reader);

        if(input.peek() == -1)
        {
            return;
        }

        char fileStart = input.readCharAndSkip(SPACES);

        if(fileStart != '{')
        {
            throw new ParsingException("Invalid character for the root json object: " + fileStart);
        }

        parsingMode.prepareParsing(output);
        this.parseObject(input, output, parsingMode);
    }

    private <T extends Config> T parseObject(CharacterInput input, T config, ParsingMode parsingMode)
    {
        char keyStart = input.readCharAndSkip(SPACES);

        if(keyStart == '}')
        {
            return config;
        }
        else if(keyStart != '"')
        {
            throw new ParsingException("Invalid beginning of a key: " + keyStart);
        }

        this.parseMapEntry(input, config, parsingMode);

        while(true)
        {
            char valueSeparator = input.readCharAndSkip(SPACES);

            if(valueSeparator == '}')
            {
                return config;
            }
            else if(valueSeparator != ',')
            {
                throw new ParsingException("Invalid value separator: " + valueSeparator);
            }

            keyStart = input.readCharAndSkip(SPACES);

            if(keyStart != '"')
            {
                throw new ParsingException("Invalid beginning of a key: " + keyStart);
            }

            this.parseMapEntry(input, config, parsingMode);
        }
    }

    private void parseMapEntry(CharacterInput input, Config config, ParsingMode parsingMode)
    {
        String key = this.parseString(input);
        char separator = input.readCharAndSkip(SPACES);

        if(separator != ':')
        {
            throw new ParsingException("Invalid key/value separator: " + separator);
        }

        char valueStart = input.readCharAndSkip(SPACES);
        Object value = this.parseValue(input, valueStart, parsingMode);
        parsingMode.put(config, key, value);
    }

    private <T> List<T> parseArray(CharacterInput input, List<T> list, ParsingMode parsingMode)
    {
        boolean first = true;

        while(true)
        {
            char valueStart = input.readCharAndSkip(SPACES);

            if(first && valueStart == ']')
            {
                return list;
            }

            first = false;
            T value = (T) this.parseValue(input, valueStart, parsingMode);
            list.add(value);
            char next = input.readCharAndSkip(SPACES);

            if(next == ']')
            {
                return list;
            }
            else if(next != ',')
            {
                throw new ParsingException("Invalid value separator: " + valueStart);
            }
        }
    }

    private Object parseValue(CharacterInput input, char firstChar, ParsingMode parsingMode)
    {
        switch(firstChar)
        {
            case '"':
                return this.parseString(input);
            case '{':
                return this.parseObject(input, ModJsonConfigFormat.newConfig(), parsingMode);
            case '[':
                return this.parseArray(input, new ArrayList<>(), parsingMode);
            case 't':
                return this.parseTrue(input);
            case 'f':
                return this.parseFalse(input);
            case 'n':
                return this.parseNull(input);
            default:
                input.pushBack(firstChar);
                return this.parseNumber(input);
        }
    }

    private Number parseNumber(CharacterInput input)
    {
        CharsWrapper number = input.readCharsUntil(NUMBER_END);

        if(number.contains('.') || number.contains('e') || number.contains('E'))
        {
            return Utils.parseDouble(number);
        }
        long l = Utils.parseLong(number, 10);
        int small = (int) l;

        if(l == small)
        {
            return small;
        }

        return l;
    }

    private boolean parseTrue(CharacterInput input)
    {
        CharsWrapper chars = input.readChars(3);

        if(!chars.contentEquals(TRUE_TAIL))
        {
            throw new ParsingException("Invalid value: t" + chars + " - expected boolean true");
        }
        return true;
    }

    private boolean parseFalse(CharacterInput input)
    {
        CharsWrapper chars = input.readChars(4);
        if(!chars.contentEquals(FALSE_TAIL))
        {
            throw new ParsingException("Invalid value: f" + chars + " - expected boolean false");
        }
        return false;
    }

    private Object parseNull(CharacterInput input)
    {
        CharsWrapper chars = input.readChars(3);
        if(!chars.contentEquals(NULL_TAIL))
        {
            throw new ParsingException("Invalid value: n" + chars + " - expected null");
        }
        return null;
    }

    private String parseString(CharacterInput input)
    {
        StringBuilder builder = new StringBuilder();
        boolean escape = false;
        char stringChar;

        while((stringChar = input.readChar()) != '"' || escape)
        {
            if(escape)
            {
                builder.append(this.escape(stringChar, input));
                escape = false;
            }
            else if(stringChar == '\\')
            {
                escape = true;
            }
            else
            {
                builder.append(stringChar);
            }
        }

        return builder.toString();
    }

    private char escape(char c, CharacterInput input)
    {
        switch(c)
        {
            case '"':
            case '\\':
            case '/':
                return c;
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'u':
                CharsWrapper chars = input.readChars(4);
                return (char) Utils.parseInt(chars, 16);
            default:
                throw new ParsingException("Invalid escapement: \\" + c);
        }
    }

    @Override
    public ConfigFormat<Config> getFormat()
    {
        return ModJsonConfigFormat.instance();
    }
}
