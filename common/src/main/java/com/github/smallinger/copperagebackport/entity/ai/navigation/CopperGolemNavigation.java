package com.github.smallinger.copperagebackport.entity.ai.navigation;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.Path;

/**
 * Extended navigation for Copper Golem with improved pathfinding.
 * Uses longer paths to avoid getting stuck.
 * 
 * Implements setCanPathToTargetsBelowSurface() functionality that exists in 1.21.10
 * but is missing in 1.20.1/1.21.1. This allows the golem to navigate to targets below
 * the surface (e.g. stacked chests).
 */
public class CopperGolemNavigation extends GroundPathNavigation {
    private static final float VANILLA_COPPER_GOLEM_FOLLOW_RANGE = 48.0F;
    private float maxFollowRange = VANILLA_COPPER_GOLEM_FOLLOW_RANGE;
    private boolean canPathToTargetsBelowSurface = false;
    
    public CopperGolemNavigation(Mob mob, Level level) {
        super(mob, level);
    }
    
    /**
     * Backports modern EntityNavigation#setMaxFollowRange, which does not exist
     * in 1.21.1. Copper Golems use a minimum navigation range of 48 blocks.
     */
    public void setMaxFollowRange(float maxFollowRange) {
        this.maxFollowRange = maxFollowRange;
    }

    public float getMaxFollowRange() {
        return this.maxFollowRange;
    }

    /**
     * 1.21.1 normally passes the mob's FOLLOW_RANGE attribute directly into
     * pathfinding. Modern vanilla instead uses max(FOLLOW_RANGE, 48) for the
     * Copper Golem without changing the public entity attribute itself.
     */
    @Override
    protected Path createPath(Set<BlockPos> positions, int regionOffset, boolean useHeadPos, int accuracy) {
        float followRange = Math.max((float) this.mob.getAttributeValue(Attributes.FOLLOW_RANGE), this.maxFollowRange);
        return super.createPath(positions, regionOffset, useHeadPos, accuracy, followRange);
    }
    
    /**
     * Sets whether the golem can navigate to targets below the surface.
     * This is important for reaching stacked containers (e.g. chests on top of each other).
     * 
     * In vanilla 1.21.10 this method exists in GroundPathNavigation.
     * We implement it here for 1.21.1 compatibility.
     * 
     * @param canPathToTargetsBelowSurface true to allow navigation to underground targets
     */
    public void setCanPathToTargetsBelowSurface(boolean canPathToTargetsBelowSurface) {
        this.canPathToTargetsBelowSurface = canPathToTargetsBelowSurface;
    }
    
    /**
     * Returns whether the golem can navigate to targets below the surface.
     */
    public boolean canPathToTargetsBelowSurface() {
        return this.canPathToTargetsBelowSurface;
    }
    
    /**
     * Overrides createPath to skip surface search when canPathToTargetsBelowSurface is active.
     * 
     * The vanilla GroundPathNavigation.createPath() automatically searches for the surface
     * and adjusts the target position. This prevents reaching stacked containers.
     * 
     * When canPathToTargetsBelowSurface is true, we call the base implementation
     * of PathNavigation directly, which does not perform surface search.
     */
    @Override
    public Path createPath(BlockPos pos, int accuracy) {
        if (!this.canPathToTargetsBelowSurface) {
            return super.createPath(pos, accuracy);
        }

        LevelChunk chunk = this.level
            .getChunkSource()
            .getChunkNow(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
        if (chunk == null) {
            return null;
        }

        return this.createPathDirect(pos, accuracy);
    }
    
    /**
     * Creates a path directly to the requested position without applying the
     * GroundPathNavigation surface adjustment. Mirrors vanilla 1.21.10 behavior
     * when setCanPathToTargetsBelowSurface(true) is active.
     */
    private Path createPathDirect(BlockPos pos, int accuracy) {
        // Route through the overridden 4-argument method so the 48-block
        // Copper Golem follow range is preserved while skipping retargeting.
        return this.createPath(Set.of(pos), 8, false, accuracy);
    }
}

