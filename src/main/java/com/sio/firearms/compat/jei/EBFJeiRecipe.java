package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * A single Electric Blast Furnace conversion for the JEI page. Smelting is powered
 * purely by FE now, so each recipe is one material input → one output at a required
 * coil temperature. Kept in sync with EBFControllerBlockEntity#getRecipeOutput.
 */
public class EBFJeiRecipe {

    private final ItemStack input;
    private final ItemStack output;
    private final int requiredTemp;

    public EBFJeiRecipe(ItemStack input, ItemStack output, int requiredTemp) {
        this.input        = input;
        this.output       = output;
        this.requiredTemp = requiredTemp;
    }

    public ItemStack getInput()  { return input; }
    public ItemStack getOutput() { return output; }
    public int getRequiredTemp() { return requiredTemp; }

    public static List<EBFJeiRecipe> getAllRecipes() {
        return List.of(
            // ── @800°C ────────────────────────────────────────────────────────
            new EBFJeiRecipe(new ItemStack(Items.RAW_IRON),
                    new ItemStack(ModItems.STEEL_INGOT.get(), 2), 800),
            new EBFJeiRecipe(new ItemStack(Items.IRON_INGOT),
                    new ItemStack(ModItems.STEEL_INGOT.get(), 3), 800),
            new EBFJeiRecipe(new ItemStack(ModItems.STEEL_INGOT.get()),
                    new ItemStack(ModItems.HARDENED_STEEL_INGOT.get()), 800),
            new EBFJeiRecipe(new ItemStack(Items.RAW_GOLD),
                    new ItemStack(Items.GOLD_INGOT, 2), 800),
            new EBFJeiRecipe(new ItemStack(Items.COPPER_INGOT),
                    new ItemStack(ModItems.CARBON_STEEL.get()), 800),
            new EBFJeiRecipe(new ItemStack(Items.COAL),
                    new ItemStack(ModItems.GRAPHITE_BLOCK_ITEM.get(), 2), 800),
            new EBFJeiRecipe(new ItemStack(ModItems.ZIRCONIUM_ORE_RAW.get()),
                    new ItemStack(ModItems.ZIRCONIUM_INGOT.get()), 800),
            new EBFJeiRecipe(new ItemStack(ModItems.TUNGSTEN_INGOT.get()),
                    new ItemStack(ModItems.TUNGSTEN_CARBIDE.get(), 2), 800),
            // ── @1200°C ───────────────────────────────────────────────────────
            new EBFJeiRecipe(new ItemStack(ModItems.TUNGSTEN_ORE_RAW.get()),
                    new ItemStack(ModItems.TUNGSTEN_INGOT.get()), 1200),
            new EBFJeiRecipe(new ItemStack(ModItems.TITANIUM_ORE_RAW.get()),
                    new ItemStack(ModItems.TITANIUM_INGOT.get()), 1200),
            // ── @2000°C ───────────────────────────────────────────────────────
            new EBFJeiRecipe(new ItemStack(ModItems.URANIUM_ORE_RAW.get()),
                    new ItemStack(ModItems.URANIUM_INGOT.get()), 2000)
        );
    }
}
