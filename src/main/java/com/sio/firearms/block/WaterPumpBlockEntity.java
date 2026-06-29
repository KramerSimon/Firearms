package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.WaterPumpMenu;
import com.sio.firearms.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class WaterPumpBlockEntity extends EnergyStorageBlock implements MenuProvider {

    public static final int CAPACITY     = 10_000;
    public static final int MAX_RECEIVE  = 200;
    public static final int FE_PER_TICK  = 20;
    public static final int TANK_SIZE    = 10_000;
    public static final int PUMP_INTERVAL = 20;   // ticks between pumping
    public static final int PUMP_AMOUNT   = 1_000; // mB per pump cycle

    public final FluidTank waterTank = new FluidTank(TANK_SIZE) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == Fluids.WATER;
        }
        @Override
        protected void onContentsChanged() { setChanged(); }
    };

    // Drain-only: water output only, no fill accepted
    public final IFluidHandler drainOnlyHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int t) { return waterTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t) { return waterTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack s) { return false; }
        @Override public int fill(FluidStack r, FluidAction a) { return 0; }
        @Override public FluidStack drain(FluidStack resource, FluidAction a) { return waterTank.drain(resource, a); }
        @Override public FluidStack drain(int maxDrain, FluidAction a) { return waterTank.drain(maxDrain, a); }
    };

    // Output-only machine; fullAccessHandler delegates entirely to drainOnlyHandler
    public final IFluidHandler fullAccessHandler = drainOnlyHandler;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> waterTank.getFluidAmount();
                case 3 -> TANK_SIZE;
                default -> 0;
            };
        }
        @Override
        public void set(int i, int v) {}
        @Override
        public int getCount() { return 4; }
    };

    public WaterPumpBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WATER_PUMP.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public FluidTank getWaterTank()   { return waterTank; }
    public ContainerData getContainerData() { return data; }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.water_pump");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new WaterPumpMenu(id, inv, data);
    }

    // ── Tick ─────────────────────────────────────────────────────────────────

    public void serverTick() {
        if (level == null) return;

        if (level.getGameTime() % PUMP_INTERVAL == 0) {
            int feNeeded = FE_PER_TICK * PUMP_INTERVAL;
            if (waterTank.getSpace() >= PUMP_AMOUNT
                    && energy.getEnergyStored() >= feNeeded
                    && hasAdjacentWaterSource()) {
                energy.extractEnergy(feNeeded, false);
                waterTank.fill(new FluidStack(Fluids.WATER, PUMP_AMOUNT),
                        IFluidHandler.FluidAction.EXECUTE);
                setChanged();
            }
        }
    }

    private boolean hasAdjacentWaterSource() {
        for (Direction dir : Direction.values()) {
            BlockPos adj = worldPosition.relative(dir);
            FluidState fs = level.getFluidState(adj);
            if (fs.is(FluidTags.WATER) && fs.isSource()) return true;
        }
        return false;
    }

    // ── NBT ──────────────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("WaterTank", waterTank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("WaterTank")) waterTank.readFromNBT(registries, tag.getCompound("WaterTank"));
    }
}
