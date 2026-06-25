package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.FuelGeneratorMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

public class FuelGeneratorBlockEntity extends EnergyStorageBlock implements MenuProvider {

    private static final int FE_PER_TICK = 160;
    private static final int CAPACITY = 100_000;
    private static final int FUEL_CAPACITY = 10_000;
    private static final int FUEL_PER_TICK = 1;

    private final FluidTank fuelTank = new FluidTank(FUEL_CAPACITY, stack ->
            stack.getFluid().isSame(ModFluids.FUEL_STILL.get()));

    private final IFluidHandler fuelInputHandler = new IFluidHandler() {
        @Override
        public int getTanks() { return 1; }

        @Override
        public FluidStack getFluidInTank(int tank) { return fuelTank.getFluidInTank(0); }

        @Override
        public int getTankCapacity(int tank) { return fuelTank.getTankCapacity(0); }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) { return fuelTank.isFluidValid(0, stack); }

        @Override
        public int fill(FluidStack resource, FluidAction action) { return fuelTank.fill(resource, action); }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    };

    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) return stack.is(ModItems.FUEL_BUCKET.get());
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private boolean burning = false;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> fuelTank.getFluidAmount();
                case 3 -> fuelTank.getCapacity();
                case 4 -> burning ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 4) burning = value == 1;
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public FuelGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FUEL_GENERATOR.get(), pos, state, CAPACITY, FE_PER_TICK, FE_PER_TICK);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public FluidTank getFuelTank() {
        return fuelTank;
    }

    public IFluidHandler getFuelInputHandler() {
        return fuelInputHandler;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.fuel_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FuelGeneratorMenu(containerId, playerInventory, inventory, data);
    }

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;

        if (tryDrainFuelBucket()) changed = true;

        boolean wasBurning = burning;
        burning = fuelTank.getFluidAmount() >= FUEL_PER_TICK
                && energy.getEnergyStored() < energy.getMaxEnergyStored();

        if (burning) {
            fuelTank.drain(FUEL_PER_TICK, IFluidHandler.FluidAction.EXECUTE);
            energy.receiveEnergy(FE_PER_TICK, false);
            changed = true;
        }

        if (wasBurning != burning) changed = true;

        if (energy.getEnergyStored() > 0) {
            if (pushEnergyToNeighbors()) changed = true;
        }

        if (changed) setChanged();
    }

    private boolean tryDrainFuelBucket() {
        ItemStack fuelBucket = inventory.getStackInSlot(0);
        ItemStack outputSlot = inventory.getStackInSlot(1);

        if (fuelBucket.is(ModItems.FUEL_BUCKET.get())
                && fuelTank.getFluidAmount() + 1000 <= fuelTank.getCapacity()
                && (outputSlot.isEmpty() || (outputSlot.is(Items.BUCKET) && outputSlot.getCount() < outputSlot.getMaxStackSize()))) {
            fuelBucket.shrink(1);
            fuelTank.fill(new FluidStack(ModFluids.FUEL_STILL.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
            if (outputSlot.isEmpty()) {
                inventory.setStackInSlot(1, new ItemStack(Items.BUCKET));
            } else {
                outputSlot.grow(1);
            }
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
        tag.put("FuelTank", fuelTank.writeToNBT(registries, new CompoundTag()));
        tag.putBoolean("Burning", burning);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("FuelTank")) fuelTank.readFromNBT(registries, tag.getCompound("FuelTank"));
        burning = tag.getBoolean("Burning");
    }
}
