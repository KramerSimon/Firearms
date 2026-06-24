package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class FluidPortBlockEntity extends BlockEntity {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CAPACITY = 2_000;
    private static final int MAX_TRANSFER = 100;
    private static final int MAX_BFS_DEPTH = 20;
    private static final int RESCAN_INTERVAL = 100;

    public enum Mode { INPUT, OUTPUT }

    private final FluidTank tank = new FluidTank(CAPACITY);
    private int tickCount = 0;
    private BlockPos cachedControllerPos = null;
    private Mode mode = Mode.INPUT;

    private final IFluidHandler fillOnlyHandler = new IFluidHandler() {
        @Override
        public int getTanks() { return 1; }

        @Override
        public FluidStack getFluidInTank(int t) { return tank.getFluidInTank(0); }

        @Override
        public int getTankCapacity(int t) { return tank.getTankCapacity(0); }

        @Override
        public boolean isFluidValid(int t, FluidStack stack) { return tank.isFluidValid(0, stack); }

        @Override
        public int fill(FluidStack resource, FluidAction action) { return tank.fill(resource, action); }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    };

    private final IFluidHandler drainOnlyHandler = new IFluidHandler() {
        @Override
        public int getTanks() { return 1; }

        @Override
        public FluidStack getFluidInTank(int t) { return tank.getFluidInTank(0); }

        @Override
        public int getTankCapacity(int t) { return tank.getTankCapacity(0); }

        @Override
        public boolean isFluidValid(int t, FluidStack stack) { return tank.isFluidValid(0, stack); }

        @Override
        public int fill(FluidStack resource, FluidAction action) { return 0; }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) { return tank.drain(resource, action); }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) { return tank.drain(maxDrain, action); }
    };

    public FluidPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_PORT.get(), pos, state);
    }

    public FluidTank getFluidTank() {
        return tank;
    }

    public Mode getMode() {
        return mode;
    }

    public void toggleMode() {
        mode = mode == Mode.INPUT ? Mode.OUTPUT : Mode.INPUT;
        setChanged();
    }

    public IFluidHandler getExposedHandler() {
        return mode == Mode.INPUT ? fillOnlyHandler : drainOnlyHandler;
    }

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;
        tickCount++;
        boolean shouldLog = tickCount % 40 == 0;

        if (tickCount % RESCAN_INTERVAL == 0) {
            cachedControllerPos = findControllerBFS();
            if (shouldLog) {
                LOGGER.info("FluidPort@{} [{}]: BFS rescan, controller={}",
                        worldPosition.toShortString(), mode,
                        cachedControllerPos != null ? cachedControllerPos.toShortString() : "none");
            }
        }

        if (cachedControllerPos == null) return;

        BlockEntity be = level.getBlockEntity(cachedControllerPos);
        if (be == null) {
            cachedControllerPos = null;
            return;
        }

        if (mode == Mode.INPUT) {
            if (tank.getFluidAmount() > 0 && be instanceof RefineryControllerBlockEntity refinery) {
                IFluidHandler target = refinery.getOilInputHandler();
                FluidStack toOffer = tank.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                if (!toOffer.isEmpty()) {
                    int filled = target.fill(toOffer, IFluidHandler.FluidAction.EXECUTE);
                    if (filled > 0) {
                        tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                        changed = true;
                    }
                }
            }
        } else {
            if (tank.getFluidAmount() < tank.getCapacity()) {
                if (be instanceof OilDerrickControllerBlockEntity derrick) {
                    FluidTank source = derrick.getFluidTank();
                    FluidStack drained = source.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                    if (!drained.isEmpty()) {
                        int accepted = tank.fill(drained, IFluidHandler.FluidAction.SIMULATE);
                        if (accepted > 0) {
                            FluidStack actual = source.drain(new FluidStack(drained.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
                            if (!actual.isEmpty()) {
                                tank.fill(actual, IFluidHandler.FluidAction.EXECUTE);
                                changed = true;
                            }
                        }
                    }
                } else if (be instanceof RefineryControllerBlockEntity refinery) {
                    IFluidHandler source = refinery.getFuelOutputHandler();
                    FluidStack drained = source.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                    if (!drained.isEmpty()) {
                        int accepted = tank.fill(drained, IFluidHandler.FluidAction.SIMULATE);
                        if (accepted > 0) {
                            FluidStack actual = source.drain(new FluidStack(drained.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
                            if (!actual.isEmpty()) {
                                tank.fill(actual, IFluidHandler.FluidAction.EXECUTE);
                                changed = true;
                            }
                        }
                    }
                }
            }
        }

        if (changed) setChanged();
    }

    private BlockPos findControllerBFS() {
        if (level == null) return null;

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        visited.add(worldPosition);
        queue.add(worldPosition);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (visited.contains(neighbor)) continue;
                if (neighbor.distManhattan(worldPosition) > MAX_BFS_DEPTH) continue;
                visited.add(neighbor);

                BlockEntity be = level.getBlockEntity(neighbor);
                if (be instanceof OilDerrickControllerBlockEntity || be instanceof RefineryControllerBlockEntity) {
                    return neighbor;
                }

                if (isStructureBlock(level.getBlockState(neighbor).getBlock())) {
                    queue.add(neighbor);
                }
            }
        }
        return null;
    }

    private static boolean isStructureBlock(Block block) {
        return block == ModBlocks.OIL_DERRICK_BASE.get()
                || block == ModBlocks.OIL_DERRICK_PILLAR.get()
                || block == ModBlocks.OIL_DERRICK_CONTROLLER.get()
                || block == ModBlocks.REFINERY_BASE.get()
                || block == ModBlocks.REFINERY_WALL.get()
                || block == ModBlocks.REFINERY_TOP.get()
                || block == ModBlocks.REFINERY_CONTROLLER.get()
                || block == ModBlocks.ENERGY_PORT.get()
                || block == ModBlocks.FLUID_PORT.get();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FluidTank", tank.writeToNBT(registries, new CompoundTag()));
        tag.putString("Mode", mode.name());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FluidTank")) tank.readFromNBT(registries, tag.getCompound("FluidTank"));
        if (tag.contains("Mode")) {
            try { mode = Mode.valueOf(tag.getString("Mode")); } catch (IllegalArgumentException ignored) {}
        }
    }
}
