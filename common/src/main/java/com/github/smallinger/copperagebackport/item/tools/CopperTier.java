package com.github.smallinger.copperagebackport.item.tools;

import com.github.smallinger.copperagebackport.ModTags;
import com.google.common.base.Suppliers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * Copper tool tier with stats between Stone and Iron.
 * Based on Minecraft 1.21.10 ToolMaterial.COPPER:
 * - Durability: 190 (Stone: 131, Iron: 250)
 * - Speed: 5.0 (Stone: 4.0, Iron: 6.0)
 * - Attack Damage Bonus: 1.0 (Stone: 1.0, Iron: 2.0)
 * - Enchantment Value: 13 (Stone: 5, Iron: 14)
 * - Repair: Copper Ingot
 */
public enum CopperTier implements Tier {
    INSTANCE(ModTags.Blocks.INCORRECT_FOR_COPPER_TOOL, 190, 5.0F, 1.0F, 13, () -> Ingredient.of(Items.COPPER_INGOT));

    private final TagKey<Block> incorrectBlocksForDrops;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    CopperTier(TagKey<Block> incorrectBlocksForDrops, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = Suppliers.memoize(repairIngredient::get);
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrectBlocksForDrops;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
