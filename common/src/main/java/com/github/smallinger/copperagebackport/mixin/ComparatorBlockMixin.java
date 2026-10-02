package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.block.shelf.ShelfBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.1's comparator API asks a block for analog output without a side.
 * Finalized Copper Age shelves are directional and only expose comparator
 * output from their back face, so reproduce that direction check here.
 */
@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin {
    @Inject(method = "getInputSignal", at = @At("HEAD"), cancellable = true)
    private void copperagebackport$directionalShelfComparator(
        Level level,
        BlockPos comparatorPos,
        BlockState comparatorState,
        CallbackInfoReturnable<Integer> cir
    ) {
        Direction comparatorFacing = comparatorState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos shelfPos = comparatorPos.relative(comparatorFacing);
        BlockState shelfState = level.getBlockState(shelfPos);

        if (!(shelfState.getBlock() instanceof ShelfBlock)) {
            return;
        }

        // Modern ComparatorBlock passes comparatorFacing.getOpposite() to the
        // target block. ShelfBlock returns 0 unless that equals
        // shelfFacing.getOpposite(), i.e. unless both facings are identical.
        if (comparatorFacing != shelfState.getValue(ShelfBlock.FACING)) {
            cir.setReturnValue(0);
            return;
        }

        cir.setReturnValue(shelfState.getAnalogOutputSignal(level, shelfPos));
    }
}
