package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.ElectrolysisMachineMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ElectrolysisMachineBlockEntity extends EnergyStorageBlock implements MenuProvider {

    public static final int CAPACITY     = 40_000;
    public static final int MAX_RECEIVE  = 500;
    public static final int FE_PER_TICK  = 100;
    public static final int PROCESS_TIME = 300;
    public static final int TANK_SIZE    = 5_000;

    // slot 0: item input (saltpeter, fluorite_crystal, sand)
    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    public final FluidTank fluidInputTank = new FluidTank(TANK_SIZE) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return !stack.isEmpty() && stack.getFluid().isSame(Fluids.WATER);
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

    // Fill-only wrapper for fluid pipe input
    public final IFluidHandler fluidInputHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int t) { return fluidInputTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t) { return fluidInputTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack s) { return fluidInputTank.isFluidValid(0, s); }
        @Override public int fill(FluidStack r, FluidAction a) { return fluidInputTank.fill(r, a); }
        @Override public FluidStack drain(FluidStack r, FluidAction a) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int max, FluidAction a) { return FluidStack.EMPTY; }
    };

    // Drain-only wrapper for both output tanks
    public final IFluidHandler drainOnlyHandler = new IFluidHandler() {
        @Override public int getTanks() { return 2; }
        @Override public FluidStack getFluidInTank(int t) {
            return t == 0 ? fluidOutputTank1.getFluidInTank(0) : fluidOutputTank2.getFluidInTank(0);
        }
        @Override public int getTankCapacity(int t) {
            return t == 0 ? fluidOutputTank1.getTankCapacity(0) : fluidOutputTank2.getTankCapacity(0);
        }
        @Override public boolean isFluidValid(int t, FluidStack s) { return false; }
        @Override public int fill(FluidStack r, FluidAction a) { return 0; }
        @Override public FluidStack drain(FluidStack resource, FluidAction a) {
            FluidStack r = fluidOutputTank1.drain(resource, a);
            if (r.isEmpty()) r = fluidOutputTank2.drain(resource, a);
            return r;
        }
        @Override public FluidStack drain(int maxDrain, FluidAction a) {
            FluidStack r = fluidOutputTank1.drain(maxDrain, a);
            if (r.isEmpty()) r = fluidOutputTank2.drain(maxDrain, a);
            return r;
        }
    };

    // Full-access: fill→water input, drain→gas output tanks. Registered on all sides.
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
        @Override public int fill(FluidStack resource, FluidAction a) { return fluidInputTank.fill(resource, a); }
        @Override public FluidStack drain(FluidStack resource, FluidAction a) {
            FluidStack r = fluidOutputTank1.drain(resource, a);
            if (r.isEmpty()) r = fluidOutputTank2.drain(resource, a);
            return r;
        }
        @Override public FluidStack drain(int maxDrain, FluidAction a) {
            FluidStack r = fluidOutputTank1.drain(maxDrain, a);
            if (r.isEmpty()) r = fluidOutputTank2.drain(maxDrain, a);
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
                default -> 0;
            };
        }
        @Override public void set(int i, int v) { if (i == 2) progress = v; }
        @Override public int getCount() { return 10; }
    };

    public ElectrolysisMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELECTROLYSIS_MACHINE.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.electrolysis_machine");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ElectrolysisMachineMenu(id, inv, inventory, data);
    }

    private static String itemKey(ItemStack s) {
        return s.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(s.getItem()).toString();
    }

    private static String fluidKey(FluidStack s) {
        return s.isEmpty() ? "" : BuiltInRegistries.FLUID.getKey(s.getFluid()).toString();
    }

    private boolean itemIs(ItemStack s, String id) { return itemKey(s).equals(id); }
    private boolean waterHas(int mb) { return fluidKey(fluidInputTank.getFluid()).equals("minecraft:water") && fluidInputTank.getFluidAmount() >= mb; }

    private record Recipe(
        String itemId, int waterMb,
        FluidStack out1, FluidStack out2
    ) {}

    private Recipe findRecipe() {
        ItemStack slot = inventory.getStackInSlot(0);

        // water 1000mB → hydrogen 500mB + oxygen 500mB  (no item needed)
        if (slot.isEmpty() && waterHas(1000)
                && tankCanAccept(fluidOutputTank1, new FluidStack(ModFluids.HYDROGEN_GAS_STILL.get(), 500))
                && tankCanAccept(fluidOutputTank2, new FluidStack(ModFluids.OXYGEN_GAS_STILL.get(), 500))) {
            return new Recipe(null, 1000,
                new FluidStack(ModFluids.HYDROGEN_GAS_STILL.get(), 500),
                new FluidStack(ModFluids.OXYGEN_GAS_STILL.get(), 500));
        }

        // saltpeter + water 500mB → hydrogen 250mB + nitrate_solution 500mB
        if (itemIs(slot, "firearms:saltpeter") && waterHas(500)
                && tankCanAccept(fluidOutputTank1, new FluidStack(ModFluids.HYDROGEN_GAS_STILL.get(), 250))
                && tankCanAccept(fluidOutputTank2, new FluidStack(ModFluids.NITRATE_SOLUTION_STILL.get(), 500))) {
            return new Recipe("firearms:saltpeter", 500,
                new FluidStack(ModFluids.HYDROGEN_GAS_STILL.get(), 250),
                new FluidStack(ModFluids.NITRATE_SOLUTION_STILL.get(), 500));
        }

        // fluorite_crystal + water 500mB → fluorine_gas 500mB + hydrogen 250mB
        if (itemIs(slot, "firearms:fluorite_crystal") && waterHas(500)
                && tankCanAccept(fluidOutputTank1, new FluidStack(ModFluids.FLUORINE_GAS_STILL.get(), 500))
                && tankCanAccept(fluidOutputTank2, new FluidStack(ModFluids.HYDROGEN_GAS_STILL.get(), 250))) {
            return new Recipe("firearms:fluorite_crystal", 500,
                new FluidStack(ModFluids.FLUORINE_GAS_STILL.get(), 500),
                new FluidStack(ModFluids.HYDROGEN_GAS_STILL.get(), 250));
        }

        // sand + water 500mB → chlorine_gas 500mB + hydrogen 250mB
        if (itemIs(slot, "minecraft:sand") && waterHas(500)
                && tankCanAccept(fluidOutputTank1, new FluidStack(ModFluids.CHLORINE_GAS_STILL.get(), 500))
                && tankCanAccept(fluidOutputTank2, new FluidStack(ModFluids.HYDROGEN_GAS_STILL.get(), 250))) {
            return new Recipe("minecraft:sand", 500,
                new FluidStack(ModFluids.CHLORINE_GAS_STILL.get(), 500),
                new FluidStack(ModFluids.HYDROGEN_GAS_STILL.get(), 250));
        }

        return null;
    }

    private boolean tankCanAccept(FluidTank tank, FluidStack fs) {
        if (tank.isEmpty()) return tank.getCapacity() >= fs.getAmount();
        if (!fluidKey(tank.getFluid()).equals(fluidKey(fs))) return false;
        return tank.getSpace() >= fs.getAmount();
    }

    public void serverTick() {
        if (level == null) return;
        Recipe recipe = findRecipe();

        if (recipe != null && energy.getEnergyStored() >= FE_PER_TICK) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;

            if (progress >= PROCESS_TIME) {
                fluidInputTank.drain(recipe.waterMb(), IFluidHandler.FluidAction.EXECUTE);
                if (recipe.itemId() != null) inventory.getStackInSlot(0).shrink(1);
                fluidOutputTank1.fill(recipe.out1(), IFluidHandler.FluidAction.EXECUTE);
                fluidOutputTank2.fill(recipe.out2(), IFluidHandler.FluidAction.EXECUTE);
                progress = 0;
            }
            setChanged();
        } else if (progress > 0) {
            progress = 0;
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
        super.saveAdditional(tag, reg);
        tag.put("Inventory", inventory.serializeNBT(reg));
        tag.put("FluidIn",   fluidInputTank.writeToNBT(reg, new CompoundTag()));
        tag.put("FluidOut1", fluidOutputTank1.writeToNBT(reg, new CompoundTag()));
        tag.put("FluidOut2", fluidOutputTank2.writeToNBT(reg, new CompoundTag()));
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
        super.loadAdditional(tag, reg);
        if (tag.contains("Inventory")) inventory.deserializeNBT(reg, tag.getCompound("Inventory"));
        if (tag.contains("FluidIn"))   fluidInputTank.readFromNBT(reg, tag.getCompound("FluidIn"));
        if (tag.contains("FluidOut1")) fluidOutputTank1.readFromNBT(reg, tag.getCompound("FluidOut1"));
        if (tag.contains("FluidOut2")) fluidOutputTank2.readFromNBT(reg, tag.getCompound("FluidOut2"));
        progress = tag.getInt("Progress");
    }
}
