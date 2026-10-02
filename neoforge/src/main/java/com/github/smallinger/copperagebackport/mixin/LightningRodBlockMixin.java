package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.config.CommonConfig;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.github.smallinger.copperagebackport.util.CopperInteractionHelper;
import com.github.smallinger.copperagebackport.util.WeatheringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

/**
 * Mixin to add weathering functionality to the vanilla Lightning Rod block.
 * This makes the vanilla lightning rod oxidize over time like other copper blocks.
 * 
 * IMPORTANT: We implement oxidation directly instead of using changeOverTime() because:
 * - Other mods (like Friends and Foes) register their own lightning rod variants in the
 *   WeatheringCopper static maps/DataMapHooks, which would cause our lightning rod to
 *   oxidize into THEIR blocks instead of ours.
 * - By directly setting the block state to our ModBlocks, we ensure the oxidation chain
 *   stays within our mod's blocks.
 * 
 * We use the minecraft: namespace for our blocks to improve compatibility with future
 * Minecraft versions where oxidized lightning rods may be added to vanilla.
 */
@Mixin(LightningRodBlock.class)
public abstract class LightningRodBlockMixin extends RodBlock implements WeatheringCopper, ChangeOverTimeBlock<WeatheringCopper.WeatherState> {

    public LightningRodBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public WeatherState getAge() {
        return WeatherState.UNAFFECTED;
    }

    @Unique
    private Optional<Block> copperagebackport$getNextBlock() {
        return Optional.of(ModBlocks.EXPOSED_LIGHTNING_ROD.get());
    }

    @Unique
    private Optional<Block> copperagebackport$getWaxedBlock() {
        return Optional.of(ModBlocks.WAXED_LIGHTNING_ROD.get());
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        // Always tick - oxidation is always active
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        WeatheringHelper.tryWeather(
            state,
            level,
            pos,
            random,
            ignored -> copperagebackport$getNextBlock()
        );
    }



    @Override
    public Optional<BlockState> getNext(BlockState state) {
        // Always return next oxidation state
        return copperagebackport$getNextBlock().map(block -> block.withPropertiesOf(state));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.HONEYCOMB)) {
            Optional<Block> waxedBlock = copperagebackport$getWaxedBlock();
            if (waxedBlock.isPresent()) {
                CopperInteractionHelper.wax(
                    level, pos, waxedBlock.get().withPropertiesOf(state), player, stack, null
                );
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public float getChanceModifier() {
        return 0.75F;
    }
}
