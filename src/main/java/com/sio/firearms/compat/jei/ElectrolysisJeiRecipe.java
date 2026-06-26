package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class ElectrolysisJeiRecipe {

    private final ItemStack inputItem;   // may be EMPTY (no item catalyst)
    private final FluidStack inputFluid;
    private final FluidStack output1;
    private final FluidStack output2;

    public ElectrolysisJeiRecipe(ItemStack inputItem, FluidStack inputFluid,
                                  FluidStack output1, FluidStack output2) {
        this.inputItem  = inputItem;
        this.inputFluid = inputFluid;
        this.output1    = output1;
        this.output2    = output2;
    }

    public ItemStack  getInputItem()  { return inputItem; }
    public FluidStack getInputFluid() { return inputFluid; }
    public FluidStack getOutput1()    { return output1; }
    public FluidStack getOutput2()    { return output2; }

    public static List<ElectrolysisJeiRecipe> getAllRecipes() {
        return List.of(
            // water 1000 mB → hydrogen 500 + oxygen 500
            new ElectrolysisJeiRecipe(
                ItemStack.EMPTY,
                new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 1000),
                new FluidStack(ModFluids.HYDROGEN_GAS_STILL.get(), 500),
                new FluidStack(ModFluids.OXYGEN_GAS_STILL.get(), 500)),

            // saltpeter + water 500 → hydrogen 250 + nitrate_solution 500
            new ElectrolysisJeiRecipe(
                new ItemStack(ModItems.SALTPETER.get()),
                new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 500),
                new FluidStack(ModFluids.HYDROGEN_GAS_STILL.get(), 250),
                new FluidStack(ModFluids.NITRATE_SOLUTION_STILL.get(), 500)),

            // fluorite_crystal + water 500 → fluorine_gas 500 + hydrogen 250
            new ElectrolysisJeiRecipe(
                new ItemStack(ModItems.FLUORITE_CRYSTAL.get()),
                new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 500),
                new FluidStack(ModFluids.FLUORINE_GAS_STILL.get(), 500),
                new FluidStack(ModFluids.HYDROGEN_GAS_STILL.get(), 250)),

            // sand + water 500 → chlorine_gas 500 + hydrogen 250
            new ElectrolysisJeiRecipe(
                new ItemStack(net.minecraft.world.item.Items.SAND),
                new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 500),
                new FluidStack(ModFluids.CHLORINE_GAS_STILL.get(), 500),
                new FluidStack(ModFluids.HYDROGEN_GAS_STILL.get(), 250))
        );
    }
}
