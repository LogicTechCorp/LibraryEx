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

import lex.LibEx;
import lex.config.Config;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class FeatureRegistry
{
    private static final Map<ResourceLocation, Class<? extends Feature>> FEATURES = new HashMap<>();

    public static void registerFeature(ResourceLocation name, Class<? extends Feature> cls)
    {
        if(!FEATURES.containsKey(name))
        {
            FEATURES.put(name, cls);
        }
        else
        {
            LibEx.LOGGER.warn("A feature with the name, {}, is already registered!", name.toString());
        }
    }

    public static Feature createFeature(ResourceLocation name, Config config)
    {
        if(FEATURES.containsKey(name))
        {
            try
            {
                return FEATURES.get(name).getConstructor(Config.class).newInstance(config);
            }
            catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    static
    {
        registerFeature(new ResourceLocation(LibEx.MOD_ID + ":scatter"), FeatureScatter.class);
        registerFeature(new ResourceLocation(LibEx.MOD_ID + ":cluster"), FeatureCluster.class);
        registerFeature(new ResourceLocation(LibEx.MOD_ID + ":fluid"), FeatureFluid.class);
        registerFeature(new ResourceLocation(LibEx.MOD_ID + ":ore"), FeatureOre.class);
        registerFeature(new ResourceLocation(LibEx.MOD_ID + ":pool"), FeaturePool.class);
        registerFeature(new ResourceLocation(LibEx.MOD_ID + ":big_mushroom"), FeatureBigMushroom.class);
        registerFeature(new ResourceLocation(LibEx.MOD_ID + ":oak_tree"), FeatureOakTree.class);
        registerFeature(new ResourceLocation(LibEx.MOD_ID + ":structure"), FeatureStructure.class);
    }
}
