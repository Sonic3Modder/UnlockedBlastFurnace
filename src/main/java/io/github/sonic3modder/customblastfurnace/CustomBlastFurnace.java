package io.github.sonic3modder.customblastfurnace;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

public class CustomBlastFurnace extends JavaPlugin {

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        getLogger().info("CustomBlastFurnace plugin enabled!");
        
        // Load custom recipes
        loadCustomRecipes();
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomBlastFurnace plugin disabled!");
    }

    private void loadCustomRecipes() {
        // Check if we should allow all items in blast furnace
        boolean allowAll = getConfig().getBoolean("allow-all-items", false);
        
        if (allowAll) {
            getLogger().info("Allow-all mode enabled - copying all furnace recipes to blast furnace");
            enableAllItems();
        }
        
        // Load custom recipes from config
        if (getConfig().contains("custom-recipes")) {
            var recipes = getConfig().getConfigurationSection("custom-recipes");
            if (recipes != null) {
                for (String key : recipes.getKeys(false)) {
                    try {
                        String input = recipes.getString(key + ".input");
                        String output = recipes.getString(key + ".output");
                        int outputAmount = recipes.getInt(key + ".output-amount", 1);
                        float experience = (float) recipes.getDouble(key + ".experience", 0.1);
                        int cookingTime = recipes.getInt(key + ".cooking-time", 100);
                        
                        if (input == null || output == null) {
                            getLogger().warning("Invalid recipe: " + key);
                            continue;
                        }
                        
                        Material inputMaterial = Material.matchMaterial(input.toUpperCase());
                        Material outputMaterial = Material.matchMaterial(output.toUpperCase());
                        
                        if (inputMaterial == null || outputMaterial == null) {
                            getLogger().warning("Invalid materials in recipe: " + key);
                            continue;
                        }
                        
                        addBlastingRecipe(key, inputMaterial, outputMaterial, outputAmount, experience, cookingTime);
                        getLogger().info("Added custom recipe: " + input + " -> " + output);
                        
                    } catch (Exception e) {
                        getLogger().warning("Error loading recipe " + key + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private void addBlastingRecipe(String key, Material input, Material output, int amount, float exp, int cookTime) {
        NamespacedKey recipeKey = new NamespacedKey(this, key.toLowerCase().replace(" ", "_"));
        
        // Remove existing recipe if it exists
        Bukkit.removeRecipe(recipeKey);
        
        ItemStack result = new ItemStack(output, amount);
        BlastingRecipe recipe = new BlastingRecipe(
            recipeKey,
            result,
            new RecipeChoice.MaterialChoice(input),
            exp,
            cookTime
        );
        
        Bukkit.addRecipe(recipe);
    }

    private void enableAllItems() {
        // This copies all regular furnace recipes to blast furnace
        // In practice, you'd iterate through furnace recipes and create blast furnace versions
        
        // Example: Add some common items that aren't normally in blast furnace
        addCommonRecipes();
    }

    private void addCommonRecipes() {
        // Add cobblestone -> stone
        addBlastingRecipe("cobblestone_to_stone", Material.COBBLESTONE, Material.STONE, 1, 0.1f, 100);
        
        // Add sand -> glass
        addBlastingRecipe("sand_to_glass", Material.SAND, Material.GLASS, 1, 0.1f, 100);
        
        // Add log types -> charcoal
        addBlastingRecipe("oak_log_to_charcoal", Material.OAK_LOG, Material.CHARCOAL, 1, 0.15f, 100);
        addBlastingRecipe("birch_log_to_charcoal", Material.BIRCH_LOG, Material.CHARCOAL, 1, 0.15f, 100);
        addBlastingRecipe("spruce_log_to_charcoal", Material.SPRUCE_LOG, Material.CHARCOAL, 1, 0.15f, 100);
        
        // Add raw food -> cooked food
        addBlastingRecipe("raw_beef_to_steak", Material.BEEF, Material.COOKED_BEEF, 1, 0.35f, 100);
        addBlastingRecipe("raw_porkchop_to_cooked", Material.PORKCHOP, Material.COOKED_PORKCHOP, 1, 0.35f, 100);
        addBlastingRecipe("raw_chicken_to_cooked", Material.CHICKEN, Material.COOKED_CHICKEN, 1, 0.35f, 100);
        
        getLogger().info("Added common furnace recipes to blast furnace");
    }
}