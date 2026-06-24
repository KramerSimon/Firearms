package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import java.util.EnumSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.slf4j.Logger;

public class FluidPipeBlockEntity extends BlockEntity {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CAPACITY = 1_000;
    private static final int MAX_TRANSFER = 100;

    private final FluidTank tank = new FluidTank(CAPACITY);

    public FluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_PIPE.get(), pos, state);
    }

    public FluidTank getFluidTank() {
        return tank;
    }

    private int tickCount = 0;

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;
        boolean shouldLog = ++tickCount % 40 == 0;

        if (shouldLog) {
            LOGGER.info("Pipe@{}: tank={}/{} mB, fluid={}",
                    worldPosition.toShortString(), tank.getFluidAmount(), tank.getCapacity(),
                    tank.isEmpty() ? "empty" : tank.getFluid().getFluid());
        }

        EnumSet<Direction> pulledFrom = EnumSet.noneOf(Direction.class);

        for (Direction dir : Direction.values()) {
            if (tank.getFluidAmount() >= tank.getCapacity()) break;
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity be = level.getBlockEntity(neighborPos);
            if (be == null || be instanceof FluidPipeBlockEntity) continue;
            IFluidHandler neighbor = level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, dir.getOpposite());
            if (shouldLog) {
                LOGGER.info("Pipe@{} PULL {}: BE={}, cap={}", worldPosition.toShortString(), dir,
                        be.getClass().getSimpleName(), neighbor != null);
            }
            if (neighbor == null) continue;

            FluidStack simDrain = neighbor.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
            if (shouldLog) {
                LOGGER.info("Pipe@{} PULL {}: simDrain={} mB of {}", worldPosition.toShortString(), dir,
                        simDrain.getAmount(), simDrain.isEmpty() ? "nothing" : simDrain.getFluid());
            }
            if (simDrain.isEmpty()) continue;

            int accepted = tank.fill(simDrain, IFluidHandler.FluidAction.SIMULATE);
            if (accepted <= 0) continue;

            FluidStack actualDrain = neighbor.drain(new FluidStack(simDrain.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
            if (!actualDrain.isEmpty()) {
                tank.fill(actualDrain, IFluidHandler.FluidAction.EXECUTE);
                pulledFrom.add(dir);
                changed = true;
            }
        }

        for (Direction dir : Direction.values()) {
            if (tank.getFluidAmount() <= 0) break;
            if (pulledFrom.contains(dir)) continue;
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity be = level.getBlockEntity(neighborPos);
            if (be == null || be instanceof FluidPipeBlockEntity) continue;
            IFluidHandler neighbor = level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, dir.getOpposite());
            if (shouldLog) {
                LOGGER.info("Pipe@{} PUSH {}: BE={}, cap={}", worldPosition.toShortString(), dir,
                        be.getClass().getSimpleName(), neighbor != null);
            }
            if (neighbor == null) continue;

            FluidStack inTank = tank.getFluid();
            if (inTank.isEmpty()) break;

            FluidStack toOffer = new FluidStack(inTank.getFluid(), Math.min(inTank.getAmount(), MAX_TRANSFER));
            int accepted = neighbor.fill(toOffer, IFluidHandler.FluidAction.SIMULATE);
            if (shouldLog) {
                LOGGER.info("Pipe@{} PUSH {}: offered={} mB, simAccepted={}", worldPosition.toShortString(), dir,
                        toOffer.getAmount(), accepted);
            }
            if (accepted <= 0) continue;

            FluidStack drained = tank.drain(new FluidStack(inTank.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
            if (!drained.isEmpty()) {
                neighbor.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                changed = true;
            }
        }

        for (Direction dir : Direction.values()) {
            if (tank.getFluidAmount() <= 0) break;
            BlockPos neighborPos = worldPosition.relative(dir);
            if (!(level.getBlockEntity(neighborPos) instanceof FluidPipeBlockEntity pipeNeighbor)) continue;

            int myAmount = tank.getFluidAmount();
            int theirAmount = pipeNeighbor.tank.getFluidAmount();
            if (myAmount <= theirAmount) continue;

            FluidStack myFluid = tank.getFluid();
            if (myFluid.isEmpty()) break;

            if (!pipeNeighbor.tank.isEmpty()
                    && !pipeNeighbor.tank.getFluid().getFluid().isSame(myFluid.getFluid())) continue;

            int diff = (myAmount - theirAmount) / 2;
            int toTransfer = Math.min(diff, MAX_TRANSFER);
            FluidStack drained = tank.drain(new FluidStack(myFluid.getFluid(), toTransfer), IFluidHandler.FluidAction.EXECUTE);
            if (!drained.isEmpty()) {
                pipeNeighbor.tank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                pipeNeighbor.setChanged();
                changed = true;
            }
        }

        if (changed) setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FluidTank", tank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FluidTank")) tank.readFromNBT(registries, tag.getCompound("FluidTank"));
    }
}
