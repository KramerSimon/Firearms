package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WaferCuttingMachineJeiRecipe {

    private final ItemStack input;
    private final ItemStack tool;
    private final ItemStack output;

    public WaferCuttingMachineJeiRecipe(ItemStack input, ItemStack tool, ItemStack output) {
        this.input  = input;
        this.tool   = tool;
        this.output = output;
    }

    public ItemStack getInput()  { return input; }
    public ItemStack getTool()   { return tool; }
    public ItemStack getOutput() { return output; }

    public static List<WaferCuttingMachineJeiRecipe> getAllRecipes() {
        return List.of(
            new WaferCuttingMachineJeiRecipe(
                new ItemStack(ModItems.SILICON_INGOT.get()),
                new ItemStack(ModItems.DIAMOND_SAW_BLADE.get()),
                new ItemStack(ModItems.SILICON_WAFER.get(), 4))
        );
    }
}
