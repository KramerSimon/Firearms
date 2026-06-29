package com.sio.firearms.registry;

import com.sio.firearms.screen.AcidBathScreen;
import com.sio.firearms.screen.FluidTankScreen;
import com.sio.firearms.screen.ReactorScreen;
import com.sio.firearms.screen.CoolingTowerScreen;
import com.sio.firearms.screen.SteamTurbineScreen;
import com.sio.firearms.screen.GasCentrifugeScreen;
import com.sio.firearms.screen.CrystalGrowthControllerScreen;
import com.sio.firearms.screen.EuvLithographyScreen;
import com.sio.firearms.screen.WaferCuttingMachineScreen;
import com.sio.firearms.screen.DepositionChamberScreen;
import com.sio.firearms.screen.PlasmaEtcherScreen;
import com.sio.firearms.screen.IonImplanterScreen;
import com.sio.firearms.screen.MetallizationChamberScreen;
import com.sio.firearms.screen.WaferTesterScreen;
import com.sio.firearms.screen.DicingSawScreen;
import com.sio.firearms.screen.ChipPackagingMachineScreen;
import com.sio.firearms.screen.AssemblyBenchScreen;
import com.sio.firearms.screen.ElectrolysisMachineScreen;
import com.sio.firearms.screen.ChemicalMixerScreen;
import com.sio.firearms.screen.WaterPumpScreen;
import com.sio.firearms.screen.EBFScreen;
import com.sio.firearms.screen.EbfBusScreen;
import com.sio.firearms.screen.CokeOvenScreen;
import com.sio.firearms.screen.AutoTurretScreen;
import com.sio.firearms.screen.CoalGeneratorScreen;
import com.sio.firearms.screen.FuelGeneratorScreen;
import com.sio.firearms.screen.OilDerrickScreen;
import com.sio.firearms.screen.RefineryScreen;
import com.sio.firearms.screen.HeatTreatmentFurnaceScreen;
import com.sio.firearms.screen.LatheScreen;
import com.sio.firearms.screen.GunModificationTableScreen;
import com.sio.firearms.screen.MetalPressScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "firearms", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModScreens {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.METAL_PRESS_MENU.get(), MetalPressScreen::new);
        event.register(ModMenuTypes.GUN_MODIFICATION_TABLE_MENU.get(), GunModificationTableScreen::new);
        event.register(ModMenuTypes.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
        event.register(ModMenuTypes.HEAT_TREATMENT_FURNACE_MENU.get(), HeatTreatmentFurnaceScreen::new);
        event.register(ModMenuTypes.LATHE_MENU.get(), LatheScreen::new);
        event.register(ModMenuTypes.ASSEMBLY_BENCH_MENU.get(), AssemblyBenchScreen::new);
        event.register(ModMenuTypes.FUEL_GENERATOR_MENU.get(), FuelGeneratorScreen::new);
        event.register(ModMenuTypes.OIL_DERRICK_MENU.get(), OilDerrickScreen::new);
        event.register(ModMenuTypes.REFINERY_MENU.get(), RefineryScreen::new);
        event.register(ModMenuTypes.AUTO_TURRET_MENU.get(), AutoTurretScreen::new);
        event.register(ModMenuTypes.COKE_OVEN_MENU.get(), CokeOvenScreen::new);
        event.register(ModMenuTypes.EBF_MENU.get(), EBFScreen::new);
        event.register(ModMenuTypes.EBF_BUS_MENU.get(), EbfBusScreen::new);
        event.register(ModMenuTypes.CHEMICAL_MIXER_MENU.get(), ChemicalMixerScreen::new);
        event.register(ModMenuTypes.ACID_BATH_MENU.get(), AcidBathScreen::new);
        event.register(ModMenuTypes.WATER_PUMP_MENU.get(), WaterPumpScreen::new);
        event.register(ModMenuTypes.ELECTROLYSIS_MACHINE_MENU.get(), ElectrolysisMachineScreen::new);
        event.register(ModMenuTypes.WAFER_CUTTING_MACHINE_MENU.get(), WaferCuttingMachineScreen::new);
        event.register(ModMenuTypes.DEPOSITION_CHAMBER_MENU.get(), DepositionChamberScreen::new);
        event.register(ModMenuTypes.PLASMA_ETCHER_MENU.get(), PlasmaEtcherScreen::new);
        event.register(ModMenuTypes.ION_IMPLANTER_MENU.get(), IonImplanterScreen::new);
        event.register(ModMenuTypes.METALLIZATION_CHAMBER_MENU.get(), MetallizationChamberScreen::new);
        event.register(ModMenuTypes.WAFER_TESTER_MENU.get(), WaferTesterScreen::new);
        event.register(ModMenuTypes.DICING_SAW_MENU.get(), DicingSawScreen::new);
        event.register(ModMenuTypes.CHIP_PACKAGING_MACHINE_MENU.get(), ChipPackagingMachineScreen::new);
        event.register(ModMenuTypes.CRYSTAL_GROWTH_CONTROLLER_MENU.get(), CrystalGrowthControllerScreen::new);
        event.register(ModMenuTypes.EUV_LITHOGRAPHY_MENU.get(), EuvLithographyScreen::new);
        event.register(ModMenuTypes.GAS_CENTRIFUGE_MENU.get(), GasCentrifugeScreen::new);
        event.register(ModMenuTypes.FLUID_TANK_MENU.get(), FluidTankScreen::new);
        event.register(ModMenuTypes.REACTOR_MENU.get(), ReactorScreen::new);
        event.register(ModMenuTypes.STEAM_TURBINE_MENU.get(), SteamTurbineScreen::new);
        event.register(ModMenuTypes.COOLING_TOWER_MENU.get(), CoolingTowerScreen::new);
        event.register(ModMenuTypes.TRASH_CAN_MENU.get(), com.sio.firearms.screen.TrashCanScreen::new);
    }
}