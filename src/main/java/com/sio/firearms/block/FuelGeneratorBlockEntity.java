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
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class FuelGeneratorBlockEntity extends EnergyStorageBlock implements MenuProvider {

    // FE per tick for each accepted fuel
    private static final int FE_FUEL             = 160;
    private static final int FE_GASOLINE         = 160;
    private static final int FE_DIESEL           = 200;
    private static final int FE_KEROSENE         = 140;
    private static final int FE_RESIDUAL_FUEL    = 80;
    private static final int MAX_FE_PER_TICK     = 200;  // upper bound (diesel)

    private static final int CAPACITY       = 100_000;
    private static final int FUEL_CAPACITY  = 10_000;
    private static final int FUEL_PER_TICK  = 1;

    private final FluidTank fuelTank = new FluidTank(FUEL_CAPACITY, stack -> {
        Fluid f = stack.getFluid();
        return f.isSame(ModFluids.FUEL_STILL.get())
            || f.isSame(ModFluids.GASOLINE_STILL.get())
            || f.isSame(ModFluids.DIESEL_STILL.get())
            || f.isSame(ModFluids.KEROSENE_STILL.get())
            || f.isSame(ModFluids.RESIDUAL_FUEL_OIL_STILL.get());
    });

    private final IFluidHandler fuelInputHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int t) { return fuelTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t) { return fuelTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack s) { return fuelTank.isFluidValid(0, s); }
        @Override public int fill(FluidStack r, FluidAction a) { return fuelTank.fill(r, a); }
        @Override public FluidStack drain(FluidStack r, FluidAction a) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int max, FluidAction a) { return FluidStack.EMPTY; }
    };

    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot != 0) return false;
            return stack.is(ModItems.FUEL_BUCKET.get())
                || stack.is(ModItems.GASOLINE_BUCKET.get())
                || stack.is(ModItems.DIESEL_BUCKET.get())
                || stack.is(ModItems.KEROSENE_BUCKET.get())
                || stack.is(ModItems.RESIDUAL_FUEL_OIL_BUCKET.get());
        }
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    private boolean burning = false;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> fuelTank.getFluidAmount();
                case 3 -> fuelTank.getCapacity();
                case 4 -> burning ? 1 : 0;
                default -> 0;
            };
        }
        @Override public void set(int i, int v) { if (i == 4) burning = v == 1; }
        @Override public int getCount() { return 5; }
    };

    public FuelGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FUEL_GENERATOR.get(), pos, state, CAPACITY, MAX_FE_PER_TICK, MAX_FE_PER_TICK);
    }

    public ItemStackHandler getInventory()     { return inventory; }
    public FluidTank getFuelTank()             { return fuelTank; }
    public IFluidHandler getFuelInputHandler() { return fuelInputHandler; }

    @Override public Component getDisplayName() {
        return Component.translatable("block.firearms.fuel_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new FuelGeneratorMenu(id, inv, inventory, data);
    }

    private int getFEPerTick() {
        if (fuelTank.isEmpty()) return FE_FUEL;
        Fluid f = fuelTank.getFluid().getFluid();
        if (f.isSame(ModFluids.DIESEL_STILL.get()))            return FE_DIESEL;
        if (f.isSame(ModFluids.KEROSENE_STILL.get()))          return FE_KEROSENE;
        if (f.isSame(ModFluids.RESIDUAL_FUEL_OIL_STILL.get())) return FE_RESIDUAL_FUEL;
        return FE_FUEL; // fuel + gasoline both 160
    }

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;

        if (tryDrainFuelBucket()) changed = true;

        boolean wasBurning = burning;
        int fePerTick = getFEPerTick();
        burning = fuelTank.getFluidAmount() >= FUEL_PER_TICK
                && energy.getEnergyStored() < energy.getMaxEnergyStored();

        if (burning) {
            fuelTank.drain(FUEL_PER_TICK, IFluidHandler.FluidAction.EXECUTE);
            energy.receiveEnergy(fePerTick, false);
            changed = true;
        }

        if (wasBurning != burning) changed = true;

        if (energy.getEnergyStored() > 0) {
            if (pushEnergyToNeighbors()) changed = true;
        }

        if (changed) setChanged();
    }

    private boolean tryDrainFuelBucket() {
        ItemStack bucketSlot = inventory.getStackInSlot(0);
        ItemStack outputSlot = inventory.getStackInSlot(1);

        FluidStack toFill = getBucketFluid(bucketSlot);
        if (toFill == null) return false;
        if (fuelTank.fill(toFill, IFluidHandler.FluidAction.SIMULATE) <= 0) return false;
        if (!outputSlot.isEmpty() && (!outputSlot.is(Items.BUCKET) || outputSlot.getCount() >= outputSlot.getMaxStackSize())) return false;

        fuelTank.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
        bucketSlot.shrink(1);
        if (outputSlot.isEmpty()) inventory.setStackInSlot(1, new ItemStack(Items.BUCKET));
        else outputSlot.grow(1);
        return true;
    }

    @Nullable
    private FluidStack getBucketFluid(ItemStack stack) {
        if (stack.is(ModItems.FUEL_BUCKET.get()))             return new FluidStack(ModFluids.FUEL_STILL.get(),             1000);
        if (stack.is(ModItems.GASOLINE_BUCKET.get()))         return new FluidStack(ModFluids.GASOLINE_STILL.get(),         1000);
        if (stack.is(ModItems.DIESEL_BUCKET.get()))           return new FluidStack(ModFluids.DIESEL_STILL.get(),           1000);
        if (stack.is(ModItems.KEROSENE_BUCKET.get()))         return new FluidStack(ModFluids.KEROSENE_STILL.get(),         1000);
        if (stack.is(ModItems.RESIDUAL_FUEL_OIL_BUCKET.get()))return new FluidStack(ModFluids.RESIDUAL_FUEL_OIL_STILL.get(),1000);
        return null;
    }

    private boolean pushEnergyToNeighbors() {
        boolean pushed = false;
        int fePerTick = getFEPerTick();
        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() <= 0) break;
            BlockPos neighborPos = worldPosition.relative(dir);
            IEnergyStorage neighbor = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
            if (neighbor != null && neighbor.canReceive()) {
                int toExtract = energy.extractEnergy(fePerTick, true);
                int received  = neighbor.receiveEnergy(toExtract, false);
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
        tag.put("FuelTank",  fuelTank.writeToNBT(registries, new CompoundTag()));
        tag.putBoolean("Burning", burning);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("FuelTank"))  fuelTank.readFromNBT(registries, tag.getCompound("FuelTank"));
        burning = tag.getBoolean("Burning");
    }
}
