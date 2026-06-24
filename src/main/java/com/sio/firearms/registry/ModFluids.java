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
}
