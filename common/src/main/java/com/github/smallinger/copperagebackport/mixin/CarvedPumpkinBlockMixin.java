package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.event.CopperGolemSpawnLogic;
import com.github.smallinger.copperagebackport.ModTags;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin {
    @Inject(method = "canSpawnGolem", at = @At("RETURN"), cancellable = true)
    private void copperagebackport$canDispenseCopperGolem(
        LevelReader level,
        BlockPos pos,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!cir.getReturnValue()
            && CommonConfig.golemBuildSpawning()
            && level.getBlockState(pos.below()).is(ModTags.Blocks.COPPER)) {
            cir.setReturnValue(true);
        }
    }

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
