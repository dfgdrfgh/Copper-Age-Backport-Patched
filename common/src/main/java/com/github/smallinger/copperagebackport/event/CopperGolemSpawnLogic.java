package com.github.smallinger.copperagebackport.event;

import com.github.smallinger.copperagebackport.ModTags;
import com.github.smallinger.copperagebackport.block.CopperChestBlock;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.registry.ModEntities;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Shared Copper Golem construction logic matching the vanilla Copper Age pattern.
 */
public final class CopperGolemSpawnLogic {

    private CopperGolemSpawnLogic() {
    }

    public static void handleBlockPlaced(Level level, BlockPos pos, BlockState placedState, Direction fallbackDirection) {
        if (!(level instanceof ServerLevel serverLevel) || !CommonConfig.golemBuildSpawning()) {
            return;
        }
        if (!isGolemPumpkin(placedState)) {
            return;
        }

        Direction direction;
        if (placedState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            direction = placedState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        } else if (fallbackDirection != null) {
            direction = fallbackDirection.getOpposite();
        } else {
            direction = Direction.NORTH;
        }

        trySpawnCopperGolem(serverLevel, pos, placedState, direction);
    }

    private static boolean isGolemPumpkin(BlockState state) {
        return state.is(Blocks.CARVED_PUMPKIN) || state.is(Blocks.JACK_O_LANTERN);
    }

    private static void trySpawnCopperGolem(ServerLevel level, BlockPos pumpkinPos, BlockState pumpkinState, Direction chestFacing) {
        BlockPos copperPos = null;
        BlockState copperState = null;

        // Vanilla searches the two-block pattern in every orientation, so the
        // copper block may be below, above, or on any horizontal side.
        for (Direction bodyDirection : Direction.values()) {
            BlockPos candidatePos = pumpkinPos.relative(bodyDirection);
            BlockState candidateState = level.getBlockState(candidatePos);
            if (candidateState.is(ModTags.Blocks.COPPER)) {
                copperPos = candidatePos;
                copperState = candidateState;
                break;
            }
        }

        if (copperPos == null || copperState == null) {
            return;
        }

        level.levelEvent(2001, pumpkinPos, Block.getId(pumpkinState));
        level.levelEvent(2001, copperPos, Block.getId(copperState));

        // Vanilla clears the complete pattern before spawning the entity.
        level.setBlock(pumpkinPos, Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(copperPos, Blocks.AIR.defaultBlockState(), 2);

        CopperGolemEntity copperGolem = ModEntities.COPPER_GOLEM.get().create(level);
        if (copperGolem == null) {
            // Restore the structure if entity creation unexpectedly fails.
            level.setBlock(pumpkinPos, pumpkinState, 2);
            level.setBlock(copperPos, copperState, 2);
            return;
        }

        copperGolem.moveTo(
            pumpkinPos.getX() + 0.5,
            pumpkinPos.getY() + 0.05,
            pumpkinPos.getZ() + 0.5,
            0.0F,
            0.0F
        );
        copperGolem.spawn(getWeatherStateFromBlock(copperState.getBlock()));

        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, copperGolem.getBoundingBox().inflate(5.0))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, copperGolem);
        }

        level.addFreshEntity(copperGolem);

        BlockState chestState = CopperChestBlock.getFromCopperBlock(
            copperState.getBlock(),
            chestFacing,
            level,
            copperPos
        );
        level.setBlock(copperPos, chestState, 2);

        level.updateNeighborsAt(copperPos, chestState.getBlock());
        level.updateNeighborsAt(pumpkinPos, Blocks.AIR);
    }

    private static WeatheringCopper.WeatherState getWeatherStateFromBlock(Block block) {
        if (block instanceof WeatheringCopper weatheringCopper) {
            return weatheringCopper.getAge();
        }
        if (block == Blocks.WAXED_COPPER_BLOCK) {
            return WeatheringCopper.WeatherState.UNAFFECTED;
        } else if (block == Blocks.WAXED_EXPOSED_COPPER) {
            return WeatheringCopper.WeatherState.EXPOSED;
        } else if (block == Blocks.WAXED_WEATHERED_COPPER) {
            return WeatheringCopper.WeatherState.WEATHERED;
        } else if (block == Blocks.WAXED_OXIDIZED_COPPER) {
            return WeatheringCopper.WeatherState.OXIDIZED;
        }
        return WeatheringCopper.WeatherState.UNAFFECTED;
    }
}
