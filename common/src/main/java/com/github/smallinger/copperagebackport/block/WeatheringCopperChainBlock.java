package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.github.smallinger.copperagebackport.util.CopperInteractionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Weathering Copper Chain block - oxidizes over time and can be waxed or scraped.
 */
public class WeatheringCopperChainBlock extends ChainBlock implements WeatheringCopper {
    private final WeatherState weatherState;

    public WeatheringCopperChainBlock(WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatherState = weatherState;
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }

    /**
     * Get the next oxidation stage block
     */
    public Optional<Block> getNextBlock() {
        return switch (this.weatherState) {
            case UNAFFECTED -> Optional.of(ModBlocks.EXPOSED_COPPER_CHAIN.get());
            case EXPOSED -> Optional.of(ModBlocks.WEATHERED_COPPER_CHAIN.get());
            case WEATHERED -> Optional.of(ModBlocks.OXIDIZED_COPPER_CHAIN.get());
            case OXIDIZED -> Optional.empty();
        };
    }

    /**
     * Get the previous oxidation stage block (for axe scraping)
     */
    public Optional<Block> getPreviousBlock() {
        return switch (this.weatherState) {
            case UNAFFECTED -> Optional.empty();
            case EXPOSED -> Optional.of(ModBlocks.COPPER_CHAIN.get());
            case WEATHERED -> Optional.of(ModBlocks.EXPOSED_COPPER_CHAIN.get());
            case OXIDIZED -> Optional.of(ModBlocks.WEATHERED_COPPER_CHAIN.get());
        };
    }

    /**
     * Get the waxed version of this block
     */
    public Optional<Block> getWaxedBlock() {
        return switch (this.weatherState) {
            case UNAFFECTED -> Optional.of(ModBlocks.WAXED_COPPER_CHAIN.get());
            case EXPOSED -> Optional.of(ModBlocks.WAXED_EXPOSED_COPPER_CHAIN.get());
            case WEATHERED -> Optional.of(ModBlocks.WAXED_WEATHERED_COPPER_CHAIN.get());
            case OXIDIZED -> Optional.of(ModBlocks.WAXED_OXIDIZED_COPPER_CHAIN.get());
        };
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.changeOverTime(state, level, pos, random);
    }

    @Override
    public Optional<BlockState> getNext(BlockState state) {
        return getNextBlock().map(block -> block.defaultBlockState()
            .setValue(AXIS, state.getValue(AXIS))
            .setValue(WATERLOGGED, state.getValue(WATERLOGGED)));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.HONEYCOMB)) {
            Optional<Block> waxed = getWaxedBlock();
            if (waxed.isPresent()) {
                BlockState newState = waxed.get().defaultBlockState()
                    .setValue(AXIS, state.getValue(AXIS))
                    .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                CopperInteractionHelper.wax(level, pos, newState, player, stack, null);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (stack.is(ItemTags.AXES)) {
            if (CopperInteractionHelper.shouldCancelAxeUse(player, hand)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            Optional<Block> previous = getPreviousBlock();
            if (previous.isPresent()) {
                BlockState newState = previous.get().defaultBlockState()
                    .setValue(AXIS, state.getValue(AXIS))
                    .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                CopperInteractionHelper.axeTransform(
                    level, pos, newState, player, hand, stack, SoundEvents.AXE_SCRAPE, 3005, null
                );
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
