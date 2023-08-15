package io.github.minerobber9000.minerbags.screen;

import io.github.minerobber9000.minerbags.MinerBagsMod;
import io.github.minerobber9000.minerbags.inventory.BagInventory;
import io.github.minerobber9000.minerbags.inventory.DiamondBagInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class BagScreenHandlerFactory implements NamedScreenHandlerFactory {

    private ItemStack stack;

    public BagScreenHandlerFactory(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        // create diamond bag (9x6) or normal bag (9x3) depending on which bag item we're using
        if (stack.isOf(MinerBagsMod.DIAMOND_BAG)) {
            return BagScreenHandler.createDiamondBag(syncId, playerInventory, new DiamondBagInventory(stack));
        }
        return BagScreenHandler.createBag(syncId,playerInventory,new BagInventory(stack));
    }

    @Override
    public Text getDisplayName() {
        return stack.getName();
    }
    
}
