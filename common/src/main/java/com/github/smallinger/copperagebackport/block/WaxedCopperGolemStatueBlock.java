package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.block.entity.CopperGolemStatueBlockEntity;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.ModSounds;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class WaxedCopperGolemStatueBlock extends CopperGolemStatueBlock {
    public static final MapCodec<WaxedCopperGolemStatueBlock> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
                WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(WaxedCopperGolemStatueBlock::getWeatheringState),
                propertiesCodec()
            )
            .apply(instance, WaxedCopperGolemStatueBlock::new)
    );

    public WaxedCopperGolemStatueBlock(WeatheringCopper.WeatherState weatheringState, Properties properties) {
        super(weatheringState, properties);
    }

    @Override
    protected MapCodec<? extends WaxedCopperGolemStatueBlock> codec() {
        return CODEC;
    }

    /**
     * Get the unwaxed version of this waxed statue
     */
    public static Optional<Block> getUnwaxedBlock(Block block) {
        if (block == ModBlocks.WAXED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.COPPER_GOLEM_STATUE.get());
        } else if (block == ModBlocks.WAXED_EXPOSED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_GOLEM_STATUE.get());
        } else if (block == ModBlocks.WAXED_WEATHERED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_GOLEM_STATUE.get());
        } else if (block == ModBlocks.WAXED_OXIDIZED_COPPER_GOLEM_STATUE.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_GOLEM_STATUE.get());
        }
        return Optional.empty();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                             Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Axe interaction - dewax if waxed, otherwise restore golem
        if (stack.is(ItemTags.AXES)) {
            // Try dewaxing first
            Optional<Block> unwaxedBlock = getUnwaxedBlock(state.getBlock());
            
            if (unwaxedBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3004, pos, 0); // WAX_OFF particles
                
                if (!level.isClientSide) {
                    BlockState newState = unwaxedBlock.get().defaultBlockState()
                        .setValue(FACING, state.getValue(FACING))
                        .setValue(POSE, state.getValue(POSE))
                        .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                    level.setBlock(pos, newState, Block.UPDATE_ALL);
                    
                    if (!player.isCreative()) {
                        stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
                    }
                }
                
                return ItemInteractionResult.SUCCESS;
            }
            
            // Not waxed - restore golem (use parent behavior)
            if (!level.isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) level;
                
                if (level.getBlockEntity(pos) instanceof CopperGolemStatueBlockEntity statueEntity) {
                    CopperGolemEntity golem = statueEntity.removeStatue(state, serverLevel);
                    if (golem != null) {
                        level.removeBlock(pos, false);
                        serverLevel.addFreshEntity(golem);
                        level.playSound(null, pos, ModSounds.COPPER_STATUE_BREAK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        // TODO: Maybe change particle effect - currently using SCRAPE (3005)
                        level.levelEvent(null, 3005, pos, 0);
                        level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
                        stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }
        }
        
        if (!stack.is(ItemTags.AXES)) {
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
    protected boolean isRandomlyTicking(BlockState state) {
        return false; // Waxed statues don't oxidize
    }
}
