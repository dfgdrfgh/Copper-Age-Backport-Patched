package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.block.entity.CopperChestBlockEntity;
import com.github.smallinger.copperagebackport.util.CopperInteractionHelper;
import com.github.smallinger.copperagebackport.util.WeatheringHelper;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class WeatheringCopperChestBlock extends CopperChestBlock implements WeatheringCopper {
    
    public WeatheringCopperChestBlock(WeatherState weatheringState, Properties properties) {
        super(weatheringState, properties);
    }

    @Override
    public MapCodec<? extends WeatheringCopperChestBlock> codec() {
        return null; // Simplified for 1.21.1
    }

    /**
     * Get the next oxidation stage
     */
    public static Optional<Block> getNextBlock(Block block) {
        if (block == ModBlocks.COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_CHEST.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_CHEST.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_CHEST.get());
        }
        return WeatheringCopper.getNext(block);
    }
    
    /**
     * Get the previous oxidation stage for scraping
     */
    public static Optional<Block> getPreviousBlock(Block block) {
        if (block == ModBlocks.OXIDIZED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_CHEST.get());
        } else if (block == ModBlocks.WEATHERED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_CHEST.get());
        } else if (block == ModBlocks.EXPOSED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.COPPER_CHEST.get());
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
            Block waxedBlock = null;
            if (this == ModBlocks.COPPER_CHEST.get()) {
                waxedBlock = ModBlocks.WAXED_COPPER_CHEST.get();
            } else if (this == ModBlocks.EXPOSED_COPPER_CHEST.get()) {
                waxedBlock = ModBlocks.WAXED_EXPOSED_COPPER_CHEST.get();
            } else if (this == ModBlocks.WEATHERED_COPPER_CHEST.get()) {
                waxedBlock = ModBlocks.WAXED_WEATHERED_COPPER_CHEST.get();
            } else if (this == ModBlocks.OXIDIZED_COPPER_CHEST.get()) {
                waxedBlock = ModBlocks.WAXED_OXIDIZED_COPPER_CHEST.get();
            }

            if (waxedBlock != null) {
                BlockState newState = waxedBlock.withPropertiesOf(state);
                if (!level.isClientSide) {
                    level.setBlock(pos, newState, 11);
                    level.gameEvent(
                        net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE,
                        pos,
                        net.minecraft.world.level.gameevent.GameEvent.Context.of(player, newState)
                    );
                    stack.shrink(1);
                }

                level.levelEvent(player, 3003, pos, 0);
                mirrorDoubleChestEffect(level, player, state, pos, 3003);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (stack.is(ItemTags.AXES)) {
            Optional<Block> previousBlock = getPreviousBlock(state.getBlock());
            if (previousBlock.isPresent()) {
                BlockState newState = previousBlock.get().withPropertiesOf(state);

                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, pos, 0);
                mirrorDoubleChestEffect(level, player, state, pos, 3005);

                if (!level.isClientSide) {
                    level.setBlock(pos, newState, 11);
                    level.gameEvent(
                        net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE,
                        pos,
                        net.minecraft.world.level.gameevent.GameEvent.Context.of(player, newState)
                    );
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                }

                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    private static void mirrorDoubleChestEffect(
        Level level,
        Player player,
        BlockState state,
        BlockPos pos,
        int eventId
    ) {
        if (state.getValue(TYPE) == ChestType.SINGLE) {
            return;
        }

        BlockPos connectedPos = pos.relative(ChestBlock.getConnectedDirection(state));
        BlockState connectedState = level.getBlockState(connectedPos);
        level.levelEvent(player, eventId, connectedPos, 0);

        if (!level.isClientSide) {
            level.gameEvent(
                net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE,
                connectedPos,
                net.minecraft.world.level.gameevent.GameEvent.Context.of(player, connectedState)
            );
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(TYPE) == ChestType.RIGHT) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof CopperChestBlockEntity chestEntity
            && chestEntity.hasAnyViewer()) {
            return;
        }

        WeatheringHelper.tryWeather(state, level, pos, random, WeatheringCopperChestBlock::getNextBlock);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, WeatheringCopperChestBlock::getNextBlock);
    }

    @Override
    public WeatherState getAge() {
        return this.getState();
    }

    @Override
    public boolean isWaxed() {
        return false;
    }
}
