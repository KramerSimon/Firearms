package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Firearms.MOD_ID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, Firearms.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> OIL_TYPE =
            FLUID_TYPES.register("oil", () -> new FluidType(FluidType.Properties.create()
                    .density(1500)
                    .viscosity(3000)
                    .lightLevel(0)
                    .canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> OIL_STILL =
            FLUIDS.register("oil_still", () -> new BaseFlowingFluid.Source(ModFluids.oilProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> OIL_FLOWING =
            FLUIDS.register("oil_flowing", () -> new BaseFlowingFluid.Flowing(ModFluids.oilProperties()));

    public static final DeferredHolder<FluidType, FluidType> FUEL_TYPE =
            FLUID_TYPES.register("fuel", () -> new FluidType(FluidType.Properties.create()
                    .density(800)
                    .viscosity(1000)
                    .lightLevel(0)
                    .canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> FUEL_STILL =
            FLUIDS.register("fuel_still", () -> new BaseFlowingFluid.Source(ModFluids.fuelProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> FUEL_FLOWING =
            FLUIDS.register("fuel_flowing", () -> new BaseFlowingFluid.Flowing(ModFluids.fuelProperties()));

    public static BaseFlowingFluid.Properties oilProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> OIL_TYPE.get(),
                        () -> OIL_STILL.get(),
                        () -> OIL_FLOWING.get())
                .bucket(() -> ModItems.OIL_BUCKET.get())
                .block(() -> ModBlocks.OIL_FLUID.get())
                .slopeFindDistance(3)
                .levelDecreasePerBlock(2)
                .tickRate(10);
    }

    public static final DeferredHolder<FluidType, FluidType> CREOSOTE_OIL_TYPE =
            FLUID_TYPES.register("creosote_oil", () -> new FluidType(FluidType.Properties.create()
                    .density(1200)
                    .viscosity(2000)
                    .lightLevel(0)
                    .canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> CREOSOTE_OIL_STILL =
            FLUIDS.register("creosote_oil_still", () -> new BaseFlowingFluid.Source(ModFluids.creosoteProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> CREOSOTE_OIL_FLOWING =
            FLUIDS.register("creosote_oil_flowing", () -> new BaseFlowingFluid.Flowing(ModFluids.creosoteProperties()));

    public static BaseFlowingFluid.Properties creosoteProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> CREOSOTE_OIL_TYPE.get(),
                        () -> CREOSOTE_OIL_STILL.get(),
                        () -> CREOSOTE_OIL_FLOWING.get())
                .bucket(() -> ModItems.CREOSOTE_OIL_BUCKET.get())
                .block(() -> ModBlocks.CREOSOTE_OIL_FLUID.get())
                .slopeFindDistance(3)
                .levelDecreasePerBlock(2)
                .tickRate(15);
    }

    public static BaseFlowingFluid.Properties fuelProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> FUEL_TYPE.get(),
                        () -> FUEL_STILL.get(),
                        () -> FUEL_FLOWING.get())
                .bucket(() -> ModItems.FUEL_BUCKET.get())
                .block(() -> ModBlocks.FUEL_FLUID.get())
                .slopeFindDistance(4)
                .levelDecreasePerBlock(1)
                .tickRate(5);
    }

    // ── Sulfuric Acid ────────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> SULFURIC_ACID_TYPE =
            FLUID_TYPES.register("sulfuric_acid", () -> new FluidType(FluidType.Properties.create()
                    .density(1840)
                    .viscosity(4000)
                    .lightLevel(1)
                    .canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> SULFURIC_ACID_STILL =
            FLUIDS.register("sulfuric_acid_still", () -> new BaseFlowingFluid.Source(sulfuricAcidProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> SULFURIC_ACID_FLOWING =
            FLUIDS.register("sulfuric_acid_flowing", () -> new BaseFlowingFluid.Flowing(sulfuricAcidProperties()));

    public static BaseFlowingFluid.Properties sulfuricAcidProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> SULFURIC_ACID_TYPE.get(),
                        () -> SULFURIC_ACID_STILL.get(),
                        () -> SULFURIC_ACID_FLOWING.get())
                .bucket(() -> ModItems.SULFURIC_ACID_BUCKET.get())
                .block(() -> ModBlocks.SULFURIC_ACID_FLUID.get())
                .slopeFindDistance(3)
                .levelDecreasePerBlock(2)
                .tickRate(10);
    }

    // ── Nitric Acid ──────────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> NITRIC_ACID_TYPE =
            FLUID_TYPES.register("nitric_acid", () -> new FluidType(FluidType.Properties.create()
                    .density(1510)
                    .viscosity(2000)
                    .lightLevel(1)
                    .canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> NITRIC_ACID_STILL =
            FLUIDS.register("nitric_acid_still", () -> new BaseFlowingFluid.Source(nitricAcidProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> NITRIC_ACID_FLOWING =
            FLUIDS.register("nitric_acid_flowing", () -> new BaseFlowingFluid.Flowing(nitricAcidProperties()));

    public static BaseFlowingFluid.Properties nitricAcidProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> NITRIC_ACID_TYPE.get(),
                        () -> NITRIC_ACID_STILL.get(),
                        () -> NITRIC_ACID_FLOWING.get())
                .bucket(() -> ModItems.NITRIC_ACID_BUCKET.get())
                .block(() -> ModBlocks.NITRIC_ACID_FLUID.get())
                .slopeFindDistance(3)
                .levelDecreasePerBlock(2)
                .tickRate(10);
    }

    // ── Synthetic Rubber ─────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> SYNTHETIC_RUBBER_TYPE =
            FLUID_TYPES.register("synthetic_rubber", () -> new FluidType(FluidType.Properties.create()
                    .density(1100)
                    .viscosity(8000)
                    .lightLevel(0)
                    .canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> SYNTHETIC_RUBBER_STILL =
            FLUIDS.register("synthetic_rubber_still", () -> new BaseFlowingFluid.Source(syntheticRubberProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> SYNTHETIC_RUBBER_FLOWING =
            FLUIDS.register("synthetic_rubber_flowing", () -> new BaseFlowingFluid.Flowing(syntheticRubberProperties()));

    public static BaseFlowingFluid.Properties syntheticRubberProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> SYNTHETIC_RUBBER_TYPE.get(),
                        () -> SYNTHETIC_RUBBER_STILL.get(),
                        () -> SYNTHETIC_RUBBER_FLOWING.get())
                .bucket(() -> ModItems.SYNTHETIC_RUBBER_BUCKET.get())
                .block(() -> ModBlocks.SYNTHETIC_RUBBER_FLUID.get())
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2)
                .tickRate(20);
    }

    // ── Hydrogen Gas ─────────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> HYDROGEN_GAS_TYPE =
            FLUID_TYPES.register("hydrogen_gas", () -> new FluidType(FluidType.Properties.create()
                    .density(-1).viscosity(100).lightLevel(0).canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> HYDROGEN_GAS_STILL =
            FLUIDS.register("hydrogen_gas_still", () -> new BaseFlowingFluid.Source(hydrogenGasProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> HYDROGEN_GAS_FLOWING =
            FLUIDS.register("hydrogen_gas_flowing", () -> new BaseFlowingFluid.Flowing(hydrogenGasProperties()));

    public static BaseFlowingFluid.Properties hydrogenGasProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> HYDROGEN_GAS_TYPE.get(),
                        () -> HYDROGEN_GAS_STILL.get(),
                        () -> HYDROGEN_GAS_FLOWING.get())
                .bucket(() -> ModItems.HYDROGEN_GAS_BUCKET.get())
                .block(() -> ModBlocks.HYDROGEN_GAS_FLUID.get())
                .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(5);
    }

    // ── Oxygen Gas ───────────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> OXYGEN_GAS_TYPE =
            FLUID_TYPES.register("oxygen_gas", () -> new FluidType(FluidType.Properties.create()
                    .density(-1).viscosity(100).lightLevel(0).canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> OXYGEN_GAS_STILL =
            FLUIDS.register("oxygen_gas_still", () -> new BaseFlowingFluid.Source(oxygenGasProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> OXYGEN_GAS_FLOWING =
            FLUIDS.register("oxygen_gas_flowing", () -> new BaseFlowingFluid.Flowing(oxygenGasProperties()));

    public static BaseFlowingFluid.Properties oxygenGasProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> OXYGEN_GAS_TYPE.get(),
                        () -> OXYGEN_GAS_STILL.get(),
                        () -> OXYGEN_GAS_FLOWING.get())
                .bucket(() -> ModItems.OXYGEN_GAS_BUCKET.get())
                .block(() -> ModBlocks.OXYGEN_GAS_FLUID.get())
                .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(5);
    }

    // ── Fluorine Gas ─────────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> FLUORINE_GAS_TYPE =
            FLUID_TYPES.register("fluorine_gas", () -> new FluidType(FluidType.Properties.create()
                    .density(-1).viscosity(100).lightLevel(0).canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> FLUORINE_GAS_STILL =
            FLUIDS.register("fluorine_gas_still", () -> new BaseFlowingFluid.Source(fluorineGasProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> FLUORINE_GAS_FLOWING =
            FLUIDS.register("fluorine_gas_flowing", () -> new BaseFlowingFluid.Flowing(fluorineGasProperties()));

    public static BaseFlowingFluid.Properties fluorineGasProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> FLUORINE_GAS_TYPE.get(),
                        () -> FLUORINE_GAS_STILL.get(),
                        () -> FLUORINE_GAS_FLOWING.get())
                .bucket(() -> ModItems.FLUORINE_GAS_BUCKET.get())
                .block(() -> ModBlocks.FLUORINE_GAS_FLUID.get())
                .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(5);
    }

    // ── Chlorine Gas ─────────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> CHLORINE_GAS_TYPE =
            FLUID_TYPES.register("chlorine_gas", () -> new FluidType(FluidType.Properties.create()
                    .density(-1).viscosity(100).lightLevel(0).canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> CHLORINE_GAS_STILL =
            FLUIDS.register("chlorine_gas_still", () -> new BaseFlowingFluid.Source(chlorineGasProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> CHLORINE_GAS_FLOWING =
            FLUIDS.register("chlorine_gas_flowing", () -> new BaseFlowingFluid.Flowing(chlorineGasProperties()));

    public static BaseFlowingFluid.Properties chlorineGasProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> CHLORINE_GAS_TYPE.get(),
                        () -> CHLORINE_GAS_STILL.get(),
                        () -> CHLORINE_GAS_FLOWING.get())
                .bucket(() -> ModItems.CHLORINE_GAS_BUCKET.get())
                .block(() -> ModBlocks.CHLORINE_GAS_FLUID.get())
                .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(5);
    }

    // ── Nitrate Solution ─────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> NITRATE_SOLUTION_TYPE =
            FLUID_TYPES.register("nitrate_solution", () -> new FluidType(FluidType.Properties.create()
                    .density(1100).viscosity(1200).lightLevel(0).canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> NITRATE_SOLUTION_STILL =
            FLUIDS.register("nitrate_solution_still", () -> new BaseFlowingFluid.Source(nitrateSolutionProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> NITRATE_SOLUTION_FLOWING =
            FLUIDS.register("nitrate_solution_flowing", () -> new BaseFlowingFluid.Flowing(nitrateSolutionProperties()));

    public static BaseFlowingFluid.Properties nitrateSolutionProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> NITRATE_SOLUTION_TYPE.get(),
                        () -> NITRATE_SOLUTION_STILL.get(),
                        () -> NITRATE_SOLUTION_FLOWING.get())
                .bucket(() -> ModItems.NITRATE_SOLUTION_BUCKET.get())
                .block(() -> ModBlocks.NITRATE_SOLUTION_FLUID.get())
                .slopeFindDistance(3).levelDecreasePerBlock(2).tickRate(10);
    }

    // ── PVC Resin ────────────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> PVC_RESIN_TYPE =
            FLUID_TYPES.register("pvc_resin", () -> new FluidType(FluidType.Properties.create()
                    .density(1400).viscosity(6000).lightLevel(0).canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> PVC_RESIN_STILL =
            FLUIDS.register("pvc_resin_still", () -> new BaseFlowingFluid.Source(pvcResinProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> PVC_RESIN_FLOWING =
            FLUIDS.register("pvc_resin_flowing", () -> new BaseFlowingFluid.Flowing(pvcResinProperties()));

    public static BaseFlowingFluid.Properties pvcResinProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> PVC_RESIN_TYPE.get(),
                        () -> PVC_RESIN_STILL.get(),
                        () -> PVC_RESIN_FLOWING.get())
                .bucket(() -> ModItems.PVC_RESIN_BUCKET.get())
                .block(() -> ModBlocks.PVC_RESIN_FLUID.get())
                .slopeFindDistance(2).levelDecreasePerBlock(2).tickRate(20);
    }

    // ── Distillation products ─────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> BUTANE_TYPE =
            FLUID_TYPES.register("butane", () -> new FluidType(FluidType.Properties.create()
                    .density(600).viscosity(500).lightLevel(0).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, FlowingFluid> BUTANE_STILL =
            FLUIDS.register("butane_still", () -> new BaseFlowingFluid.Source(butaneProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> BUTANE_FLOWING =
            FLUIDS.register("butane_flowing", () -> new BaseFlowingFluid.Flowing(butaneProperties()));
    public static BaseFlowingFluid.Properties butaneProperties() {
        return new BaseFlowingFluid.Properties(() -> BUTANE_TYPE.get(), () -> BUTANE_STILL.get(), () -> BUTANE_FLOWING.get())
                .bucket(() -> ModItems.BUTANE_BUCKET.get()).block(() -> ModBlocks.BUTANE_FLUID.get())
                .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(5);
    }

    public static final DeferredHolder<FluidType, FluidType> GASOLINE_TYPE =
            FLUID_TYPES.register("gasoline", () -> new FluidType(FluidType.Properties.create()
                    .density(750).viscosity(800).lightLevel(0).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, FlowingFluid> GASOLINE_STILL =
            FLUIDS.register("gasoline_still", () -> new BaseFlowingFluid.Source(gasolineProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> GASOLINE_FLOWING =
            FLUIDS.register("gasoline_flowing", () -> new BaseFlowingFluid.Flowing(gasolineProperties()));
    public static BaseFlowingFluid.Properties gasolineProperties() {
        return new BaseFlowingFluid.Properties(() -> GASOLINE_TYPE.get(), () -> GASOLINE_STILL.get(), () -> GASOLINE_FLOWING.get())
                .bucket(() -> ModItems.GASOLINE_BUCKET.get()).block(() -> ModBlocks.GASOLINE_FLUID.get())
                .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(5);
    }

    public static final DeferredHolder<FluidType, FluidType> NAPHTHA_TYPE =
            FLUID_TYPES.register("naphtha", () -> new FluidType(FluidType.Properties.create()
                    .density(700).viscosity(700).lightLevel(0).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, FlowingFluid> NAPHTHA_STILL =
            FLUIDS.register("naphtha_still", () -> new BaseFlowingFluid.Source(naphthaProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> NAPHTHA_FLOWING =
            FLUIDS.register("naphtha_flowing", () -> new BaseFlowingFluid.Flowing(naphthaProperties()));
    public static BaseFlowingFluid.Properties naphthaProperties() {
        return new BaseFlowingFluid.Properties(() -> NAPHTHA_TYPE.get(), () -> NAPHTHA_STILL.get(), () -> NAPHTHA_FLOWING.get())
                .bucket(() -> ModItems.NAPHTHA_BUCKET.get()).block(() -> ModBlocks.NAPHTHA_FLUID.get())
                .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(5);
    }

    public static final DeferredHolder<FluidType, FluidType> KEROSENE_TYPE =
            FLUID_TYPES.register("kerosene", () -> new FluidType(FluidType.Properties.create()
                    .density(820).viscosity(1200).lightLevel(0).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, FlowingFluid> KEROSENE_STILL =
            FLUIDS.register("kerosene_still", () -> new BaseFlowingFluid.Source(keroseneProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> KEROSENE_FLOWING =
            FLUIDS.register("kerosene_flowing", () -> new BaseFlowingFluid.Flowing(keroseneProperties()));
    public static BaseFlowingFluid.Properties keroseneProperties() {
        return new BaseFlowingFluid.Properties(() -> KEROSENE_TYPE.get(), () -> KEROSENE_STILL.get(), () -> KEROSENE_FLOWING.get())
                .bucket(() -> ModItems.KEROSENE_BUCKET.get()).block(() -> ModBlocks.KEROSENE_FLUID.get())
                .slopeFindDistance(3).levelDecreasePerBlock(2).tickRate(8);
    }

    public static final DeferredHolder<FluidType, FluidType> DIESEL_TYPE =
            FLUID_TYPES.register("diesel", () -> new FluidType(FluidType.Properties.create()
                    .density(850).viscosity(1500).lightLevel(0).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, FlowingFluid> DIESEL_STILL =
            FLUIDS.register("diesel_still", () -> new BaseFlowingFluid.Source(dieselProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> DIESEL_FLOWING =
            FLUIDS.register("diesel_flowing", () -> new BaseFlowingFluid.Flowing(dieselProperties()));
    public static BaseFlowingFluid.Properties dieselProperties() {
        return new BaseFlowingFluid.Properties(() -> DIESEL_TYPE.get(), () -> DIESEL_STILL.get(), () -> DIESEL_FLOWING.get())
                .bucket(() -> ModItems.DIESEL_BUCKET.get()).block(() -> ModBlocks.DIESEL_FLUID.get())
                .slopeFindDistance(3).levelDecreasePerBlock(2).tickRate(10);
    }

    public static final DeferredHolder<FluidType, FluidType> HEAVY_GAS_OIL_TYPE =
            FLUID_TYPES.register("heavy_gas_oil", () -> new FluidType(FluidType.Properties.create()
                    .density(1000).viscosity(2500).lightLevel(0).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, FlowingFluid> HEAVY_GAS_OIL_STILL =
            FLUIDS.register("heavy_gas_oil_still", () -> new BaseFlowingFluid.Source(heavyGasOilProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> HEAVY_GAS_OIL_FLOWING =
            FLUIDS.register("heavy_gas_oil_flowing", () -> new BaseFlowingFluid.Flowing(heavyGasOilProperties()));
    public static BaseFlowingFluid.Properties heavyGasOilProperties() {
        return new BaseFlowingFluid.Properties(() -> HEAVY_GAS_OIL_TYPE.get(), () -> HEAVY_GAS_OIL_STILL.get(), () -> HEAVY_GAS_OIL_FLOWING.get())
                .bucket(() -> ModItems.HEAVY_GAS_OIL_BUCKET.get()).block(() -> ModBlocks.HEAVY_GAS_OIL_FLUID.get())
                .slopeFindDistance(3).levelDecreasePerBlock(2).tickRate(12);
    }

    public static final DeferredHolder<FluidType, FluidType> RESIDUAL_FUEL_OIL_TYPE =
            FLUID_TYPES.register("residual_fuel_oil", () -> new FluidType(FluidType.Properties.create()
                    .density(1100).viscosity(4000).lightLevel(0).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, FlowingFluid> RESIDUAL_FUEL_OIL_STILL =
            FLUIDS.register("residual_fuel_oil_still", () -> new BaseFlowingFluid.Source(residualFuelOilProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> RESIDUAL_FUEL_OIL_FLOWING =
            FLUIDS.register("residual_fuel_oil_flowing", () -> new BaseFlowingFluid.Flowing(residualFuelOilProperties()));
    public static BaseFlowingFluid.Properties residualFuelOilProperties() {
        return new BaseFlowingFluid.Properties(() -> RESIDUAL_FUEL_OIL_TYPE.get(), () -> RESIDUAL_FUEL_OIL_STILL.get(), () -> RESIDUAL_FUEL_OIL_FLOWING.get())
                .bucket(() -> ModItems.RESIDUAL_FUEL_OIL_BUCKET.get()).block(() -> ModBlocks.RESIDUAL_FUEL_OIL_FLUID.get())
                .slopeFindDistance(2).levelDecreasePerBlock(2).tickRate(15);
    }

    // ── Photoresist ──────────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> PHOTORESIST_TYPE =
            FLUID_TYPES.register("photoresist", () -> new FluidType(FluidType.Properties.create()
                    .density(1050).viscosity(2500).lightLevel(0).canConvertToSource(false)));

    public static final DeferredHolder<Fluid, FlowingFluid> PHOTORESIST_STILL =
            FLUIDS.register("photoresist_still", () -> new BaseFlowingFluid.Source(photoresistProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> PHOTORESIST_FLOWING =
            FLUIDS.register("photoresist_flowing", () -> new BaseFlowingFluid.Flowing(photoresistProperties()));

    public static BaseFlowingFluid.Properties photoresistProperties() {
        return new BaseFlowingFluid.Properties(
                        () -> PHOTORESIST_TYPE.get(),
                        () -> PHOTORESIST_STILL.get(),
                        () -> PHOTORESIST_FLOWING.get())
                .bucket(() -> ModItems.PHOTORESIST_BUCKET.get())
                .block(() -> ModBlocks.PHOTORESIST_FLUID.get())
                .slopeFindDistance(3).levelDecreasePerBlock(2).tickRate(10);
    }

    // ── Uranium Hexafluoride (UF6) ───────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> URANIUM_HEXAFLUORIDE_TYPE =
            FLUID_TYPES.register("uranium_hexafluoride", () -> new FluidType(FluidType.Properties.create()
                    .density(1300).viscosity(1500).lightLevel(0).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, FlowingFluid> URANIUM_HEXAFLUORIDE_STILL =
            FLUIDS.register("uranium_hexafluoride_still", () -> new BaseFlowingFluid.Source(uraniumHexafluorideProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> URANIUM_HEXAFLUORIDE_FLOWING =
            FLUIDS.register("uranium_hexafluoride_flowing", () -> new BaseFlowingFluid.Flowing(uraniumHexafluorideProperties()));
    public static BaseFlowingFluid.Properties uraniumHexafluorideProperties() {
        return new BaseFlowingFluid.Properties(() -> URANIUM_HEXAFLUORIDE_TYPE.get(), () -> URANIUM_HEXAFLUORIDE_STILL.get(), () -> URANIUM_HEXAFLUORIDE_FLOWING.get())
                .bucket(() -> ModItems.URANIUM_HEXAFLUORIDE_BUCKET.get()).block(() -> ModBlocks.URANIUM_HEXAFLUORIDE_FLUID.get())
                .slopeFindDistance(3).levelDecreasePerBlock(2).tickRate(10);
    }

    // ── Enriched UF6 ────────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> ENRICHED_UF6_TYPE =
            FLUID_TYPES.register("enriched_uf6", () -> new FluidType(FluidType.Properties.create()
                    .density(1300).viscosity(1500).lightLevel(0).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, FlowingFluid> ENRICHED_UF6_STILL =
            FLUIDS.register("enriched_uf6_still", () -> new BaseFlowingFluid.Source(enrichedUf6Properties()));
    public static final DeferredHolder<Fluid, FlowingFluid> ENRICHED_UF6_FLOWING =
            FLUIDS.register("enriched_uf6_flowing", () -> new BaseFlowingFluid.Flowing(enrichedUf6Properties()));
    public static BaseFlowingFluid.Properties enrichedUf6Properties() {
        return new BaseFlowingFluid.Properties(() -> ENRICHED_UF6_TYPE.get(), () -> ENRICHED_UF6_STILL.get(), () -> ENRICHED_UF6_FLOWING.get())
                .bucket(() -> ModItems.ENRICHED_UF6_BUCKET.get()).block(() -> ModBlocks.ENRICHED_UF6_FLUID.get())
                .slopeFindDistance(3).levelDecreasePerBlock(2).tickRate(10);
    }

    // ── Depleted UF6 ────────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> DEPLETED_UF6_TYPE =
            FLUID_TYPES.register("depleted_uf6", () -> new FluidType(FluidType.Properties.create()
                    .density(1300).viscosity(1500).lightLevel(0).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, FlowingFluid> DEPLETED_UF6_STILL =
            FLUIDS.register("depleted_uf6_still", () -> new BaseFlowingFluid.Source(depletedUf6Properties()));
    public static final DeferredHolder<Fluid, FlowingFluid> DEPLETED_UF6_FLOWING =
            FLUIDS.register("depleted_uf6_flowing", () -> new BaseFlowingFluid.Flowing(depletedUf6Properties()));
    public static BaseFlowingFluid.Properties depletedUf6Properties() {
        return new BaseFlowingFluid.Properties(() -> DEPLETED_UF6_TYPE.get(), () -> DEPLETED_UF6_STILL.get(), () -> DEPLETED_UF6_FLOWING.get())
                .bucket(() -> ModItems.DEPLETED_UF6_BUCKET.get()).block(() -> ModBlocks.DEPLETED_UF6_FLUID.get())
                .slopeFindDistance(3).levelDecreasePerBlock(2).tickRate(10);
    }

    // ── Heavy Water ──────────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> HEAVY_WATER_TYPE =
            FLUID_TYPES.register("heavy_water", () -> new FluidType(FluidType.Properties.create()
                    .density(1100).viscosity(1200).lightLevel(0).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, FlowingFluid> HEAVY_WATER_STILL =
            FLUIDS.register("heavy_water_still", () -> new BaseFlowingFluid.Source(heavyWaterProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> HEAVY_WATER_FLOWING =
            FLUIDS.register("heavy_water_flowing", () -> new BaseFlowingFluid.Flowing(heavyWaterProperties()));
    public static BaseFlowingFluid.Properties heavyWaterProperties() {
        return new BaseFlowingFluid.Properties(() -> HEAVY_WATER_TYPE.get(), () -> HEAVY_WATER_STILL.get(), () -> HEAVY_WATER_FLOWING.get())
                .bucket(() -> ModItems.HEAVY_WATER_BUCKET.get()).block(() -> ModBlocks.HEAVY_WATER_FLUID.get())
                .slopeFindDistance(3).levelDecreasePerBlock(2).tickRate(10);
    }

    // ── Steam ────────────────────────────────────────────────────────────────

    public static final DeferredHolder<FluidType, FluidType> STEAM_TYPE =
            FLUID_TYPES.register("steam", () -> new FluidType(FluidType.Properties.create()
                    .density(1).viscosity(200).lightLevel(0).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, FlowingFluid> STEAM_STILL =
            FLUIDS.register("steam_still", () -> new BaseFlowingFluid.Source(steamProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> STEAM_FLOWING =
            FLUIDS.register("steam_flowing", () -> new BaseFlowingFluid.Flowing(steamProperties()));
    public static BaseFlowingFluid.Properties steamProperties() {
        return new BaseFlowingFluid.Properties(() -> STEAM_TYPE.get(), () -> STEAM_STILL.get(), () -> STEAM_FLOWING.get())
                .bucket(() -> ModItems.STEAM_BUCKET.get()).block(() -> ModBlocks.STEAM_FLUID.get())
                .slopeFindDistance(3).levelDecreasePerBlock(2).tickRate(5);
    }
}
