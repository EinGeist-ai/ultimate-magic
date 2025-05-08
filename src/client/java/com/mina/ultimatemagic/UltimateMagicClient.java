package com.mina.ultimatemagic;

import com.mina.ultimatemagic.Tirms.TrimEffectClient;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class UltimateMagicClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        UltimateMagic.LOGGER.debug("[UltimateMagicClient] Initializing client");
        TrimEffectClient.registerClient();
        UltimateMagic.LOGGER.debug("[UltimateMagicClient] Client initialization completed");
    }
}