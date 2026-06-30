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
    private final FluidStack inputFluid;      // tank1
    private final FluidStack secondInputFluid; // tank2 — may be EMPTY
    private final ItemStack outputItem;       // may be EMPTY
    private final FluidStack outputFluid;     // may be EMPTY

    public ChemicalMixerJeiRecipe(ItemStack inputItem, ItemStack secondInputItem,
                                   FluidStack inputFluid, FluidStack secondInputFluid,
                                   ItemStack outputItem, FluidStack outputFluid) {
        this.inputItem         = inputItem;
        this.secondInputItem   = secondInputItem;
        this.inputFluid        = inputFluid;
        this.secondInputFluid  = secondInputFluid;
        this.outputItem        = outputItem;
        this.outputFluid       = outputFluid;
    }

    // No second fluid (most recipes)
    public ChemicalMixerJeiRecipe(ItemStack inputItem, ItemStack secondInputItem, FluidStack inputFluid,
                                   ItemStack outputItem, FluidStack outputFluid) {
        this(inputItem, secondInputItem, inputFluid, FluidStack.EMPTY, outputItem, outputFluid);
    }

    // Single-item-input convenience constructor (second item slot empty, no second fluid)
    public ChemicalMixerJeiRecipe(ItemStack inputItem, FluidStack inputFluid,
                                   ItemStack outputItem, FluidStack outputFluid) {
        this(inputItem, ItemStack.EMPTY, inputFluid, FluidStack.EMPTY, outputItem, outputFluid);
    }

    public ItemStack  getInputItem()         { return inputItem; }
    public ItemStack  getSecondInputItem()   { return secondInputItem; }
    public FluidStack getInputFluid()        { return inputFluid; }
    public FluidStack getSecondInputFluid()  { return secondInputFluid; }
    public ItemStack  getOutputItem()        { return outputItem; }
    public FluidStack getOutputFluid()       { return outputFluid; }

    public static List<ChemicalMixerJeiRecipe> getAllRecipes() {
        return List.of(
            // sugar + nitric_acid 500mB + sulfuric_acid 250mB → 2x nitroglycerin
            new ChemicalMixerJeiRecipe(
                new ItemStack(net.minecraft.world.item.Items.SUGAR), ItemStack.EMPTY,
                new FluidStack(ModFluids.NITRIC_ACID_STILL.get(), 500),
                new FluidStack(ModFluids.SULFURIC_ACID_STILL.get(), 250),
                new ItemStack(ModItems.NITROGLYCERIN.get(), 2),
                FluidStack.EMPTY),

            // sulfur + saltpeter + water 100mB → 8x propellant_powder
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.SULFUR.get()),
                new ItemStack(ModItems.SALTPETER.get()),
                new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 100),
                new ItemStack(ModItems.PROPELLANT_POWDER.get(), 8),
                FluidStack.EMPTY),

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
                FluidStack.EMPTY),

            // naphtha 500mB (tank2) + nitric_acid 500mB (tank1) → photoresist 1000mB
            new ChemicalMixerJeiRecipe(
                ItemStack.EMPTY, ItemStack.EMPTY,
                new FluidStack(ModFluids.NITRIC_ACID_STILL.get(), 500),
                new FluidStack(ModFluids.NAPHTHA_STILL.get(), 500),
                ItemStack.EMPTY,
                new FluidStack(ModFluids.PHOTORESIST_STILL.get(), 1000)),

            // synthetic_rubber + nitric_acid 500mB → photoresist 1000mB
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.SYNTHETIC_RUBBER.get()),
                new FluidStack(ModFluids.NITRIC_ACID_STILL.get(), 500),
                ItemStack.EMPTY,
                new FluidStack(ModFluids.PHOTORESIST_STILL.get(), 1000)),

            // ── Nuclear Reactor Stage 1 ───────────────────────────────────────
            // uranium_ingot + fluorine_gas_bucket → uranium_hexafluoride 1000mB
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.URANIUM_INGOT.get()),
                new ItemStack(ModItems.FLUORINE_GAS_BUCKET.get()),
                FluidStack.EMPTY,
                ItemStack.EMPTY,
                new FluidStack(ModFluids.URANIUM_HEXAFLUORIDE_STILL.get(), 1000)),

            // enriched_uf6 500mB → uranium_dioxide_powder x4
            new ChemicalMixerJeiRecipe(
                ItemStack.EMPTY,
                new FluidStack(ModFluids.ENRICHED_UF6_STILL.get(), 500),
                new ItemStack(ModItems.URANIUM_DIOXIDE_POWDER.get(), 4),
                FluidStack.EMPTY),

            // boron + coal → boron_carbide x2 (no fluid)
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.BORON.get()),
                new ItemStack(net.minecraft.world.item.Items.COAL),
                FluidStack.EMPTY,
                new ItemStack(ModItems.BORON_CARBIDE.get(), 2),
                FluidStack.EMPTY),

            // water 1000mB → heavy_water 500mB
            new ChemicalMixerJeiRecipe(
                ItemStack.EMPTY,
                new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 1000),
                ItemStack.EMPTY,
                new FluidStack(ModFluids.HEAVY_WATER_STILL.get(), 500)),

            // pvc_resin 1000mB → pvc_pellets x4
            new ChemicalMixerJeiRecipe(
                ItemStack.EMPTY, ItemStack.EMPTY,
                new FluidStack(ModFluids.PVC_RESIN_STILL.get(), 1000),
                new ItemStack(ModItems.PVC_PELLETS.get(), 4),
                FluidStack.EMPTY),

            // ── Pharmaceutical ────────────────────────────────────────────────
            // raw_opium + water 250mB → refined_opium
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.RAW_OPIUM.get()),
                new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 250),
                new ItemStack(ModItems.REFINED_OPIUM.get()),
                FluidStack.EMPTY),

            // refined_opium + sulfuric_acid 100mB → 2x morphine
            new ChemicalMixerJeiRecipe(
                new ItemStack(ModItems.REFINED_OPIUM.get()),
                new FluidStack(ModFluids.SULFURIC_ACID_STILL.get(), 100),
                new ItemStack(ModItems.MORPHINE.get(), 2),
                FluidStack.EMPTY),

            // sugar + glass_bottle + nitric_acid 100mB → adrenaline
            new ChemicalMixerJeiRecipe(
                new ItemStack(net.minecraft.world.item.Items.SUGAR),
                new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE),
                new FluidStack(ModFluids.NITRIC_ACID_STILL.get(), 100),
                new ItemStack(ModItems.ADRENALINE.get()),
                FluidStack.EMPTY),

            // spider_eye + saltpeter + water 100mB → 2x coagulant
            new ChemicalMixerJeiRecipe(
                new ItemStack(net.minecraft.world.item.Items.SPIDER_EYE),
                new ItemStack(ModItems.SALTPETER.get()),
                new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 100),
                new ItemStack(ModItems.COAGULANT.get(), 2),
                FluidStack.EMPTY)
        );
    }
}
