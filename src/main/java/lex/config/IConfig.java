/*
 * LibEx
 * Copyright (c) 2017 by MineEx
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

package lex.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.state.IBlockState;

import java.util.List;
import java.util.Map;

public interface IConfig
{
    void parse(String jsonString);

    JsonElement compose();

    boolean has(String key);

    JsonElement get(String key);

    Map<String, JsonElement> getElements();

    boolean isSavable();

    String getString(String key, String fallbackValue);

    int getInt(String key, int fallbackValue);

    float getFloat(String key, float fallbackValue);

    boolean getBoolean(String key, boolean fallbackValue);

    <E extends Enum> E getEnum(String key, Class<? extends E> enumClass, E fallbackValue);

    IBlockState getBlock(String key, IBlockState fallbackValue);

    IConfig getInnerConfig(String key, JsonObject defaultValue);

    List<IConfig> getInnerConfigs(String key, List<JsonObject> defaultValue);

    String getString(String key);

    int getInt(String key);

    float getFloat(String key);

    boolean getBoolean(String key);

    <E extends Enum> E getEnum(String key, Class<? extends E> enumClass);

    IBlockState getBlock(String key);

    IConfig getInnerConfig(String key);

    List<IConfig> getInnerConfigs(String key);

    List<String> getStrings(String key, List<String> defaultValue);

    List<String> getStrings(String key);

}
