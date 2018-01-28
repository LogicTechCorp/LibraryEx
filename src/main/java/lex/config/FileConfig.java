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

package lex.config;

import com.google.gson.JsonObject;
import lex.LibEx;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileConfig extends Config
{
    public FileConfig(File configFile)
    {
        String jsonString = new JsonObject().toString();

        if(configFile.getPath().startsWith("~"))
        {
            configFile = new File(configFile.getPath().replace("~", LibEx.CONFIG_DIRECTORY.getPath()));
        }

        if(configFile.exists())
        {
            try
            {
                jsonString = FileUtils.readFileToString(configFile, Charset.defaultCharset());
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        parse(jsonString);
    }

    @Override
    public boolean isSavable()
    {
        return true;
    }
}
