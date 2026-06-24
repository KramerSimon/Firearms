package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.item.BatteryItem;
import com.sio.firearms.menu.CoalGeneratorMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CoalGeneratorBlockEntity extends EnergyStorageBlock implements MenuProvider {

    private static final int FE_PER_TICK = 80;
    private static final int CAPACITY = 50_000;
    private static final int BATTERY_CHARGE_RATE = 1_000;

    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) return stack.getBurnTime(RecipeType.SMELTING) > 0;
            if (slot == 1) return stack.getItem() instanceof BatteryItem;
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private int burnTime = 0;
    private int maxBurnTime = 0;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> burnTime;
                case 3 -> maxBurnTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 2 -> burnTime = value;
                case 3 -> maxBurnTime = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COAL_GENERATOR.get(), pos, state, CAPACITY, FE_PER_TICK, FE_PER_TICK);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.coal_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CoalGeneratorMenu(containerId, playerInventory, inventory, data);
    }

    public void serverTick() {
        boolean changed = false;

        if (burnTime > 0) {
            burnTime--;
            energy.receiveEnergy(FE_PER_TICK, false);
            changed = true;
        }

        if (burnTime <= 0) {
            ItemStack fuel = inventory.getStackInSlot(0);
            int duration = fuel.getBurnTime(RecipeType.SMELTING);
            if (duration > 0 && energy.getEnergyStored() < energy.getMaxEnergyStored()) {
                burnTime = duration;
                maxBurnTime = duration;
                fuel.shrink(1);
                changed = true;
            }
        }

        if (energy.getEnergyStored() > 0) {
            if (chargeBattery()) changed = true;
            if (pushEnergyToNeighbors()) changed = true;
        }

        if (changed) setChanged();
    }

    private boolean chargeBattery() {
        ItemStack batteryStack = inventory.getStackInSlot(1);
        if (!(batteryStack.getItem() instanceof BatteryItem)) return false;

        ComponentEnergyStorage battery = new ComponentEnergyStorage(
                batteryStack, ModDataComponents.ENERGY.get(),
                BatteryItem.CAPACITY, BatteryItem.MAX_TRANSFER, BatteryItem.MAX_TRANSFER);

        int toTransfer = energy.extractEnergy(BATTERY_CHARGE_RATE, true);
        int received = battery.receiveEnergy(toTransfer, false);
        if (received > 0) {
            energy.extractEnergy(received, false);
            return true;
        }
        return false;
    }

    private boolean pushEnergyToNeighbors() {
        boolean pushed = false;
        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() <= 0) break;
            BlockPos neighborPos = worldPosition.relative(dir);
            IEnergyStorage neighbor = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
            if (neighbor != null && neighbor.canReceive()) {
                int toExtract = energy.extractEnergy(FE_PER_TICK, true);
                int received = neighbor.receiveEnergy(toExtract, false);
                if (received > 0) {
                    energy.extractEnergy(received, false);
                    pushed = true;
                }
            }
        }
        return pushed;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("BurnTime", burnTime);
        tag.putInt("MaxBurnTime", maxBurnTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        burnTime = tag.getInt("BurnTime");
        maxBurnTime = tag.getInt("MaxBurnTime");
    }
}
