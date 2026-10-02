package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.block.entity.CopperGolemStatueBlockEntity;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.ModSounds;
import com.github.smallinger.copperagebackport.util.WeatheringHelper;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.github.smallinger.copperagebackport.util.CopperInteractionHelper;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class WeatheringCopperGolemStatueBlock extends CopperGolemStatueBlock implements WeatheringCopper {
    public static final MapCodec<WeatheringCopperGolemStatueBlock> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
                WeatherState.CODEC.fieldOf("weathering_state").forGetter(WeatheringCopperGolemStatueBlock::getWeatheringState),
                propertiesCodec()
            )
            .apply(instance, WeatheringCopperGolemStatueBlock::new)
    );

    public WeatheringCopperGolemStatueBlock(WeatherState weatheringState, Properties properties) {
        super(weatheringState, properties);
    }

    @Override
    protected MapCodec<? extends WeatheringCopperGolemStatueBlock> codec() {
        return CODEC;
    }
    
    /**
     * Override to provide our own oxidation chain
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == ModBlocks.COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_GOLEM_STATUE.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_GOLEM_STATUE.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_GOLEM_STATUE.get());
        }
        return WeatheringCopper.getNext(block);
    }

    /**
     * Get the previous oxidation stage for scraping with axe
     */
    public static Optional<Block> getPreviousBlock(Block block) {
        if (block == ModBlocks.OXIDIZED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_GOLEM_STATUE.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_GOLEM_STATUE.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.COPPER_GOLEM_STATUE.get());
        }
        return Optional.empty();
    }

    /**
     * Get the waxed version of this statue
     */
    public static Optional<Block> getWaxedBlock(Block block) {
        if (block == ModBlocks.COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.WAXED_COPPER_GOLEM_STATUE.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.WAXED_EXPOSED_COPPER_GOLEM_STATUE.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.WAXED_WEATHERED_COPPER_GOLEM_STATUE.get());
        } else if (block == ModBlocks.OXIDIZED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.WAXED_OXIDIZED_COPPER_GOLEM_STATUE.get());
        }
        return Optional.empty();
    }

    @Override
    protected ItemInteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
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
            Optional<Block> previousBlock = getPreviousBlock(state.getBlock());
            if (previousBlock.isPresent()) {
                if (CopperInteractionHelper.shouldCancelAxeUse(player, hand)) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }

                CopperInteractionHelper.axeTransform(
                    level, pos, previousBlock.get().withPropertiesOf(state),
                    player, hand, stack, SoundEvents.AXE_SCRAPE, 3005, null
                );
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }

            // Finalized vanilla restores an unaffected, unwaxed statue to a
            // Copper Golem directly from the statue block interaction.
            if (!level.isClientSide()
                && level.getBlockEntity(pos) instanceof CopperGolemStatueBlockEntity statueEntity) {
                CopperGolemEntity golem = statueEntity.removeStatue(state, (ServerLevel) level);
                stack.hurtAndBreak(1, player, net.minecraft.world.entity.LivingEntity.getSlotForHand(hand));
                if (golem != null) {
                    ((ServerLevel) level).addFreshEntity(golem);
                    level.removeBlock(pos, false);
                    return ItemInteractionResult.SUCCESS;
                }
            }

            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Vanilla cycles the pose for every non-axe, non-honeycomb interaction.
        if (!stack.is(Items.HONEYCOMB)) {
            if (!level.isClientSide()) {
                Pose nextPose = state.getValue(POSE).getNextPose();
                level.setBlock(pos, state.setValue(POSE, nextPose), Block.UPDATE_ALL);
                level.playSound(null, pos, ModSounds.COPPER_GOLEM_BECOME_STATUE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        WeatheringHelper.tryWeather(state, level, pos, random, WeatheringCopperGolemStatueBlock::getNextBlock);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, WeatheringCopperGolemStatueBlock::getNextBlock);
    }

    @Override
    public WeatherState getAge() {
        return this.getWeatheringState();
    }
}


