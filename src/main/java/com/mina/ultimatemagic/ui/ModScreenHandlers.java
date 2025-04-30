package com.mina.ultimatemagic.ui;

import com.mina.ultimatemagic.UltimateMagic;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<SpellBookScreenHandler> SPELL_BOOK_GUI;

    public static void registerScreenHandlers() {
        UltimateMagic.LOGGER.debug("[ModScreenHandlers] Registering creen handlers");
        SPELL_BOOK_GUI = Registry.register(
            Registries.SCREEN_HANDLER,
            new Identifier("ultimatemagic", "spell_book_gui"),
            new ExtendedScreenHandlerType<>(SpellBookScreenHandler::new)
        );
        UltimateMagic.LOGGER.debug("[ModScreenHandlers] Screen handlers registration completed");
    }
}