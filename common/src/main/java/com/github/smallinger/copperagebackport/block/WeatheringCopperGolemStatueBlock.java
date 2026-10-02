package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.block.entity.CopperGolemStatueBlockEntity;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.ModSounds;
import com.github.smallinger.copperagebackport.util.WeatheringHelper;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                             Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Honeycomb interaction - wax the statue
        if (stack.is(Items.HONEYCOMB)) {
            Optional<Block> waxedBlock = getWaxedBlock(state.getBlock());
            
            if (waxedBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3003, pos, 0); // WAX_ON particles
                
                if (!level.isClientSide) {
                    BlockState waxedState = waxedBlock.get().defaultBlockState()
                        .setValue(FACING, state.getValue(FACING))
                        .setValue(POSE, state.getValue(POSE))
                        .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                    level.setBlock(pos, waxedState, Block.UPDATE_ALL);
                    
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
                
                return ItemInteractionResult.SUCCESS;
            }
        }
        
        // Axe interaction - scrape oxidation if possible, otherwise restore golem
        if (stack.is(ItemTags.AXES)) {
            // Try scraping first if there's oxidation to remove
            Optional<Block> previousBlock = getPreviousBlock(state.getBlock());
            
            if (previousBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, pos, 0); // SCRAPE particles
                
                if (!level.isClientSide) {
                    BlockState newState = previousBlock.get().defaultBlockState()
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
            
            // No oxidation to remove - restore golem
            if (!level.isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) level;
                
                if (level.getBlockEntity(pos) instanceof CopperGolemStatueBlockEntity statueEntity) {
                    CopperGolemEntity golem = statueEntity.removeStatue(state, serverLevel);
                    stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
                    if (golem != null) {
                        serverLevel.addFreshEntity(golem);
                        level.removeBlock(pos, false);
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }
        }
        
        // Vanilla cycles the pose for every non-axe, non-honeycomb interaction.
        if (!stack.is(ItemTags.AXES) && !stack.is(Items.HONEYCOMB)) {
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


