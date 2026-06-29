package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.GasCentrifugeMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class GasCentrifugeBlockEntity extends EnergyStorageBlock implements MenuProvider {

    public static final int CAPACITY     = 50_000;
    public static final int MAX_RECEIVE  = 500;
    public static final int FE_PER_TICK  = 200;
    public static final int PROCESS_TIME = 600;
    public static final int TANK_SIZE    = 5_000;

    // Input: UF6; Output1: enriched UF6; Output2: depleted UF6
    public final FluidTank fluidInputTank = new FluidTank(TANK_SIZE) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return !stack.isEmpty() && stack.getFluid().isSame(ModFluids.URANIUM_HEXAFLUORIDE_STILL.get());
        }
        @Override
        protected void onContentsChanged() { setChanged(); }
    };

    public final FluidTank fluidOutputTank1 = new FluidTank(TANK_SIZE) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    public final FluidTank fluidOutputTank2 = new FluidTank(TANK_SIZE) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    // Fill-only handler for fluid pipe input
    public final IFluidHandler fluidInputHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int t) { return fluidInputTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t) { return fluidInputTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack s) { return fluidInputTank.isFluidValid(0, s); }
        @Override public int fill(FluidStack resource, FluidAction action) { return fluidInputTank.fill(resource, action); }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    };

    // Drain-only handler for output tanks (enriched UF6 first, then depleted UF6)
    public final IFluidHandler fluidOutputHandler = new IFluidHandler() {
        @Override public int getTanks() { return 2; }
        @Override public FluidStack getFluidInTank(int t) {
            return t == 0 ? fluidOutputTank1.getFluidInTank(0) : fluidOutputTank2.getFluidInTank(0);
        }
        @Override public int getTankCapacity(int t) {
            return t == 0 ? fluidOutputTank1.getTankCapacity(0) : fluidOutputTank2.getTankCapacity(0);
        }
        @Override public boolean isFluidValid(int t, FluidStack s) { return false; }
        @Override public int fill(FluidStack resource, FluidAction action) { return 0; }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) {
            FluidStack r = fluidOutputTank1.drain(resource, action);
            if (r.isEmpty()) r = fluidOutputTank2.drain(resource, action);
            return r;
        }
        @Override public FluidStack drain(int maxDrain, FluidAction action) {
            FluidStack r = fluidOutputTank1.drain(maxDrain, action);
            if (r.isEmpty()) r = fluidOutputTank2.drain(maxDrain, action);
            return r;
        }
    };

    // Full-access handler: fill() routes to input tank, drain() routes to output tanks.
    // Registered on all sides so external pipes can push UF6 in and pull products out.
    public final IFluidHandler fullAccessHandler = new IFluidHandler() {
        @Override public int getTanks() { return 3; }
        @Override public FluidStack getFluidInTank(int t) {
            return switch (t) {
                case 0 -> fluidInputTank.getFluidInTank(0);
                case 1 -> fluidOutputTank1.getFluidInTank(0);
                default -> fluidOutputTank2.getFluidInTank(0);
            };
        }
        @Override public int getTankCapacity(int t) {
            return switch (t) {
                case 0 -> fluidInputTank.getTankCapacity(0);
                case 1 -> fluidOutputTank1.getTankCapacity(0);
                default -> fluidOutputTank2.getTankCapacity(0);
            };
        }
        @Override public boolean isFluidValid(int t, FluidStack s) {
            return t == 0 && fluidInputTank.isFluidValid(0, s);
        }
        @Override public int fill(FluidStack resource, FluidAction action) {
            return fluidInputTank.fill(resource, action);
        }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) {
            FluidStack r = fluidOutputTank1.drain(resource, action);
            if (r.isEmpty()) r = fluidOutputTank2.drain(resource, action);
            return r;
        }
        @Override public FluidStack drain(int maxDrain, FluidAction action) {
            FluidStack r = fluidOutputTank1.drain(maxDrain, action);
            if (r.isEmpty()) r = fluidOutputTank2.drain(maxDrain, action);
            return r;
        }
    };

    private int progress = 0;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> progress;
                case 3 -> PROCESS_TIME;
                case 4 -> fluidInputTank.getFluidAmount();
                case 5 -> TANK_SIZE;
                case 6 -> fluidOutputTank1.getFluidAmount();
                case 7 -> TANK_SIZE;
                case 8 -> fluidOutputTank2.getFluidAmount();
                case 9 -> TANK_SIZE;
                case 10 -> fluidInputTank.isEmpty() ? 0 : BuiltInRegistries.FLUID.getId(fluidInputTank.getFluid().getFluid());
                case 11 -> fluidOutputTank1.isEmpty() ? 0 : BuiltInRegistries.FLUID.getId(fluidOutputTank1.getFluid().getFluid());
                case 12 -> fluidOutputTank2.isEmpty() ? 0 : BuiltInRegistries.FLUID.getId(fluidOutputTank2.getFluid().getFluid());
                default -> 0;
            };
        }
        @Override public void set(int i, int v) { if (i == 2) progress = v; }
        @Override public int getCount() { return 13; }
    };

    public GasCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GAS_CENTRIFUGE.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public FluidTank getFluidInputTank()   { return fluidInputTank; }
    public FluidTank getFluidOutputTank1() { return fluidOutputTank1; }
    public FluidTank getFluidOutputTank2() { return fluidOutputTank2; }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.gas_centrifuge");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new GasCentrifugeMenu(id, inv, data);
    }

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;

        boolean hasInput   = fluidInputTank.getFluidAmount() >= 1000;
        boolean hasSpace1  = fluidOutputTank1.getSpace() >= 300;
        boolean hasSpace2  = fluidOutputTank2.getSpace() >= 700;
        boolean hasEnergy  = energy.getEnergyStored() >= FE_PER_TICK;

        if (hasInput && hasSpace1 && hasSpace2 && hasEnergy) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            changed = true;

            if (progress >= PROCESS_TIME) {
                fluidInputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                fluidOutputTank1.fill(new FluidStack(ModFluids.ENRICHED_UF6_STILL.get(), 300), IFluidHandler.FluidAction.EXECUTE);
                fluidOutputTank2.fill(new FluidStack(ModFluids.DEPLETED_UF6_STILL.get(), 700), IFluidHandler.FluidAction.EXECUTE);
                progress = 0;
            }
        } else if (progress > 0) {
            progress = 0;
            changed = true;
        }

        if (changed) setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FluidIn",  fluidInputTank.writeToNBT(registries, new CompoundTag()));
        tag.put("FluidOut1", fluidOutputTank1.writeToNBT(registries, new CompoundTag()));
        tag.put("FluidOut2", fluidOutputTank2.writeToNBT(registries, new CompoundTag()));
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FluidIn"))   fluidInputTank.readFromNBT(registries, tag.getCompound("FluidIn"));
        if (tag.contains("FluidOut1")) fluidOutputTank1.readFromNBT(registries, tag.getCompound("FluidOut1"));
        if (tag.contains("FluidOut2")) fluidOutputTank2.readFromNBT(registries, tag.getCompound("FluidOut2"));
        progress = tag.getInt("Progress");
    }
}
