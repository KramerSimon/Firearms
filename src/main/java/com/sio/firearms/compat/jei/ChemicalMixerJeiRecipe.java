package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class ChemicalMixerJeiRecipe {

    private final ItemStack inputItem;        // slot A — may be EMPTY
    private final ItemStack secondInputItem;  // slot B — may be EMPTY
    private final FluidStack inputFluid;
    private final ItemStack outputItem;       // may be EMPTY
    private final FluidStack outputFluid;     // may be EMPTY

    public ChemicalMixerJeiRecipe(ItemStack inputItem, ItemStack secondInputItem, FluidStack inputFluid,
                                   ItemStack outputItem, FluidStack outputFluid) {
        this.inputItem        = inputItem;
        this.secondInputItem  = secondInputItem;
        this.inputFluid       = inputFluid;
        this.outputItem       = outputItem;
        this.outputFluid      = outputFluid;
    }

    // Single-item-input convenience constructor (second slot empty)
    public ChemicalMixerJeiRecipe(ItemStack inputItem, FluidStack inputFluid,
                                   ItemStack outputItem, FluidStack outputFluid) {
        this(inputItem, ItemStack.EMPTY, inputFluid, outputItem, outputFluid);
    }

    public ItemStack  getInputItem()        { return inputItem; }
    public ItemStack  getSecondInputItem()  { return secondInputItem; }
    public FluidStack getInputFluid()       { return inputFluid; }
    public ItemStack  getOutputItem()       { return outputItem; }
    public FluidStack getOutputFluid()      { return outputFluid; }

    public static List<ChemicalMixerJeiRecipe> getAllRecipes() {
        return List.of(
            // sulfur + water → sulfuric acid
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.SULFUR.get()),
                new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 500),
                ItemStack.EMPTY,
                new FluidStack(ModFluids.SULFURIC_ACID_STILL.get(), 500)),

            // rubber_sheet + fuel → synthetic_rubber
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.RUBBER_SHEET.get()),
                new FluidStack(ModFluids.FUEL_STILL.get(), 500),
                ItemStack.EMPTY,
                new FluidStack(ModFluids.SYNTHETIC_RUBBER_STILL.get(), 500)),

            // sand + quartz → quartz_sand×2  (no fluid)
            new ChemicalMixerJeiRecipe(
                new ItemStack(Items.SAND),
                FluidStack.EMPTY,
                new ItemStack(ModItems.QUARTZ_SAND.get(), 2),
                FluidStack.EMPTY),

            // saltpeter + sulfuric_acid → nitric_acid
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.SALTPETER.get()),
                new FluidStack(ModFluids.SULFURIC_ACID_STILL.get(), 250),
                ItemStack.EMPTY,
                new FluidStack(ModFluids.NITRIC_ACID_STILL.get(), 250)),

            // paper + nitric_acid → nitrocellulose
            new ChemicalMixerJeiRecipe(
                new ItemStack(Items.PAPER),
                new FluidStack(ModFluids.NITRIC_ACID_STILL.get(), 250),
                new ItemStack(ModItems.NITROCELLULOSE.get()),
                FluidStack.EMPTY),

            // chlorine_gas_bucket + fuel → pvc_resin
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.CHLORINE_GAS_BUCKET.get()),
                new FluidStack(ModFluids.FUEL_STILL.get(), 500),
                ItemStack.EMPTY,
                new FluidStack(ModFluids.PVC_RESIN_STILL.get(), 500)),

            // sulfur + saltpeter + water 250mB → refined_gunpowder x4
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.SULFUR.get()),
                new ItemStack(ModItems.SALTPETER.get()),
                new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 250),
                new ItemStack(ModItems.REFINED_GUNPOWDER.get(), 4),
                FluidStack.EMPTY),

            // bauxite_dust + sulfuric_acid 500mB → aluminum_ingot x2
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.BAUXITE_DUST.get()),
                new FluidStack(ModFluids.SULFURIC_ACID_STILL.get(), 500),
                new ItemStack(ModItems.ALUMINUM_INGOT.get(), 2),
                FluidStack.EMPTY),

            // nickel_ingot + chromium_ingot (no fluid) → nichrome_alloy x2
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.NICKEL_INGOT.get()),
                new ItemStack(ModItems.CHROMIUM_INGOT.get()),
                FluidStack.EMPTY,
                new ItemStack(ModItems.NICHROME_ALLOY.get(), 2),
                FluidStack.EMPTY),

            // metallurgical_silicon + sulfuric_acid 500mB + nitric_acid_bucket (250mB) → electronic_grade_silicon
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.METALLURGICAL_SILICON.get()),
                new ItemStack(ModItems.NITRIC_ACID_BUCKET.get()),
                new FluidStack(ModFluids.SULFURIC_ACID_STILL.get(), 500),
                new ItemStack(ModItems.ELECTRONIC_GRADE_SILICON.get()),
                FluidStack.EMPTY)
        );
    }
}
