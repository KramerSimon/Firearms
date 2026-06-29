package com.sio.firearms.block;

import com.sio.firearms.menu.TrashCanMenu;
import com.sio.firearms.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;

public class TrashCanBlockEntity extends BlockEntity implements MenuProvider {

    private static final int FLUID_CAPACITY  = 16_000;
    private static final int ENERGY_CAPACITY = Integer.MAX_VALUE / 2;

    public final SimpleContainer items = new SimpleContainer(9) {
        @Override public void setChanged() { TrashCanBlockEntity.this.setChanged(); }
    };

    private final FluidTank fluidTank = new FluidTank(FLUID_CAPACITY) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    // Internal energy with full extract so we can drain it each tick ourselves
    private final EnergyStorage internalEnergy = new EnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY);

    private int tickCount = 0;

    final ContainerData data = new ContainerData() {
        @Override public int get(int i) {
            return switch (i) {
                case 0 -> Math.min(fluidTank.getFluidAmount(), Short.MAX_VALUE);
                case 1 -> Math.min(FLUID_CAPACITY, Short.MAX_VALUE);
                default -> 0;
            };
        }
        @Override public void set(int i, int v) {}
        @Override public int getCount() { return 2; }
    };

    // Insert-only: items are trashed on the next 20-tick purge
    public final IItemHandler itemHandler = new IItemHandler() {
        @Override public int getSlots() { return 9; }
        @Override public ItemStack getStackInSlot(int slot) { return items.getItem(slot); }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!simulate) items.setItem(slot, stack);
            return ItemStack.EMPTY;
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
        @Override public int getSlotLimit(int slot) { return 64; }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return true; }
    };

    // Fill-only fluid handler — accepts any fluid, deletes it every 20 ticks
    public final IFluidHandler fluidHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int t) { return fluidTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t) { return FLUID_CAPACITY; }
        @Override public boolean isFluidValid(int t, FluidStack s) { return true; }
        @Override public int fill(FluidStack resource, FluidAction action) { return fluidTank.fill(resource, action); }
        @Override public FluidStack drain(FluidStack r, FluidAction a) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int max, FluidAction a) { return FluidStack.EMPTY; }
    };

    // Receive-only energy handler — accepts FE from wires/ports, deletes it every tick
    public final IEnergyStorage energyHandler = new IEnergyStorage() {
        @Override public int receiveEnergy(int max, boolean simulate) { return internalEnergy.receiveEnergy(max, simulate); }
        @Override public int extractEnergy(int max, boolean simulate) { return 0; }
        @Override public int getEnergyStored() { return internalEnergy.getEnergyStored(); }
        @Override public int getMaxEnergyStored() { return ENERGY_CAPACITY; }
        @Override public boolean canExtract() { return false; }
        @Override public boolean canReceive() { return true; }
    };

    public TrashCanBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRASH_CAN.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TrashCanBlockEntity be) {
        be.tick();
    }

    private void tick() {
        tickCount++;
        boolean changed = false;

        // Drain all energy every tick
        if (internalEnergy.getEnergyStored() > 0) {
            internalEnergy.extractEnergy(internalEnergy.getEnergyStored(), false);
            changed = true;
        }

        // Purge items and fluid every 20 ticks
        if (tickCount % 20 == 0) {
            for (int i = 0; i < 9; i++) {
                if (!items.getItem(i).isEmpty()) {
                    items.setItem(i, ItemStack.EMPTY);
                    changed = true;
                }
            }
            if (!fluidTank.isEmpty()) {
                fluidTank.drain(fluidTank.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
                changed = true;
            }
        }

        if (changed) setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.trash_can");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new TrashCanMenu(id, inv, items, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag itemsTag = new CompoundTag();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = items.getItem(i);
            if (!stack.isEmpty()) itemsTag.put(String.valueOf(i), stack.save(registries));
        }
        tag.put("Items", itemsTag);
        tag.put("Fluid", fluidTank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Items")) {
            CompoundTag itemsTag = tag.getCompound("Items");
            for (int i = 0; i < 9; i++) {
                if (itemsTag.contains(String.valueOf(i)))
                    items.setItem(i, ItemStack.parseOptional(registries, itemsTag.getCompound(String.valueOf(i))));
            }
        }
        if (tag.contains("Fluid")) fluidTank.readFromNBT(registries, tag.getCompound("Fluid"));
    }
}
