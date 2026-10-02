package com.github.smallinger.copperagebackport.loot;

import net.minecraft.resources.ResourceLocation;

/**
 * Finalized Copper Age copper-horse-armor loot entries.
 *
 * Vanilla adds the item to the first existing pool in each target chest table;
 * these are the exact entry weights used by the finalized 1.21.9 data.
 */
public final class CopperHorseArmorLoot {
    private CopperHorseArmorLoot() {
    }

    public static int getEntryWeight(ResourceLocation lootTableId) {
        String path = lootTableId.getPath();
        return switch (path) {
            case "chests/simple_dungeon" -> 15;
            case "chests/desert_pyramid" -> 15;
            case "chests/nether_bridge" -> 5;
            case "chests/jungle_temple",
                 "chests/stronghold_corridor",
                 "chests/end_city_treasure",
                 "chests/village/village_weaponsmith" -> 1;
            default -> 0;
        };
    }

    public static boolean shouldModifyLootTable(ResourceLocation lootTableId) {
        return lootTableId.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)
            && getEntryWeight(lootTableId) > 0;
    }
}
