package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DicingSawJeiRecipe {

    private final ItemStack input;
    private final ItemStack tool;
    private final ItemStack output;

    public DicingSawJeiRecipe(ItemStack input, ItemStack tool, ItemStack output) {
        this.input  = input;
        this.tool   = tool;
        this.output = output;
    }

    public ItemStack getInput()  { return input; }
    public ItemStack getTool()   { return tool; }
    public ItemStack getOutput() { return output; }

    public static List<DicingSawJeiRecipe> getAllRecipes() {
        return List.of(
            new DicingSawJeiRecipe(
                new ItemStack(ModItems.TESTED_WAFER.get()),
                new ItemStack(ModItems.DIAMOND_SAW_BLADE.get()),
                new ItemStack(ModItems.SILICON_DIE.get(), 8))
        );
    }
}
