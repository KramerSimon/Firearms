package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModFluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class GasCentrifugeJeiRecipe {

    private final FluidStack inputFluid;
    private final FluidStack outputFluid1;
    private final FluidStack outputFluid2;

    public GasCentrifugeJeiRecipe(FluidStack inputFluid, FluidStack outputFluid1, FluidStack outputFluid2) {
        this.inputFluid  = inputFluid;
        this.outputFluid1 = outputFluid1;
        this.outputFluid2 = outputFluid2;
    }

    public FluidStack getInputFluid()  { return inputFluid; }
    public FluidStack getOutputFluid1() { return outputFluid1; }
    public FluidStack getOutputFluid2() { return outputFluid2; }

    public static List<GasCentrifugeJeiRecipe> getAllRecipes() {
        return List.of(
            new GasCentrifugeJeiRecipe(
                new FluidStack(ModFluids.URANIUM_HEXAFLUORIDE_STILL.get(), 1000),
                new FluidStack(ModFluids.ENRICHED_UF6_STILL.get(), 300),
                new FluidStack(ModFluids.DEPLETED_UF6_STILL.get(), 700))
        );
    }
}
