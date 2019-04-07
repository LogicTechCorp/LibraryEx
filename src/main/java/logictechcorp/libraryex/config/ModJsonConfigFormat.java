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

package logictechcorp.libraryex.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.core.utils.WriterSupplier;
import com.electronwill.nightconfig.json.FancyJsonWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;

public class ModJsonConfigFormat implements ConfigFormat<Config>
{
    private static final ModJsonConfigFormat INSTANCE = new ModJsonConfigFormat();

    private ModJsonConfigFormat()
    {
    }

    public static ConfigFormat<Config> instance()
    {
        return INSTANCE;
    }

    @Override
    public ConfigWriter createWriter()
    {
        return new FancyJsonWriter();
    }

    @Override
    public ConfigParser<Config> createParser()
    {
        return new ModJsonConfigParser();
    }

    @Override
    public Config createConfig()
    {
        return Config.wrap(new LinkedHashMap<>(), this);
    }

    @Override
    public Config createConcurrentConfig()
    {
        return Config.ofConcurrent(this);
    }

    @Override
    public boolean supportsComments()
    {
        return false;
    }

    @Override
    public void initEmptyFile(WriterSupplier supplier) throws IOException
    {
        try(Writer writer = supplier.get())
        {
            writer.write("{}");
        }
    }

    public static Config newConfig()
    {
        return INSTANCE.createConfig();
    }

    public static Config newConcurrentConfig()
    {
        return INSTANCE.createConcurrentConfig();
    }

}
