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

package logictechcorp.libraryex.item;

import logictechcorp.libraryex.item.builder.ItemEdibleProperties;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class EdibleBlockItem extends BlockItem
{
    private final int healAmount;
    private final float saturation;
    private final boolean isWolfFood;
    private boolean alwaysEdible;
    private EffectInstance potionEffect;
    private float potionEffectProbability;

    public EdibleBlockItem(Block block, ItemEdibleProperties properties)
    {
        super(block, properties);
        this.healAmount = properties.getHealAmount();
        this.saturation = properties.getSaturation();
        this.isWolfFood = properties.isWolfFood();
        this.alwaysEdible = properties.isAlwaysEdible();
        this.potionEffect = properties.getPotionEffect();
        this.potionEffectProbability = properties.getPotionEffectProbability();
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entityLiving)
    {
        if(entityLiving instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) entityLiving;
            player.getFoodStats().addStats(this.healAmount, this.saturation);
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
            this.onFoodEaten(stack, world, player);
            player.addStat(Stats.ITEM_USED.get(this));
        }

        stack.shrink(1);
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.EAT;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getHeldItem(hand);

        if(player.canEat(this.alwaysEdible))
        {
            player.setActiveHand(hand);
            return new ActionResult(ActionResultType.SUCCESS, stack);
        }
        else
        {
            return new ActionResult(ActionResultType.FAIL, stack);
        }
    }

    protected void onFoodEaten(ItemStack stack, World world, PlayerEntity player)
    {
        if(!world.isRemote && this.potionEffect != null && world.rand.nextFloat() < this.potionEffectProbability)
        {
            player.addPotionEffect(new EffectInstance(this.potionEffect));
        }
    }

    public int getHealAmount(ItemStack stack)
    {
        return this.healAmount;
    }

    public float getSaturation(ItemStack stack)
    {
        return this.saturation;
    }

    public boolean isWolfsFood()
    {
        return this.isWolfFood;
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
