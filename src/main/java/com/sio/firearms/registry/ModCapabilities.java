package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.AcidBathBlockEntity;
import com.sio.firearms.block.AssemblyBenchBlockEntity;
import com.sio.firearms.block.ChemicalMixerBlockEntity;
import com.sio.firearms.block.CokeOvenControllerBlockEntity;
import com.sio.firearms.block.CoalGeneratorBlockEntity;
import com.sio.firearms.block.EBFControllerBlockEntity;
import com.sio.firearms.block.ElectrolysisMachineBlockEntity;
import com.sio.firearms.block.FuelGeneratorBlockEntity;
import com.sio.firearms.block.HeatTreatmentFurnaceBlockEntity;
import com.sio.firearms.block.ItemPipeBlockEntity;
import com.sio.firearms.block.LatheBlockEntity;
import com.sio.firearms.block.WaterPumpBlockEntity;
import com.sio.firearms.block.WireBlockEntity;
import com.sio.firearms.item.BatteryItem;
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
                (blockEntity, direction) -> blockEntity.getOilInputHandler()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.COKE_OVEN_CONTROLLER.get(),
                (blockEntity, direction) -> blockEntity.getCreosoteTank()
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
                (blockEntity, direction) -> blockEntity.fluidInputHandler
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ACID_BATH.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.ACID_BATH.get(),
                (blockEntity, direction) -> blockEntity.getAcidTank()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.WATER_PUMP.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.WATER_PUMP.get(),
                (blockEntity, direction) -> blockEntity.getWaterTank()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ELECTROLYSIS_MACHINE.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.ELECTROLYSIS_MACHINE.get(),
                (blockEntity, direction) -> blockEntity.fluidInputHandler
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

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.EBF_CONTROLLER.get(),
                (blockEntity, direction) -> blockEntity.getInventory()
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
    }
}
