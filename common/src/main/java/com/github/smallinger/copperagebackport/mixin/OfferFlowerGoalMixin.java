package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.OfferFlowerGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Backports the finalized Iron Golem gift target behavior.
 *
 * 1.21.1 only searches for Villagers. Copper Age extends the same daytime,
 * 1-in-8000 offer behavior to Copper Golems and keeps the normal 400-tick
 * interaction instead of giving them a special shortened timer.
 */
@Mixin(OfferFlowerGoal.class)
public abstract class OfferFlowerGoalMixin extends Goal {
    @Shadow @Final private IronGolem golem;
    @Shadow private Villager villager;
    @Shadow private int tick;

    @Unique
    private static final TargetingConditions copperagebackport$OFFER_TARGET_CONTEXT =
        TargetingConditions.forNonCombat().range(6.0D);

    @Unique
    private CopperGolemEntity copperagebackport$copperGolemTarget;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void copperagebackport$selectGiftRecipient(CallbackInfoReturnable<Boolean> cir) {
        this.copperagebackport$copperGolemTarget = null;
        this.villager = null;

        if (!this.golem.level().isDay() || this.golem.getRandom().nextInt(8000) != 0) {
            cir.setReturnValue(false);
            return;
        }

        AABB range = this.golem.getBoundingBox().inflate(6.0D, 2.0D, 6.0D);
        Villager nearestVillager = this.golem.level().getNearestEntity(
            Villager.class,
            copperagebackport$OFFER_TARGET_CONTEXT,
            this.golem,
            this.golem.getX(),
            this.golem.getY(),
            this.golem.getZ(),
            range
        );
        CopperGolemEntity nearestCopperGolem = this.golem.level().getNearestEntity(
            CopperGolemEntity.class,
            copperagebackport$OFFER_TARGET_CONTEXT,
            this.golem,
            this.golem.getX(),
            this.golem.getY(),
            this.golem.getZ(),
            range
        );

        if (nearestVillager == null && nearestCopperGolem == null) {
            cir.setReturnValue(false);
            return;
        }

        if (nearestCopperGolem != null
            && (nearestVillager == null
                || this.golem.distanceToSqr(nearestCopperGolem) < this.golem.distanceToSqr(nearestVillager))) {
            this.copperagebackport$copperGolemTarget = nearestCopperGolem;
        } else {
            this.villager = nearestVillager;
        }

        cir.setReturnValue(true);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void copperagebackport$lookAtCopperGolem(CallbackInfo ci) {
        if (this.copperagebackport$copperGolemTarget != null) {
            this.golem.getLookControl().setLookAt(this.copperagebackport$copperGolemTarget, 30.0F, 30.0F);
            --this.tick;
            ci.cancel();
        }
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void copperagebackport$givePoppyToCopperGolem(CallbackInfo ci) {
        CopperGolemEntity recipient = this.copperagebackport$copperGolemTarget;
        if (recipient != null && this.tick == 0) {
            AABB range = this.golem.getBoundingBox().inflate(6.0D, 2.0D, 6.0D);
            if (range.intersects(recipient.getBoundingBox())
                && recipient.getItemBySlot(CopperGolemEntity.EQUIPMENT_SLOT_ANTENNA).isEmpty()) {
                recipient.setItemSlot(
                    CopperGolemEntity.EQUIPMENT_SLOT_ANTENNA,
                    Items.POPPY.getDefaultInstance()
                );
                recipient.setGuaranteedDrop(CopperGolemEntity.EQUIPMENT_SLOT_ANTENNA);
            }
        }

        this.copperagebackport$copperGolemTarget = null;
    }
}
