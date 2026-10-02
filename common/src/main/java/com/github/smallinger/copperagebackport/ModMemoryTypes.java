package com.github.smallinger.copperagebackport;

import com.github.smallinger.copperagebackport.registry.RegistryHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Custom memory modules needed to reproduce the Copper Golem's vanilla item-sorting brain.
 */
public class ModMemoryTypes {
    public static Supplier<MemoryModuleType<Integer>> TRANSPORT_ITEMS_COOLDOWN_TICKS;
    public static Supplier<MemoryModuleType<Set<GlobalPos>>> VISITED_BLOCK_POSITIONS;
    public static Supplier<MemoryModuleType<Set<GlobalPos>>> UNREACHABLE_TRANSPORT_BLOCK_POSITIONS;
    public static Supplier<MemoryModuleType<Integer>> GAZE_COOLDOWN_TICKS;

    public static void register() {
        Constants.LOG.info("Registering memory module types for {}", Constants.MOD_NAME);
        RegistryHelper helper = RegistryHelper.getInstance();

        TRANSPORT_ITEMS_COOLDOWN_TICKS = helper.register(
            Registries.MEMORY_MODULE_TYPE,
            "transport_items_cooldown_ticks",
            () -> new MemoryModuleType<>(Optional.of(Codec.INT))
        );

        VISITED_BLOCK_POSITIONS = helper.register(
            Registries.MEMORY_MODULE_TYPE,
            "visited_block_positions",
            () -> new MemoryModuleType<>(Optional.of(
                GlobalPos.CODEC.listOf().xmap(Sets::newHashSet, Lists::newArrayList)
            ))
        );

        UNREACHABLE_TRANSPORT_BLOCK_POSITIONS = helper.register(
            Registries.MEMORY_MODULE_TYPE,
            "unreachable_transport_block_positions",
            () -> new MemoryModuleType<>(Optional.of(
                GlobalPos.CODEC.listOf().xmap(Sets::newHashSet, Lists::newArrayList)
            ))
        );

        GAZE_COOLDOWN_TICKS = helper.register(
            Registries.MEMORY_MODULE_TYPE,
            "gaze_cooldown_ticks",
            () -> new MemoryModuleType<>(Optional.of(Codec.INT))
        );
    }
}
