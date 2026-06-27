package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WaferTesterJeiRecipe {

    private final ItemStack input;
    private final ItemStack outputGood;
    private final ItemStack outputDefective;

    public WaferTesterJeiRecipe(ItemStack input, ItemStack outputGood, ItemStack outputDefective) {
        this.input           = input;
        this.outputGood      = outputGood;
        this.outputDefective = outputDefective;
    }

    public ItemStack getInput()           { return input; }
    public ItemStack getOutputGood()      { return outputGood; }
    public ItemStack getOutputDefective() { return outputDefective; }

    public static List<WaferTesterJeiRecipe> getAllRecipes() {
        return List.of(
            new WaferTesterJeiRecipe(
                new ItemStack(ModItems.FINISHED_WAFER.get()),
                new ItemStack(ModItems.TESTED_WAFER.get()),
                new ItemStack(ModItems.DEFECTIVE_WAFER.get()))
        );
    }
}
