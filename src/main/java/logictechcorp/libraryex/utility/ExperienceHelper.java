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

package logictechcorp.libraryex.utility;

import net.minecraft.entity.player.EntityPlayer;

/**
 * A class of methods that help with manipulating experience
 * <p>
 * Written by Open Mods team here:
 * https://github.com/OpenMods/OpenModsLib/blob/9d7ebad18c9f2c45372411f8b07f17f29dedebe1/src/main/java/openmods/utils/EnchantmentUtils.java
 *
 * @author Open Mods team
 */
public class ExperienceHelper
{
    public static void adjustPlayerExperience(EntityPlayer player, int amount)
    {
        int experience = getPlayerExperience(player) + amount;
        player.experienceTotal = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int experienceLevel = getExperienceForLevel(player.experienceLevel);
        player.experience = (float) (experience - experienceLevel) / (float) player.xpBarCap();
    }

    public static int getPlayerExperience(EntityPlayer player)
    {
        return (int) (getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
    }

    public static int getLevelForExperience(int experience)
    {
        int level = 0;

        while(true)
        {
            int levelCap = getLevelExperienceCap(level);

            if(experience < levelCap)
            {
                return level;
            }

            level++;
            experience -= levelCap;
        }
    }

    public static int getExperienceForLevel(int level)
    {
        if(level == 0)
        {
            return 0;
        }
        if(level <= 15)
        {
            return calculateExperience(level, 7, 2);
        }
        if(level <= 30)
        {
            return 315 + calculateExperience(level - 15, 37, 5);
        }

        return 1395 + calculateExperience(level - 30, 112, 9);
    }

    public static int getLevelExperienceCap(int level)
    {
        if(level >= 30)
        {
            return 112 + (level - 30) * 9;
        }
        else if(level >= 15)
        {
            return 37 + (level - 15) * 5;
        }
        else if(level == 0)
        {
            return 0;
        }

        return 7 + level * 2;
    }

    private static int calculateExperience(int level, int minimum, int multiplier)
    {
        return level * (2 * minimum + (level - 1) * multiplier) / 2;
    }
}
