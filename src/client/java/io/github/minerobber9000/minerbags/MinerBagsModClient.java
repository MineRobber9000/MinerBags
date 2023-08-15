package io.github.minerobber9000.minerbags;

import io.github.minerobber9000.minerbags.screen.BagScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class MinerBagsModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(MinerBagsMod.BAG_SCREEN_HANDLER_TYPE, BagScreen::new);
		HandledScreens.register(MinerBagsMod.DIAMOND_BAG_SCREEN_HANDLER_TYPE, BagScreen::new);
	}
}