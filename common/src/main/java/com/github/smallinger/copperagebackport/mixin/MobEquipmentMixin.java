package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.registry.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Backports the finalized Copper Age armor selection used by naturally geared mobs.
 *
 * Minecraft 1.21.9 changes the initial equipment tier roll from two choices
 * (Leather/Gold) to three (Leather/Copper/Gold), and raises each tier-up roll
 * from 9.5% to 10.87%. The remaining vanilla 1.21.1 spawn logic is preserved.
 */
@Mixin(Mob.class)
public abstract class MobEquipmentMixin {

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("HEAD"), cancellable = true)
    private void copperagebackport$populateCopperAgeEquipment(
        RandomSource random,
        DifficultyInstance difficulty,
        CallbackInfo ci
    ) {
        Mob self = (Mob) (Object) this;

        if (random.nextFloat() < 0.15F * difficulty.getSpecialMultiplier()) {
            int equipmentTier = random.nextInt(3);
            float partialSetChance = self.level().getDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;

            if (random.nextFloat() < 0.1087F) {
                ++equipmentTier;
            }
            if (random.nextFloat() < 0.1087F) {
                ++equipmentTier;
            }
            if (random.nextFloat() < 0.1087F) {
                ++equipmentTier;
            }

            EquipmentSlot[] armorSlots = {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
            };

            boolean firstSlot = true;
            for (EquipmentSlot slot : armorSlots) {
                if (!firstSlot && random.nextFloat() < partialSetChance) {
                    break;
                }
                firstSlot = false;

                if (self.getItemBySlot(slot).isEmpty()) {
                    Item equipment = copperagebackport$getEquipmentForSlot(slot, equipmentTier);
                    if (equipment != null) {
                        self.setItemSlot(slot, new ItemStack(equipment));
                    }
                }
            }
        }

        ci.cancel();
    }

    @Unique
    private static Item copperagebackport$getEquipmentForSlot(EquipmentSlot slot, int tier) {
        return switch (tier) {
            case 0 -> copperagebackport$getArmorPiece(slot,
                Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS);
            case 1 -> copperagebackport$getArmorPiece(slot,
                ModItems.COPPER_HELMET.get(), ModItems.COPPER_CHESTPLATE.get(),
                ModItems.COPPER_LEGGINGS.get(), ModItems.COPPER_BOOTS.get());
            case 2 -> copperagebackport$getArmorPiece(slot,
                Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS);
            case 3 -> copperagebackport$getArmorPiece(slot,
                Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS);
            case 4 -> copperagebackport$getArmorPiece(slot,
                Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
            case 5 -> copperagebackport$getArmorPiece(slot,
                Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS);
            default -> null;
        };
    }

    @Unique
    private static Item copperagebackport$getArmorPiece(
        EquipmentSlot slot,
        Item helmet,
        Item chestplate,
        Item leggings,
        Item boots
    ) {
        return switch (slot) {
            case HEAD -> helmet;
            case CHEST -> chestplate;
            case LEGS -> leggings;
            case FEET -> boots;
            default -> null;
        };
    }
}
