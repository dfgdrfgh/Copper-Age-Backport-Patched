package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.ModSounds;
import com.github.smallinger.copperagebackport.ModTags;
import com.github.smallinger.copperagebackport.block.entity.CopperChestBlockEntity;
import com.github.smallinger.copperagebackport.platform.Services;
import com.github.smallinger.copperagebackport.registry.ModBlockEntities;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.github.smallinger.copperagebackport.util.CopperInteractionHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;


public class CopperChestBlock extends ChestBlock {
    private static final Map<Block, Supplier<Block>> COPPER_TO_COPPER_CHEST_MAPPING = Map.of(
        Blocks.COPPER_BLOCK, () -> ModBlocks.COPPER_CHEST.get(),
        Blocks.EXPOSED_COPPER, () -> ModBlocks.EXPOSED_COPPER_CHEST.get(),
        Blocks.WEATHERED_COPPER, () -> ModBlocks.WEATHERED_COPPER_CHEST.get(),
        Blocks.OXIDIZED_COPPER, () -> ModBlocks.OXIDIZED_COPPER_CHEST.get(),
        Blocks.WAXED_COPPER_BLOCK, () -> ModBlocks.COPPER_CHEST.get(),
        Blocks.WAXED_EXPOSED_COPPER, () -> ModBlocks.EXPOSED_COPPER_CHEST.get(),
        Blocks.WAXED_WEATHERED_COPPER, () -> ModBlocks.WEATHERED_COPPER_CHEST.get(),
        Blocks.WAXED_OXIDIZED_COPPER, () -> ModBlocks.OXIDIZED_COPPER_CHEST.get()
    );

    protected final WeatheringCopper.WeatherState weatherState;
    private final SoundEvent openSound;
    private final SoundEvent closeSound;

    public CopperChestBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties, () -> ModBlockEntities.COPPER_CHEST_BLOCK_ENTITY.get());
        this.weatherState = weatherState;
        switch (weatherState) {
            case WEATHERED -> {
                this.openSound = ModSounds.COPPER_CHEST_WEATHERED_OPEN.get();
                this.closeSound = ModSounds.COPPER_CHEST_WEATHERED_CLOSE.get();
            }
            case OXIDIZED -> {
                this.openSound = ModSounds.COPPER_CHEST_OXIDIZED_OPEN.get();
                this.closeSound = ModSounds.COPPER_CHEST_OXIDIZED_CLOSE.get();
            }
            default -> {
                this.openSound = ModSounds.COPPER_CHEST_OPEN.get();
                this.closeSound = ModSounds.COPPER_CHEST_CLOSE.get();
            }
        }
    }

    @Override
    public MapCodec<? extends ChestBlock> codec() {
        return null; // Simplified for 1.21.1
    }

    public WeatheringCopper.WeatherState getState() {
        return this.weatherState;
    }

    public SoundEvent getOpenSound() {
        return this.openSound;
    }

    public SoundEvent getCloseSound() {
        return this.closeSound;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CopperChestBlockEntity(pos, state);
    }
    
    /**
     * Override render shape to support FastChest mod on Fabric.
     * When FastChest's simplified mode is enabled, use MODEL rendering instead of ENTITYBLOCK_ANIMATED.
     */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        if (Services.PLATFORM.isFastChestSimplifiedEnabled()) {
            return RenderShape.MODEL;
        }
        return super.getRenderShape(state);
    }
    
    /**
     * Override ticker to support FastChest mod on Fabric.
     * When FastChest's simplified mode is enabled, disable the ticker for better performance.
     */
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (Services.PLATFORM.isFastChestSimplifiedEnabled()) {
            return null;
        }
        return super.getTicker(level, state, type);
    }

    public static BlockState getFromCopperBlock(Block block, Direction direction, Level level, BlockPos pos) {
        Block mapped = COPPER_TO_COPPER_CHEST_MAPPING.getOrDefault(block, () -> ModBlocks.COPPER_CHEST.get()).get();
        CopperChestBlock chest = (CopperChestBlock) mapped;
        ChestType chestType = chest.getCopperChestType(level, pos, direction);
        BlockState initial = chest.defaultBlockState()
            .setValue(FACING, direction)
            .setValue(TYPE, chestType);
        return getNormalizedDoubleChestState(initial, level, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ChestType chestType = ChestType.SINGLE;
        Direction facing = context.getHorizontalDirection().getOpposite();
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        boolean secondaryUse = context.isSecondaryUseActive();
        Direction clickedFace = context.getClickedFace();

        if (clickedFace.getAxis().isHorizontal() && secondaryUse) {
            Direction neighborFacing = candidateCopperPartnerFacing(
                context.getLevel(),
                context.getClickedPos(),
                clickedFace.getOpposite()
            );
            if (neighborFacing != null && neighborFacing.getAxis() != clickedFace.getAxis()) {
                facing = neighborFacing;
                chestType = neighborFacing.getCounterClockWise() == clickedFace.getOpposite()
                    ? ChestType.RIGHT
                    : ChestType.LEFT;
            }
        }

        if (chestType == ChestType.SINGLE && !secondaryUse) {
            chestType = getCopperChestType(context.getLevel(), context.getClickedPos(), facing);
        }

        BlockState state = this.defaultBlockState()
            .setValue(FACING, facing)
            .setValue(TYPE, chestType)
            .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        return getNormalizedDoubleChestState(state, context.getLevel(), context.getClickedPos());
    }

    private ChestType getCopperChestType(Level level, BlockPos pos, Direction facing) {
        if (facing == candidateCopperPartnerFacing(level, pos, facing.getClockWise())) {
            return ChestType.LEFT;
        }
        if (facing == candidateCopperPartnerFacing(level, pos, facing.getCounterClockWise())) {
            return ChestType.RIGHT;
        }
        return ChestType.SINGLE;
    }

    @Nullable
    private static Direction candidateCopperPartnerFacing(Level level, BlockPos pos, Direction neighborDirection) {
        BlockState neighborState = level.getBlockState(pos.relative(neighborDirection));
        return canMergeWithCopperChest(neighborState) && neighborState.getValue(TYPE) == ChestType.SINGLE
            ? neighborState.getValue(FACING)
            : null;
    }

    private static boolean canMergeWithCopperChest(BlockState state) {
        return state.is(ModTags.Blocks.COPPER_CHESTS) && state.hasProperty(TYPE) && state.hasProperty(FACING);
    }

    private static BlockState getNormalizedDoubleChestState(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(TYPE) == ChestType.SINGLE || !(state.getBlock() instanceof CopperChestBlock self)) {
            return state;
        }

        BlockPos neighborPos = pos.relative(ChestBlock.getConnectedDirection(state));
        BlockState neighborState = level.getBlockState(neighborPos);
        if (!(neighborState.getBlock() instanceof CopperChestBlock neighbor)) {
            return state;
        }

        BlockState selfState = state;
        BlockState resolvedNeighbor = neighborState;
        if (self.isWaxed() != neighbor.isWaxed()) {
            Optional<Block> selfUnwaxed = getUnwaxedBlock(selfState.getBlock());
            if (selfUnwaxed.isPresent()) {
                selfState = selfUnwaxed.get().withPropertiesOf(selfState);
            }

            Optional<Block> neighborUnwaxed = getUnwaxedBlock(resolvedNeighbor.getBlock());
            if (neighborUnwaxed.isPresent()) {
                resolvedNeighbor = neighborUnwaxed.get().withPropertiesOf(resolvedNeighbor);
            }
        }

        Block lessOxidized = self.weatherState.ordinal() <= neighbor.weatherState.ordinal()
            ? selfState.getBlock()
            : resolvedNeighbor.getBlock();
        return lessOxidized.withPropertiesOf(selfState);
    }

    @Override
    protected BlockState updateShape(
        BlockState state,
        Direction direction,
        BlockState neighborState,
        LevelAccessor level,
        BlockPos pos,
        BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (canMergeWithCopperChest(neighborState) && direction.getAxis().isHorizontal()) {
            ChestType neighborType = neighborState.getValue(TYPE);
            BlockState result = state;

            if (state.getValue(TYPE) == ChestType.SINGLE
                && neighborType != ChestType.SINGLE
                && state.getValue(FACING) == neighborState.getValue(FACING)
                && ChestBlock.getConnectedDirection(neighborState) == direction.getOpposite()) {
                result = state.setValue(TYPE, neighborType.getOpposite());
            } else if (ChestBlock.getConnectedDirection(state) == direction
                && neighborType == ChestType.SINGLE) {
                result = state.setValue(TYPE, ChestType.SINGLE);
            }

            if (result.getValue(TYPE) != ChestType.SINGLE
                && ChestBlock.getConnectedDirection(result) == direction) {
                return neighborState.getBlock().withPropertiesOf(result);
            }

            return result;
        }

        if (ChestBlock.getConnectedDirection(state) == direction) {
            return state.setValue(TYPE, ChestType.SINGLE);
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        // All copper chest variants share one block-entity type. Keep it in place when
        // oxidation/waxing/double-chest normalization swaps one copper chest block for another.
        if (state.is(ModTags.Blocks.COPPER_CHESTS) && newState.is(ModTags.Blocks.COPPER_CHESTS)) {
            return;
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    public boolean isWaxed() {
        return true;
    }
    
    /**
     * Get the unwaxed version of a waxed chest block
     */
    public static Optional<Block> getUnwaxedBlock(Block block) {
        if (block == ModBlocks.WAXED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.COPPER_CHEST.get());
        } else if (block == ModBlocks.WAXED_EXPOSED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.EXPOSED_COPPER_CHEST.get());
        } else if (block == ModBlocks.WAXED_WEATHERED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.WEATHERED_COPPER_CHEST.get());
        } else if (block == ModBlocks.WAXED_OXIDIZED_COPPER_CHEST.get()) {
            return Optional.of(ModBlocks.OXIDIZED_COPPER_CHEST.get());
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
        if (stack.is(ItemTags.AXES)) {
            if (CopperInteractionHelper.shouldCancelAxeUse(player, hand)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            Optional<Block> unwaxedBlock = getUnwaxedBlock(state.getBlock());
            if (unwaxedBlock.isPresent()) {
                BlockPos connectedEffectPos = state.getValue(TYPE) == ChestType.SINGLE
                    ? null
                    : pos.relative(ChestBlock.getConnectedDirection(state));

                CopperInteractionHelper.axeTransform(
                    level,
                    pos,
                    unwaxedBlock.get().withPropertiesOf(state),
                    player,
                    hand,
                    stack,
                    SoundEvents.AXE_WAX_OFF,
                    3004,
                    connectedEffectPos
                );
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
