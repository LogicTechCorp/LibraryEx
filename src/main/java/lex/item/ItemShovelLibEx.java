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

package lex.item;

import com.google.common.base.CaseFormat;
import lex.IModData;
import net.minecraft.item.ItemSpade;

public class ItemShovelLibEx extends ItemSpade
{
    protected ItemShovelLibEx(IModData data, String name, ToolMaterial material)
    {
        super(material);
        setRegistryName(data.getModId() + ":" + name);
        setUnlocalizedName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, getRegistryName().toString()));
        setCreativeTab(data.getCreativeTab());
    }
}
