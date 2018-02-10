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

package lex.world.gen.feature;

import lex.api.config.IConfig;
import lex.config.Config;
import lex.util.StructureHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
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
    private Mirror mirror;
    private Rotation rotation;
    private Block ignoredBlock;
    private Type type;
    private float clearancePercentage;

    public FeatureStructure(IConfig config)
    {
        super(config);
        structure = config.getResource("structure");
        mirror = config.getEnum("mirror", Mirror.class, Mirror.NONE);
        rotation = config.getEnum("rotation", Rotation.class, Rotation.NONE);
        ignoredBlock = config.getBlock("ignoredBlock", Blocks.STRUCTURE_VOID.getDefaultState()).getBlock();
        type = config.getEnum("type", Type.class);
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
            handleDataBlocks(world, pos, template, placementSettings);
            return true;
        }

        return false;
    }

    private void handleDataBlocks(World world, BlockPos pos, Template template, PlacementSettings placementSettings)
    {
        Map<BlockPos, String> map = template.getDataBlocks(pos, placementSettings);

        for(Map.Entry<BlockPos, String> entry : map.entrySet())
        {
            BlockPos dataPos = entry.getKey();
            IConfig config = new Config(entry.getValue());
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
