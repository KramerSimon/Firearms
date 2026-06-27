package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class PlasmaEtcherJeiRecipe {

    private final ItemStack input;
    private final ItemStack output;

    // 500 mB chlorine gas is always required — shown as a fixed fluid input
    public static final FluidStack CHLORINE_INPUT = new FluidStack(ModFluids.CHLORINE_GAS_STILL.get(), 500);

    public PlasmaEtcherJeiRecipe(ItemStack input, ItemStack output) {
        this.input  = input;
        this.output = output;
    }

    public ItemStack getInput()  { return input; }
    public ItemStack getOutput() { return output; }

    public static List<PlasmaEtcherJeiRecipe> getAllRecipes() {
        return List.of(
            new PlasmaEtcherJeiRecipe(
                new ItemStack(ModItems.PATTERNED_WAFER.get()),
                new ItemStack(ModItems.ETCHED_WAFER.get()))
        );
    }
}
