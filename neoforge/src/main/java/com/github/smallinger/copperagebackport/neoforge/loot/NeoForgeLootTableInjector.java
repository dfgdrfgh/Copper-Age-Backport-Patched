package com.github.smallinger.copperagebackport.neoforge.loot;

import com.github.smallinger.copperagebackport.loot.CopperHorseArmorLoot;
import com.github.smallinger.copperagebackport.mixin.LootPoolAccessor;
import com.github.smallinger.copperagebackport.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * NeoForge equivalent of vanilla's finalized Copper Horse Armor loot-table
 * entries. The entry is appended directly to pool 0 before loot tables freeze.
 */
public final class NeoForgeLootTableInjector {
    private NeoForgeLootTableInjector() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(NeoForgeLootTableInjector::onLootTableLoad);
    }

    private static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation lootTableId = event.getName();
        int weight = CopperHorseArmorLoot.getEntryWeight(lootTableId);
        if (weight <= 0) {
            return;
        }

        // All seven finalized vanilla target tables have multiple pools, so
        // NeoForge names the first unnamed pool "pool0". Keep "main" as a
        // defensive fallback in case another data pack reduces the table to one.
        LootPool firstPool = event.getTable().getPool("pool0");
        if (firstPool == null) {
            firstPool = event.getTable().getPool("main");
        }
        if (firstPool == null) {
            return;
        }

        LootPoolAccessor accessor = (LootPoolAccessor) (Object) firstPool;
        List<LootPoolEntryContainer> entries = new ArrayList<>(accessor.copperagebackport$getEntries());
        entries.add(
            LootItem.lootTableItem(ModItems.COPPER_HORSE_ARMOR.get())
                .setWeight(weight)
                .build()
        );
        accessor.copperagebackport$setEntries(List.copyOf(entries));
    }
}
