package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class MetallizationChamberJeiRecipe {

    private final ItemStack input;
    private final ItemStack secondary;
    private final ItemStack output;

    public MetallizationChamberJeiRecipe(ItemStack input, ItemStack secondary, ItemStack output) {
        this.input     = input;
        this.secondary = secondary;
        this.output    = output;
    }

    public ItemStack getInput()     { return input; }
    public ItemStack getSecondary() { return secondary; }
    public ItemStack getOutput()    { return output; }

    public static List<MetallizationChamberJeiRecipe> getAllRecipes() {
        return List.of(
            new MetallizationChamberJeiRecipe(
                new ItemStack(ModItems.DOPED_WAFER.get()),
                new ItemStack(Items.COPPER_INGOT),
                new ItemStack(ModItems.FINISHED_WAFER.get()))
        );
    }
}
