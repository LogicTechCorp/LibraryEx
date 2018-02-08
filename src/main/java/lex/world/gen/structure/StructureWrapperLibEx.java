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

package lex.world.gen.structure;

import com.google.gson.JsonElement;
import lex.api.config.IConfig;
import lex.api.world.gen.structure.StructureWrapper;
import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static lex.util.ConfigHelper.isString;

public class StructureWrapperLibEx extends StructureWrapper
{
    private IConfig config;

    public StructureWrapperLibEx(IConfig configIn)
    {
        super(configIn.getResource("structure"));
        config = configIn;
        parse();
    }

    private void parse()
    {
        mirror = config.getEnum("mirror", Mirror.class, Mirror.NONE);
        rotation = config.getEnum("rotation", Rotation.class, Rotation.NONE);
        ignoredBlock = config.getBlock("ignoredBlock", Blocks.STRUCTURE_VOID.getDefaultState());

        List<IConfig> lootTableConfigs = config.getInnerConfigs("lootTables", new ArrayList<>());

        for(IConfig lootTableConfig : lootTableConfigs)
        {
            for(Map.Entry<String, JsonElement> entry : lootTableConfig.getElements().entrySet())
            {
                if(isString(entry.getValue()))
                {
                    lootTables.add(lootTableConfig.getResource(entry.getKey()));
                }
            }
        }

        List<IConfig> spawnerMobConfigs = config.getInnerConfigs("spawnerMobs", new ArrayList<>());

        for(IConfig spawnerMobConfig : spawnerMobConfigs)
        {
            for(Map.Entry<String, JsonElement> entry : spawnerMobConfig.getElements().entrySet())
            {
                if(isString(entry.getValue()))
                {
                    spawnerMobs.add(spawnerMobConfig.getResource(entry.getKey()));
                }
            }
        }

    }
}
