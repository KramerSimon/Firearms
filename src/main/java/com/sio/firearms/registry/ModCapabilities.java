package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.CoalGeneratorBlockEntity;
import com.sio.firearms.block.FuelGeneratorBlockEntity;
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

        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, ctx) -> new ComponentEnergyStorage(
                        stack, ModDataComponents.ENERGY.get(),
                        BatteryItem.CAPACITY, BatteryItem.MAX_TRANSFER, BatteryItem.MAX_TRANSFER),
                ModItems.BATTERY.get()
        );
    }
}
