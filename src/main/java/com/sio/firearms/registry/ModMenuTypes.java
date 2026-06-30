package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.menu.AcidBathMenu;
import com.sio.firearms.menu.FluidTankMenu;
import com.sio.firearms.menu.ReactorMenu;
import com.sio.firearms.menu.CoolingTowerMenu;
import com.sio.firearms.menu.SteamTurbineMenu;
import com.sio.firearms.menu.GasCentrifugeMenu;
import com.sio.firearms.menu.CrystalGrowthControllerMenu;
import com.sio.firearms.menu.EuvLithographyMenu;
import com.sio.firearms.menu.WaferCuttingMachineMenu;
import com.sio.firearms.menu.DepositionChamberMenu;
import com.sio.firearms.menu.PlasmaEtcherMenu;
import com.sio.firearms.menu.IonImplanterMenu;
import com.sio.firearms.menu.MetallizationChamberMenu;
import com.sio.firearms.menu.WaferTesterMenu;
import com.sio.firearms.menu.DicingSawMenu;
import com.sio.firearms.menu.ChipPackagingMachineMenu;
import com.sio.firearms.menu.AmmoBoxMenu;
import com.sio.firearms.menu.AssemblyBenchMenu;
import com.sio.firearms.menu.ElectrolysisMachineMenu;
import com.sio.firearms.menu.GunCaseMenu;
import com.sio.firearms.menu.ChemicalMixerMenu;
import com.sio.firearms.menu.WaterPumpMenu;
import com.sio.firearms.menu.EBFMenu;
import com.sio.firearms.menu.EbfBusMenu;
import com.sio.firearms.menu.CokeOvenMenu;
import com.sio.firearms.menu.AutoTurretMenu;
import com.sio.firearms.menu.CoalGeneratorMenu;
import com.sio.firearms.menu.FuelGeneratorMenu;
import com.sio.firearms.menu.OilDerrickMenu;
import com.sio.firearms.menu.RefineryMenu;
import com.sio.firearms.menu.HeatTreatmentFurnaceMenu;
import com.sio.firearms.menu.LatheMenu;
import com.sio.firearms.menu.GunModificationTableMenu;
import com.sio.firearms.menu.VehicleGarageMenu;
import com.sio.firearms.menu.MetalPressMenu;
import com.sio.firearms.menu.SpentFuelStorageMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, Firearms.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<MetalPressMenu>> METAL_PRESS_MENU =
            MENU_TYPES.register("metal_press",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new MetalPressMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<GunModificationTableMenu>> GUN_MODIFICATION_TABLE_MENU =
            MENU_TYPES.register("gun_modification_table",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new GunModificationTableMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<CoalGeneratorMenu>> COAL_GENERATOR_MENU =
            MENU_TYPES.register("coal_generator",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new CoalGeneratorMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<HeatTreatmentFurnaceMenu>> HEAT_TREATMENT_FURNACE_MENU =
            MENU_TYPES.register("heat_treatment_furnace",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new HeatTreatmentFurnaceMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<LatheMenu>> LATHE_MENU =
            MENU_TYPES.register("lathe",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new LatheMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<AssemblyBenchMenu>> ASSEMBLY_BENCH_MENU =
            MENU_TYPES.register("assembly_bench",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new AssemblyBenchMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<RefineryMenu>> REFINERY_MENU =
            MENU_TYPES.register("refinery",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new RefineryMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<FuelGeneratorMenu>> FUEL_GENERATOR_MENU =
            MENU_TYPES.register("fuel_generator",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new FuelGeneratorMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<AutoTurretMenu>> AUTO_TURRET_MENU =
            MENU_TYPES.register("auto_turret",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new AutoTurretMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<OilDerrickMenu>> OIL_DERRICK_MENU =
            MENU_TYPES.register("oil_derrick",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new OilDerrickMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<CokeOvenMenu>> COKE_OVEN_MENU =
            MENU_TYPES.register("coke_oven",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new CokeOvenMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<EBFMenu>> EBF_MENU =
            MENU_TYPES.register("ebf",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new EBFMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<EbfBusMenu>> EBF_BUS_MENU =
            MENU_TYPES.register("ebf_bus",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new EbfBusMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<ChemicalMixerMenu>> CHEMICAL_MIXER_MENU =
            MENU_TYPES.register("chemical_mixer",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new ChemicalMixerMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<AcidBathMenu>> ACID_BATH_MENU =
            MENU_TYPES.register("acid_bath",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new AcidBathMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<WaterPumpMenu>> WATER_PUMP_MENU =
            MENU_TYPES.register("water_pump",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new WaterPumpMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<ElectrolysisMachineMenu>> ELECTROLYSIS_MACHINE_MENU =
            MENU_TYPES.register("electrolysis_machine",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new ElectrolysisMachineMenu(windowId, inv)));

    // ── Stage 2 microchip fabrication machines ───────────────────────────────
    public static final DeferredHolder<MenuType<?>, MenuType<WaferCuttingMachineMenu>> WAFER_CUTTING_MACHINE_MENU =
            MENU_TYPES.register("wafer_cutting_machine",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new WaferCuttingMachineMenu(windowId, inv)));
    public static final DeferredHolder<MenuType<?>, MenuType<DepositionChamberMenu>> DEPOSITION_CHAMBER_MENU =
            MENU_TYPES.register("deposition_chamber",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new DepositionChamberMenu(windowId, inv)));
    public static final DeferredHolder<MenuType<?>, MenuType<PlasmaEtcherMenu>> PLASMA_ETCHER_MENU =
            MENU_TYPES.register("plasma_etcher",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new PlasmaEtcherMenu(windowId, inv)));
    public static final DeferredHolder<MenuType<?>, MenuType<IonImplanterMenu>> ION_IMPLANTER_MENU =
            MENU_TYPES.register("ion_implanter",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new IonImplanterMenu(windowId, inv)));
    public static final DeferredHolder<MenuType<?>, MenuType<MetallizationChamberMenu>> METALLIZATION_CHAMBER_MENU =
            MENU_TYPES.register("metallization_chamber",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new MetallizationChamberMenu(windowId, inv)));
    public static final DeferredHolder<MenuType<?>, MenuType<WaferTesterMenu>> WAFER_TESTER_MENU =
            MENU_TYPES.register("wafer_tester",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new WaferTesterMenu(windowId, inv)));
    public static final DeferredHolder<MenuType<?>, MenuType<DicingSawMenu>> DICING_SAW_MENU =
            MENU_TYPES.register("dicing_saw",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new DicingSawMenu(windowId, inv)));
    public static final DeferredHolder<MenuType<?>, MenuType<ChipPackagingMachineMenu>> CHIP_PACKAGING_MACHINE_MENU =
            MENU_TYPES.register("chip_packaging_machine",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new ChipPackagingMachineMenu(windowId, inv)));

    // ── Multiblock controllers ───────────────────────────────────────────────
    public static final DeferredHolder<MenuType<?>, MenuType<CrystalGrowthControllerMenu>> CRYSTAL_GROWTH_CONTROLLER_MENU =
            MENU_TYPES.register("crystal_growth_controller",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new CrystalGrowthControllerMenu(windowId, inv)));
    public static final DeferredHolder<MenuType<?>, MenuType<EuvLithographyMenu>> EUV_LITHOGRAPHY_MENU =
            MENU_TYPES.register("euv_lithography_controller",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new EuvLithographyMenu(windowId, inv)));

    // ── Nuclear Reactor Stage 1 ───────────────────────────────────────────────
    public static final DeferredHolder<MenuType<?>, MenuType<GasCentrifugeMenu>> GAS_CENTRIFUGE_MENU =
            MENU_TYPES.register("gas_centrifuge",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new GasCentrifugeMenu(windowId, inv)));

    // ── Fluid storage ────────────────────────────────────────────────────────
    public static final DeferredHolder<MenuType<?>, MenuType<FluidTankMenu>> FLUID_TANK_MENU =
            MENU_TYPES.register("fluid_tank",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new FluidTankMenu(windowId, inv)));

    // ── Nuclear Reactor Stage 2 ───────────────────────────────────────────────
    public static final DeferredHolder<MenuType<?>, MenuType<ReactorMenu>> REACTOR_MENU =
            MENU_TYPES.register("reactor_controller",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new ReactorMenu(windowId, inv)));
    public static final DeferredHolder<MenuType<?>, MenuType<SteamTurbineMenu>> STEAM_TURBINE_MENU =
            MENU_TYPES.register("steam_turbine",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new SteamTurbineMenu(windowId, inv)));

    // ── Cooling Tower ─────────────────────────────────────────────────────────
    public static final DeferredHolder<MenuType<?>, MenuType<CoolingTowerMenu>> COOLING_TOWER_MENU =
            MENU_TYPES.register("cooling_tower_controller",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new CoolingTowerMenu(windowId, inv)));

    // ── Vehicle Garage ────────────────────────────────────────────────────────
    public static final DeferredHolder<MenuType<?>, MenuType<VehicleGarageMenu>> VEHICLE_GARAGE_MENU =
            MENU_TYPES.register("vehicle_garage_controller",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new VehicleGarageMenu(windowId, inv)));

    // ── Utility ───────────────────────────────────────────────────────────────
    public static final DeferredHolder<MenuType<?>, MenuType<com.sio.firearms.menu.TrashCanMenu>> TRASH_CAN_MENU =
            MENU_TYPES.register("trash_can",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new com.sio.firearms.menu.TrashCanMenu(windowId, inv)));

    // ── Item containers ───────────────────────────────────────────────────────
    public static final DeferredHolder<MenuType<?>, MenuType<AmmoBoxMenu>> AMMO_BOX_MENU =
            MENU_TYPES.register("ammo_box",
                    () -> IMenuTypeExtension.create((windowId, inv, data) ->
                            new AmmoBoxMenu(windowId, inv, data.readEnum(net.minecraft.world.InteractionHand.class))));

    public static final DeferredHolder<MenuType<?>, MenuType<GunCaseMenu>> GUN_CASE_MENU =
            MENU_TYPES.register("gun_case",
                    () -> IMenuTypeExtension.create((windowId, inv, data) ->
                            new GunCaseMenu(windowId, inv, data.readEnum(net.minecraft.world.InteractionHand.class))));

    // ── Spent Fuel Storage ────────────────────────────────────────────────────
    public static final DeferredHolder<MenuType<?>, MenuType<SpentFuelStorageMenu>> SPENT_FUEL_STORAGE_MENU =
            MENU_TYPES.register("spent_fuel_storage",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new SpentFuelStorageMenu(windowId, inv)));

    // ── Item pipe filter screen ───────────────────────────────────────────────
    public static final DeferredHolder<MenuType<?>, MenuType<com.sio.firearms.menu.ItemPipeFilterMenu>> ITEM_PIPE_FILTER_MENU =
            MENU_TYPES.register("item_pipe_filter",
                    () -> IMenuTypeExtension.create((windowId, inv, data) ->
                            new com.sio.firearms.menu.ItemPipeFilterMenu(windowId, inv, data)));

    // ── Fluid routing config screens ──────────────────────────────────────────
    public static final DeferredHolder<MenuType<?>, MenuType<com.sio.firearms.menu.FluidPortConfigMenu>> FLUID_PORT_CONFIG_MENU =
            MENU_TYPES.register("fluid_port_config",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> {
                        net.minecraft.core.BlockPos pos = data.readBlockPos();
                        String target = data.readUtf();
                        boolean outputMode = data.readBoolean();
                        return new com.sio.firearms.menu.FluidPortConfigMenu(windowId, inv, pos, target,
                                outputMode ? com.sio.firearms.block.FluidPortBlockEntity.Mode.OUTPUT
                                           : com.sio.firearms.block.FluidPortBlockEntity.Mode.INPUT);
                    }));

    public static final DeferredHolder<MenuType<?>, MenuType<com.sio.firearms.menu.FluidPipeConfigMenu>> FLUID_PIPE_CONFIG_MENU =
            MENU_TYPES.register("fluid_pipe_config",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> {
                        net.minecraft.core.BlockPos pos = data.readBlockPos();
                        boolean hasFilter = data.readBoolean();
                        net.minecraft.resources.ResourceLocation filter = hasFilter ? data.readResourceLocation() : null;
                        return new com.sio.firearms.menu.FluidPipeConfigMenu(windowId, inv, pos, filter);
                    }));
}