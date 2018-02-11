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

package lex.world.gen.feature;

import lex.api.config.IConfig;
import lex.util.StructureHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Map;
import java.util.Random;

public class FeatureStructure extends Feature
{
    private ResourceLocation structure;
    private Type type;
    private Mirror mirror;
    private Rotation rotation;
    private Block ignoredBlock;
    private float clearancePercentage;

    public FeatureStructure(IConfig config)
    {
        super(config);
        structure = config.getResource("structure");
        type = config.getEnum("type", Type.class, Type.GROUNDED);
        mirror = config.getEnum("mirror", Mirror.class, Mirror.NONE);
        rotation = config.getEnum("rotation", Rotation.class, Rotation.NONE);
        ignoredBlock = config.getBlock("ignoredBlock", Blocks.STRUCTURE_VOID.getDefaultState()).getBlock();
        clearancePercentage = config.getFloat("clearancePercentage", 0.875F);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos)
    {
        MinecraftServer server = world.getMinecraftServer();
        TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
        Template template = manager.getTemplate(server, structure);
        PlacementSettings placementSettings = new PlacementSettings().setMirror(mirror).setRotation(rotation).setReplacedBlock(ignoredBlock).setRandom(rand);
        BlockPos structureSize = template.transformedSize(rotation);
        BlockPos spawnPos = null;

        if(type == Type.GROUNDED)
        {
            spawnPos = StructureHelper.getGroundedPos(world, pos, structureSize, clearancePercentage);
        }
        else if(type == Type.FLOATING)
        {
            spawnPos = StructureHelper.getFloatingPos(world, pos, structureSize, clearancePercentage);
        }
        else if(type == Type.HANGING)
        {
            spawnPos = StructureHelper.getHangingPos(world, pos, structureSize, clearancePercentage);
        }
        else if(type == Type.BURIED)
        {
            spawnPos = StructureHelper.getBuriedPos(world, pos, structureSize, clearancePercentage);
        }

        if(spawnPos != null && spawnPos.getY() >= minHeight && spawnPos.getY() <= maxHeight)
        {
            template.addBlocksToWorld(world, spawnPos, placementSettings);
            handleDataBlocks(world, pos, template, placementSettings, rand);
            return true;
        }

        return false;
    }

    private void handleDataBlocks(World world, BlockPos pos, Template template, PlacementSettings placementSettings, Random rand)
    {
        Map<BlockPos, String> map = template.getDataBlocks(pos, placementSettings);

        for(Map.Entry<BlockPos, String> entry : map.entrySet())
        {
            BlockPos dataPos = entry.getKey();
            String[] data = entry.getValue().split("\\s+");

            if(data[0].equals("chest") && data.length == 2)
            {
                world.setBlockState(dataPos, Blocks.CHEST.correctFacing(world, dataPos, Blocks.CHEST.getDefaultState()));
                TileEntityChest chest = (TileEntityChest) world.getTileEntity(dataPos);

                if(chest != null)
                {
                    chest.setLootTable(new ResourceLocation(data[1]), rand.nextLong());
                }
            }
            else if(data[0].equals("spawner") && data.length == 2)
            {
                world.setBlockState(dataPos, Blocks.MOB_SPAWNER.getDefaultState());
                TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(dataPos);

                if(spawner != null)
                {
                    MobSpawnerBaseLogic logic = spawner.getSpawnerBaseLogic();
                    NBTTagCompound compound = new NBTTagCompound();
                    logic.writeToNBT(compound);
                    compound.removeTag("SpawnPotentials");
                    logic.readFromNBT(compound);
                    logic.setEntityId(new ResourceLocation(data[1]));
                    spawner.markDirty();
                    IBlockState state = world.getBlockState(dataPos);
                    world.notifyBlockUpdate(pos, state, state, 3);
                }
            }
            else if(data[0].equals("entity") && data.length == 2)
            {
                Entity entity = EntityList.newEntity(EntityList.getClassFromName(data[1]), world);

                if(entity != null)
                {
                    entity.setPosition(dataPos.getX() + 0.5F, dataPos.getY(), dataPos.getZ() + 0.5F);
                    world.spawnEntity(entity);
                }
            }
            else
            {
                world.setBlockToAir(dataPos);
            }
        }
    }

    public enum Type
    {
        GROUNDED,
        FLOATING,
        HANGING,
        BURIED
    }
}
