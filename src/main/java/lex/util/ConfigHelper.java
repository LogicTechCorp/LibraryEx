/*
 * LibEx
 * Copyright (c) 2017-2018 by MineEx
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

package lex.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import lex.LibEx;
import lex.api.config.IConfig;
import lex.config.Config;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ConfigHelper
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void saveConfig(IConfig config, File configFile)
    {
        if(configFile != null)
        {
            if(configFile.getPath().startsWith("~"))
            {
                configFile = new File(configFile.getPath().replace("~", LibEx.CONFIG_DIRECTORY.getPath()));
            }

            String jsonString = GSON.toJson(config.serialize());

            try
            {
                FileUtils.write(configFile, jsonString, Charset.defaultCharset());
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static List<IConfig> getFileConfigs(File directory)
    {
        List<IConfig> configs = new ArrayList<>();

        if(directory.exists())
        {
            for(File file : FileUtils.listFilesAndDirs(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE))
            {
                if(FileHelper.getFileExtension(file).equals("json"))
                {
                    configs.add(new Config(file));
                }
            }
        }

        return configs;
    }

    public static boolean isString(JsonElement element)
    {
        return isPrimitive(element) && element.getAsJsonPrimitive().isString();
    }

    public static boolean isInt(JsonElement element)
    {
        return isPrimitive(element) && element.getAsJsonPrimitive().isNumber();
    }

    public static boolean isFloat(JsonElement element)
    {
        return isPrimitive(element) && element.getAsJsonPrimitive().isNumber();
    }

    public static boolean isBoolean(JsonElement element)
    {
        return isPrimitive(element) && element.getAsJsonPrimitive().isBoolean();
    }

    public static boolean isPrimitive(JsonElement element)
    {
        return !isNull(element) && element.isJsonPrimitive();
    }

    public static boolean isObject(JsonElement element)
    {
        return !isNull(element) && element.isJsonObject();
    }

    public static boolean isArray(JsonElement element)
    {
        return !isNull(element) && element.isJsonArray();
    }

    public static boolean isNull(JsonElement element)
    {
        return element == null || element.isJsonNull();
    }
}
