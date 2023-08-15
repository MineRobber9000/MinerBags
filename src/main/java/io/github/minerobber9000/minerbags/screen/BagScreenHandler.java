package io.github.minerobber9000.minerbags.screen;

import io.github.minerobber9000.minerbags.MinerBagsMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.ShulkerBoxSlot;

/* I'm gonna be honest with y'all; this is just GenericContainerScreenHandler
 * rewritten to use ShulkerBoxSlot. Since we want to avoid nesting shulker
 * boxes/bags while also being allowed to have diamond bags, this was a
 * necessary evil.
 * 
 * Same goes for BagScreen (GenericContainerScreen), since the screen takes its
 * handler as an argument and, thusly, we can't just tell it to work with our
 * BagScreenHandler instead.
 * 
 * Notably though, if I decide to go the ironchests route and have a bag larger
 * than a doublechest it wouldn't be as much effort to make it so the mod
 * could handle that. I have no plans to do that at the current moment though.
*/
public class BagScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final int rows;

    public static BagScreenHandler createBag(int syncId, PlayerInventory playerInventory) {
        return new BagScreenHandler(MinerBagsMod.BAG_SCREEN_HANDLER_TYPE, syncId, playerInventory, 3);
    }

    public static BagScreenHandler createBag(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        return new BagScreenHandler(MinerBagsMod.BAG_SCREEN_HANDLER_TYPE, syncId, playerInventory, inventory, 3);
    }

    public static BagScreenHandler createDiamondBag(int syncId, PlayerInventory playerInventory) {
        return new BagScreenHandler(MinerBagsMod.DIAMOND_BAG_SCREEN_HANDLER_TYPE, syncId, playerInventory, 6);
    }

    public static BagScreenHandler createDiamondBag(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        return new BagScreenHandler(MinerBagsMod.DIAMOND_BAG_SCREEN_HANDLER_TYPE, syncId, playerInventory, inventory, 6);
    }

    public BagScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, int rows) {
        this(type,syncId,playerInventory,new SimpleInventory(rows*9),rows);
    }

    public BagScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
        super(type, syncId);
        checkSize(inventory, rows * 9);
        this.inventory = inventory;
        this.rows = rows;
        inventory.onOpen(playerInventory.player);
        int yOffset = (this.rows - 4) * 18;
        for (int row = 0; row < this.rows; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new ShulkerBoxSlot(inventory, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 103 + row * 18 + yOffset));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 161 + yOffset));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = (Slot)this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < this.rows * 9 ? !this.insertItem(itemStack2, this.rows * 9, this.slots.size(), true) : !this.insertItem(itemStack2, 0, this.rows * 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public int getRows() {
        return this.rows;
    }
}
