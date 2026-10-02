package com.github.smallinger.copperagebackport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import com.github.smallinger.copperagebackport.block.CopperChestBlock;
import com.github.smallinger.copperagebackport.registry.ModBlockEntities;

public class CopperChestBlockEntity extends ChestBlockEntity {
    private final ContainerOpenersCounter openersCounter;
    
    public CopperChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COPPER_CHEST_BLOCK_ENTITY.get(), pos, state);
        
        this.openersCounter = new ContainerOpenersCounter() {
            @Override
            protected void onOpen(Level level, BlockPos blockPos, BlockState blockState) {
                // Sound wird in openerCountChanged gespielt
            }

            @Override
            protected void onClose(Level level, BlockPos blockPos, BlockState blockState) {
                // Sound wird in openerCountChanged gespielt
            }

            @Override
            protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
                if (state.getBlock() instanceof CopperChestBlock chestBlock) {
                    if (oldCount == 0 && newCount > 0) {
                        playChestSound(level, pos, state, chestBlock.getOpenSound());
                    } else if (newCount == 0 && oldCount > 0) {
                        playChestSound(level, pos, state, chestBlock.getCloseSound());
                    }
                }
                level.blockEvent(pos, state.getBlock(), 1, newCount);
            }

            @Override
            protected boolean isOwnContainer(Player player) {
                if (!(player.containerMenu instanceof ChestMenu)) {
                    return false;
                } else {
                    Container container = ((ChestMenu) player.containerMenu).getContainer();
                    return container == CopperChestBlockEntity.this || 
                           container instanceof CompoundContainer && 
                           ((CompoundContainer) container).contains(CopperChestBlockEntity.this);
                }
            }
        };
    }
    
    private static void playChestSound(Level level, BlockPos pos, BlockState state, SoundEvent sound) {
        ChestType chestType = state.getValue(ChestBlock.TYPE);
        // Vanilla plays one sound for a double chest: LEFT is silent, RIGHT offsets
        // the sound to the center of the combined chest.
        if (chestType == ChestType.LEFT) {
            return;
        }

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        if (chestType == ChestType.RIGHT) {
            Direction connected = ChestBlock.getConnectedDirection(state);
            x += connected.getStepX() * 0.5;
            z += connected.getStepZ() * 0.5;
        }

        level.playSound(
            null,
            x,
            y,
            z,
            sound,
            SoundSource.BLOCKS,
            0.5F,
            level.random.nextFloat() * 0.1F + 0.9F
        );
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator() && this.getLevel() != null) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
    
    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator() && this.getLevel() != null) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
    
    public void recheckOpen() {
        if (!this.remove && this.getLevel() != null) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
    
    /**
     * Check if the chest is currently open
     * @return true if the chest has viewers
     */
    public boolean isChestOpen() {
        return this.openersCounter.getOpenerCount() > 0;
    }

    public int getPlayerOpenerCount() {
        return this.openersCounter.getOpenerCount();
    }
}
