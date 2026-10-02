package com.github.smallinger.copperagebackport.util;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Backports the relevant parts of HoneycombItem and AxeItem for Copper Age
 * blocks whose mappings do not exist in Minecraft 1.21.1.
 */
public final class CopperInteractionHelper {
    private CopperInteractionHelper() {
    }

    public static boolean shouldCancelAxeUse(Player player, InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND
            && player.getOffhandItem().is(Items.SHIELD)
            && !player.isSecondaryUseActive();
    }

    public static void wax(
        Level level,
        BlockPos pos,
        BlockState waxedState,
        Player player,
        ItemStack stack,
        @Nullable BlockPos connectedEffectPos
    ) {
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
        }

        stack.shrink(1);
        level.setBlock(pos, waxedState, 11);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, waxedState));
        // Event 3003 already includes the vanilla honeycomb wax-on sound.
        level.levelEvent(player, 3003, pos, 0);

        if (connectedEffectPos != null) {
            level.gameEvent(
                GameEvent.BLOCK_CHANGE,
                connectedEffectPos,
                GameEvent.Context.of(player, level.getBlockState(connectedEffectPos))
            );
            level.levelEvent(player, 3003, connectedEffectPos, 0);
        }
    }

    public static void axeTransform(
        Level level,
        BlockPos pos,
        BlockState newState,
        Player player,
        InteractionHand hand,
        ItemStack stack,
        SoundEvent sound,
        int levelEvent,
        @Nullable BlockPos connectedEffectPos
    ) {
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.levelEvent(player, levelEvent, pos, 0);

        // Finalized vanilla mirrors the visual/game event to the other half of
        // a double Copper Chest, but not the axe sound or durability cost.
        if (connectedEffectPos != null) {
            level.gameEvent(
                GameEvent.BLOCK_CHANGE,
                connectedEffectPos,
                GameEvent.Context.of(player, level.getBlockState(connectedEffectPos))
            );
            level.levelEvent(player, levelEvent, connectedEffectPos, 0);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
        }

        level.setBlock(pos, newState, 11);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
    }
}
