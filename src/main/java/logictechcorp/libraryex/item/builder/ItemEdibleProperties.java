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

import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;

public class ItemEdibleProperties extends Item.Properties
{
    private int healAmount;
    private float saturation;
    private boolean isWolfFood;
    private boolean alwaysEdible;
    private EffectInstance potionEffect;
    private float potionEffectProbability;

    public ItemEdibleProperties(int healAmount, float saturation, boolean isWolfFood)
    {
        this.healAmount = healAmount;
        this.saturation = saturation;
        this.isWolfFood = isWolfFood;
    }

    public ItemEdibleProperties potionEffect(EffectInstance potionEffect, float probability)
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

    public EffectInstance getPotionEffect()
    {
        return this.potionEffect;
    }

    public float getPotionEffectProbability()
    {
        return this.potionEffectProbability;
    }
}
