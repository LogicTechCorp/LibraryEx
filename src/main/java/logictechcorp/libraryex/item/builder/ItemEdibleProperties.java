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

package logictechcorp.libraryex.item.builder;

import net.minecraft.potion.PotionEffect;

public class ItemEdibleProperties extends ItemProperties
{
    private int healAmount;
    private float saturation;
    private boolean isWolfFood;
    private boolean alwaysEdible;
    private PotionEffect potionEffect;
    private float potionEffectProbability;

    public ItemEdibleProperties(int healAmount, float saturation, boolean isWolfFood)
    {
        this.healAmount = healAmount;
        this.saturation = saturation;
        this.isWolfFood = isWolfFood;
    }

    public ItemEdibleProperties potionEffect(PotionEffect potionEffect, float probability)
    {
        this.potionEffect = potionEffect;
        this.potionEffectProbability = probability;
        return this;
    }

    public ItemEdibleProperties alwaysEdible()
    {
        this.alwaysEdible = true;
        return this;
    }

    @Override
    public ItemEdibleProperties copy()
    {
        ItemEdibleProperties properties = (ItemEdibleProperties) super.copy();
        properties.healAmount = this.healAmount;
        properties.saturation = this.saturation;
        properties.isWolfFood = this.isWolfFood;
        properties.alwaysEdible = this.alwaysEdible;
        properties.potionEffect = this.potionEffect;
        properties.potionEffectProbability = this.potionEffectProbability;
        return properties;
    }

    public int getHealAmount()
    {
        return this.healAmount;
    }

    public float getSaturation()
    {
        return this.saturation;
    }

    public boolean isWolfFood()
    {
        return this.isWolfFood;
    }

    public boolean isAlwaysEdible()
    {
        return this.alwaysEdible;
    }

    public PotionEffect getPotionEffect()
    {
        return this.potionEffect;
    }

    public float getPotionEffectProbability()
    {
        return this.potionEffectProbability;
    }
}
