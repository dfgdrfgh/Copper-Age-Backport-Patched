package com.github.smallinger.copperagebackport;

import com.github.smallinger.copperagebackport.registry.RegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class ModSounds {

    // Copper Golem sounds - Unaffected/Exposed share the finalized regular set.
    public static Supplier<SoundEvent> COPPER_GOLEM_DEATH_UNAFFECTED;
    public static Supplier<SoundEvent> COPPER_GOLEM_HURT_UNAFFECTED;
    public static Supplier<SoundEvent> COPPER_GOLEM_STEP_UNAFFECTED;
    public static Supplier<SoundEvent> COPPER_GOLEM_HEAD_SPIN_UNAFFECTED;

    public static Supplier<SoundEvent> COPPER_GOLEM_DEATH_EXPOSED;
    public static Supplier<SoundEvent> COPPER_GOLEM_HURT_EXPOSED;
    public static Supplier<SoundEvent> COPPER_GOLEM_STEP_EXPOSED;
    public static Supplier<SoundEvent> COPPER_GOLEM_HEAD_SPIN_EXPOSED;

    // Copper Golem sounds - Weathered
    public static Supplier<SoundEvent> COPPER_GOLEM_DEATH_WEATHERED;
    public static Supplier<SoundEvent> COPPER_GOLEM_HURT_WEATHERED;
    public static Supplier<SoundEvent> COPPER_GOLEM_STEP_WEATHERED;
    public static Supplier<SoundEvent> COPPER_GOLEM_HEAD_SPIN_WEATHERED;

    // Copper Golem sounds - Oxidized
    public static Supplier<SoundEvent> COPPER_GOLEM_DEATH_OXIDIZED;
    public static Supplier<SoundEvent> COPPER_GOLEM_HURT_OXIDIZED;
    public static Supplier<SoundEvent> COPPER_GOLEM_STEP_OXIDIZED;
    public static Supplier<SoundEvent> COPPER_GOLEM_HEAD_SPIN_OXIDIZED;

    // Copper Golem sounds - General
    public static Supplier<SoundEvent> COPPER_GOLEM_SPAWN;
    public static Supplier<SoundEvent> COPPER_GOLEM_BECOME_STATUE;
    public static Supplier<SoundEvent> COPPER_GOLEM_SHEAR;

    // Copper Golem sounds - Item Interaction
    public static Supplier<SoundEvent> COPPER_GOLEM_ITEM_DROP;
    public static Supplier<SoundEvent> COPPER_GOLEM_ITEM_NO_DROP;
    public static Supplier<SoundEvent> COPPER_GOLEM_ITEM_GET;
    public static Supplier<SoundEvent> COPPER_GOLEM_ITEM_NO_GET;

    // Copper Chest sounds
    public static Supplier<SoundEvent> COPPER_CHEST_CLOSE;
    public static Supplier<SoundEvent> COPPER_CHEST_OPEN;
    public static Supplier<SoundEvent> COPPER_CHEST_WEATHERED_CLOSE;
    public static Supplier<SoundEvent> COPPER_CHEST_WEATHERED_OPEN;
    public static Supplier<SoundEvent> COPPER_CHEST_OXIDIZED_CLOSE;
    public static Supplier<SoundEvent> COPPER_CHEST_OXIDIZED_OPEN;

    // Copper Golem Statue sounds
    public static Supplier<SoundEvent> COPPER_STATUE_BREAK;
    public static Supplier<SoundEvent> COPPER_STATUE_STEP;
    public static Supplier<SoundEvent> COPPER_STATUE_PLACE;
    public static Supplier<SoundEvent> COPPER_STATUE_HIT;
    public static Supplier<SoundEvent> COPPER_STATUE_FALL;

    // Shelf interaction sounds
    public static Supplier<SoundEvent> SHELF_ACTIVATE;
    public static Supplier<SoundEvent> SHELF_DEACTIVATE;
    public static Supplier<SoundEvent> SHELF_PLACE_ITEM;
    public static Supplier<SoundEvent> SHELF_TAKE_ITEM;
    public static Supplier<SoundEvent> SHELF_SINGLE_SWAP;
    public static Supplier<SoundEvent> SHELF_MULTI_SWAP;

    // Shelf block sound type events
    public static Supplier<SoundEvent> SHELF_BREAK;
    public static Supplier<SoundEvent> SHELF_STEP;
    public static Supplier<SoundEvent> SHELF_PLACE;
    public static Supplier<SoundEvent> SHELF_HIT;
    public static Supplier<SoundEvent> SHELF_FALL;

    // Armor sounds
    public static Supplier<SoundEvent> ARMOR_EQUIP_COPPER;

    // Weather sounds (End Flash)
    public static Supplier<SoundEvent> WEATHER_END_FLASH;

    public static void register() {
        Constants.LOG.info("Registering sounds for {}", Constants.MOD_NAME);

        RegistryHelper helper = RegistryHelper.getInstance();

        // Finalized 1.21.9 regular Copper Golem events.
        COPPER_GOLEM_DEATH_UNAFFECTED = registerSound(helper, "entity.copper_golem.death");
        COPPER_GOLEM_HURT_UNAFFECTED = registerSound(helper, "entity.copper_golem.hurt");
        COPPER_GOLEM_STEP_UNAFFECTED = registerSound(helper, "entity.copper_golem.step");
        COPPER_GOLEM_HEAD_SPIN_UNAFFECTED = registerSound(helper, "entity.copper_golem.spin");

        // Exposed Copper Golems intentionally reuse the regular sound events.
        COPPER_GOLEM_DEATH_EXPOSED = COPPER_GOLEM_DEATH_UNAFFECTED;
        COPPER_GOLEM_HURT_EXPOSED = COPPER_GOLEM_HURT_UNAFFECTED;
        COPPER_GOLEM_STEP_EXPOSED = COPPER_GOLEM_STEP_UNAFFECTED;
        COPPER_GOLEM_HEAD_SPIN_EXPOSED = COPPER_GOLEM_HEAD_SPIN_UNAFFECTED;

        COPPER_GOLEM_DEATH_WEATHERED = registerSound(helper, "entity.copper_golem_weathered.death");
        COPPER_GOLEM_HURT_WEATHERED = registerSound(helper, "entity.copper_golem_weathered.hurt");
        COPPER_GOLEM_STEP_WEATHERED = registerSound(helper, "entity.copper_golem_weathered.step");
        COPPER_GOLEM_HEAD_SPIN_WEATHERED = registerSound(helper, "entity.copper_golem_weathered.spin");

        COPPER_GOLEM_DEATH_OXIDIZED = registerSound(helper, "entity.copper_golem_oxidized.death");
        COPPER_GOLEM_HURT_OXIDIZED = registerSound(helper, "entity.copper_golem_oxidized.hurt");
        COPPER_GOLEM_STEP_OXIDIZED = registerSound(helper, "entity.copper_golem_oxidized.step");
        COPPER_GOLEM_HEAD_SPIN_OXIDIZED = registerSound(helper, "entity.copper_golem_oxidized.spin");

        COPPER_GOLEM_SPAWN = registerSound(helper, "entity.copper_golem.spawn");
        COPPER_GOLEM_BECOME_STATUE = registerSound(helper, "entity.copper_golem_become_statue");
        COPPER_GOLEM_SHEAR = registerSound(helper, "entity.copper_golem.shear");

        COPPER_GOLEM_ITEM_DROP = registerSound(helper, "entity.copper_golem.item_drop");
        COPPER_GOLEM_ITEM_NO_DROP = registerSound(helper, "entity.copper_golem.item_no_drop");
        COPPER_GOLEM_ITEM_GET = registerSound(helper, "entity.copper_golem.no_item_get");
        COPPER_GOLEM_ITEM_NO_GET = registerSound(helper, "entity.copper_golem.no_item_no_get");

        COPPER_CHEST_CLOSE = registerSound(helper, "block.copper_chest.close");
        COPPER_CHEST_OPEN = registerSound(helper, "block.copper_chest.open");
        COPPER_CHEST_WEATHERED_CLOSE = registerSound(helper, "block.copper_chest_weathered.close");
        COPPER_CHEST_WEATHERED_OPEN = registerSound(helper, "block.copper_chest_weathered.open");
        COPPER_CHEST_OXIDIZED_CLOSE = registerSound(helper, "block.copper_chest_oxidized.close");
        COPPER_CHEST_OXIDIZED_OPEN = registerSound(helper, "block.copper_chest_oxidized.open");

        COPPER_STATUE_BREAK = registerSound(helper, "block.copper_golem_statue.break");
        COPPER_STATUE_STEP = registerSound(helper, "block.copper_golem_statue.step");
        COPPER_STATUE_PLACE = registerSound(helper, "block.copper_golem_statue.place");
        COPPER_STATUE_HIT = registerSound(helper, "block.copper_golem_statue.hit");
        COPPER_STATUE_FALL = registerSound(helper, "block.copper_golem_statue.fall");

        SHELF_ACTIVATE = registerSound(helper, "block.shelf.activate");
        SHELF_DEACTIVATE = registerSound(helper, "block.shelf.deactivate");
        SHELF_PLACE_ITEM = registerSound(helper, "block.shelf.place_item");
        SHELF_TAKE_ITEM = registerSound(helper, "block.shelf.take_item");
        SHELF_SINGLE_SWAP = registerSound(helper, "block.shelf.single_swap");
        SHELF_MULTI_SWAP = registerSound(helper, "block.shelf.multi_swap");

        SHELF_BREAK = registerSound(helper, "block.shelf.break");
        SHELF_STEP = registerSound(helper, "block.shelf.step");
        SHELF_PLACE = registerSound(helper, "block.shelf.place");
        SHELF_HIT = registerSound(helper, "block.shelf.hit");
        SHELF_FALL = registerSound(helper, "block.shelf.fall");

        ARMOR_EQUIP_COPPER = registerSound(helper, "item.armor.equip_copper");
        WEATHER_END_FLASH = registerSound(helper, "weather.end_flash");
    }

    private static Supplier<SoundEvent> registerSound(RegistryHelper helper, String name) {
        ResourceLocation id = ResourceLocation.withDefaultNamespace(name);
        return helper.registerAuto(Registries.SOUND_EVENT, name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
