package com.mina.ultimatemagic.Items.Custom;

import com.mina.ultimatemagic.UltimateMagic;
import com.mina.ultimatemagic.ui.SpellBookScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SpellBookItem extends Item {
    public SpellBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        UltimateMagic.LOGGER.debug("[SpellBookItem] Player {} using spell book", player.getName().getString());
        if (!world.isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    // Write any additional data needed by the screen handler
                }

                @Override
                public Text getDisplayName() {
                    return Text.literal("Spell Book");
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    UltimateMagic.LOGGER.debug("[SpellBookItem] Opening spell book screen for player {}", player.getName().getString());
                    return new SpellBookScreenHandler(syncId, inv, new SimpleInventory(9));
                }
            });
        }
        return TypedActionResult.success(player.getStackInHand(hand));
    }
}