package com.github.smallinger.copperagebackport.util;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.mixin.PoiTypesAccessor;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;

/**
 * Registers the backported lightning-rod variants as vanilla lightning-rod POIs.
 * ServerLevel's natural lightning targeting can then find every oxidation/wax state
 * through the same 128-block POI search used by vanilla.
 */
public final class LightningRodPoiHelper {
    private LightningRodPoiHelper() {
    }

    public static void register() {
        Map<BlockState, Holder<PoiType>> typeByState = PoiTypesAccessor.copperagebackport$getTypeByState();
        Holder<PoiType> lightningRodPoi = typeByState.get(Blocks.LIGHTNING_ROD.defaultBlockState());
        if (lightningRodPoi == null) {
            Constants.LOG.warn("Could not resolve vanilla lightning rod POI; Copper Age rod variants will not redirect natural lightning");
            return;
        }

        List<Block> rods = List.of(
            ModBlocks.EXPOSED_LIGHTNING_ROD.get(),
            ModBlocks.WEATHERED_LIGHTNING_ROD.get(),
            ModBlocks.OXIDIZED_LIGHTNING_ROD.get(),
            ModBlocks.WAXED_LIGHTNING_ROD.get(),
            ModBlocks.WAXED_EXPOSED_LIGHTNING_ROD.get(),
            ModBlocks.WAXED_WEATHERED_LIGHTNING_ROD.get(),
            ModBlocks.WAXED_OXIDIZED_LIGHTNING_ROD.get()
        );

        int registeredStates = 0;
        for (Block rod : rods) {
            for (BlockState state : rod.getStateDefinition().getPossibleStates()) {
                typeByState.put(state, lightningRodPoi);
                registeredStates++;
            }
        }

        Constants.LOG.info("Registered {} Copper Age lightning rod states with the vanilla lightning rod POI", registeredStates);
    }
}
