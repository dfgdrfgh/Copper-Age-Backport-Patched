package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.block.CopperLightningRodBlock;
import com.github.smallinger.copperagebackport.block.WeatheringCopperLightningRodBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * Makes the backported weathering/waxed lightning rods participate in the same
 * strike behavior as the finalized Copper Age rods.
 */
@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {

    @Shadow
    protected abstract BlockPos getStrikePosition();

    @Inject(method = "powerLightningRod", at = @At("HEAD"))
    private void copperagebackport$powerCopperAgeRod(CallbackInfo ci) {
        LightningBolt self = (LightningBolt) (Object) this;
        Level level = self.level();
        BlockPos rodPos = copperagebackport$findCopperAgeRod(level, this.getStrikePosition());
        if (rodPos == null) {
            return;
        }

        BlockState state = level.getBlockState(rodPos);
        if (state.getBlock() instanceof CopperLightningRodBlock rod) {
            rod.onLightningStrike(state, level, rodPos);
        }
    }

    @Inject(method = "clearCopperOnLightningStrike", at = @At("HEAD"), cancellable = true)
    private static void copperagebackport$cleanCopperAgeRod(Level level, BlockPos strikePos, CallbackInfo ci) {
        BlockPos rodPos = copperagebackport$findCopperAgeRod(level, strikePos);
        if (rodPos == null) {
            return;
        }

        BlockState rodState = level.getBlockState(rodPos);
        Direction facing = rodState.getValue(RodBlock.FACING);

        // A direct lightning strike removes one oxidation stage from an unwaxed rod.
        // Waxed rods keep their current oxidation state.
        if (rodState.getBlock() instanceof WeatheringCopperLightningRodBlock weatheringRod) {
            Optional<Block> previousBlock = weatheringRod.getPreviousBlock();
            if (previousBlock.isPresent()) {
                BlockState previousState = previousBlock.get().withPropertiesOf(rodState);
                level.setBlockAndUpdate(rodPos, previousState);
                copperagebackport$powerReplacementRod(level, rodPos, previousState);
            }
        }

        // Vanilla lightning rods redirect copper cleaning to the block they are attached to.
        BlockPos attachedPos = rodPos.relative(facing.getOpposite());
        BlockState attachedState = level.getBlockState(attachedPos);
        if (attachedState.getBlock() instanceof WeatheringCopper) {
            level.setBlockAndUpdate(attachedPos, WeatheringCopper.getFirst(attachedState));

            BlockPos.MutableBlockPos mutable = strikePos.mutable();
            int walks = level.random.nextInt(3) + 3;
            for (int i = 0; i < walks; i++) {
                copperagebackport$randomWalkCleaningCopper(
                    level,
                    attachedPos,
                    mutable,
                    level.random.nextInt(8) + 1
                );
            }
        }

        // We handled the custom rod path completely; vanilla only recognizes its own
        // minecraft:lightning_rod in this 1.21.1 target.
        ci.cancel();
    }

    private static BlockPos copperagebackport$findCopperAgeRod(Level level, BlockPos strikePos) {
        if (level.getBlockState(strikePos).getBlock() instanceof CopperLightningRodBlock) {
            return strikePos;
        }

        BlockPos below = strikePos.below();
        if (level.getBlockState(below).getBlock() instanceof CopperLightningRodBlock) {
            return below;
        }

        return null;
    }

    private static void copperagebackport$powerReplacementRod(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof CopperLightningRodBlock customRod) {
            customRod.onLightningStrike(state, level, pos);
        } else if (state.getBlock() instanceof LightningRodBlock vanillaRod) {
            vanillaRod.onLightningStrike(state, level, pos);
        }
    }

    private static void copperagebackport$randomWalkCleaningCopper(
        Level level,
        BlockPos start,
        BlockPos.MutableBlockPos mutable,
        int steps
    ) {
        mutable.set(start);

        for (int i = 0; i < steps; i++) {
            Optional<BlockPos> next = copperagebackport$randomStepCleaningCopper(level, mutable);
            if (next.isEmpty()) {
                break;
            }
            mutable.set(next.get());
        }
    }

    private static Optional<BlockPos> copperagebackport$randomStepCleaningCopper(Level level, BlockPos pos) {
        for (BlockPos candidate : BlockPos.randomInCube(level.random, 10, pos, 1)) {
            BlockState state = level.getBlockState(candidate);
            Optional<BlockState> previous = copperagebackport$getPreviousCopperState(state);
            if (previous.isPresent()) {
                level.setBlockAndUpdate(candidate, previous.get());
                level.levelEvent(3002, candidate, -1);
                return Optional.of(candidate);
            }
        }

        return Optional.empty();
    }

    private static Optional<BlockState> copperagebackport$getPreviousCopperState(BlockState state) {
        if (state.getBlock() instanceof WeatheringCopperLightningRodBlock weatheringRod) {
            return weatheringRod.getPreviousBlock().map(block -> block.withPropertiesOf(state));
        }

        if (state.getBlock() instanceof WeatheringCopper) {
            return WeatheringCopper.getPrevious(state);
        }

        return Optional.empty();
    }
}
