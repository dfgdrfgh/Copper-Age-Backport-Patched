package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.github.smallinger.copperagebackport.util.CopperInteractionHelper;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Waxed Copper Lightning Rod block - doesn't oxidize.
 * On NeoForge: Wax removal handled by Data Maps.
 * On Fabric: Wax removal handled by useItemOn.
 */
public class WaxedCopperLightningRodBlock extends CopperLightningRodBlock {
    public static final MapCodec<WaxedCopperLightningRodBlock> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(WaxedCopperLightningRodBlock::getWeatheringState),
            propertiesCodec()
        ).apply(instance, WaxedCopperLightningRodBlock::new)
    );
    
    private final WeatheringCopper.WeatherState weatheringState;

    @Override
    public MapCodec<? extends CopperLightningRodBlock> codec() {
        return CODEC;
    }

    public WaxedCopperLightningRodBlock(WeatheringCopper.WeatherState weatheringState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatheringState = weatheringState;
    }

    public WeatheringCopper.WeatherState getWeatheringState() {
        return this.weatheringState;
    }

    public Optional<Block> getUnwaxedBlock() {
        return switch (this.weatheringState) {
            case UNAFFECTED -> Optional.of(Blocks.LIGHTNING_ROD);
            case EXPOSED -> Optional.of(ModBlocks.EXPOSED_LIGHTNING_ROD.get());
            case WEATHERED -> Optional.of(ModBlocks.WEATHERED_LIGHTNING_ROD.get());
            case OXIDIZED -> Optional.of(ModBlocks.OXIDIZED_LIGHTNING_ROD.get());
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(ItemTags.AXES)) {
            if (CopperInteractionHelper.shouldCancelAxeUse(player, hand)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            Optional<Block> unwaxed = getUnwaxedBlock();
            if (unwaxed.isPresent()) {
                CopperInteractionHelper.axeTransform(
                    level, pos, unwaxed.get().withPropertiesOf(state),
                    player, hand, stack, SoundEvents.AXE_WAX_OFF, 3004, null
                );
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
