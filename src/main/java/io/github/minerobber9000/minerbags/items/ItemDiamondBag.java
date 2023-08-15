package io.github.minerobber9000.minerbags.items;

import java.util.List;

import io.github.minerobber9000.minerbags.screen.BagScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ItemDiamondBag extends Item {
    public ItemDiamondBag(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack stack = playerEntity.getStackInHand(hand);
        playerEntity.openHandledScreen(new BagScreenHandlerFactory(stack));
        return TypedActionResult.success(stack);
    }

    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        super.appendTooltip(stack, world, tooltip, tooltipContext);
        NbtCompound tag = stack.getNbt();
        if (tag==null) return;
        if (tag.contains("Items", NbtElement.LIST_TYPE)) {
            DefaultedList<ItemStack> items = DefaultedList.ofSize(27,ItemStack.EMPTY);
            Inventories.readNbt(tag,items);
            int total = 0;
            int toShow = 4;
            if (tooltipContext.isAdvanced()) toShow=55;
            int show = toShow;
            for (ItemStack itemStack : items) {
                if (itemStack.isEmpty()) continue;
                ++total;
                if ((show--)<=0) continue;
                MutableText text = itemStack.getName().copy();
                text.append(" x").append(String.valueOf(itemStack.getCount()));
                tooltip.add(text);
            }
            if (show<0) { 
                tooltip.add(Text.translatable("container.shulkerBox.more",total-toShow).formatted(Formatting.ITALIC));
            }
        }
    }

    public boolean canBeNested() {
        return false;
    }
}
