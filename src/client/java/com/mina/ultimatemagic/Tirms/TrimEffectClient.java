package com.mina.ultimatemagic.Tirms;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import com.mina.ultimatemagic.Trims.TrimEffectSystem;

public class TrimEffectClient {
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(
            TrimEffectSystem.TRIM_SYNC_PACKET,
            (client, handler, buf, responseSender) -> {
                String effectName = buf.readString();
                double bonus = buf.readDouble();

                client.execute(() -> {
                    if (MinecraftClient.getInstance().player != null) {
                        MinecraftClient.getInstance().player.sendMessage(
                            Text.literal("§6" + effectName + ": +" + String.format("%.0f", bonus * 100) + "%"),
                            true
                        );
                    }
                });
            }
        );
    }
}