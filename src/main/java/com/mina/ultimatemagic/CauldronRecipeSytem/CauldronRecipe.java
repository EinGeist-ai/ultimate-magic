package com.mina.ultimatemagic.CauldronRecipeSytem;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class CauldronRecipe {
    @SerializedName("recipe_id")
    private String recipeId;
    private Map<String, Integer> ingredients;
    private String fluid;
    @SerializedName("fluid_level")
    private int fluidLevel;
    @SerializedName("fluid_consume")
    private int fluidConsume;
    private String result;
    @SerializedName("result_count")
    private int resultCount;

    // Getter-Methoden
    public String getRecipeId() { return recipeId; }
    public Map<String, Integer> getIngredients() { return ingredients; }
    public String getFluid() { return fluid; }
    public int getFluidLevel() { return fluidLevel; }
    public int getFluidConsume() { return fluidConsume; }
    public String getResult() { return result; }
    public int getResultCount() { return resultCount; }
}