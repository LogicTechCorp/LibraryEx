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

package lex.potion;

import com.google.common.base.CaseFormat;
import lex.IModData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

public class PotionLibEx extends Potion
{
    private final ResourceLocation iconTexture;

    public PotionLibEx(IModData data, String name, boolean isBadEffect, int red, int green, int blue)
    {
        super(isBadEffect, new Color(red, green, blue).getRGB());
        this.setRegistryName(data.getModId() + ":" + name);
        this.setPotionName("potion.effect." + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.getRegistryName().toString()));
        this.iconTexture = new ResourceLocation(data.getModId() + ":textures/potions/" + name + ".png");
    }

    @Override
    public boolean hasStatusIcon()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc)
    {
        if(mc.currentScreen != null)
        {
            mc.getTextureManager().bindTexture(this.iconTexture);
            Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha)
    {
        mc.getTextureManager().bindTexture(this.iconTexture);
        Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
    }
}
