package com.mina.ultimatemagic;

import com.mina.ultimatemagic.Tirms.TrimEffectClient;
import com.mina.ultimatemagic.ui.SpellBookScreen;
import com.mina.ultimatemagic.ui.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class UltimateMagicClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        UltimateMagic.LOGGER.debug("[UltimateMagicClient] Initializing client");
        HandledScreens.register(ModScreenHandlers.SPELL_BOOK_GUI, SpellBookScreen::new);
        TrimEffectClient.registerClient();
        UltimateMagic.LOGGER.debug("[UltimateMagicClient] Client initialization completed");
    }
}