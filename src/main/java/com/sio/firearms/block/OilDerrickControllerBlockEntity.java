package com.sio.firearms.block;

import com.sio.firearms.menu.OilDerrickMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class OilDerrickControllerBlockEntity extends BlockEntity implements MenuProvider {

    private static final int ENERGY_CAPACITY = 20_000;
    private static final int MAX_RECEIVE = 500;
    private static final int ENERGY_PER_EXTRACT = 500;
    private static final int TANK_CAPACITY = 10_000;
    private static final int EXTRACT_INTERVAL = 20;
    private static final int EXTRACT_AMOUNT = 1_000;
    private static final int SEARCH_DEPTH = 20;

    private final EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY, MAX_RECEIVE, 0);

    private final FluidTank tank = new FluidTank(TANK_CAPACITY, stack ->
            stack.getFluid().isSame(ModFluids.OIL_STILL.get()));

    private int tickCounter = 0;
    private boolean structureValid = false;
    private BlockPos structureCenter = null;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> tank.getFluidAmount();
                case 3 -> tank.getCapacity();
                case 4 -> structureValid ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
            return 5;
        }
    };

    public OilDerrickControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OIL_DERRICK_CONTROLLER.get(), pos, state);
    }

    public EnergyStorage getEnergyStorage() {
        return energy;
    }

    public FluidTank getFluidTank() {
        return tank;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.oil_derrick_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new OilDerrickMenu(containerId, playerInventory, data);
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
                BlockPos basePos = center.offset(x, 0, z);
                if (basePos.equals(worldPosition)) continue;
                if (!isValidStructureBlock(basePos, ModBlocks.OIL_DERRICK_BASE.get())) {
                    structureValid = false;
                    return false;
                }
            }
        }

        for (int y = 1; y <= 5; y++) {
            BlockPos pillarPos = center.above(y);
            if (!isValidStructureBlock(pillarPos, ModBlocks.OIL_DERRICK_PILLAR.get())) {
                structureValid = false;
                return false;
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
                if (!isValidStructureBlock(pos, ModBlocks.OIL_DERRICK_BASE.get())) return false;
            }
        }
        return isValidStructureBlock(center, ModBlocks.OIL_DERRICK_BASE.get());
    }

    private boolean isValidStructureBlock(BlockPos pos, net.minecraft.world.level.block.Block expected) {
        BlockState state = level.getBlockState(pos);
        return state.is(expected)
                || state.is(ModBlocks.ENERGY_PORT.get())
                || state.is(ModBlocks.FLUID_PORT.get());
    }

    public void serverTick() {
        if (level == null) return;

        tickCounter++;
        if (tickCounter >= EXTRACT_INTERVAL) {
            tickCounter = 0;
            checkStructure();
        } else {
            return;
        }

        if (!structureValid) return;
        if (energy.getEnergyStored() < ENERGY_PER_EXTRACT) return;
        if (tank.getFluidAmount() >= tank.getCapacity()) return;

        BlockPos searchOrigin = structureCenter != null ? structureCenter : worldPosition;
        for (int y = 1; y <= SEARCH_DEPTH; y++) {
            BlockPos searchPos = searchOrigin.below(y);
            FluidState fluidState = level.getFluidState(searchPos);
            if (fluidState.isSource() && fluidState.getType().isSame(ModFluids.OIL_STILL.get())) {
                energy.extractEnergy(ENERGY_PER_EXTRACT, false);
                level.removeBlock(searchPos, false);
                tank.fill(new FluidStack(ModFluids.OIL_STILL.get(), EXTRACT_AMOUNT), FluidTank.FluidAction.EXECUTE);
                setChanged();
                return;
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energy.serializeNBT(registries));
        tag.put("FluidTank", tank.writeToNBT(registries, new CompoundTag()));
        tag.putBoolean("StructureValid", structureValid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Energy")) energy.deserializeNBT(registries, tag.get("Energy"));
        if (tag.contains("FluidTank")) tank.readFromNBT(registries, tag.getCompound("FluidTank"));
        structureValid = tag.getBoolean("StructureValid");
    }
}
