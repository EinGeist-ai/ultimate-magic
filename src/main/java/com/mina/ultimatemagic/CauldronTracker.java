package com.mina.ultimatemagic;

import com.mina.ultimatemagic.CauldronRecipeSytem.CauldronRecipeManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.Registries;

import java.util.*;

public class CauldronTracker {
    private static final Map<BlockPos, CauldronContent> lastKnownStates = new HashMap<>();

    public static CauldronContent getCauldronAt(ServerWorld world, BlockPos pos) {
        if (!world.isChunkLoaded(pos)) {
            return null;
        }

        BlockState state = world.getBlockState(pos);
        if (!isCauldron(state)) {
            return null;
        }

        return getCauldronContent(world, pos);
    }

    public static class CauldronContent {
        public String fluidType = "";
        public int fluidLevel = 0;
        List<ItemInfo> items = new ArrayList<>();

        public boolean hasItem(String itemName) {
            return items.stream().anyMatch(item -> item.name.equals(itemName));
        }

        public int getItemCount(String itemName) {
            return items.stream()
                    .filter(item -> item.name.equals(itemName))
                    .mapToInt(item -> item.count)
                    .sum();
        }

        public boolean isEmpty() {
            return fluidLevel == 0 && items.isEmpty();
        }

        public List<ItemInfo> getItems() {
            return Collections.unmodifiableList(items);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CauldronContent that = (CauldronContent) o;
            return fluidLevel == that.fluidLevel &&
                   Objects.equals(fluidType, that.fluidType) &&
                   Objects.equals(items, that.items);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fluidType, fluidLevel, items);
        }
    }

    public static class ItemInfo {
        public String name;  // Dies wird jetzt die Item-ID sein
        public int count;

        ItemInfo(String name, int count) {
            this.name = name;
            this.count = count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemInfo itemInfo = (ItemInfo) o;
            return count == itemInfo.count &&
                   Objects.equals(name, itemInfo.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, count);
        }
    }

    private static CauldronContent getCauldronContent(ServerWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        CauldronContent content = new CauldronContent();

        // Debug für Flüssigkeitserkennung
        if (state.isOf(Blocks.CAULDRON)) {
            content.fluidType = "Leer";
            content.fluidLevel = 0;
            UltimateMagic.LOGGER.debug("[Kessel] Leerer Kessel erkannt bei " + pos);
        } else if (state.getBlock() instanceof LeveledCauldronBlock) {
            content.fluidLevel = state.get(LeveledCauldronBlock.LEVEL);
            if (state.isOf(Blocks.WATER_CAULDRON)) {
                content.fluidType = "Wasser";
                UltimateMagic.LOGGER.debug("[Kessel] Wasserkessel erkannt: Level " + content.fluidLevel);
            } else if (state.isOf(Blocks.LAVA_CAULDRON)) {
                content.fluidType = "Lava";
                UltimateMagic.LOGGER.debug("[Kessel] Lavakessel erkannt: Level " + content.fluidLevel);
            } else if (state.isOf(Blocks.POWDER_SNOW_CAULDRON)) {
                content.fluidType = "Pulverschnee";
                UltimateMagic.LOGGER.debug("[Kessel] Schneekessel erkannt: Level " + content.fluidLevel);
            }
        }

        Box searchBox = new Box(pos).expand(0.125);
        List<ItemEntity> itemEntities = world.getEntitiesByClass(ItemEntity.class, searchBox, entity -> true);

        UltimateMagic.LOGGER.debug("[Kessel] Suche nach Items im Kessel bei " + pos);
        UltimateMagic.LOGGER.debug("[Kessel] Gefundene Entities: " + itemEntities.size());

        for (ItemEntity entity : itemEntities) {
            String itemName = entity.getStack().getItem().getName().getString();
            String itemId = Registries.ITEM.getId(entity.getStack().getItem()).toString();
            int count = entity.getStack().getCount();

            UltimateMagic.LOGGER.debug("[Kessel] Gefundenes Item:");
            UltimateMagic.LOGGER.debug("  - Name: " + itemName);
            UltimateMagic.LOGGER.debug("  - ID: " + itemId);
            UltimateMagic.LOGGER.debug("  - Anzahl: " + count);

            content.items.add(new ItemInfo(itemName, count));
        }

        return content;
    }

    // Öffentliche Hilfsmethoden
    public static List<BlockPos> findNearbyCauldrons(ServerWorld world, BlockPos center, int radius) {
        List<BlockPos> cauldrons = new ArrayList<>();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = center.add(x, y, z);
                    if (world.isChunkLoaded(checkPos) && isCauldron(world.getBlockState(checkPos))) {
                        cauldrons.add(checkPos);
                    }
                }
            }
        }
        
        return cauldrons;
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                trackCauldrons(world);
            }
        });
    }

    private static void trackCauldrons(ServerWorld world) {
        // Überprüfe alle geladenen Chunks in der Welt
        for (BlockPos pos : getAllKnownCauldrons(world)) {
            if (world.isChunkLoaded(pos)) {
                BlockState state = world.getBlockState(pos);
                if (isCauldron(state)) {
                    checkAndUpdateCauldron(world, pos);
                }
            }
        }
    }

    private static Set<BlockPos> getAllKnownCauldrons(ServerWorld world) {
        Set<BlockPos> cauldrons = new HashSet<>(lastKnownStates.keySet());
        
        // Suche in einem Bereich um jeden Spieler
        for (ServerPlayerEntity player : world.getPlayers()) {
            BlockPos playerPos = player.getBlockPos();
            int searchRadius = 16; // Suchradius in Blöcken
            
            for (int x = -searchRadius; x <= searchRadius; x++) {
                for (int y = -searchRadius; y <= searchRadius; y++) {
                    for (int z = -searchRadius; z <= searchRadius; z++) {
                        BlockPos checkPos = playerPos.add(x, y, z);
                        BlockState state = world.getBlockState(checkPos);
                        
                        if (isCauldron(state)) {
                            cauldrons.add(checkPos);
                        }
                    }
                }
            }
        }
        
        return cauldrons;
    }

    private static boolean isCauldron(BlockState state) {
        return state.isOf(Blocks.CAULDRON) || 
               state.isOf(Blocks.WATER_CAULDRON) || 
               state.isOf(Blocks.LAVA_CAULDRON) || 
               state.isOf(Blocks.POWDER_SNOW_CAULDRON);
    }

    private static void checkAndUpdateCauldron(ServerWorld world, BlockPos pos) {
        UltimateMagic.LOGGER.debug("[Kessel] Prüfe Kessel bei " + pos);

        CauldronContent currentContent = getCauldronContent(world, pos);
        CauldronContent previousContent = lastKnownStates.get(pos);

        if (hasContentChanged(previousContent, currentContent)) {
            UltimateMagic.LOGGER.debug("[Kessel] Inhalt hat sich geändert:");
            UltimateMagic.LOGGER.debug("  Vorher: " + (previousContent != null ? formatCauldronContent(previousContent) : "null"));
            UltimateMagic.LOGGER.debug("  Nachher: " + formatCauldronContent(currentContent));

            // Chat-Nachricht senden
            String message = String.format("Kessel bei %s, %s, %s: %s", 
                pos.getX(), pos.getY(), pos.getZ(), 
                formatCauldronContent(currentContent));
                
            world.getPlayers().forEach(player -> 
                player.sendMessage(Text.of(message)));

            // Status aktualisieren
            
            // Fügen Sie diese Zeile hinzu:
            

            CauldronRecipeManager.checkAndCraft(world, pos);
        
            lastKnownStates.put(pos, currentContent);
        }
    }

    private static boolean hasContentChanged(CauldronContent previous, CauldronContent current) {
        if (previous == null) return true;
        
        return !previous.fluidType.equals(current.fluidType) ||
               previous.fluidLevel != current.fluidLevel ||
               !previous.items.equals(current.items);
    }

    private static String formatCauldronContent(CauldronContent content) {
        StringBuilder sb = new StringBuilder();
        sb.append(content.fluidType).append(" (Level: ").append(content.fluidLevel).append("/3)");
        
        if (!content.items.isEmpty()) {
            sb.append(", Items: ");
            content.items.forEach(item -> 
                sb.append(item.name).append(" x").append(item.count).append(", "));
            sb.setLength(sb.length() - 2); // Letztes Komma entfernen
        }
        
        return sb.toString();
    }
}