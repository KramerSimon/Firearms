package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.AcidBathBlockEntity;
import com.sio.firearms.block.GasCentrifugeBlockEntity;
import com.sio.firearms.block.CrystalGrowthControllerBlockEntity;
import com.sio.firearms.block.EuvLithographyControllerBlockEntity;
import com.sio.firearms.block.WaferCuttingMachineBlockEntity;
import com.sio.firearms.block.DepositionChamberBlockEntity;
import com.sio.firearms.block.PlasmaEtcherBlockEntity;
import com.sio.firearms.block.IonImplanterBlockEntity;
import com.sio.firearms.block.MetallizationChamberBlockEntity;
import com.sio.firearms.block.WaferTesterBlockEntity;
import com.sio.firearms.block.DicingSawBlockEntity;
import com.sio.firearms.block.ChipPackagingMachineBlockEntity;
import com.sio.firearms.block.AssemblyBenchBlockEntity;
import com.sio.firearms.block.CrateBlockEntity;
import com.sio.firearms.block.ChemicalMixerBlockEntity;
import com.sio.firearms.block.ChemicalMixerControllerBlockEntity;
import com.sio.firearms.block.ElectrolysisMachineBlockEntity;
import com.sio.firearms.block.ItemPipeBlockEntity;
import com.sio.firearms.block.WaterPumpBlockEntity;
import com.sio.firearms.block.WeaponRackBlockEntity;
import com.sio.firearms.block.EBFControllerBlockEntity;
import com.sio.firearms.block.EbfImportBusBlockEntity;
import com.sio.firearms.block.EbfOutputBusBlockEntity;
import com.sio.firearms.block.CokeOvenControllerBlockEntity;
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
import com.sio.firearms.block.FluidTankBlockEntity;
import com.sio.firearms.block.ReactorControllerBlockEntity;
import com.sio.firearms.block.CoolingTowerControllerBlockEntity;
import com.sio.firearms.block.SteamTurbineBlockEntity;
import com.sio.firearms.block.VehicleGarageControllerBlockEntity;
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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CokeOvenControllerBlockEntity>> COKE_OVEN_CONTROLLER =
            BLOCK_ENTITIES.register("coke_oven_controller",
                    () -> BlockEntityType.Builder.of(CokeOvenControllerBlockEntity::new,
                            ModBlocks.COKE_OVEN_CONTROLLER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EBFControllerBlockEntity>> EBF_CONTROLLER =
            BLOCK_ENTITIES.register("ebf_controller",
                    () -> BlockEntityType.Builder.of(EBFControllerBlockEntity::new,
                            ModBlocks.EBF_CONTROLLER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EbfImportBusBlockEntity>> EBF_IMPORT_BUS =
            BLOCK_ENTITIES.register("ebf_import_bus",
                    () -> BlockEntityType.Builder.of(EbfImportBusBlockEntity::new,
                            ModBlocks.EBF_IMPORT_BUS.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EbfOutputBusBlockEntity>> EBF_OUTPUT_BUS =
            BLOCK_ENTITIES.register("ebf_output_bus",
                    () -> BlockEntityType.Builder.of(EbfOutputBusBlockEntity::new,
                            ModBlocks.EBF_OUTPUT_BUS.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LandMineBlockEntity>> LAND_MINE =
            BLOCK_ENTITIES.register("land_mine",
                    () -> BlockEntityType.Builder.of(LandMineBlockEntity::new,
                            ModBlocks.LAND_MINE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChemicalMixerBlockEntity>> CHEMICAL_MIXER =
            BLOCK_ENTITIES.register("chemical_mixer",
                    () -> BlockEntityType.Builder.of(ChemicalMixerBlockEntity::new,
                            ModBlocks.CHEMICAL_MIXER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChemicalMixerControllerBlockEntity>> CHEMICAL_MIXER_CONTROLLER =
            BLOCK_ENTITIES.register("chemical_mixer_controller",
                    () -> BlockEntityType.Builder.of(ChemicalMixerControllerBlockEntity::new,
                            ModBlocks.CHEMICAL_MIXER_CONTROLLER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AcidBathBlockEntity>> ACID_BATH =
            BLOCK_ENTITIES.register("acid_bath",
                    () -> BlockEntityType.Builder.of(AcidBathBlockEntity::new,
                            ModBlocks.ACID_BATH.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaterPumpBlockEntity>> WATER_PUMP =
            BLOCK_ENTITIES.register("water_pump",
                    () -> BlockEntityType.Builder.of(WaterPumpBlockEntity::new,
                            ModBlocks.WATER_PUMP.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElectrolysisMachineBlockEntity>> ELECTROLYSIS_MACHINE =
            BLOCK_ENTITIES.register("electrolysis_machine",
                    () -> BlockEntityType.Builder.of(ElectrolysisMachineBlockEntity::new,
                            ModBlocks.ELECTROLYSIS_MACHINE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WeaponRackBlockEntity>> WEAPON_RACK =
            BLOCK_ENTITIES.register("weapon_rack",
                    () -> BlockEntityType.Builder.of(WeaponRackBlockEntity::new,
                            ModBlocks.WEAPON_RACK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemPipeBlockEntity>> ITEM_PIPE =
            BLOCK_ENTITIES.register("item_pipe",
                    () -> BlockEntityType.Builder.of(ItemPipeBlockEntity::new,
                            ModBlocks.ITEM_PIPE.get()).build(null));

    // ── Stage 2 microchip fabrication machines ───────────────────────────────
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaferCuttingMachineBlockEntity>> WAFER_CUTTING_MACHINE =
            BLOCK_ENTITIES.register("wafer_cutting_machine",
                    () -> BlockEntityType.Builder.of(WaferCuttingMachineBlockEntity::new,
                            ModBlocks.WAFER_CUTTING_MACHINE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DepositionChamberBlockEntity>> DEPOSITION_CHAMBER =
            BLOCK_ENTITIES.register("deposition_chamber",
                    () -> BlockEntityType.Builder.of(DepositionChamberBlockEntity::new,
                            ModBlocks.DEPOSITION_CHAMBER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlasmaEtcherBlockEntity>> PLASMA_ETCHER =
            BLOCK_ENTITIES.register("plasma_etcher",
                    () -> BlockEntityType.Builder.of(PlasmaEtcherBlockEntity::new,
                            ModBlocks.PLASMA_ETCHER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IonImplanterBlockEntity>> ION_IMPLANTER =
            BLOCK_ENTITIES.register("ion_implanter",
                    () -> BlockEntityType.Builder.of(IonImplanterBlockEntity::new,
                            ModBlocks.ION_IMPLANTER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MetallizationChamberBlockEntity>> METALLIZATION_CHAMBER =
            BLOCK_ENTITIES.register("metallization_chamber",
                    () -> BlockEntityType.Builder.of(MetallizationChamberBlockEntity::new,
                            ModBlocks.METALLIZATION_CHAMBER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaferTesterBlockEntity>> WAFER_TESTER =
            BLOCK_ENTITIES.register("wafer_tester",
                    () -> BlockEntityType.Builder.of(WaferTesterBlockEntity::new,
                            ModBlocks.WAFER_TESTER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DicingSawBlockEntity>> DICING_SAW =
            BLOCK_ENTITIES.register("dicing_saw",
                    () -> BlockEntityType.Builder.of(DicingSawBlockEntity::new,
                            ModBlocks.DICING_SAW.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChipPackagingMachineBlockEntity>> CHIP_PACKAGING_MACHINE =
            BLOCK_ENTITIES.register("chip_packaging_machine",
                    () -> BlockEntityType.Builder.of(ChipPackagingMachineBlockEntity::new,
                            ModBlocks.CHIP_PACKAGING_MACHINE.get()).build(null));

    // ── Multiblock controllers ───────────────────────────────────────────────
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalGrowthControllerBlockEntity>> CRYSTAL_GROWTH_CONTROLLER =
            BLOCK_ENTITIES.register("crystal_growth_controller",
                    () -> BlockEntityType.Builder.of(CrystalGrowthControllerBlockEntity::new,
                            ModBlocks.CRYSTAL_GROWTH_CONTROLLER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EuvLithographyControllerBlockEntity>> EUV_LITHOGRAPHY_CONTROLLER =
            BLOCK_ENTITIES.register("euv_lithography_controller",
                    () -> BlockEntityType.Builder.of(EuvLithographyControllerBlockEntity::new,
                            ModBlocks.EUV_LITHOGRAPHY_CONTROLLER.get()).build(null));

    // ── Nuclear Reactor Stage 1 ───────────────────────────────────────────────
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GasCentrifugeBlockEntity>> GAS_CENTRIFUGE =
            BLOCK_ENTITIES.register("gas_centrifuge",
                    () -> BlockEntityType.Builder.of(GasCentrifugeBlockEntity::new,
                            ModBlocks.GAS_CENTRIFUGE.get()).build(null));

    // ── Fluid storage ────────────────────────────────────────────────────────
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidTankBlockEntity>> FLUID_TANK =
            BLOCK_ENTITIES.register("fluid_tank",
                    () -> BlockEntityType.Builder.of(FluidTankBlockEntity::new,
                            ModBlocks.FLUID_TANK.get()).build(null));

    // ── Nuclear Reactor Stage 2 ───────────────────────────────────────────────
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReactorControllerBlockEntity>> REACTOR_CONTROLLER =
            BLOCK_ENTITIES.register("reactor_controller",
                    () -> BlockEntityType.Builder.of(ReactorControllerBlockEntity::new,
                            ModBlocks.REACTOR_CONTROLLER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SteamTurbineBlockEntity>> STEAM_TURBINE =
            BLOCK_ENTITIES.register("steam_turbine",
                    () -> BlockEntityType.Builder.of(SteamTurbineBlockEntity::new,
                            ModBlocks.STEAM_TURBINE.get()).build(null));

    // ── Cooling Tower ─────────────────────────────────────────────────────────
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoolingTowerControllerBlockEntity>> COOLING_TOWER_CONTROLLER =
            BLOCK_ENTITIES.register("cooling_tower_controller",
                    () -> BlockEntityType.Builder.of(CoolingTowerControllerBlockEntity::new,
                            ModBlocks.COOLING_TOWER_CONTROLLER.get()).build(null));

    // ── Vehicle Garage ────────────────────────────────────────────────────────
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VehicleGarageControllerBlockEntity>> VEHICLE_GARAGE_CONTROLLER =
            BLOCK_ENTITIES.register("vehicle_garage_controller",
                    () -> BlockEntityType.Builder.of(VehicleGarageControllerBlockEntity::new,
                            ModBlocks.GARAGE_CONTROLLER.get()).build(null));

    // ── Utility ───────────────────────────────────────────────────────────────
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrateBlockEntity>> CRATE =
            BLOCK_ENTITIES.register("crate",
                    () -> BlockEntityType.Builder.of(CrateBlockEntity::new,
                            ModBlocks.CRATE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.sio.firearms.block.TrashCanBlockEntity>> TRASH_CAN =
            BLOCK_ENTITIES.register("trash_can",
                    () -> BlockEntityType.Builder.of(com.sio.firearms.block.TrashCanBlockEntity::new,
                            ModBlocks.TRASH_CAN.get()).build(null));

    // ── Spent Fuel Storage ────────────────────────────────────────────────────
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.sio.firearms.block.SpentFuelStorageBlockEntity>> SPENT_FUEL_STORAGE =
            BLOCK_ENTITIES.register("spent_fuel_storage",
                    () -> BlockEntityType.Builder.of(com.sio.firearms.block.SpentFuelStorageBlockEntity::new,
                            ModBlocks.SPENT_FUEL_STORAGE_CONTROLLER.get()).build(null));
}
