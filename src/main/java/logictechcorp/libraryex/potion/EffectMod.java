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

package logictechcorp.libraryex.potion;

import logictechcorp.libraryex.api.IModData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

public class EffectMod extends Effect
{
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private final ResourceLocation iconTexture;

    public EffectMod(IModData data, String name, EffectType type, int red, int green, int blue)
    {
        super(type, new Color(red, green, blue).getRGB());
        this.setRegistryName(data.getModId() + ":" + name);
        this.iconTexture = new ResourceLocation(data.getModId() + ":textures/potions/" + name + ".png");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, int x, int y, float z)
    {
        if(gui != null)
        {
            MINECRAFT.getTextureManager().bindTexture(this.iconTexture);
            AbstractGui.blit(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHUDEffect(EffectInstance effect, AbstractGui gui, int x, int y, float z, float alpha)
    {
        if(gui != null)
        {
            MINECRAFT.getTextureManager().bindTexture(this.iconTexture);
            AbstractGui.blit(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
        }
    }
}
