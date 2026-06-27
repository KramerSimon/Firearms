package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CrystalGrowthJeiRecipe {

    private final ItemStack input;
    private final ItemStack output;

    public CrystalGrowthJeiRecipe(ItemStack input, ItemStack output) {
        this.input  = input;
        this.output = output;
    }

    public ItemStack getInput()  { return input; }
    public ItemStack getOutput() { return output; }

    public static List<CrystalGrowthJeiRecipe> getAllRecipes() {
        return List.of(new CrystalGrowthJeiRecipe(
                new ItemStack(ModItems.ELECTRONIC_GRADE_SILICON.get()),
                new ItemStack(ModItems.SILICON_INGOT.get())));
    }
}
