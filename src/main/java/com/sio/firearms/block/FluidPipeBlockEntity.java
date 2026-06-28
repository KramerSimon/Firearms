package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.EnumSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
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
    private ResourceLocation lockedFluid = null; // null = accepts any fluid; set on first fill, cleared on empty

    public FluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_PIPE.get(), pos, state);
    }

    public FluidTank getFluidTank() { return tank; }
    public ResourceLocation getLockedFluid() { return lockedFluid; }

    private int tickCount = 0;

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;
        boolean shouldLog = ++tickCount % 40 == 0;

        if (shouldLog) {
            LOGGER.info("Pipe@{}: tank fluid={}, locked={}",
                    worldPosition.toShortString(),
                    tank.isEmpty() ? "empty" : tank.getFluid().getFluid() + " " + tank.getFluidAmount() + "mB",
                    lockedFluid);
        }

        EnumSet<Direction> pulledFrom = EnumSet.noneOf(Direction.class);
        BlockState pipeState = level.getBlockState(worldPosition);

        // PULL pass — draw fluid from non-pipe neighbors into this pipe's tank
        for (Direction dir : Direction.values()) {
            if (tank.getFluidAmount() >= tank.getCapacity()) break;
            if (pipeState.getValue(FluidPipeBlock.blockedPropFor(dir))) continue;
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

            ResourceLocation incomingKey = BuiltInRegistries.FLUID.getKey(simDrain.getFluid());
            if (lockedFluid != null && !lockedFluid.equals(incomingKey)) {
                if (shouldLog) {
                    LOGGER.info("Pipe@{} PULL {}: SKIP — locked to {} but source offers {}",
                            worldPosition.toShortString(), dir, lockedFluid, incomingKey);
                }
                continue;
            }

            int accepted = tank.fill(simDrain, IFluidHandler.FluidAction.SIMULATE);
            if (shouldLog) {
                LOGGER.info("Pipe@{} PULL {}: simFill accepted={} mB (pipe tank {}/{})",
                        worldPosition.toShortString(), dir, accepted,
                        tank.getFluidAmount(), tank.getCapacity());
            }
            if (accepted <= 0) continue;

            // Use copyWithAmount to preserve fluid components — new FluidStack() drops them
            // and FluidTank.drain(FluidStack) fails the equality check if components differ.
            FluidStack toExecute = simDrain.copyWithAmount(accepted);
            if (shouldLog) {
                LOGGER.info("Pipe@{} PULL {} EXECUTE: calling drain({} mB of {})",
                        worldPosition.toShortString(), dir, toExecute.getAmount(), toExecute.getFluid());
            }
            FluidStack actualDrain = neighbor.drain(toExecute, IFluidHandler.FluidAction.EXECUTE);
            if (shouldLog) {
                LOGGER.info("Pipe@{} PULL {} EXECUTE result: got {} mB of {}",
                        worldPosition.toShortString(), dir,
                        actualDrain.getAmount(), actualDrain.isEmpty() ? "nothing" : actualDrain.getFluid());
            }
            if (!actualDrain.isEmpty()) {
                int filled = tank.fill(actualDrain, IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0 && lockedFluid == null) {
                    lockedFluid = BuiltInRegistries.FLUID.getKey(actualDrain.getFluid());
                }
                if (shouldLog) {
                    LOGGER.info("Pipe@{} PULL {} tank.fill EXECUTE: accepted {} mB (pipe tank now {}/{}, locked={})",
                            worldPosition.toShortString(), dir, filled,
                            tank.getFluidAmount(), tank.getCapacity(), lockedFluid);
                }
                pulledFrom.add(dir);
                changed = true;
            }
        }

        if (shouldLog) {
            LOGGER.info("Pipe@{} PUSH pass start: tank={}/{} mB, pulledFrom={}",
                    worldPosition.toShortString(),
                    tank.getFluidAmount(), tank.getCapacity(), pulledFrom);
        }

        // PUSH pass — push fluid from this pipe's tank into non-pipe neighbors
        for (Direction dir : Direction.values()) {
            if (tank.getFluidAmount() <= 0) break;
            if (pipeState.getValue(FluidPipeBlock.blockedPropFor(dir))) continue;
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity be = level.getBlockEntity(neighborPos);
            if (be == null || be instanceof FluidPipeBlockEntity) continue;

            // Chemical Mixer accepts pushes from any non-bottom face regardless of pull direction.
            // For all other blocks, skip directions we just pulled from to avoid loopback.
            boolean isChemMixer = be instanceof ChemicalMixerBlockEntity;
            if (!isChemMixer && pulledFrom.contains(dir)) {
                if (shouldLog) {
                    LOGGER.info("Pipe@{} PUSH {}: SKIPPED — direction is in pulledFrom",
                            worldPosition.toShortString(), dir);
                }
                continue;
            }

            IFluidHandler neighbor = level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, dir.getOpposite());
            if (shouldLog) {
                LOGGER.info("Pipe@{} PUSH {}: BE={}, cap={}", worldPosition.toShortString(), dir,
                        be.getClass().getSimpleName(), neighbor != null);
            }
            if (neighbor == null) continue;

            FluidStack inTank = tank.getFluid();
            if (inTank.isEmpty()) break;

            // copyWithAmount preserves fluid components for correct fill() matching
            FluidStack toOffer = inTank.copyWithAmount(Math.min(inTank.getAmount(), MAX_TRANSFER));
            int accepted = neighbor.fill(toOffer, IFluidHandler.FluidAction.SIMULATE);
            if (shouldLog) {
                LOGGER.info("Pipe@{} PUSH {}: offered={} mB of {}, simAccepted={}", worldPosition.toShortString(), dir,
                        toOffer.getAmount(), toOffer.getFluid(), accepted);
            }
            if (accepted <= 0) {
                if (isChemMixer && shouldLog) {
                    LOGGER.info("Pipe@{} PUSH {}: ChemMixer rejected {} mB of {} (tank full or wrong fluid?)",
                            worldPosition.toShortString(), dir, toOffer.getAmount(), toOffer.getFluid());
                }
                continue;
            }

            FluidStack drained = tank.drain(inTank.copyWithAmount(accepted), IFluidHandler.FluidAction.EXECUTE);
            if (!drained.isEmpty()) {
                neighbor.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                changed = true;
                if (shouldLog) {
                    LOGGER.info("Pipe@{} PUSH {}: pushed {} mB of {} to {}",
                            worldPosition.toShortString(), dir, drained.getAmount(), drained.getFluid(),
                            be.getClass().getSimpleName());
                }
            }
        }

        // EQUALIZE pass — balance fluid between adjacent pipes carrying the same fluid
        for (Direction dir : Direction.values()) {
            if (tank.getFluidAmount() <= 0) break;
            if (pipeState.getValue(FluidPipeBlock.blockedPropFor(dir))) continue;
            BlockPos neighborPos = worldPosition.relative(dir);
            if (!(level.getBlockEntity(neighborPos) instanceof FluidPipeBlockEntity pipeNeighbor)) continue;

            int myAmount = tank.getFluidAmount();
            int theirAmount = pipeNeighbor.tank.getFluidAmount();
            if (myAmount <= theirAmount) continue;

            FluidStack myFluid = tank.getFluid();
            if (myFluid.isEmpty()) break;

            // Skip if neighbor tank already holds a different fluid
            if (!pipeNeighbor.tank.isEmpty()
                    && !pipeNeighbor.tank.getFluid().getFluid().isSame(myFluid.getFluid())) continue;
            // Skip if neighbor is locked to a different fluid type
            if (pipeNeighbor.lockedFluid != null && !pipeNeighbor.lockedFluid.equals(lockedFluid)) continue;

            int diff = (myAmount - theirAmount) / 2;
            int toTransfer = Math.min(diff, MAX_TRANSFER);
            FluidStack drained = tank.drain(myFluid.copyWithAmount(toTransfer), IFluidHandler.FluidAction.EXECUTE);
            if (!drained.isEmpty()) {
                pipeNeighbor.tank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                if (pipeNeighbor.lockedFluid == null) {
                    pipeNeighbor.lockedFluid = lockedFluid;
                }
                pipeNeighbor.setChanged();
                changed = true;
            }
        }

        // Clear lock once pipe drains empty
        if (tank.isEmpty() && lockedFluid != null) {
            lockedFluid = null;
            changed = true;
        }

        if (changed) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("FluidTank", tank.writeToNBT(registries, new CompoundTag()));
        if (lockedFluid != null) tag.putString("LockedFluid", lockedFluid.toString());
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FluidTank", tank.writeToNBT(registries, new CompoundTag()));
        if (lockedFluid != null) tag.putString("LockedFluid", lockedFluid.toString());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FluidTank")) tank.readFromNBT(registries, tag.getCompound("FluidTank"));
        if (tag.contains("LockedFluid")) lockedFluid = ResourceLocation.parse(tag.getString("LockedFluid"));
    }
}
