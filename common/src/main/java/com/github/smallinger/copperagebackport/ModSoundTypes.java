package com.github.smallinger.copperagebackport;

import net.minecraft.world.level.block.SoundType;

public class ModSoundTypes {
    public static final SoundType COPPER_STATUE = new SoundType(
        1.0F,
        1.0F,
        ModSounds.COPPER_STATUE_BREAK.get(),
        ModSounds.COPPER_STATUE_STEP.get(),
        ModSounds.COPPER_STATUE_PLACE.get(),
        ModSounds.COPPER_STATUE_HIT.get(),
        ModSounds.COPPER_STATUE_FALL.get()
    );

    public static final SoundType SHELF = new SoundType(
        1.0F,
        1.0F,
        ModSounds.SHELF_BREAK.get(),
        ModSounds.SHELF_STEP.get(),
        ModSounds.SHELF_PLACE.get(),
        ModSounds.SHELF_HIT.get(),
        ModSounds.SHELF_FALL.get()
    );
}
