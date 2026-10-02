package com.github.smallinger.copperagebackport;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> COPPER = tag("copper");
        public static final TagKey<Block> COPPER_CHESTS = tag("copper_chests");
        public static final TagKey<Block> COPPER_GOLEM_STATUES = tag("copper_golem_statues");
        public static final TagKey<Block> INCORRECT_FOR_COPPER_TOOL = tag("incorrect_for_copper_tool");
        public static final TagKey<Block> WOODEN_SHELVES = tag("wooden_shelves");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", name));
        }
    }

    public static class Items {
        public static final TagKey<Item> SHEARABLE_FROM_COPPER_GOLEM = tag("shearable_from_copper_golem");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", name));
        }
    }
}
