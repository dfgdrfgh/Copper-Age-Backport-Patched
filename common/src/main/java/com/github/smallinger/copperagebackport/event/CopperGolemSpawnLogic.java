package com.github.smallinger.copperagebackport.event;

import com.github.smallinger.copperagebackport.ModMemoryTypes;
import com.github.smallinger.copperagebackport.ModTags;
import com.github.smallinger.copperagebackport.block.CopperChestBlock;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.registry.ModEntities;
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
 * Shared vanilla-style Copper Golem construction logic.
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

    private static void trySpawnCopperGolem(ServerLevel level, BlockPos pumpkinPos, BlockState pumpkinState, Direction direction) {
        BlockPos copperPos = pumpkinPos.below();
        BlockState copperState = level.getBlockState(copperPos);

        if (!copperState.is(ModTags.Blocks.COPPER)) {
            return;
        }

        level.levelEvent(2001, pumpkinPos, Block.getId(pumpkinState));
        level.levelEvent(2001, copperPos, Block.getId(copperState));

        level.setBlock(pumpkinPos, Blocks.AIR.defaultBlockState(), 2);

        BlockState chestState = CopperChestBlock.getFromCopperBlock(copperState.getBlock(), direction, level, copperPos);
        level.setBlock(copperPos, chestState, 2);

        CopperGolemEntity copperGolem = ModEntities.COPPER_GOLEM.get().create(level);
        if (copperGolem == null) {
            return;
        }

        float yaw = direction.toYRot();
        copperGolem.moveTo(
            copperPos.getX() + 0.5,
            copperPos.getY() + 1.0,
            copperPos.getZ() + 0.5,
            yaw,
            0.0F
        );
        copperGolem.setYRot(yaw);
        copperGolem.yRotO = yaw;
        copperGolem.setYBodyRot(yaw);
        copperGolem.yBodyRotO = yaw;
        copperGolem.setYHeadRot(yaw);
        copperGolem.yHeadRotO = yaw;

        copperGolem.spawn(getWeatherStateFromBlock(copperState.getBlock()));
        copperGolem.getBrain().setMemory(ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get(), 140);
        level.addFreshEntity(copperGolem);

        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, copperGolem.getBoundingBox().inflate(5.0))) {
            // Hook retained for vanilla-equivalent advancement integration.
        }

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
