package logictechcorp.libraryex.data.loottable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraft.world.storage.loot.conditions.MatchTool;
import net.minecraft.world.storage.loot.conditions.SurvivesExplosion;
import net.minecraft.world.storage.loot.functions.ApplyBonus;
import net.minecraft.world.storage.loot.functions.ExplosionDecay;
import net.minecraft.world.storage.loot.functions.LootingEnchantBonus;
import net.minecraft.world.storage.loot.functions.SetCount;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ModLootTableProvider implements IDataProvider
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String modId;
    private final DataGenerator generator;
    private final Map<ResourceLocation, LootTable> lootTables;

    public ModLootTableProvider(String modId, DataGenerator generator)
    {
        this.modId = modId;
        this.generator = generator;
        this.lootTables = new HashMap<>();
    }

    @Override
    public void act(DirectoryCache cache)
    {
        this.lootTables.forEach((location, lootTable) ->
        {
            Path lootTablePath = this.generator.getOutputFolder().resolve("data/" + location.getNamespace() + "/loot_tables/" + location.getPath() + ".json");

            try
            {
                IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), lootTablePath);
            }
            catch(IOException ignored)
            {
            }
        });
    }

    public void addEmptyBlockLootTable(Block block)
    {
        this.lootTables.put(block.getLootTable(), LootTable.builder().setParameterSet(LootParameterSets.BLOCK).build());
    }

    public void addBasicBlockLootTable(Block block, IItemProvider itemProvider)
    {
        LootPool.Builder lootPool = LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(itemProvider))
                .acceptCondition(SurvivesExplosion.builder());

        LootTable.Builder lootTable = LootTable.builder()
                .setParameterSet(LootParameterSets.BLOCK)
                .addLootPool(lootPool);

        this.lootTables.put(block.getLootTable(), lootTable.build());
    }

    public void addBasicBlockLootTable(Block block)
    {
        this.addBasicBlockLootTable(block, block);
    }

    public void addOreBlockLootTable(Block block, IItemProvider itemProvider)
    {
        LootEntry.Builder lootEntry = ItemLootEntry.builder(block)
                .acceptCondition(MatchTool.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1)))))
                .alternatively(ItemLootEntry.builder(itemProvider).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE)).acceptFunction(ExplosionDecay.builder()));

        LootPool.Builder lootPool = LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(lootEntry);

        LootTable.Builder lootTable = LootTable.builder()
                .setParameterSet(LootParameterSets.BLOCK)
                .addLootPool(lootPool);

        this.lootTables.put(block.getLootTable(), lootTable.build());
    }

    public void addOreBlockLootTable(Block block)
    {
        this.addOreBlockLootTable(block, block);
    }

    public void addSilkBlockLootTable(Block block)
    {
        LootEntry.Builder lootEntry = ItemLootEntry.builder(block)
                .acceptCondition(MatchTool.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1)))));

        LootPool.Builder lootPool = LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(lootEntry);

        LootTable.Builder lootTable = LootTable.builder()
                .setParameterSet(LootParameterSets.BLOCK)
                .addLootPool(lootPool);

        this.lootTables.put(block.getLootTable(), lootTable.build());
    }

    public void addSlabBlockLootTable(Block block)
    {
        LootEntry.Builder lootEntry = ItemLootEntry.builder(block)
                .acceptFunction(SetCount.builder(ConstantRange.of(2)).acceptCondition(BlockStateProperty.builder(block).with(SlabBlock.TYPE, SlabType.DOUBLE)))
                .acceptCondition(SurvivesExplosion.builder());

        LootPool.Builder lootPool = LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(lootEntry);

        LootTable.Builder lootTable = LootTable.builder()
                .setParameterSet(LootParameterSets.BLOCK)
                .addLootPool(lootPool);

        this.lootTables.put(block.getLootTable(), lootTable.build());
    }

    public void addEmptyEntityLootTable(ResourceLocation lootTableLocation)
    {
        this.lootTables.put(lootTableLocation, LootTable.builder().setParameterSet(LootParameterSets.ENTITY).build());
    }

    public void addEmptyEntityLootTable(EntityType<?> entityType)
    {
        this.addEmptyEntityLootTable(entityType.getLootTable());
    }

    public void addBasicEntityLootTable(ResourceLocation lootTableLocation, IItemProvider... itemProviders)
    {
        LootTable.Builder lootTable = LootTable.builder()
                .setParameterSet(LootParameterSets.ENTITY);

        for(IItemProvider itemProvider : itemProviders)
        {
            LootEntry.Builder lootEntry = ItemLootEntry.builder(itemProvider)
                    .acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F)))
                    .acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)));

            LootPool.Builder lootPool = LootPool.builder()
                    .rolls(ConstantRange.of(1))
                    .addEntry(lootEntry);

            lootTable.addLootPool(lootPool);
        }

        this.lootTables.put(lootTableLocation, lootTable.build());
    }

    public void addBasicEntityLootTable(EntityType<?> entityType, IItemProvider... itemProviders)
    {
        this.addBasicEntityLootTable(entityType.getLootTable(), itemProviders);
    }

    @Override
    public String getName()
    {
        return this.modId + " loot tables";
    }
}
