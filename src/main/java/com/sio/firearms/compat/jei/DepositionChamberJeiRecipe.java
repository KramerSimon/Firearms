package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DepositionChamberJeiRecipe {

    private final ItemStack input;
    private final ItemStack secondary;
    private final ItemStack output;

    public DepositionChamberJeiRecipe(ItemStack input, ItemStack secondary, ItemStack output) {
        this.input     = input;
        this.secondary = secondary;
        this.output    = output;
    }

    public ItemStack getInput()     { return input; }
    public ItemStack getSecondary() { return secondary; }
    public ItemStack getOutput()    { return output; }

    public static List<DepositionChamberJeiRecipe> getAllRecipes() {
        return List.of(
            new DepositionChamberJeiRecipe(
                new ItemStack(ModItems.SILICON_WAFER.get()),
                new ItemStack(ModItems.ALUMINUM_INGOT.get()),
                new ItemStack(ModItems.COATED_WAFER.get()))
        );
    }
}
