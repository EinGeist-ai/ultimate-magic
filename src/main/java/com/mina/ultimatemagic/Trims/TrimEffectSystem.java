package com.mina.ultimatemagic.Trims;

import com.mina.ultimatemagic.UltimateMagic;
import com.mina.ultimatemagic.config.ModConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class TrimEffectSystem {
    private static final Map<String, TrimEffect> TRIM_EFFECTS = new HashMap<>();
    private static final Map<UUID, Map<EntityAttribute, EntityAttributeModifier>> ACTIVE_MODIFIERS = new HashMap<>();
    public static final Identifier TRIM_SYNC_PACKET = new Identifier("ultimatemagic", "trim_sync");

    // Record-Definition für TrimEffect
    private record TrimEffect(
            UUID modifierId,
            EntityAttribute attribute,
            Map<String, Double> materialBonuses,
            String modifierName,
            EntityAttributeModifier.Operation operation
    ) {}

    public static void registerServer() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                checkAndUpdateEffects(player);
            }
        });

        loadEffectsFromConfig();
    }

    private static void checkAndUpdateEffects(PlayerEntity player) {
        String currentTrimSet = ArmorSetChecker.getCurrentTrimSet();
        String currentMaterial = ArmorSetChecker.getCurrentTrimMaterial();

        // Überprüfen Sie, ob sich der aktuelle Effekt von dem vorherigen unterscheidet
        TrimEffect currentEffect = !currentTrimSet.equals("No Trim") ?
                TRIM_EFFECTS.get(currentTrimSet.toLowerCase()) : null;
        Double currentBonus = (currentEffect != null && !currentMaterial.equals("No Material")) ?
                currentEffect.materialBonuses.get(currentMaterial.toLowerCase()) : null;

        Map<EntityAttribute, EntityAttributeModifier> playerModifiers =
                ACTIVE_MODIFIERS.get(player.getUuid());

        // Wenn es keine aktiven Modifikatoren gibt und kein neuer Effekt angewendet werden soll
        if (playerModifiers == null && currentEffect == null) {
            return;
        }

        // Wenn sich der Effekt geändert hat, entferne die alten Modifikatoren
        if (needsUpdate(player, currentEffect, currentBonus)) {
            removeAllModifiers(player);

            // Wende den neuen Effekt an, wenn einer vorhanden ist
            if (currentEffect != null && currentBonus != null) {
                applyEffect(player, currentEffect, currentBonus);
            }
        }
    }

    private static boolean needsUpdate(PlayerEntity player, TrimEffect currentEffect, Double currentBonus) {
        Map<EntityAttribute, EntityAttributeModifier> playerModifiers =
                ACTIVE_MODIFIERS.get(player.getUuid());

        if (playerModifiers == null || playerModifiers.isEmpty()) {
            return currentEffect != null && currentBonus != null;
        }

        if (currentEffect == null || currentBonus == null) {
            return true;
        }

        EntityAttributeModifier activeModifier = playerModifiers.get(currentEffect.attribute);
        return activeModifier == null || activeModifier.getValue() != currentBonus;
    }

    private static void applyEffect(PlayerEntity player, TrimEffect effect, double bonus) {
        if (player == null || effect.attribute == null) return;

        var attributeInstance = player.getAttributeInstance(effect.attribute);
        if (attributeInstance == null) {
            UltimateMagic.LOGGER.error("Konnte Attribut nicht finden: " + effect.attribute);
            return;
        }

        try {
            EntityAttributeModifier modifier = new EntityAttributeModifier(
                    effect.modifierId,
                    effect.modifierName,
                    bonus,
                    effect.operation
            );

            attributeInstance.removeModifier(effect.modifierId);
            attributeInstance.addPersistentModifier(modifier);

            ACTIVE_MODIFIERS
                    .computeIfAbsent(player.getUuid(), k -> new HashMap<>())
                    .put(effect.attribute, modifier);

            if (player instanceof ServerPlayerEntity serverPlayer) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString(effect.modifierName);
                buf.writeDouble(bonus);
                ServerPlayNetworking.send(serverPlayer, TRIM_SYNC_PACKET, buf);
            }
        } catch (Exception e) {
            UltimateMagic.LOGGER.error("Fehler beim Anwenden des Effekts: " + e.getMessage());
        }
    }

    private static void removeAllModifiers(PlayerEntity player) {
        if (player == null) return;

        Map<EntityAttribute, EntityAttributeModifier> playerModifiers =
                ACTIVE_MODIFIERS.get(player.getUuid());

        if (playerModifiers != null) {
            playerModifiers.forEach((attribute, modifier) -> {
                var instance = player.getAttributeInstance(attribute);
                if (instance != null) {
                    instance.removeModifier(modifier.getId());
                }
            });
            playerModifiers.clear();
        }
    }

    private static void loadEffectsFromConfig() {
        ModConfig.TrimValues config = ModConfig.getConfig();
        TRIM_EFFECTS.clear();

        for (Map.Entry<String, ModConfig.TrimConfig> entry : config.trimEffects.entrySet()) {
            String trimPattern = entry.getKey();
            ModConfig.TrimConfig trimConfig = entry.getValue();

            EntityAttribute attribute = null;
            if (trimConfig.attribute != null) {
                Identifier attributeId = new Identifier(trimConfig.attribute);
                attribute = Registries.ATTRIBUTE.get(attributeId);

                if (attribute == null) {
                    UltimateMagic.LOGGER.error(
                            "Attribut nicht gefunden: " + trimConfig.attribute +
                                    " für Trim-Muster: " + trimPattern
                    );
                    continue;
                }
            }

            UUID modifierId = UUID.nameUUIDFromBytes(("trim_effect_" + trimPattern).getBytes());
            EntityAttributeModifier.Operation operation =
                    EntityAttributeModifier.Operation.valueOf(trimConfig.operation);

            TRIM_EFFECTS.put(trimPattern.toLowerCase(), new TrimEffect(
                    modifierId,
                    attribute,
                    trimConfig.materialBonuses,
                    trimConfig.effectName,
                    operation
            ));
        }
    }
}