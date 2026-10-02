package com.github.smallinger.copperagebackport.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Function;

/**
 * Vanilla-style oxidation helper for custom Copper Age blocks that cannot be
 * inserted into Minecraft 1.21.1's immutable WeatheringCopper block maps.
 */
public final class WeatheringHelper {
    public static final float OXIDATION_CHANCE = 0.05688889F;
    private static final int SCAN_DISTANCE = 4;

    private WeatheringHelper() {
    }

    /**
     * Runs the same two-stage oxidation roll and nearby-copper age comparison
     * used by vanilla WeatheringCopper/ChangeOverTimeBlock.
     */
    public static boolean shouldWeather(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!(state.getBlock() instanceof WeatheringCopper current)) {
            return false;
        }

        if (random.nextFloat() >= OXIDATION_CHANCE) {
            return false;
        }

        WeatheringCopper.WeatherState currentAge = current.getAge();
        int currentOrdinal = currentAge.ordinal();
        int sameAge = 0;
        int olderAge = 0;

        for (BlockPos nearbyPos : BlockPos.withinManhattan(pos, SCAN_DISTANCE, SCAN_DISTANCE, SCAN_DISTANCE)) {
            int distance = nearbyPos.distManhattan(pos);
            if (distance > SCAN_DISTANCE) {
                break;
            }
            if (nearbyPos.equals(pos)) {
                continue;
            }

            Block nearbyBlock = level.getBlockState(nearbyPos).getBlock();
            if (!(nearbyBlock instanceof ChangeOverTimeBlock<?> changeOverTimeBlock)) {
                continue;
            }

            Enum<?> nearbyAge = changeOverTimeBlock.getAge();
            if (nearbyAge == null || nearbyAge.getClass() != currentAge.getClass()) {
                continue;
            }

            int nearbyOrdinal = nearbyAge.ordinal();
            if (nearbyOrdinal < currentOrdinal) {
                return false;
            }
            if (nearbyOrdinal > currentOrdinal) {
                olderAge++;
            } else {
                sameAge++;
            }
        }

        float ratio = (float) (olderAge + 1) / (float) (olderAge + sameAge + 1);
        float chanceModifier = currentAge == WeatheringCopper.WeatherState.UNAFFECTED ? 0.75F : 1.0F;
        return random.nextFloat() < ratio * ratio * chanceModifier;
    }

    public static boolean tryWeather(
        BlockState state,
        ServerLevel level,
        BlockPos pos,
        RandomSource random,
        Function<Block, Optional<Block>> getNextBlock
    ) {
        Optional<Block> nextBlock = getNextBlock.apply(state.getBlock());
        if (nextBlock.isEmpty() || !shouldWeather(state, level, pos, random)) {
            return false;
        }

        level.setBlockAndUpdate(pos, nextBlock.get().withPropertiesOf(state));
        return true;
    }

    public static boolean canWeather(BlockState state, Function<Block, Optional<Block>> getNextBlock) {
        return getNextBlock.apply(state.getBlock()).isPresent();
    }
}
