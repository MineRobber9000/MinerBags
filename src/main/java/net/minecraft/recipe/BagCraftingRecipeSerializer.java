package net.minecraft.recipe;

import java.util.Map;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
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

public class BagCraftingRecipeSerializer
implements RecipeSerializer<BagCraftingRecipe> {

    private BagCraftingRecipeSerializer() {}
    public static final BagCraftingRecipeSerializer INSTANCE = new BagCraftingRecipeSerializer();

    @Override
    public BagCraftingRecipe read(Identifier identifier, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "group", "");
        CraftingRecipeCategory craftingRecipeCategory = CraftingRecipeCategory.CODEC.byId(JsonHelper.getString(jsonObject, "category", null), CraftingRecipeCategory.MISC);
        Map<String, Ingredient> map = ShapedRecipe.readSymbols(JsonHelper.getObject(jsonObject, "key"));
        String[] strings = ShapedRecipe.removePadding(ShapedRecipe.getPattern(JsonHelper.getArray(jsonObject, "pattern")));
        int i = strings[0].length();
        int j = strings.length;
        DefaultedList<Ingredient> defaultedList = ShapedRecipe.createPatternMatrix(strings, map, i, j);
        ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
        boolean bl = JsonHelper.getBoolean(jsonObject, "show_notification", true);
        return new BagCraftingRecipe(identifier, string, craftingRecipeCategory, i, j, defaultedList, itemStack, bl);
    }

    @Override
    public BagCraftingRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
        int i = packetByteBuf.readVarInt();
        int j = packetByteBuf.readVarInt();
        String string = packetByteBuf.readString();
        CraftingRecipeCategory craftingRecipeCategory = packetByteBuf.readEnumConstant(CraftingRecipeCategory.class);
        DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i * j, Ingredient.EMPTY);
        for (int k = 0; k < defaultedList.size(); ++k) {
            defaultedList.set(k, Ingredient.fromPacket(packetByteBuf));
        }
        ItemStack itemStack = packetByteBuf.readItemStack();
        boolean bl = packetByteBuf.readBoolean();
        return new BagCraftingRecipe(identifier, string, craftingRecipeCategory, i, j, defaultedList, itemStack, bl);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, BagCraftingRecipe shapedRecipe) {
        packetByteBuf.writeVarInt(shapedRecipe.width);
        packetByteBuf.writeVarInt(shapedRecipe.height);
        packetByteBuf.writeString(shapedRecipe.group);
        packetByteBuf.writeEnumConstant(shapedRecipe.category);
        for (Ingredient ingredient : shapedRecipe.input) {
            ingredient.write(packetByteBuf);
        }
        packetByteBuf.writeItemStack(shapedRecipe.output);
        packetByteBuf.writeBoolean(shapedRecipe.showNotification);
    }
}
