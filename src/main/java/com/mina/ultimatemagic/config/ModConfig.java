package com.mina.ultimatemagic.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mina.ultimatemagic.UltimateMagic;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ModConfig {
    private static final String CONFIG_FILE = "ultimatemagic.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public static class TrimConfig {
        public String attribute;
        public String operation;
        public Map<String, Double> materialBonuses = new HashMap<>();
        public String effectName;
        
        public TrimConfig(String attribute, String operation, Map<String, Double> bonuses, String effectName) {
            this.attribute = attribute;
            this.operation = operation;
            this.materialBonuses = bonuses;
            this.effectName = effectName;
        }
    }
    
    public static class TrimValues {
        public Map<String, TrimConfig> trimEffects = new HashMap<>();
        
        public TrimValues() {
            // Sentry Trim Konfiguration
            Map<String, Double> sentryBonuses = new HashMap<>();
            sentryBonuses.put("netherite", 0.4);
            sentryBonuses.put("diamond", 0.3);
            sentryBonuses.put("gold", 0.25);
            sentryBonuses.put("emerald", 0.25);
            sentryBonuses.put("iron", 0.2);
            sentryBonuses.put("copper", 0.2);
            sentryBonuses.put("redstone", 0.15);
            sentryBonuses.put("amethyst", 0.15);
            sentryBonuses.put("quartz", 0.15);
            sentryBonuses.put("lapis", 0.15);
            
            trimEffects.put("sentry", new TrimConfig(
                "minecraft:generic.max_health",
                "MULTIPLY_BASE",
                sentryBonuses,
                "Sentry Health Bonus"
            ));
            
            // Wild Trim Konfiguration
            Map<String, Double> wildBonuses = new HashMap<>();
            wildBonuses.put("netherite", 0.4);
            wildBonuses.put("diamond", 0.3);
            wildBonuses.put("gold", 0.25);
            wildBonuses.put("emerald", 0.25);
            wildBonuses.put("iron", 0.2);
            wildBonuses.put("copper", 0.2);
            wildBonuses.put("redstone", 0.15);
            wildBonuses.put("amethyst", 0.15);
            wildBonuses.put("quartz", 0.15);
            wildBonuses.put("lapis", 0.15);
            
            trimEffects.put("wild", new TrimConfig(
                "minecraft:generic.attack_damage",
                "MULTIPLY_BASE",
                wildBonuses,
                "Wild Damage Bonus"
            ));
            
            // Vex Trim Konfiguration
            Map<String, Double> vexBonuses = new HashMap<>();
            vexBonuses.put("netherite", 0.3);
            vexBonuses.put("diamond", 0.25);
            vexBonuses.put("gold", 0.2);
            vexBonuses.put("emerald", 0.2);
            vexBonuses.put("iron", 0.15);
            vexBonuses.put("copper", 0.15);
            vexBonuses.put("redstone", 0.1);
            vexBonuses.put("amethyst", 0.1);
            vexBonuses.put("quartz", 0.1);
            vexBonuses.put("lapis", 0.1);
            
            trimEffects.put("vex", new TrimConfig(
                "minecraft:generic.movement_speed",
                "MULTIPLY_BASE",
                vexBonuses,
                "Vex Speed Bonus"
            ));

            // Eye Trim Konfiguration
            Map<String, Double> eyeBonuses = new HashMap<>();
            eyeBonuses.put("netherite", 0.3);
            eyeBonuses.put("diamond", 0.25);
            eyeBonuses.put("gold", 0.2);
            eyeBonuses.put("emerald", 0.2);
            eyeBonuses.put("iron", 0.15);
            eyeBonuses.put("copper", 0.15);
            eyeBonuses.put("redstone", 0.1);
            eyeBonuses.put("amethyst", 0.1);
            eyeBonuses.put("quartz", 0.1);
            eyeBonuses.put("lapis", 0.1);
            
            trimEffects.put("eye", new TrimConfig(
                "minecraft:generic.attack_speed",
                "MULTIPLY_BASE",
                eyeBonuses,
                "Eye Speed Bonus"
            ));
        }
    }
    
    private static TrimValues CONFIG;
    
    public static void loadConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        
        if (configPath.toFile().exists()) {
            try (Reader reader = new FileReader(configPath.toFile())) {
                CONFIG = GSON.fromJson(reader, TrimValues.class);
            } catch (IOException e) {
                UltimateMagic.LOGGER.error("Error while loading config: ", e);
                CONFIG = new TrimValues();
                saveConfig();
            }
        } else {
            CONFIG = new TrimValues();
            saveConfig();
        }
    }
    
    public static void saveConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        try (Writer writer = new FileWriter(configPath.toFile())) {
            GSON.toJson(CONFIG, writer);
        } catch (IOException e) {
            UltimateMagic.LOGGER.error("Error while saving config: ", e);
        }
    }
    
    public static TrimValues getConfig() {
        if (CONFIG == null) {
            loadConfig();
        }
        return CONFIG;
    }
}