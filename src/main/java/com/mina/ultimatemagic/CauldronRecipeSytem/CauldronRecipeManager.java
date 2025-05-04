package com.mina.ultimatemagic.CauldronRecipeSytem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mina.ultimatemagic.CauldronTracker;
import com.mina.ultimatemagic.UltimateMagic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CauldronRecipeManager {
    private static final List<CauldronRecipe> recipes = new ArrayList<>();

    public static void loadRecipes() {
        try {
            String path = "/data/ultimatemagic/recipes/cauldron/recipes.json";
            InputStream inputStream = CauldronRecipeManager.class.getResourceAsStream(path);
            if (inputStream == null) {
                UltimateMagic.LOGGER.error("Rezeptdatei nicht gefunden: " + path);
                return;
            }

            Gson gson = new Gson();
            List<CauldronRecipe> loadedRecipes = gson.fromJson(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8),
                new TypeToken<List<CauldronRecipe>>(){}.getType()
            );

            recipes.addAll(loadedRecipes);
            
            UltimateMagic.LOGGER.info("Erfolgreich " + recipes.size() + " Rezepte geladen");
            
            // Debug-Ausgabe für jedes geladene Rezept
            for (CauldronRecipe recipe : loadedRecipes) {
                UltimateMagic.LOGGER.debug("Geladenes Rezept: " + recipe.getRecipeId());
                recipe.getIngredients().forEach((item, count) -> 
                    UltimateMagic.LOGGER.debug("  - " + item + " x" + count));
            }
            
        } catch (Exception e) {
            UltimateMagic.LOGGER.error("Fehler beim Laden der Rezepte", e);
        }
    }

    public static void checkAndCraft(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld)) return;

        CauldronTracker.CauldronContent content = CauldronTracker.getCauldronAt((ServerWorld) world, pos);
        if (content == null) return;

        for (CauldronRecipe recipe : recipes) {
            if (matchesRecipe(content, recipe)) {
                craftRecipe(world, pos, recipe);
                break;
            }
        }
    }

    private static boolean matchesRecipe(CauldronTracker.CauldronContent content, CauldronRecipe recipe) {
        // Prüfe Flüssigkeit und Level
        if (!recipe.getFluid().equals(content.fluidType)) {
            return false;
        }
        if (content.fluidLevel < recipe.getFluidLevel()) {
            return false;
        }

        // Prüfe Zutaten
        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            String itemName = entry.getKey();
            int requiredCount = entry.getValue();
            
            int foundCount = content.getItemCount(itemName);
            if (foundCount != requiredCount) {
                return false;
            }
        }

        return true;
    }

    private static void craftRecipe(World world, BlockPos pos, CauldronRecipe recipe) {
        // Entferne Zutaten
        Box searchBox = new Box(pos).expand(0.125);
        List<ItemEntity> itemsInCauldron = world.getEntitiesByClass(ItemEntity.class, searchBox, entity -> true);

        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            String itemName = entry.getKey();
            int remainingToRemove = entry.getValue();

            for (ItemEntity entity : itemsInCauldron) {
                if (remainingToRemove <= 0) break;

                if (entity.getStack().getItem().getName().getString().equals(itemName)) {
                    int count = entity.getStack().getCount();
                    if (count <= remainingToRemove) {
                        remainingToRemove -= count;
                        entity.discard();
                    } else {
                        entity.getStack().decrement(remainingToRemove);
                        remainingToRemove = 0;
                    }
                }
            }
        }

        // Aktualisiere Flüssigkeitsstand
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof LeveledCauldronBlock) {
            int newLevel = state.get(LeveledCauldronBlock.LEVEL) - recipe.getFluidConsume();
            if (newLevel > 0) {
                world.setBlockState(pos, state.with(LeveledCauldronBlock.LEVEL, newLevel));
            } else {
                world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
            }
        }

        // Erstelle das Ergebnis
        Identifier resultId = new Identifier("ultimatemagic", recipe.getResult());
        Item resultItem = Registries.ITEM.get(resultId);
        ItemStack resultStack = new ItemStack(resultItem, recipe.getResultCount());

        UltimateMagic.LOGGER.debug("Erstelle Item: " + resultId); // Debug-Ausgabe

        ItemEntity result = new ItemEntity(world, 
            pos.getX() + 0.5, 
            pos.getY() + 0.5, 
            pos.getZ() + 0.5, 
            resultStack
        );

        world.spawnEntity(result);
    }
}