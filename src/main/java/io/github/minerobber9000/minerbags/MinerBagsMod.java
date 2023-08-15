package io.github.minerobber9000.minerbags;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.minerobber9000.minerbags.items.ItemBag;
import io.github.minerobber9000.minerbags.items.ItemDiamondBag;
import io.github.minerobber9000.minerbags.screen.BagScreenHandler;

// see net/minecaft/recipe/BagCraftingRecipe(Serializer).java for explanation
import net.minecraft.recipe.BagCraftingRecipe;
import net.minecraft.recipe.BagCraftingRecipeSerializer;

public class MinerBagsMod implements ModInitializer {
	public static final String MODID = "minerbags";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static Identifier id(String path) {
		return new Identifier(MODID,path);
	}

	public static final Item BAG = new ItemBag(new Item.Settings().maxCount(1));
	public static final Item DIAMOND_BAG = new ItemDiamondBag(new Item.Settings().maxCount(1));

	public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP,id("items"));

	public static final ScreenHandlerType<BagScreenHandler> BAG_SCREEN_HANDLER_TYPE = new ScreenHandlerType<>(BagScreenHandler::createBag,FeatureFlags.VANILLA_FEATURES);
	public static final ScreenHandlerType<BagScreenHandler> DIAMOND_BAG_SCREEN_HANDLER_TYPE = new ScreenHandlerType<>(BagScreenHandler::createDiamondBag,FeatureFlags.VANILLA_FEATURES);

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, id("bag"), BAG);
		Registry.register(Registries.ITEM, id("diamond_bag"), DIAMOND_BAG);
		Registry.register(Registries.ITEM_GROUP,ITEM_GROUP,FabricItemGroup.builder()
			.icon(new Supplier<ItemStack>() {
				@Override
				public ItemStack get() {
					return new ItemStack(BAG);
				}			
			})
			.displayName(Text.translatable("itemGroup.minerbags.items"))
			.build()
		);
		Registry.register(Registries.SCREEN_HANDLER, id("bag"), BAG_SCREEN_HANDLER_TYPE);
		Registry.register(Registries.SCREEN_HANDLER, id("diamond_bag"), DIAMOND_BAG_SCREEN_HANDLER_TYPE);
		Registry.register(Registries.RECIPE_SERIALIZER, id("bag_crafting"), BagCraftingRecipeSerializer.INSTANCE);
		Registry.register(Registries.RECIPE_TYPE, id("bag_crafting"), BagCraftingRecipe.Type.INSTANCE);
		ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(content -> {
			content.add(BAG);
			content.add(DIAMOND_BAG);
		});
	}
}