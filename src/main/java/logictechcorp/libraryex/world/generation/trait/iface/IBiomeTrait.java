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

package logictechcorp.libraryex.world.generation.trait.iface;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface IBiomeTrait
{
    /**
     * Called to generate the biome trait.
     *
     * @param world  The world that the biome trait will generate in.
     * @param pos    The pos that the biome trait will generate at.
     * @param random The random number generator.
     * @return Whether the biome trait was generated.
     */
    boolean generate(World world, BlockPos pos, Random random);

    /**
     * Called to check if the generation attempts for this biome trait should be randomized.
     * It randomizes the value from 1 to the value returned in {@link #getGenerationAttempts}
     *
     * @return If the generation attempts for this biome trait should be randomized.
     */
    boolean useRandomizedGenerationAttempts();

    /**
     * Called to get the amount of times the biome trait should generate.
     *
     * @param world  The world that the biome trait will generate in.
     * @param pos    The pos that the biome trait will generate at.
     * @param random The random number generator.
     * @return The amount of times the biome trait should generate.
     */
    int getGenerationAttempts(World world, BlockPos pos, Random random);

    /**
     * Called to get the probability that the biome trait will generate.
     *
     * @param world  The world that the biome trait will generate in.
     * @param pos    The pos that the biome trait will generate at.
     * @param random The random number generator.
     * @return The probability that the biome trait will generate.
     */
    double getGenerationProbability(World world, BlockPos pos, Random random);

    /**
     * Called to get the minimum generation height for the biome trait.
     *
     * @param world  The world that the biome trait will generate in.
     * @param pos    The pos that the biome trait will generate at.
     * @param random The random number generator.
     * @return The minimum generation height for the biome trait.
     */
    int getMinimumGenerationHeight(World world, BlockPos pos, Random random);

    /**
     * Called to get the maximum generation height for the biome trait.
     *
     * @param world  The world that the biome trait will generate in.
     * @param pos    The pos that the biome trait will generate at.
     * @param random The random number generator.
     * @return The maximum generation height for the biome trait.
     */
    int getMaximumGenerationHeight(World world, BlockPos pos, Random random);
}
