package com.mina.ultimatemagic.Trims;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArmorSetChecker {
    private static final Map<UUID, String> lastSetStates = new HashMap<>();
    
    // Static variables to track current trim state
    private static String currentTrimSet = "No Trim";
    private static String currentTrimMaterial = "No Material";
    
    // Getters for the trim state
    public static String getCurrentTrimSet() {
        return currentTrimSet;
    }
    
    public static String getCurrentTrimMaterial() {
        return currentTrimMaterial;
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                checkArmorSet(player);
            }
        });
    }

    private static String extractPattern(String rawPattern) {
        Pattern regex = Pattern.compile("key='trim_pattern\\.minecraft\\.(\\w+)'");
        Matcher matcher = regex.matcher(rawPattern);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "unknown";
    }

    private static String extractMaterial(String rawMaterial) {
        Pattern regex = Pattern.compile("minecraft:(\\w+)]=");
        Matcher matcher = regex.matcher(rawMaterial);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "unknown";
    }

    private static void checkArmorSet(PlayerEntity player) {
        if (player == null) return;

        String firstPattern = null;
        String firstMaterial = null;
        boolean hasFullSet = true;

        for (ItemStack armorPiece : player.getArmorItems()) {
            if (armorPiece.isEmpty() || !(armorPiece.getItem() instanceof ArmorItem)) {
                hasFullSet = false;
                break;
            }

            Optional<ArmorTrim> trimOptional = ArmorTrim.getTrim(player.getWorld().getRegistryManager(), armorPiece);
            if (trimOptional.isEmpty()) {
                hasFullSet = false;
                break;
            }

            ArmorTrim trim = trimOptional.get();
            String currentPattern = trim.getPattern().toString();
            String currentMaterial = trim.getMaterial().toString();

            String extractedPattern = extractPattern(currentPattern);
            String extractedMaterial = extractMaterial(currentMaterial);

            if (firstPattern == null) {
                firstPattern = extractedPattern;
                firstMaterial = extractedMaterial;
            } else {
                if (!extractedPattern.equals(firstPattern) || 
                    !extractedMaterial.equals(firstMaterial)) {
                    hasFullSet = false;
                    break;
                }
            }
        }

        // Update the static variables
        if (hasFullSet && firstPattern != null && firstMaterial != null) {
            currentTrimSet = capitalizeFirstLetter(firstPattern);
            currentTrimMaterial = capitalizeFirstLetter(firstMaterial);
            
            String currentSet = firstPattern + firstMaterial;
            String lastSet = lastSetStates.get(player.getUuid());
            
            if (!currentSet.equals(lastSet)) {
                player.sendMessage(Text.literal("§6Full set of " + currentTrimSet + " trim with " + currentTrimMaterial + " material!"), false);
                lastSetStates.put(player.getUuid(), currentSet);
            }
        } else {
            currentTrimSet = "No Trim";
            currentTrimMaterial = "No Material";
            lastSetStates.remove(player.getUuid());
        }
    }

    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}