package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.github.smallinger.copperagebackport.util.CopperInteractionHelper;
import com.github.smallinger.copperagebackport.util.WeatheringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Weathering Copper Lantern block that oxidizes over time.
 */
public class WeatheringCopperLanternBlock extends CopperLanternBlock implements WeatheringCopper {

    public WeatheringCopperLanternBlock(WeatherState weatheringState, Properties properties) {
        super(weatheringState, properties);
    }

    /**
     * Provides our own oxidation chain for copper lanterns.
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == ModBlocks.COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_LANTERN.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_LANTERN.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_LANTERN.get());
        }
        return Optional.empty();
    }
    
    /**
     * Gets the previous oxidation stage (for scraping with axe).
     */
    public static Optional<Block> getPreviousBlock(Block block) {
        if (block == ModBlocks.EXPOSED_COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.COPPER_LANTERN.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_LANTERN.get());
        } else if (block == ModBlocks.OXIDIZED_COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_LANTERN.get());
        }
        return Optional.empty();
    }
    
    /**
     * Gets the waxed variant of a copper lantern.
     */
    public static Optional<Block> getWaxedBlock(Block block) {
        if (block == ModBlocks.COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.WAXED_COPPER_LANTERN.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.WAXED_EXPOSED_COPPER_LANTERN.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.WAXED_WEATHERED_COPPER_LANTERN.get());
        } else if (block == ModBlocks.OXIDIZED_COPPER_LANTERN.get()) {
            return Optional.of(ModBlocks.WAXED_OXIDIZED_COPPER_LANTERN.get());
        }
        return Optional.empty();
    }
    
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.HONEYCOMB)) {
            Optional<Block> waxedBlock = getWaxedBlock(state.getBlock());
            if (waxedBlock.isPresent()) {
                CopperInteractionHelper.wax(
                    level, pos, waxedBlock.get().withPropertiesOf(state), player, stack, null
                );
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (stack.is(ItemTags.AXES)) {
            if (CopperInteractionHelper.shouldCancelAxeUse(player, hand)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            Optional<Block> previousBlock = getPreviousBlock(state.getBlock());
            if (previousBlock.isPresent()) {
                CopperInteractionHelper.axeTransform(
                    level, pos, previousBlock.get().withPropertiesOf(state),
                    player, hand, stack, SoundEvents.AXE_SCRAPE, 3005, null
                );
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        WeatheringHelper.tryWeather(state, level, pos, random, WeatheringCopperLanternBlock::getNextBlock);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, WeatheringCopperLanternBlock::getNextBlock);
    }

    @Override
    public WeatherState getAge() {
        return this.getWeatheringState();
    }
}
