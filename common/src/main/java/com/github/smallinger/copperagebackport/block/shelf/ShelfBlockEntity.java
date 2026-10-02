package com.github.smallinger.copperagebackport.block.shelf;

import com.github.smallinger.copperagebackport.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

/**
 * Block entity for the shelf block that stores up to 3 items.
 */
public class ShelfBlockEntity extends BlockEntity implements ListBackedContainer {
    
    public static final int MAX_ITEMS = 3;
    private static final String ITEMS_TAG = "Items";
    private static final String ALIGN_ITEMS_TO_BOTTOM_TAG = "align_items_to_bottom";
    
    private final NonNullList<ItemStack> items = NonNullList.withSize(MAX_ITEMS, ItemStack.EMPTY);
    private boolean alignItemsToBottom = false;

    public ShelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHELF_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    /**
     * Swaps an item in the given slot with the provided stack.
     * @return The item that was previously in the slot
     */
    public ItemStack swapItemNoUpdate(int slot, ItemStack stack) {
        ItemStack oldItem = this.removeItemNoUpdate(slot);
        this.setItemNoUpdate(slot, stack.copy());
        return oldItem;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, true, registries);
        tag.putBoolean(ALIGN_ITEMS_TO_BOTTOM_TAG, this.alignItemsToBottom);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.alignItemsToBottom = tag.getBoolean(ALIGN_ITEMS_TO_BOTTOM_TAG);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, this.items, true, registries);
        tag.putBoolean(ALIGN_ITEMS_TO_BOTTOM_TAG, this.alignItemsToBottom);
        return tag;
    }

    public boolean getAlignItemsToBottom() {
        return this.alignItemsToBottom;
    }

    public void setAlignItemsToBottom(boolean alignItemsToBottom) {
        this.alignItemsToBottom = alignItemsToBottom;
        this.setChanged();
    }

    public float getVisualRotationYInDegrees() {
        return this.getBlockState().getValue(ShelfBlock.FACING).getOpposite().toYRot();
    }

}
