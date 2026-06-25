package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.AssemblyBenchBlockEntity;
import com.sio.firearms.block.LandMineBlockEntity;
import com.sio.firearms.block.AutoTurretBlockEntity;
import com.sio.firearms.block.CoalGeneratorBlockEntity;
import com.sio.firearms.block.EnergyPortBlockEntity;
import com.sio.firearms.block.FluidPortBlockEntity;
import com.sio.firearms.block.EnergyPylonBlockEntity;
import com.sio.firearms.block.HeatTreatmentFurnaceBlockEntity;
import com.sio.firearms.block.LatheBlockEntity;
import com.sio.firearms.block.OilDerrickControllerBlockEntity;
import com.sio.firearms.block.RefineryControllerBlockEntity;
import com.sio.firearms.block.FluidPipeBlockEntity;
import com.sio.firearms.block.FuelGeneratorBlockEntity;
import com.sio.firearms.block.WireBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Firearms.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoalGeneratorBlockEntity>> COAL_GENERATOR =
            BLOCK_ENTITIES.register("coal_generator",
                    () -> BlockEntityType.Builder.of(CoalGeneratorBlockEntity::new,
                            ModBlocks.COAL_GENERATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HeatTreatmentFurnaceBlockEntity>> HEAT_TREATMENT_FURNACE =
            BLOCK_ENTITIES.register("heat_treatment_furnace",
                    () -> BlockEntityType.Builder.of(HeatTreatmentFurnaceBlockEntity::new,
                            ModBlocks.HEAT_TREATMENT_FURNACE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LatheBlockEntity>> LATHE =
            BLOCK_ENTITIES.register("lathe",
                    () -> BlockEntityType.Builder.of(LatheBlockEntity::new,
                            ModBlocks.LATHE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AssemblyBenchBlockEntity>> ASSEMBLY_BENCH =
            BLOCK_ENTITIES.register("assembly_bench",
                    () -> BlockEntityType.Builder.of(AssemblyBenchBlockEntity::new,
                            ModBlocks.ASSEMBLY_BENCH.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyPylonBlockEntity>> ENERGY_PYLON =
            BLOCK_ENTITIES.register("energy_pylon",
                    () -> BlockEntityType.Builder.of(EnergyPylonBlockEntity::new,
                            ModBlocks.ENERGY_PYLON.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OilDerrickControllerBlockEntity>> OIL_DERRICK_CONTROLLER =
            BLOCK_ENTITIES.register("oil_derrick_controller",
                    () -> BlockEntityType.Builder.of(OilDerrickControllerBlockEntity::new,
                            ModBlocks.OIL_DERRICK_CONTROLLER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RefineryControllerBlockEntity>> REFINERY_CONTROLLER =
            BLOCK_ENTITIES.register("refinery_controller",
                    () -> BlockEntityType.Builder.of(RefineryControllerBlockEntity::new,
                            ModBlocks.REFINERY_CONTROLLER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyPortBlockEntity>> ENERGY_PORT =
            BLOCK_ENTITIES.register("energy_port",
                    () -> BlockEntityType.Builder.of(EnergyPortBlockEntity::new,
                            ModBlocks.ENERGY_PORT.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidPortBlockEntity>> FLUID_PORT =
            BLOCK_ENTITIES.register("fluid_port",
                    () -> BlockEntityType.Builder.of(FluidPortBlockEntity::new,
                            ModBlocks.FLUID_PORT.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidPipeBlockEntity>> FLUID_PIPE =
            BLOCK_ENTITIES.register("fluid_pipe",
                    () -> BlockEntityType.Builder.of(FluidPipeBlockEntity::new,
                            ModBlocks.FLUID_PIPE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FuelGeneratorBlockEntity>> FUEL_GENERATOR =
            BLOCK_ENTITIES.register("fuel_generator",
                    () -> BlockEntityType.Builder.of(FuelGeneratorBlockEntity::new,
                            ModBlocks.FUEL_GENERATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoTurretBlockEntity>> AUTO_TURRET =
            BLOCK_ENTITIES.register("auto_turret",
                    () -> BlockEntityType.Builder.of(AutoTurretBlockEntity::new,
                            ModBlocks.AUTO_TURRET.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WireBlockEntity>> COPPER_WIRE =
            BLOCK_ENTITIES.register("copper_wire",
                    () -> BlockEntityType.Builder.of(WireBlockEntity::new,
                            ModBlocks.COPPER_WIRE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LandMineBlockEntity>> LAND_MINE =
            BLOCK_ENTITIES.register("land_mine",
                    () -> BlockEntityType.Builder.of(LandMineBlockEntity::new,
                            ModBlocks.LAND_MINE.get()).build(null));
}
