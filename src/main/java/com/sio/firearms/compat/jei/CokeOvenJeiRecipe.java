package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class CokeOvenJeiRecipe {

    private final ItemStack input;
    private final ItemStack outputItem;
    private final FluidStack outputFluid;

    public CokeOvenJeiRecipe(ItemStack input, ItemStack outputItem, FluidStack outputFluid) {
        this.input       = input;
        this.outputItem  = outputItem;
        this.outputFluid = outputFluid;
    }

    public ItemStack  getInput()       { return input; }
    public ItemStack  getOutputItem()  { return outputItem; }
    public FluidStack getOutputFluid() { return outputFluid; }

    public static List<CokeOvenJeiRecipe> getAllRecipes() {
        return List.of(
            new CokeOvenJeiRecipe(
                new ItemStack(Items.COAL),
                new ItemStack(ModItems.COAL_COKE.get()),
                new FluidStack(ModFluids.CREOSOTE_OIL_STILL.get(), 100))
        );
    }
}
