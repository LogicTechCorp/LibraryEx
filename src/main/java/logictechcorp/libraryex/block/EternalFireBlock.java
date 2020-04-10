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

package logictechcorp.libraryex.block;

import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.Random;

public class EternalFireBlock extends Block
{
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_15;
    public static final BooleanProperty NORTH = SixWayBlock.NORTH;
    public static final BooleanProperty EAST = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST = SixWayBlock.WEST;
    public static final BooleanProperty UP = SixWayBlock.UP;

    private static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((direction) -> direction.getKey() != Direction.DOWN).collect(Util.toMapCollector());

    public EternalFireBlock(Block.Properties builder)
    {
        super(builder);
        this.setDefaultState(this.stateContainer.getBaseState().with(AGE, 0).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
    {
        return this.isValidPosition(state, world, currentPos) ? this.getStateForPlacement(world, currentPos).with(AGE, state.get(AGE)) : Blocks.AIR.getDefaultState();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getStateForPlacement(context.getWorld(), context.getPos());
    }

    public BlockState getStateForPlacement(IBlockReader world, BlockPos pos)
    {
        BlockPos downPos = pos.down();
        BlockState downState = world.getBlockState(downPos);

        if(!this.canCatchFire(world, pos, Direction.UP) && !Block.hasSolidSide(downState, world, downPos, Direction.UP))
        {
            BlockState state = this.getDefaultState();

            for(Direction direction : Direction.values())
            {
                BooleanProperty property = FACING_TO_PROPERTY_MAP.get(direction);

                if(property != null)
                {
                    state = state.with(property, this.canCatchFire(world, pos.offset(direction), direction.getOpposite()));
                }
            }

            return state;
        }
        else
        {
            return this.getDefaultState();
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        BlockPos downPos = pos.down();
        return world.getBlockState(downPos).isSolidSide(world, downPos, Direction.UP) || this.areNeighborsFlammable(world, pos);
    }

    @Override
    public int tickRate(IWorldReader world)
    {
        return 30;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if(world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK))
        {
            if(!world.isAreaLoaded(pos, 2))
            {
                return;
            }
            if(!state.isValidPosition(world, pos))
            {
                world.removeBlock(pos, false);
            }

            BlockPos downPos = pos.down();
            boolean isFireSource = world.getBlockState(downPos).isFireSource(world, downPos, Direction.UP);
            int age = state.get(AGE);

            if(!isFireSource && world.isRaining() && this.isBeingRainedOn(world, pos) && age > 0 && random.nextFloat() < 0.2F + (float) age * 0.03F)
            {
                world.removeBlock(pos, false);
            }
            else
            {
                if(age > 0 && age < 15)
                {
                    state = state.with(AGE, age + random.nextInt(3) / 2);
                    world.setBlockState(pos, state, 4);
                }

                if(!isFireSource)
                {
                    world.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(world) + random.nextInt(10));

                    if(!this.areNeighborsFlammable(world, pos))
                    {
                        if(!world.getBlockState(downPos).isSolidSide(world, downPos, Direction.UP) || age > 3)
                        {
                            world.removeBlock(pos, false);
                        }
                        return;
                    }

                    if(age == 15 && random.nextInt(4) == 0 && !this.canCatchFire(world, pos.down(), Direction.UP))
                    {
                        world.removeBlock(pos, false);
                        return;
                    }
                }

                boolean isHighHumidity = world.isBlockinHighHumidity(pos);
                int humidityChance = world.isBlockinHighHumidity(pos) ? -50 : 0;
                this.tryCatchFire(world, pos, 300 + humidityChance, random, age, Direction.WEST);
                this.tryCatchFire(world, pos, 300 + humidityChance, random, age, Direction.EAST);
                this.tryCatchFire(world, pos, 250 + humidityChance, random, age, Direction.UP);
                this.tryCatchFire(world, pos, 250 + humidityChance, random, age, Direction.DOWN);
                this.tryCatchFire(world, pos, 300 + humidityChance, random, age, Direction.SOUTH);
                this.tryCatchFire(world, pos, 300 + humidityChance, random, age, Direction.NORTH);
                BlockPos.Mutable mutablePos = new BlockPos.Mutable();

                for(int xOffset = -1; xOffset <= 1; ++xOffset)
                {
                    for(int zOffset = -1; zOffset <= 1; ++zOffset)
                    {
                        for(int yOffset = -1; yOffset <= 4; ++yOffset)
                        {
                            if(xOffset != 0 || yOffset != 0 || zOffset != 0)
                            {
                                int encouragement = 100;

                                if(yOffset > 1)
                                {
                                    encouragement += (yOffset - 1) * 100;
                                }

                                mutablePos.setPos(pos).move(xOffset, yOffset, zOffset);
                                int neighborEncouragement = this.getNeighborEncouragement(world, mutablePos);

                                if(neighborEncouragement > 0)
                                {
                                    int adjustedEncouragement = (neighborEncouragement + 40 + world.getDifficulty().getId() * 7) / (age + 30);

                                    if(isHighHumidity)
                                    {
                                        adjustedEncouragement /= 2;
                                    }

                                    if(adjustedEncouragement > 0 && random.nextInt(encouragement) <= adjustedEncouragement && (!world.isRaining() && !this.isBeingRainedOn(world, mutablePos)))
                                    {
                                        int neighborAge = Math.min(15, age + 1);
                                        world.setBlockState(mutablePos, this.getStateForPlacement(world, mutablePos).with(AGE, neighborAge), 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean isBeingRainedOn(World world, BlockPos pos)
    {
        return world.isRainingAt(pos) || world.isRainingAt(pos.west()) || world.isRainingAt(pos.east()) || world.isRainingAt(pos.north()) || world.isRainingAt(pos.south());
    }

    protected void tryCatchFire(World world, BlockPos pos, int chance, Random random, int age, Direction face)
    {
        if(random.nextInt(chance) < world.getBlockState(pos).getFlammability(world, pos, face))
        {
            BlockState state = world.getBlockState(pos);
            Block block = world.getBlockState(pos).getBlock();

            if(block instanceof TNTBlock)
            {
                block.catchFire(state, world, pos, face, null);
            }
        }
    }

    private boolean areNeighborsFlammable(IBlockReader world, BlockPos pos)
    {
        for(Direction direction : Direction.values())
        {
            if(this.canCatchFire(world, pos.offset(direction), direction.getOpposite()))
            {
                return true;
            }
        }

        return false;
    }

    private int getNeighborEncouragement(IWorldReader world, BlockPos pos)
    {
        if(!world.isAirBlock(pos))
        {
            return 0;
        }
        else
        {
            int flammability = 0;

            for(Direction direction : Direction.values())
            {
                flammability = Math.max(world.getBlockState(pos.offset(direction)).getFlammability(world, pos.offset(direction), direction.getOpposite()), flammability);
            }
            return flammability;
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if(oldState.getBlock() != state.getBlock())
        {
            if(world.dimension.getType() != DimensionType.OVERWORLD && world.dimension.getType() != DimensionType.THE_NETHER || !((NetherPortalBlock) Blocks.NETHER_PORTAL).trySpawnPortal(world, pos))
            {
                if(!state.isValidPosition(world, pos))
                {
                    world.removeBlock(pos, false);
                }
                else
                {
                    world.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(world) + world.rand.nextInt(10));
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand)
    {
        if(rand.nextInt(24) == 0)
        {
            world.playSound(((float) pos.getX() + 0.5F), ((float) pos.getY() + 0.5F), ((float) pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }

        BlockPos downPos = pos.down();

        if(!this.canCatchFire(world, downPos, Direction.UP) && !Block.hasSolidSide(world.getBlockState(downPos), world, downPos, Direction.UP))
        {
            if(this.canCatchFire(world, downPos.west(), Direction.EAST))
            {
                for(int particle = 0; particle < 2; particle++)
                {
                    double posX = (double) pos.getX() + rand.nextDouble() * (double) 0.1F;
                    double posY = (double) pos.getY() + rand.nextDouble();
                    double posZ = (double) pos.getZ() + rand.nextDouble();
                    world.addParticle(ParticleTypes.LARGE_SMOKE, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
                }
            }

            if(this.canCatchFire(world, pos.east(), Direction.WEST))
            {
                for(int particle = 0; particle < 2; particle++)
                {
                    double posX = (double) (pos.getX() + 1) - rand.nextDouble() * (double) 0.1F;
                    double posY = (double) pos.getY() + rand.nextDouble();
                    double posZ = (double) pos.getZ() + rand.nextDouble();
                    world.addParticle(ParticleTypes.LARGE_SMOKE, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
                }
            }

            if(this.canCatchFire(world, pos.north(), Direction.SOUTH))
            {
                for(int particle = 0; particle < 2; particle++)
                {
                    double posX = (double) pos.getX() + rand.nextDouble();
                    double posY = (double) pos.getY() + rand.nextDouble();
                    double posZ = (double) pos.getZ() + rand.nextDouble() * (double) 0.1F;
                    world.addParticle(ParticleTypes.LARGE_SMOKE, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
                }
            }

            if(this.canCatchFire(world, pos.south(), Direction.NORTH))
            {
                for(int particle = 0; particle < 2; particle++)
                {
                    double posX = (double) pos.getX() + rand.nextDouble();
                    double posY = (double) pos.getY() + rand.nextDouble();
                    double posZ = (double) (pos.getZ() + 1) - rand.nextDouble() * (double) 0.1F;
                    world.addParticle(ParticleTypes.LARGE_SMOKE, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
                }
            }

            if(this.canCatchFire(world, pos.up(), Direction.DOWN))
            {
                for(int particle = 0; particle < 2; particle++)
                {
                    double posX = (double) pos.getX() + rand.nextDouble();
                    double posY = (double) (pos.getY() + 1) - rand.nextDouble() * (double) 0.1F;
                    double posZ = (double) pos.getZ() + rand.nextDouble();
                    world.addParticle(ParticleTypes.LARGE_SMOKE, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        else
        {
            for(int particle = 0; particle < 3; particle++)
            {
                double posX = (double) pos.getX() + rand.nextDouble();
                double posY = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
                double posZ = (double) pos.getZ() + rand.nextDouble();
                world.addParticle(ParticleTypes.LARGE_SMOKE, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
    }

    public boolean canCatchFire(IBlockReader world, BlockPos pos, Direction face)
    {
        return world.getBlockState(pos).isFlammable(world, pos, face);
    }
}
