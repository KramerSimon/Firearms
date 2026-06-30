package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.AcidBathBlockEntity;
import com.sio.firearms.block.FluidTankBlockEntity;
import com.sio.firearms.block.ReactorControllerBlockEntity;
import com.sio.firearms.block.CoolingTowerControllerBlockEntity;
import com.sio.firearms.block.SteamTurbineBlockEntity;
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
import com.sio.firearms.block.ChemicalMixerBlockEntity;
import com.sio.firearms.block.ChemicalMixerControllerBlockEntity;
import com.sio.firearms.block.CokeOvenControllerBlockEntity;
import com.sio.firearms.block.CoalGeneratorBlockEntity;
import com.sio.firearms.block.EBFControllerBlockEntity;
import com.sio.firearms.block.ElectrolysisMachineBlockEntity;
import com.sio.firearms.block.FuelGeneratorBlockEntity;
import com.sio.firearms.block.HeatTreatmentFurnaceBlockEntity;
import com.sio.firearms.block.ItemPipeBlockEntity;
import com.sio.firearms.block.LatheBlockEntity;
import com.sio.firearms.block.VehicleGarageControllerBlockEntity;
import com.sio.firearms.block.WaterPumpBlockEntity;
import com.sio.firearms.block.WireBlockEntity;
import com.sio.firearms.item.BatteryItem;
import com.sio.firearms.item.BattlesuitItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;

@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.COAL_GENERATOR.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.COPPER_WIRE.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.HEAT_TREATMENT_FURNACE.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.LATHE.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ASSEMBLY_BENCH.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.OIL_DERRICK_CONTROLLER.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.REFINERY_CONTROLLER.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ENERGY_PYLON.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ENERGY_PORT.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.FUEL_GENERATOR.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.AUTO_TURRET.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.FUEL_GENERATOR.get(),
                (blockEntity, direction) -> blockEntity.getFuelInputHandler()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.FLUID_PORT.get(),
                (blockEntity, direction) -> blockEntity.getExposedHandler()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.OIL_DERRICK_CONTROLLER.get(),
                (blockEntity, direction) -> blockEntity.getFluidTank()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.FLUID_PIPE.get(),
                (blockEntity, direction) -> blockEntity.getFluidTank()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.REFINERY_CONTROLLER.get(),
                // Full access: fill routes to oil input, drain routes to product output tanks
                (blockEntity, direction) -> blockEntity.fullAccessHandler
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.COKE_OVEN_CONTROLLER.get(),
                // Full access: output-only, drain routes to creosote tank
                (blockEntity, direction) -> blockEntity.fullAccessHandler
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.EBF_CONTROLLER.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.CHEMICAL_MIXER.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.CHEMICAL_MIXER.get(),
                // All faces: fill() routes to input tanks, drain() routes to output tank
                (blockEntity, direction) -> blockEntity.fullAccessHandler
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.CHEMICAL_MIXER_CONTROLLER.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.CHEMICAL_MIXER_CONTROLLER.get(),
                (blockEntity, direction) -> blockEntity.fullAccessHandler
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.CHEMICAL_MIXER_CONTROLLER.get(),
                (blockEntity, direction) -> blockEntity.getInventory()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ACID_BATH.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.ACID_BATH.get(),
                // Full access: input-only, fill routes to sulfuric acid tank
                (blockEntity, direction) -> blockEntity.fullAccessHandler
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.WATER_PUMP.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.WATER_PUMP.get(),
                // Full access: output-only, drain routes to water tank
                (blockEntity, direction) -> blockEntity.fullAccessHandler
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ELECTROLYSIS_MACHINE.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.ELECTROLYSIS_MACHINE.get(),
                // Full access: fill routes to water input, drain routes to gas output tanks
                (blockEntity, direction) -> blockEntity.fullAccessHandler
        );

        // ── IItemHandler for machines (enables item pipe connections) ────────────
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.COAL_GENERATOR.get(),
                (blockEntity, direction) -> blockEntity.getInventory()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.HEAT_TREATMENT_FURNACE.get(),
                (blockEntity, direction) -> blockEntity.getInventory()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.LATHE.get(),
                (blockEntity, direction) -> blockEntity.getInventory()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ASSEMBLY_BENCH.get(),
                (blockEntity, direction) -> blockEntity.getInventory()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.FUEL_GENERATOR.get(),
                (blockEntity, direction) -> blockEntity.getInventory()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.CHEMICAL_MIXER.get(),
                (blockEntity, direction) -> blockEntity.getInventory()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ACID_BATH.get(),
                (blockEntity, direction) -> blockEntity.getInventory()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.COKE_OVEN_CONTROLLER.get(),
                (blockEntity, direction) -> blockEntity.getInventory()
        );

        // EBF item handling is done through the import / output buses, not the
        // controller directly — so pipes/hoppers connect to the buses.
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.EBF_IMPORT_BUS.get(),
                (blockEntity, direction) -> blockEntity.getBuffer()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.EBF_OUTPUT_BUS.get(),
                (blockEntity, direction) -> blockEntity.getBuffer()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ITEM_PIPE.get(),
                (blockEntity, direction) -> blockEntity.getBuffer()
        );

        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, ctx) -> new ComponentEnergyStorage(
                        stack, ModDataComponents.ENERGY.get(),
                        BatteryItem.CAPACITY, BatteryItem.MAX_TRANSFER, BatteryItem.MAX_TRANSFER),
                ModItems.BATTERY.get()
        );

        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, ctx) -> new ComponentEnergyStorage(
                        stack, ModDataComponents.BATTLESUIT_ENERGY.get(),
                        BattlesuitItem.CAPACITY, BattlesuitItem.MAX_TRANSFER, BattlesuitItem.MAX_TRANSFER),
                ModItems.BATTLESUIT_HELMET.get(),
                ModItems.BATTLESUIT_CHESTPLATE.get(),
                ModItems.BATTLESUIT_LEGGINGS.get(),
                ModItems.BATTLESUIT_BOOTS.get()
        );

        // ── Stage 2 machines: energy storage ────────────────────────────────────
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.WAFER_CUTTING_MACHINE.get(),
                (be, dir) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.DEPOSITION_CHAMBER.get(),
                (be, dir) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.PLASMA_ETCHER.get(),
                (be, dir) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.ION_IMPLANTER.get(),
                (be, dir) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.METALLIZATION_CHAMBER.get(),
                (be, dir) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.WAFER_TESTER.get(),
                (be, dir) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.DICING_SAW.get(),
                (be, dir) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.CHIP_PACKAGING_MACHINE.get(),
                (be, dir) -> be.getEnergyStorage());

        // ── Stage 2 machines: item handler ──────────────────────────────────────
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.WAFER_CUTTING_MACHINE.get(),
                (be, dir) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.DEPOSITION_CHAMBER.get(),
                (be, dir) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.PLASMA_ETCHER.get(),
                (be, dir) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.ION_IMPLANTER.get(),
                (be, dir) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.METALLIZATION_CHAMBER.get(),
                (be, dir) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.WAFER_TESTER.get(),
                (be, dir) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.DICING_SAW.get(),
                (be, dir) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CHIP_PACKAGING_MACHINE.get(),
                (be, dir) -> be.getInventory());

        // ── PlasmaEtcher: fluid handler ──────────────────────────────────────────
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.PLASMA_ETCHER.get(),
                // Full access: input-only, fill routes to chlorine gas tank
                (be, dir) -> be.fullAccessHandler);

        // ── Crystal Growth Chamber ────────────────────────────────────────────────
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.CRYSTAL_GROWTH_CONTROLLER.get(),
                (be, dir) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CRYSTAL_GROWTH_CONTROLLER.get(),
                (be, dir) -> be.getInventory());

        // ── EUV Lithography Machine ───────────────────────────────────────────────
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.EUV_LITHOGRAPHY_CONTROLLER.get(),
                (be, dir) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.EUV_LITHOGRAPHY_CONTROLLER.get(),
                (be, dir) -> be.getInventory());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.EUV_LITHOGRAPHY_CONTROLLER.get(),
                // Full access: input-only, fill routes to photoresist tank
                (be, dir) -> be.fullAccessHandler);

        // ── Gas Centrifuge ────────────────────────────────────────────────────
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.GAS_CENTRIFUGE.get(),
                (be, dir) -> be.getEnergyStorage());
        // Full access: fill routes to UF6 input, drain routes to enriched/depleted output tanks
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.GAS_CENTRIFUGE.get(),
                (be, dir) -> be.fullAccessHandler);

        // ── Fluid Tank ────────────────────────────────────────────────────────
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.FLUID_TANK.get(),
                (be, dir) -> be.fullAccessHandler);

        // ── Nuclear Reactor Stage 2 ───────────────────────────────────────────
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.REACTOR_CONTROLLER.get(),
                (be, dir) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.REACTOR_CONTROLLER.get(),
                (be, dir) -> be.waterInputHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.REACTOR_CONTROLLER.get(),
                (be, dir) -> be.getInventory());

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.STEAM_TURBINE.get(),
                (be, dir) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.STEAM_TURBINE.get(),
                (be, dir) -> be.getSteamInputHandler());

        // ── Cooling Tower ──────────────────────────────────────────────────────
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.COOLING_TOWER_CONTROLLER.get(),
                (be, dir) -> be.getEnergyStorage());
        // fill() → steam input tank, drain() → water output tank
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.COOLING_TOWER_CONTROLLER.get(),
                (be, dir) -> be.fullAccessHandler);

        // ── Copper Wire ────────────────────────────────────────────────────────
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.COPPER_WIRE.get(),
                (be, dir) -> be.getEnergyStorage());

        // ── Vehicle Garage ─────────────────────────────────────────────────────
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.VEHICLE_GARAGE_CONTROLLER.get(),
                (be, dir) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.VEHICLE_GARAGE_CONTROLLER.get(),
                (be, dir) -> new net.neoforged.neoforge.items.wrapper.InvWrapper(be.inputSlots));

        // ── Trash Can ──────────────────────────────────────────────────────────
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.TRASH_CAN.get(),
                (be, dir) -> be.itemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.TRASH_CAN.get(),
                (be, dir) -> be.fluidHandler);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.TRASH_CAN.get(),
                (be, dir) -> be.energyHandler);
    }
}
