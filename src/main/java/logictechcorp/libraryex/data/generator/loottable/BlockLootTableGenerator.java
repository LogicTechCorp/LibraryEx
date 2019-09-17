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

package logictechcorp.libraryex.data.generator.loottable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ldtteam.datagenerators.loot_table.LootTableJson;
import com.ldtteam.datagenerators.loot_table.LootTableTypeEnum;
import com.ldtteam.datagenerators.loot_table.pool.PoolJson;
import com.ldtteam.datagenerators.loot_table.pool.conditions.IPoolCondition;
import com.ldtteam.datagenerators.loot_table.pool.conditions.survives_explosion.SurvivesExplosionConditionJson;
import com.ldtteam.datagenerators.loot_table.pool.entry.EntryJson;
import com.ldtteam.datagenerators.loot_table.pool.entry.EntryTypeEnum;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockLootTableGenerator implements IDataProvider
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator generator;
    private final String modId;
    private final Map<Block, LootTableJson> lootTableJsons;

    public BlockLootTableGenerator(DataGenerator generator, String modId)
    {
        this.generator = generator;
        this.modId = modId;
        this.lootTableJsons = new HashMap<>();
    }

    @Override
    public void act(DirectoryCache cache)
    {
        this.lootTableJsons.entrySet().forEach(entry ->
        {
            Path lootTablePath = this.generator.getOutputFolder().resolve("data/" + this.modId + "/loot_tables/blocks/").resolve(entry.getKey().getRegistryName().getPath() + ".json");

            try
            {
                IDataProvider.save(GSON, cache, entry.getValue().serialize(), lootTablePath);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    public BlockLootTableGenerator createLootTable(Block block)
    {
        return this.createLootTable(block, Collections.singletonList(new SurvivesExplosionConditionJson()));
    }

    public BlockLootTableGenerator createLootTable(Block block, List<IPoolCondition> poolConditions)
    {
        if(block.getRegistryName() == null)
        {
            throw new NullPointerException("Tried to create a loot table for an unregistered block");
        }

        EntryJson lootEntryJson = new EntryJson();
        lootEntryJson.setType(EntryTypeEnum.ITEM);
        lootEntryJson.setName(block.getRegistryName().toString());

        PoolJson lootPoolJson = new PoolJson();
        lootPoolJson.setEntries(Collections.singletonList(lootEntryJson));
        lootPoolJson.setRolls(1);
        lootPoolJson.setConditions(poolConditions);

        LootTableJson lootTableJson = new LootTableJson();
        lootTableJson.setType(LootTableTypeEnum.BLOCK);
        lootTableJson.setPools(Collections.singletonList(lootPoolJson));
        return this;
    }

    public BlockLootTableGenerator createLootTable(Block block, Item item)
    {
        return this.createLootTable(block, item, Collections.singletonList(new SurvivesExplosionConditionJson()));
    }

    public BlockLootTableGenerator createLootTable(Block block, Item item, List<IPoolCondition> poolConditions)
    {
        if(block.getRegistryName() == null)
        {
            throw new NullPointerException("Tried to create a loot table for an unregistered block");
        }

        if(item.getRegistryName() == null)
        {
            throw new NullPointerException("Tried to create a loot table for a block with an unregistered item");
        }

        EntryJson lootEntryJson = new EntryJson();
        lootEntryJson.setType(EntryTypeEnum.ITEM);
        lootEntryJson.setName(item.getRegistryName().toString());

        PoolJson lootPoolJson = new PoolJson();
        lootPoolJson.setEntries(Collections.singletonList(lootEntryJson));
        lootPoolJson.setRolls(1);
        lootPoolJson.setConditions(poolConditions);

        LootTableJson lootTableJson = new LootTableJson();
        lootTableJson.setType(LootTableTypeEnum.BLOCK);
        lootTableJson.setPools(Collections.singletonList(lootPoolJson));
        return this;
    }

    public void createLootTable(Block block, LootTableJson lootTableJson)
    {
        this.lootTableJsons.put(block, lootTableJson);
    }

    @Override
    public String getName()
    {
        return this.modId + " Loot Table Generator";
    }
}
