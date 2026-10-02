package com.github.smallinger.copperagebackport.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

/**
 * Copper Horse Armor with modern vanilla horse-armor equip audio.
 *
 * Minecraft 1.21.1's AnimalArmorItem inherits the armor material's equip
 * sound, which would make Copper Horse Armor use item.armor.equip_copper.
 * Modern vanilla horse armor instead uses entity.horse.armor regardless of
 * material, so override the equip sound for parity.
 */
public class CopperHorseArmorItem extends AnimalArmorItem {

    public CopperHorseArmorItem(Holder<ArmorMaterial> material, Item.Properties properties) {
        super(material, BodyType.EQUESTRIAN, false, properties);
    }

    @Override
    public Holder<SoundEvent> getEquipSound() {
        return Holder.direct(SoundEvents.HORSE_ARMOR);
    }
}
