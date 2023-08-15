package io.github.minerobber9000.minerbags.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class BagInventory implements Inventory {

    private final ItemStack stack;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public BagInventory(ItemStack stack) {
        this.stack=stack;
        NbtCompound tag = stack.getNbt();
        if (tag!=null) {
            Inventories.readNbt(tag,items);
        }
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (int i=0;i<size();i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int var1) {
        return items.get(var1);
    }

    @Override
    public ItemStack removeStack(int var1, int var2) {
        ItemStack result = Inventories.splitStack(items, var1, var2);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int var1) {
        return Inventories.removeStack(items, var1);
    }

    @Override
    public void setStack(int var1, ItemStack var2) {
        items.set(var1, var2);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity var1) {
        return true;
    }

    public void markDirty() {
        NbtCompound tag = stack.getOrCreateNbt();
        Inventories.writeNbt(tag, items);
    }

}
