package net.minecraft.recipe;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

/* Alright, this probably needs explaining.
 * 
 * In order to cheap out on implementing the bag crafting (which needs to
 * respect the items in the bag/shulker box used for crafting), I reuse a
 * lot of net.minecraft.recipe.ShapedRecipe (after all, if it's already
 * written, why not?).
 * 
 * That being said, there's some code in the ShapedRecipe serializer that
 * requires methods in ShapedRecipe that are default visibility static
 * methods. Ergo, for ease of implementation (and avoidance of having to
 * rewrite those methods in the subclass), it was easier to just shove
 * BagCraftingRecipe(Serializer) in net.minecraft.recipe.
*/

public class BagCraftingRecipe extends ShapedRecipe {
    public BagCraftingRecipe(Identifier id, String group, CraftingRecipeCategory category, int width, int height, DefaultedList<Ingredient> input, ItemStack output, boolean showNotification) {
        super(id,group,category,width,height,input,output);
    }

    public BagCraftingRecipe(Identifier id, String group, CraftingRecipeCategory category, int width, int height, DefaultedList<Ingredient> input, ItemStack output) {
        this(id, group, category, width, height, input, output, true);
    }

    public RecipeSerializer<?> getSerializer() {
        return BagCraftingRecipeSerializer.INSTANCE;
    }

    public static class Type implements RecipeType<BagCraftingRecipe> {
        private Type() {}
        public static Type INSTANCE = new Type();
    }

    private ItemStack make(ItemStack output, NbtList list) {
        NbtCompound nbtCompound = output.getOrCreateNbt();
        nbtCompound.put("Items",list);
        return output;
    }

    public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack output = this.getOutput(dynamicRegistryManager).copy();
        for (int i=0;i<recipeInputInventory.size();++i) {
            ItemStack itemStack = recipeInputInventory.getStack(i);
            if (!itemStack.hasNbt()) continue;
            NbtCompound nbtCompound = itemStack.getNbt();
            // if we're using a bag to craft, we'll use its Items list
            if (nbtCompound.contains("Items", NbtElement.LIST_TYPE)) {
                return make(output,nbtCompound.getList("Items",NbtElement.COMPOUND_TYPE));
            }
            // if we're using a Shulker Box or similar, we'll use the Items list from its BlockEntityTag
            if (nbtCompound.contains("BlockEntityTag",NbtElement.COMPOUND_TYPE)) {
                NbtCompound blockEntityTag = nbtCompound.getCompound("BlockEntityTag");
                if (blockEntityTag.contains("Items", NbtElement.LIST_TYPE)) {
                    return make(output,blockEntityTag.getList("Items",NbtElement.COMPOUND_TYPE));
                }
            }
        }
        return output;
    }
}
