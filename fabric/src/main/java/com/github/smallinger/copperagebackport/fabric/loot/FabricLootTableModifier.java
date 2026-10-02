package com.github.smallinger.copperagebackport.fabric.loot;

import com.github.smallinger.copperagebackport.loot.CopperHorseArmorLoot;
import com.github.smallinger.copperagebackport.registry.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Injects Copper Horse Armor into the same existing multi-roll loot pool that
 * finalized vanilla 1.21.9 uses, preserving vanilla roll behavior and weights.
 */
public final class FabricLootTableModifier {
    private FabricLootTableModifier() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) {
                return;
            }

            ResourceLocation lootTableId = key.location();
            int weight = CopperHorseArmorLoot.getEntryWeight(lootTableId);
            if (weight <= 0) {
                return;
            }

            AtomicBoolean firstPool = new AtomicBoolean(true);
            tableBuilder.modifyPools(pool -> {
                if (firstPool.getAndSet(false)) {
                    pool.add(
                        LootItem.lootTableItem(ModItems.COPPER_HORSE_ARMOR.get())
                            .setWeight(weight)
                    );
                }
            });
        });
    }
}
