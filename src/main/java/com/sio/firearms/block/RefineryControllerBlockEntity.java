package com.sio.firearms.block;

import com.sio.firearms.menu.RefineryMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

public class RefineryControllerBlockEntity extends BlockEntity implements MenuProvider {

    private static final int ENERGY_CAPACITY = 50_000;
    private static final int MAX_RECEIVE = 1_000;
    private static final int FE_PER_TICK = 500;
    private static final int PROCESS_TIME = 200;
    private static final int OIL_PER_CYCLE = 1_000;
    private static final int FUEL_PER_CYCLE = 500;
    private static final int TANK_CAPACITY = 10_000;

    private final EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY, MAX_RECEIVE, 0);

    private final FluidTank oilTank = new FluidTank(TANK_CAPACITY, stack ->
            stack.getFluid().isSame(ModFluids.OIL_STILL.get()));

    private final FluidTank fuelTank = new FluidTank(TANK_CAPACITY, stack ->
            stack.getFluid().isSame(ModFluids.FUEL_STILL.get()));

    private final IFluidHandler oilInputHandler = new IFluidHandler() {
        @Override
        public int getTanks() { return 1; }

        @Override
        public FluidStack getFluidInTank(int tank) { return oilTank.getFluidInTank(0); }

        @Override
        public int getTankCapacity(int tank) { return oilTank.getTankCapacity(0); }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) { return oilTank.isFluidValid(0, stack); }

        @Override
        public int fill(FluidStack resource, FluidAction action) { return oilTank.fill(resource, action); }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    };

    private final IFluidHandler fuelOutputHandler = new IFluidHandler() {
        @Override
        public int getTanks() { return 1; }

        @Override
        public FluidStack getFluidInTank(int tank) { return fuelTank.getFluidInTank(0); }

        @Override
        public int getTankCapacity(int tank) { return fuelTank.getTankCapacity(0); }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) { return fuelTank.isFluidValid(0, stack); }

        @Override
        public int fill(FluidStack resource, FluidAction action) { return 0; }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) { return fuelTank.drain(resource, action); }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) { return fuelTank.drain(maxDrain, action); }
    };

    private final ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private int progress = 0;
    private int cycleCount = 0;
    private boolean structureValid = false;
    private BlockPos structureCenter = null;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> oilTank.getFluidAmount();
                case 3 -> fuelTank.getFluidAmount();
                case 4 -> structureValid ? 1 : 0;
                case 5 -> progress;
                case 6 -> PROCESS_TIME;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 5) progress = value;
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public RefineryControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFINERY_CONTROLLER.get(), pos, state);
    }

    public EnergyStorage getEnergyStorage() {
        return energy;
    }

    public FluidTank getOilTank() {
        return oilTank;
    }

    public FluidTank getFuelTank() {
        return fuelTank;
    }

    public IFluidHandler getOilInputHandler() {
        return oilInputHandler;
    }

    public IFluidHandler getFuelOutputHandler() {
        return fuelOutputHandler;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.refinery_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RefineryMenu(containerId, playerInventory, inventory, data);
    }

    public boolean checkStructure() {
        if (level == null) return false;

        structureCenter = findCenter();
        if (structureCenter == null) {
            structureValid = false;
            return false;
        }

        BlockPos center = structureCenter;

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = center.offset(x, 0, z);
                if (!pos.equals(worldPosition) && !isValidStructureBlock(pos, ModBlocks.REFINERY_BASE.get())) {
                    structureValid = false;
                    return false;
                }
            }
        }

        for (int y = 1; y <= 2; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    if (!isValidStructureBlock(pos, ModBlocks.REFINERY_WALL.get())) {
                        structureValid = false;
                        return false;
                    }
                }
            }
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = center.offset(x, 3, z);
                if (!isValidStructureBlock(pos, ModBlocks.REFINERY_TOP.get())) {
                    structureValid = false;
                    return false;
                }
            }
        }

        structureValid = true;
        return true;
    }

    private BlockPos findCenter() {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockPos candidate = worldPosition.offset(dx, 0, dz);
                if (isValidCenter(candidate)) return candidate;
            }
        }
        return null;
    }

    private boolean isValidCenter(BlockPos center) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = center.offset(x, 0, z);
                if (pos.equals(worldPosition)) continue;
                if (!isValidStructureBlock(pos, ModBlocks.REFINERY_BASE.get())) return false;
            }
        }
        return isValidStructureBlock(center, ModBlocks.REFINERY_BASE.get());
    }

    private boolean isValidStructureBlock(BlockPos pos, Block expected) {
        BlockState state = level.getBlockState(pos);
        return state.is(expected)
                || state.is(ModBlocks.ENERGY_PORT.get())
                || state.is(ModBlocks.FLUID_PORT.get());
    }

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;

        if (level.getGameTime() % 40 == 0) {
            checkStructure();
        }

        if (tryDrainOilBucket()) changed = true;

        if (!structureValid) {
            if (progress > 0) { progress = 0; changed = true; }
            if (changed) setChanged();
            return;
        }

        boolean canProcess = energy.getEnergyStored() >= FE_PER_TICK
                && oilTank.getFluidAmount() >= OIL_PER_CYCLE
                && fuelTank.getFluidAmount() + FUEL_PER_CYCLE <= fuelTank.getCapacity();

        if (canProcess) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            changed = true;

            if (progress >= PROCESS_TIME) {
                oilTank.drain(OIL_PER_CYCLE, FluidTank.FluidAction.EXECUTE);
                fuelTank.fill(new FluidStack(ModFluids.FUEL_STILL.get(), FUEL_PER_CYCLE), FluidTank.FluidAction.EXECUTE);

                tryOutputFuelBucket();
                tryOutputRubber();
                cycleCount++;
                if (cycleCount % 5 == 0) {
                    tryOutputGunOil();
                }

                progress = 0;
            }
        } else if (progress > 0) {
            progress = 0;
            changed = true;
        }

        if (changed) setChanged();
    }

    private boolean tryDrainOilBucket() {
        ItemStack oilBucket = inventory.getStackInSlot(0);
        ItemStack emptySlot = inventory.getStackInSlot(1);

        if (oilBucket.is(ModItems.OIL_BUCKET.get())
                && oilTank.getFluidAmount() + 1000 <= oilTank.getCapacity()
                && (emptySlot.isEmpty() || (emptySlot.is(Items.BUCKET) && emptySlot.getCount() < emptySlot.getMaxStackSize()))) {
            oilBucket.shrink(1);
            oilTank.fill(new FluidStack(ModFluids.OIL_STILL.get(), 1000), FluidTank.FluidAction.EXECUTE);
            if (emptySlot.isEmpty()) {
                inventory.setStackInSlot(1, new ItemStack(Items.BUCKET));
            } else {
                emptySlot.grow(1);
            }
            return true;
        }
        return false;
    }

    private void tryOutputFuelBucket() {
        ItemStack fuelSlot = inventory.getStackInSlot(2);
        if (fuelTank.getFluidAmount() >= 1000
                && (fuelSlot.isEmpty() || (fuelSlot.is(ModItems.FUEL_BUCKET.get()) && fuelSlot.getCount() < fuelSlot.getMaxStackSize()))) {
            fuelTank.drain(1000, FluidTank.FluidAction.EXECUTE);
            if (fuelSlot.isEmpty()) {
                inventory.setStackInSlot(2, new ItemStack(ModItems.FUEL_BUCKET.get()));
            } else {
                fuelSlot.grow(1);
            }
        }
    }

    private void tryOutputRubber() {
        ItemStack rubberSlot = inventory.getStackInSlot(3);
        if (rubberSlot.isEmpty()) {
            inventory.setStackInSlot(3, new ItemStack(ModItems.RUBBER_SHEET.get()));
        } else if (rubberSlot.is(ModItems.RUBBER_SHEET.get()) && rubberSlot.getCount() < rubberSlot.getMaxStackSize()) {
            rubberSlot.grow(1);
        }
    }

    private void tryOutputGunOil() {
        ItemStack oilSlot = inventory.getStackInSlot(4);
        if (oilSlot.isEmpty()) {
            inventory.setStackInSlot(4, new ItemStack(ModItems.GUN_OIL.get()));
        } else if (oilSlot.is(ModItems.GUN_OIL.get()) && oilSlot.getCount() < oilSlot.getMaxStackSize()) {
            oilSlot.grow(1);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energy.serializeNBT(registries));
        tag.put("OilTank", oilTank.writeToNBT(registries, new CompoundTag()));
        tag.put("FuelTank", fuelTank.writeToNBT(registries, new CompoundTag()));
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
        tag.putInt("CycleCount", cycleCount);
        tag.putBoolean("StructureValid", structureValid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Energy")) energy.deserializeNBT(registries, tag.get("Energy"));
        if (tag.contains("OilTank")) oilTank.readFromNBT(registries, tag.getCompound("OilTank"));
        if (tag.contains("FuelTank")) fuelTank.readFromNBT(registries, tag.getCompound("FuelTank"));
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
        cycleCount = tag.getInt("CycleCount");
        structureValid = tag.getBoolean("StructureValid");
    }
}
