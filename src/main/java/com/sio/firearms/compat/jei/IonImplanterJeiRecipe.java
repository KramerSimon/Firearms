package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class IonImplanterJeiRecipe {

    private final ItemStack input;
    private final ItemStack dopant;
    private final ItemStack output;

    public IonImplanterJeiRecipe(ItemStack input, ItemStack dopant, ItemStack output) {
        this.input  = input;
        this.dopant = dopant;
        this.output = output;
    }

    public ItemStack getInput()  { return input; }
    public ItemStack getDopant() { return dopant; }
    public ItemStack getOutput() { return output; }

    public static List<IonImplanterJeiRecipe> getAllRecipes() {
        return List.of(
            new IonImplanterJeiRecipe(
                new ItemStack(ModItems.ETCHED_WAFER.get()),
                new ItemStack(ModItems.BORON.get()),
                new ItemStack(ModItems.DOPED_WAFER.get())),
            new IonImplanterJeiRecipe(
                new ItemStack(ModItems.ETCHED_WAFER.get()),
                new ItemStack(ModItems.PHOSPHORUS.get()),
                new ItemStack(ModItems.DOPED_WAFER.get()))
        );
    }
}
