/*
 * LibraryEx
 * Copyright (c) 2017-2020 by LogicTechCorp
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
 *
 */

package logictechcorp.libraryex.resource;

import com.google.common.base.Joiner;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.ModFileResourcePack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BuiltinDataPack extends ModFileResourcePack implements IPackFinder
{
    private ModFile modFile;
    private String modId;
    private String packName;

    public BuiltinDataPack(ModFile modFile, String packName)
    {
        super(modFile);
        this.modFile = modFile;
        this.modId = modFile.getModInfos().get(0).getModId();
        this.packName = packName;
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
    public boolean resourceExists(String name)
    {
        return Files.exists(this.modFile.getLocator().findPath(this.modFile, "datapacks" + File.separator + this.packName + File.separator + name));
    }

    @Override
    public InputStream getInputStream(String name) throws IOException
    {
        Path path = this.modFile.getLocator().findPath(this.modFile, "datapacks" + File.separator + this.packName + File.separator + name);
        return Files.newInputStream(path, StandardOpenOption.READ);
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter)
    {
        try
        {
            Path root = this.modFile.getLocator().findPath(this.modFile, "datapacks").toAbsolutePath();
            Path inputPath = root.getFileSystem().getPath(pathIn);

            return Files.walk(root)
                    .map(path -> root.relativize(path.toAbsolutePath()))
                    .filter(path -> path.getNameCount() > 3 && path.getNameCount() - 1 <= maxDepth)
                    .filter(path -> !path.toString().endsWith(".mcmeta"))
                    .filter(path -> path.subpath(3, path.getNameCount()).startsWith(inputPath))
                    .filter(path -> filter.test(path.getFileName().toString()))
                    .map(path -> new ResourceLocation(path.getName(2).toString(), Joiner.on('/').join(path.subpath(3, Math.min(maxDepth, path.getNameCount())))))
                    .collect(Collectors.toList());
        }
        catch(IOException e)
        {
            return Collections.emptyList();
        }
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type)
    {
        try
        {
            Path root = this.modFile.getLocator().findPath(this.modFile, "datapacks").toAbsolutePath();
            return Files.walk(root, 3)
                    .map(path -> root.relativize(path.toAbsolutePath()))
                    .filter(path -> path.getNameCount() == 3)
                    .map(path -> path.getName(2))
                    .map(path -> path.toString().replaceAll("/$", ""))
                    .filter(path -> !path.isEmpty())
                    .collect(Collectors.toSet());
        }
        catch(IOException e)
        {
            return Collections.emptySet();
        }
    }

    @Override
    public String getName()
    {
        return this.modId + ":" + this.packName;
    }
}
