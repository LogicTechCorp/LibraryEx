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

package lex.api.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;

public interface IConfig
{
    void deserialize(String jsonString);

    JsonElement serialize();

    void addData(String key, JsonElement element);

    void addFallbackData(String key, JsonElement element);

    void addDataBranch(String key, IConfig config);

    boolean hasData(String key);

    boolean hasFallbackData(String key);

    boolean hasDataBranch(String key);

    JsonElement getData(String key);

    JsonElement getFallbackData(String key);

    Map<String, JsonElement> getAllData();

    void removeData(String key);

    String getString(String key, String fallbackValue);

    int getInt(String key, int fallbackValue);

    float getFloat(String key, float fallbackValue);

    boolean getBoolean(String key, boolean fallbackValue);

    <E extends Enum> E getEnum(String key, Class<? extends E> enumClass, E fallbackValue);

    ResourceLocation getResource(String key, ResourceLocation fallbackValue);

    IBlockState getBlock(String key, IBlockState fallbackValue);

    ItemStack getItem(String key, ItemStack fallbackValue);

    IConfig getDataBranch(String key, JsonObject defaultValue);

    String getString(String key);

    int getInt(String key);

    float getFloat(String key);

    boolean getBoolean(String key);

    <E extends Enum> E getEnum(String key, Class<? extends E> enumClass);

    ResourceLocation getResource(String key);

    IBlockState getBlock(String key);

    ItemStack getItem(String key);

    IConfig getDataBranch(String key);

    List<IConfig> getDataBranches(String key, List<JsonObject> defaultValue);

    List<IConfig> getDataBranches(String key);

    List<String> getStrings(String key, List<String> defaultValue);

    List<String> getStrings(String key);

}
