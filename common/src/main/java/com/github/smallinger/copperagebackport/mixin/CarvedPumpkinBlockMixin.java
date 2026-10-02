package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.event.CopperGolemSpawnLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin {
    @Inject(method = "onPlace", at = @At("TAIL"))
    private void copperagebackport$trySpawnCopperGolem(
        BlockState state,
        Level level,
        BlockPos pos,
        BlockState oldState,
        boolean movedByPiston,
        CallbackInfo ci
    ) {
        CopperGolemSpawnLogic.handleBlockPlaced(level, pos, state, null);
    }
}
