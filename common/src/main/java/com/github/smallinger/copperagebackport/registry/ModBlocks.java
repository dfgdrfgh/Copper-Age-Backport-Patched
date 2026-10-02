package com.github.smallinger.copperagebackport.registry;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.ModSoundTypes;
import com.github.smallinger.copperagebackport.block.*;
import com.github.smallinger.copperagebackport.block.shelf.ShelfBlock;
import com.github.smallinger.copperagebackport.platform.Services;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;

import static net.minecraft.core.registries.Registries.BLOCK;

/**
 * Handles registration of all blocks for the mod.
 */
public class ModBlocks {
    
    // Copper Chest Blocks (Weathering)
    public static Supplier<WeatheringCopperChestBlock> COPPER_CHEST;
    public static Supplier<WeatheringCopperChestBlock> EXPOSED_COPPER_CHEST;
    public static Supplier<WeatheringCopperChestBlock> WEATHERED_COPPER_CHEST;
    public static Supplier<WeatheringCopperChestBlock> OXIDIZED_COPPER_CHEST;
    
    // Waxed Copper Chest Blocks
    public static Supplier<CopperChestBlock> WAXED_COPPER_CHEST;
    public static Supplier<CopperChestBlock> WAXED_EXPOSED_COPPER_CHEST;
    public static Supplier<CopperChestBlock> WAXED_WEATHERED_COPPER_CHEST;
    public static Supplier<CopperChestBlock> WAXED_OXIDIZED_COPPER_CHEST;
    
    // Copper Golem Statue Blocks (Weathering)
    public static Supplier<WeatheringCopperGolemStatueBlock> COPPER_GOLEM_STATUE;
    public static Supplier<WeatheringCopperGolemStatueBlock> EXPOSED_COPPER_GOLEM_STATUE;
    public static Supplier<WeatheringCopperGolemStatueBlock> WEATHERED_COPPER_GOLEM_STATUE;
    public static Supplier<WeatheringCopperGolemStatueBlock> OXIDIZED_COPPER_GOLEM_STATUE;
    
    // Waxed Copper Golem Statue Blocks
    public static Supplier<WaxedCopperGolemStatueBlock> WAXED_COPPER_GOLEM_STATUE;
    public static Supplier<WaxedCopperGolemStatueBlock> WAXED_EXPOSED_COPPER_GOLEM_STATUE;
    public static Supplier<WaxedCopperGolemStatueBlock> WAXED_WEATHERED_COPPER_GOLEM_STATUE;
    public static Supplier<WaxedCopperGolemStatueBlock> WAXED_OXIDIZED_COPPER_GOLEM_STATUE;
    
    // Shelf Blocks (all wood types)
    public static Supplier<ShelfBlock> OAK_SHELF;
    public static Supplier<ShelfBlock> SPRUCE_SHELF;
    public static Supplier<ShelfBlock> BIRCH_SHELF;
    public static Supplier<ShelfBlock> JUNGLE_SHELF;
    public static Supplier<ShelfBlock> ACACIA_SHELF;
    public static Supplier<ShelfBlock> DARK_OAK_SHELF;
    public static Supplier<ShelfBlock> MANGROVE_SHELF;
    public static Supplier<ShelfBlock> CHERRY_SHELF;
    public static Supplier<ShelfBlock> BAMBOO_SHELF;
    public static Supplier<ShelfBlock> CRIMSON_SHELF;
    public static Supplier<ShelfBlock> WARPED_SHELF;
    public static Supplier<ShelfBlock> PALE_OAK_SHELF; // Requires VanillaBackport for crafting
    
    // Copper Torch Blocks
    public static Supplier<CopperTorchBlock> COPPER_TORCH;
    public static Supplier<CopperWallTorchBlock> COPPER_WALL_TORCH;
    
    // Copper Lantern Blocks (Weathering)
    public static Supplier<WeatheringCopperLanternBlock> COPPER_LANTERN;
    public static Supplier<WeatheringCopperLanternBlock> EXPOSED_COPPER_LANTERN;
    public static Supplier<WeatheringCopperLanternBlock> WEATHERED_COPPER_LANTERN;
    public static Supplier<WeatheringCopperLanternBlock> OXIDIZED_COPPER_LANTERN;
    
    // Waxed Copper Lantern Blocks
    public static Supplier<CopperLanternBlock> WAXED_COPPER_LANTERN;
    public static Supplier<CopperLanternBlock> WAXED_EXPOSED_COPPER_LANTERN;
    public static Supplier<CopperLanternBlock> WAXED_WEATHERED_COPPER_LANTERN;
    public static Supplier<CopperLanternBlock> WAXED_OXIDIZED_COPPER_LANTERN;
    
    // Copper Chain Blocks (Weathering)
    public static Supplier<WeatheringCopperChainBlock> COPPER_CHAIN;
    public static Supplier<WeatheringCopperChainBlock> EXPOSED_COPPER_CHAIN;
    public static Supplier<WeatheringCopperChainBlock> WEATHERED_COPPER_CHAIN;
    public static Supplier<WeatheringCopperChainBlock> OXIDIZED_COPPER_CHAIN;
    
    // Waxed Copper Chain Blocks
    public static Supplier<CopperChainBlock> WAXED_COPPER_CHAIN;
    public static Supplier<CopperChainBlock> WAXED_EXPOSED_COPPER_CHAIN;
    public static Supplier<CopperChainBlock> WAXED_WEATHERED_COPPER_CHAIN;
    public static Supplier<CopperChainBlock> WAXED_OXIDIZED_COPPER_CHAIN;
    
    // Copper Bars Blocks (Weathering)
    public static Supplier<WeatheringCopperBarsBlock> COPPER_BARS;
    public static Supplier<WeatheringCopperBarsBlock> EXPOSED_COPPER_BARS;
    public static Supplier<WeatheringCopperBarsBlock> WEATHERED_COPPER_BARS;
    public static Supplier<WeatheringCopperBarsBlock> OXIDIZED_COPPER_BARS;
    
    // Waxed Copper Bars Blocks
    public static Supplier<CopperBarsBlock> WAXED_COPPER_BARS;
    public static Supplier<CopperBarsBlock> WAXED_EXPOSED_COPPER_BARS;
    public static Supplier<CopperBarsBlock> WAXED_WEATHERED_COPPER_BARS;
    public static Supplier<CopperBarsBlock> WAXED_OXIDIZED_COPPER_BARS;
    
    // Lightning Rod Blocks (Weathering) - vanilla lightning rod is extended via Mixin (LightningRodBlockMixin)
    // These are the new oxidized variants that don't exist in vanilla 1.21.1
    public static Supplier<WeatheringCopperLightningRodBlock> EXPOSED_LIGHTNING_ROD;
    public static Supplier<WeatheringCopperLightningRodBlock> WEATHERED_LIGHTNING_ROD;
    public static Supplier<WeatheringCopperLightningRodBlock> OXIDIZED_LIGHTNING_ROD;
    
    // Waxed Lightning Rod Blocks
    public static Supplier<WaxedCopperLightningRodBlock> WAXED_LIGHTNING_ROD;
    public static Supplier<WaxedCopperLightningRodBlock> WAXED_EXPOSED_LIGHTNING_ROD;
    public static Supplier<WaxedCopperLightningRodBlock> WAXED_WEATHERED_LIGHTNING_ROD;
    public static Supplier<WaxedCopperLightningRodBlock> WAXED_OXIDIZED_LIGHTNING_ROD;
    
    public static void register() {
        Constants.LOG.info("Registering blocks for {}", Constants.MOD_NAME);
        
        RegistryHelper helper = RegistryHelper.getInstance();
        
        // Register Copper Chest Blocks
        COPPER_CHEST = helper.registerAuto(BLOCK, "copper_chest",
            () -> new WeatheringCopperChestBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        EXPOSED_COPPER_CHEST = helper.registerAuto(BLOCK, "exposed_copper_chest",
            () -> new WeatheringCopperChestBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        WEATHERED_COPPER_CHEST = helper.registerAuto(BLOCK, "weathered_copper_chest",
            () -> new WeatheringCopperChestBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        OXIDIZED_COPPER_CHEST = helper.registerAuto(BLOCK, "oxidized_copper_chest",
            () -> new WeatheringCopperChestBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        // Register Waxed Copper Chest Blocks
        WAXED_COPPER_CHEST = helper.registerAuto(BLOCK, "waxed_copper_chest",
            () -> new CopperChestBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()));
        
        WAXED_EXPOSED_COPPER_CHEST = helper.registerAuto(BLOCK, "waxed_exposed_copper_chest",
            () -> new CopperChestBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()));
        
        WAXED_WEATHERED_COPPER_CHEST = helper.registerAuto(BLOCK, "waxed_weathered_copper_chest",
            () -> new CopperChestBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()));
        
        WAXED_OXIDIZED_COPPER_CHEST = helper.registerAuto(BLOCK, "waxed_oxidized_copper_chest",
            () -> new CopperChestBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()));
        
        // Register Copper Golem Statue Blocks
        COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "copper_golem_statue",
            () -> new WeatheringCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .randomTicks()
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        EXPOSED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "exposed_copper_golem_statue",
            () -> new WeatheringCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .randomTicks()
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WEATHERED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "weathered_copper_golem_statue",
            () -> new WeatheringCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .randomTicks()
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        OXIDIZED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "oxidized_copper_golem_statue",
            () -> new WeatheringCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .randomTicks()
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        // Register Waxed Copper Golem Statue Blocks
        WAXED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "waxed_copper_golem_statue",
            () -> new WaxedCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_EXPOSED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "waxed_exposed_copper_golem_statue",
            () -> new WaxedCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_WEATHERED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "waxed_weathered_copper_golem_statue",
            () -> new WaxedCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_OXIDIZED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "waxed_oxidized_copper_golem_statue",
            () -> new WaxedCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        // Register Shelf Blocks
        OAK_SHELF = helper.registerAuto(BLOCK, "oak_shelf", () -> new ShelfBlock(shelfProperties(Blocks.OAK_PLANKS.defaultMapColor())));
        SPRUCE_SHELF = helper.registerAuto(BLOCK, "spruce_shelf", () -> new ShelfBlock(shelfProperties(Blocks.SPRUCE_LOG.defaultMapColor())));
        BIRCH_SHELF = helper.registerAuto(BLOCK, "birch_shelf", () -> new ShelfBlock(shelfProperties(Blocks.BIRCH_PLANKS.defaultMapColor())));
        JUNGLE_SHELF = helper.registerAuto(BLOCK, "jungle_shelf", () -> new ShelfBlock(shelfProperties(Blocks.JUNGLE_LOG.defaultMapColor())));
        ACACIA_SHELF = helper.registerAuto(BLOCK, "acacia_shelf", () -> new ShelfBlock(shelfProperties(Blocks.ACACIA_PLANKS.defaultMapColor())));
        DARK_OAK_SHELF = helper.registerAuto(BLOCK, "dark_oak_shelf", () -> new ShelfBlock(shelfProperties(Blocks.DARK_OAK_LOG.defaultMapColor())));
        MANGROVE_SHELF = helper.registerAuto(BLOCK, "mangrove_shelf", () -> new ShelfBlock(shelfProperties(Blocks.MANGROVE_LOG.defaultMapColor())));
        CHERRY_SHELF = helper.registerAuto(BLOCK, "cherry_shelf", () -> new ShelfBlock(shelfProperties(Blocks.CHERRY_PLANKS.defaultMapColor())));
        BAMBOO_SHELF = helper.registerAuto(BLOCK, "bamboo_shelf", () -> new ShelfBlock(shelfProperties(Blocks.BAMBOO_PLANKS.defaultMapColor())));
        CRIMSON_SHELF = helper.registerAuto(BLOCK, "crimson_shelf", () -> new ShelfBlock(shelfProperties(MapColor.CRIMSON_STEM)));
        WARPED_SHELF = helper.registerAuto(BLOCK, "warped_shelf", () -> new ShelfBlock(shelfProperties(MapColor.WARPED_STEM)));
        PALE_OAK_SHELF = helper.registerAuto(BLOCK, "pale_oak_shelf", () -> new ShelfBlock(shelfProperties(Blocks.PALE_OAK_PLANKS.defaultMapColor())));
        
        // Register Copper Torch Blocks
        COPPER_TORCH = helper.registerAuto(BLOCK, "copper_torch",
            () -> new CopperTorchBlock(
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .lightLevel(p -> 14)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY)));
        
        COPPER_WALL_TORCH = helper.registerAuto(BLOCK, "copper_wall_torch",
            () -> new CopperWallTorchBlock(
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .lightLevel(p -> 14)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY)
                    .dropsLike(COPPER_TORCH.get())));
        
        // Register Copper Lantern Blocks (Weathering)
        COPPER_LANTERN = helper.registerAuto(BLOCK, "copper_lantern",
            () -> new WeatheringCopperLanternBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(state -> 15)
                    .noOcclusion()
                    .forceSolidOn()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        EXPOSED_COPPER_LANTERN = helper.registerAuto(BLOCK, "exposed_copper_lantern",
            () -> new WeatheringCopperLanternBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(state -> 15)
                    .noOcclusion()
                    .forceSolidOn()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        WEATHERED_COPPER_LANTERN = helper.registerAuto(BLOCK, "weathered_copper_lantern",
            () -> new WeatheringCopperLanternBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(state -> 15)
                    .noOcclusion()
                    .forceSolidOn()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        OXIDIZED_COPPER_LANTERN = helper.registerAuto(BLOCK, "oxidized_copper_lantern",
            () -> new WeatheringCopperLanternBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(state -> 15)
                    .noOcclusion()
                    .forceSolidOn()
                    .pushReaction(PushReaction.DESTROY)));
        
        // Register Waxed Copper Lantern Blocks
        WAXED_COPPER_LANTERN = helper.registerAuto(BLOCK, "waxed_copper_lantern",
            () -> new CopperLanternBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(state -> 15)
                    .noOcclusion()
                    .forceSolidOn()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_EXPOSED_COPPER_LANTERN = helper.registerAuto(BLOCK, "waxed_exposed_copper_lantern",
            () -> new CopperLanternBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(state -> 15)
                    .noOcclusion()
                    .forceSolidOn()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_WEATHERED_COPPER_LANTERN = helper.registerAuto(BLOCK, "waxed_weathered_copper_lantern",
            () -> new CopperLanternBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(state -> 15)
                    .noOcclusion()
                    .forceSolidOn()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_OXIDIZED_COPPER_LANTERN = helper.registerAuto(BLOCK, "waxed_oxidized_copper_lantern",
            () -> new CopperLanternBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(state -> 15)
                    .noOcclusion()
                    .forceSolidOn()
                    .pushReaction(PushReaction.DESTROY)));
        
        // Register Copper Chain Blocks (Weathering)
        COPPER_CHAIN = helper.registerAuto(BLOCK, "copper_chain",
            () -> new WeatheringCopperChainBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .forceSolidOn()
                    .randomTicks()));
        
        EXPOSED_COPPER_CHAIN = helper.registerAuto(BLOCK, "exposed_copper_chain",
            () -> new WeatheringCopperChainBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .forceSolidOn()
                    .randomTicks()));
        
        WEATHERED_COPPER_CHAIN = helper.registerAuto(BLOCK, "weathered_copper_chain",
            () -> new WeatheringCopperChainBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .forceSolidOn()
                    .randomTicks()));
        
        OXIDIZED_COPPER_CHAIN = helper.registerAuto(BLOCK, "oxidized_copper_chain",
            () -> new WeatheringCopperChainBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .forceSolidOn()));
        
        // Register Waxed Copper Chain Blocks
        WAXED_COPPER_CHAIN = helper.registerAuto(BLOCK, "waxed_copper_chain",
            () -> new CopperChainBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .forceSolidOn()));
        
        WAXED_EXPOSED_COPPER_CHAIN = helper.registerAuto(BLOCK, "waxed_exposed_copper_chain",
            () -> new CopperChainBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .forceSolidOn()));
        
        WAXED_WEATHERED_COPPER_CHAIN = helper.registerAuto(BLOCK, "waxed_weathered_copper_chain",
            () -> new CopperChainBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .forceSolidOn()));
        
        WAXED_OXIDIZED_COPPER_CHAIN = helper.registerAuto(BLOCK, "waxed_oxidized_copper_chain",
            () -> new CopperChainBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .forceSolidOn()));
        
        // Register Copper Bars Blocks (Weathering)
        COPPER_BARS = helper.registerAuto(BLOCK, "copper_bars",
            () -> new WeatheringCopperBarsBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        EXPOSED_COPPER_BARS = helper.registerAuto(BLOCK, "exposed_copper_bars",
            () -> new WeatheringCopperBarsBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        WEATHERED_COPPER_BARS = helper.registerAuto(BLOCK, "weathered_copper_bars",
            () -> new WeatheringCopperBarsBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        OXIDIZED_COPPER_BARS = helper.registerAuto(BLOCK, "oxidized_copper_bars",
            () -> new WeatheringCopperBarsBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()));
        
        // Register Waxed Copper Bars Blocks
        WAXED_COPPER_BARS = helper.registerAuto(BLOCK, "waxed_copper_bars",
            () -> new CopperBarsBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()));
        
        WAXED_EXPOSED_COPPER_BARS = helper.registerAuto(BLOCK, "waxed_exposed_copper_bars",
            () -> new CopperBarsBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()));
        
        WAXED_WEATHERED_COPPER_BARS = helper.registerAuto(BLOCK, "waxed_weathered_copper_bars",
            () -> new CopperBarsBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()));
        
        WAXED_OXIDIZED_COPPER_BARS = helper.registerAuto(BLOCK, "waxed_oxidized_copper_bars",
            () -> new CopperBarsBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()));
        
        // Register Lightning Rod Blocks (Weathering)
        // Vanilla minecraft:lightning_rod is extended via Mixin (LightningRodBlockMixin) to add weathering
        // These new oxidized variants are registered under minecraft: namespace
        EXPOSED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "exposed_lightning_rod",
            () -> new WeatheringCopperLightningRodBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        WEATHERED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "weathered_lightning_rod",
            () -> new WeatheringCopperLightningRodBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        OXIDIZED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "oxidized_lightning_rod",
            () -> new WeatheringCopperLightningRodBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()));
        
        // Register Waxed Lightning Rod Blocks
        WAXED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "waxed_lightning_rod",
            () -> new WaxedCopperLightningRodBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()));
        
        WAXED_EXPOSED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "waxed_exposed_lightning_rod",
            () -> new WaxedCopperLightningRodBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()));
        
        WAXED_WEATHERED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "waxed_weathered_lightning_rod",
            () -> new WaxedCopperLightningRodBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()));
        
        WAXED_OXIDIZED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "waxed_oxidized_lightning_rod",
            () -> new WaxedCopperLightningRodBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()));
        
    }
    
    private static BlockBehaviour.Properties shelfProperties(MapColor mapColor) {
        return BlockBehaviour.Properties.of()
            .mapColor(mapColor)
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.0F, 3.0F)
            // Finalized vanilla has a dedicated SHELF sound type; the current
            // backport assets do not yet contain its break/step/place/hit/fall files.
            .sound(SoundType.WOOD)
            .ignitedByLava();
    }

}
