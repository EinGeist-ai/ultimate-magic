package com.mina.ultimatemagic;

import com.mina.ultimatemagic.Items.ModItemGroups;
import com.mina.ultimatemagic.Items.ModItems;
import com.mina.ultimatemagic.Blocks.ModBlocks;
import com.mina.ultimatemagic.Trims.ArmorSetChecker;
import com.mina.ultimatemagic.config.ModConfig;
import com.mina.ultimatemagic.ui.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mina.ultimatemagic.Trims.TrimEffectSystem;


public class UltimateMagic implements ModInitializer {
    public static final String MOD_ID = "ultimatemagic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    @Override
    public void onInitialize() {
        LOGGER.debug("[UltimateMagic] Starting mod initialization");
        ModConfig.loadConfig(); // Lade die Konfiguration
        ModItemGroups.registerItemGroups();
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModScreenHandlers.registerScreenHandlers();
        ArmorSetChecker.register(); // Registriere den Event-Handler statt direktem Aufruf
        TrimEffectSystem.registerServer();
        LOGGER.debug("[UltimateMagic] Mod initialization completed");
    }
}