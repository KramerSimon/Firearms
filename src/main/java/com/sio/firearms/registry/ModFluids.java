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
}
