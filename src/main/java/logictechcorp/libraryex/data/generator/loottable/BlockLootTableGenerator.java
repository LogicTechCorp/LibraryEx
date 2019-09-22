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
import com.ldtteam.datagenerators.loot_table.pool.conditions.survives_explosion.SurvivesExplosionConditionJson;
import com.ldtteam.datagenerators.loot_table.pool.entry.EntryJson;
import com.ldtteam.datagenerators.loot_table.pool.entry.EntryTypeEnum;
import logictechcorp.libraryex.data.generator.loottable.pool.condition.MatchToolConditionJson;
import logictechcorp.libraryex.data.generator.loottable.pool.entry.ItemEntryChildJson;
import logictechcorp.libraryex.data.generator.loottable.pool.function.ApplyBonusFunctionJson;
import logictechcorp.libraryex.data.generator.loottable.pool.function.ExplosionDecayFunctionJson;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

    public BlockLootTableGenerator addBasicLootTable(Block block)
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
        lootPoolJson.setConditions(Collections.singletonList(new SurvivesExplosionConditionJson()));

        LootTableJson lootTableJson = new LootTableJson();
        lootTableJson.setType(LootTableTypeEnum.BLOCK);
        lootTableJson.setPools(Collections.singletonList(lootPoolJson));
        this.lootTableJsons.put(block, lootTableJson);
        return this;
    }

    public BlockLootTableGenerator addBasicLootTable(Block block, Item item)
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
        lootPoolJson.setConditions(Collections.singletonList(new SurvivesExplosionConditionJson()));

        LootTableJson lootTableJson = new LootTableJson();
        lootTableJson.setType(LootTableTypeEnum.BLOCK);
        lootTableJson.setPools(Collections.singletonList(lootPoolJson));
        this.lootTableJsons.put(block, lootTableJson);
        return this;
    }

    public BlockLootTableGenerator addOreLootTable(Block block, Item item)
    {
        if(block.getRegistryName() == null)
        {
            throw new NullPointerException("Tried to create a loot table for an unregistered block");
        }

        if(item.getRegistryName() == null)
        {
            throw new NullPointerException("Tried to create a loot table for a block with an unregistered item");
        }

        MatchToolConditionJson matchToolConditionJson = new MatchToolConditionJson();
        matchToolConditionJson.setPredicate(new ItemPredicate(null, null, MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, new EnchantmentPredicate[]{new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))}, null, NBTPredicate.ANY));

        ItemEntryChildJson blockItemEntryChildJson = new ItemEntryChildJson();
        blockItemEntryChildJson.setConditions(Collections.singletonList(matchToolConditionJson));
        blockItemEntryChildJson.setName(block.getRegistryName().toString());

        ApplyBonusFunctionJson applyBonusFunctionJson = new ApplyBonusFunctionJson();
        applyBonusFunctionJson.setEnchantment("minecraft:fortune");
        applyBonusFunctionJson.setFormula("minecraft:ore_drops");

        ItemEntryChildJson itemEntryChildJson = new ItemEntryChildJson();
        itemEntryChildJson.setFunctions(Arrays.asList(applyBonusFunctionJson, new ExplosionDecayFunctionJson()));
        itemEntryChildJson.setName(item.getRegistryName().toString());

        EntryJson lootEntryJson = new EntryJson();
        lootEntryJson.setType(EntryTypeEnum.LOOT_TABLE);
        lootEntryJson.setName(item.getRegistryName().toString());
        lootEntryJson.setChildren(Arrays.asList(blockItemEntryChildJson, itemEntryChildJson));

        PoolJson lootPoolJson = new PoolJson();
        lootPoolJson.setEntries(Collections.singletonList(lootEntryJson));
        lootPoolJson.setRolls(1);

        LootTableJson lootTableJson = new LootTableJson();
        lootTableJson.setType(LootTableTypeEnum.BLOCK);
        lootTableJson.setPools(Collections.singletonList(lootPoolJson));
        this.lootTableJsons.put(block, lootTableJson);
        return this;
    }

    @Override
    public String getName()
    {
        return this.modId + " Loot Table Generator";
    }
}
