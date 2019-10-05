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

package logictechcorp.libraryex.resource;

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.ModFileResourcePack;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

public class OptionalResourcePack extends ModFileResourcePack implements IPackFinder
{
    private final String modId;
    private final String packName;
    private final boolean isSubPack;

    public OptionalResourcePack(ModFile modFile, String packName)
    {
        this(modFile, packName, false);
    }

    public OptionalResourcePack(ModFile modFile, String packName, boolean isSubPack)
    {
        super(modFile);
        this.modId = modFile.getModInfos().get(0).getModId();
        this.packName = packName;
        this.isSubPack = isSubPack;
    }

    @Override
    public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packs, ResourcePackInfo.IFactory<T> packInfoFactory)
    {
        String fileName = this.modId + ":" + this.packName;
        T t = ResourcePackInfo.createResourcePack(fileName, false, () -> this, packInfoFactory, ResourcePackInfo.Priority.BOTTOM);

        if(t != null)
        {
            packs.put(fileName, t);
        }
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String path, int maxDepth, Predicate<String> filter)
    {
        if(this.isSubPack)
        {
            return super.getAllResourceLocations(type, this.packName + "/" + path, maxDepth, filter);
        }

        return super.getAllResourceLocations(type, path, maxDepth, filter);
    }
}
