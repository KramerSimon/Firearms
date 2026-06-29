package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.slf4j.Logger;

public class WireBlockEntity extends EnergyStorageBlock {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CAPACITY = 10_000;
    private static final int MAX_TRANSFER = 2_000;

    private int tickCount = 0;

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COPPER_WIRE.get(), pos, state, CAPACITY, MAX_TRANSFER, MAX_TRANSFER);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WireBlockEntity be) {
        be.tick();
    }

    public void tick() {
        if (level == null) return;
        tickCount++;
        boolean verbose = (tickCount % 40 == 0);
        boolean changed = false;
        BlockState wireState = level.getBlockState(worldPosition);

        if (verbose) {
            LOGGER.debug("[Wire@{}] tick #{} — buffer={}/{}FE",
                    worldPosition.toShortString(), tickCount,
                    energy.getEnergyStored(), CAPACITY);
            for (Direction dir : Direction.values()) {
                BlockPos nb = worldPosition.relative(dir);
                boolean blocked = wireState.getValue(WireBlock.blockedPropFor(dir));
                IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, nb, dir.getOpposite());
                LOGGER.debug("[Wire@{}]   {} → {} blocked={} cap={}",
                        worldPosition.toShortString(), dir, nb.toShortString(), blocked, cap != null);
            }
        }

        // Pull from non-wire neighbors that can extract (e.g. EnergyPort in SOURCE mode)
        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() >= energy.getMaxEnergyStored()) break;
            if (wireState.getValue(WireBlock.blockedPropFor(dir))) continue;
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity be = level.getBlockEntity(neighborPos);
            if (be == null || be instanceof WireBlockEntity) continue;
            IEnergyStorage neighbor = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
            if (neighbor != null && neighbor.canExtract()) {
                int space = energy.getMaxEnergyStored() - energy.getEnergyStored();
                int toReceive = Math.min(space, MAX_TRANSFER);
                int extracted = neighbor.extractEnergy(toReceive, false);
                if (extracted > 0) {
                    energy.receiveEnergy(extracted, false);
                    changed = true;
                    if (verbose) {
                        LOGGER.debug("[Wire@{}]   pulled {}FE from {} ({})",
                                worldPosition.toShortString(), extracted, dir, neighborPos.toShortString());
                    }
                }
            }
        }

        // Push to non-wire neighbors using gradient flow (high → low energy only)
        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() <= 0) break;
            if (wireState.getValue(WireBlock.blockedPropFor(dir))) continue;
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity be = level.getBlockEntity(neighborPos);
            if (be == null || be instanceof WireBlockEntity) continue;
            IEnergyStorage neighbor = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
            if (neighbor == null || !neighbor.canReceive()) continue;
            // Skip neighbors already at max capacity
            if (neighbor.getEnergyStored() >= neighbor.getMaxEnergyStored()) continue;
            int toExtract = energy.extractEnergy(MAX_TRANSFER, true);
            int received = neighbor.receiveEnergy(toExtract, false);
            if (received > 0) {
                energy.extractEnergy(received, false);
                changed = true;
                if (verbose) {
                    LOGGER.debug("[Wire@{}]   pushed {}FE to {} ({}) [neighbor={}/{}FE]",
                            worldPosition.toShortString(), received, dir, neighborPos.toShortString(),
                            neighbor.getEnergyStored(), neighbor.getMaxEnergyStored());
                }
            }
        }

        // Equalize energy between adjacent wires
        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() <= 0) break;
            if (wireState.getValue(WireBlock.blockedPropFor(dir))) continue;
            BlockPos neighborPos = worldPosition.relative(dir);
            if (level.getBlockEntity(neighborPos) instanceof WireBlockEntity wireNeighbor) {
                int myEnergy = energy.getEnergyStored();
                int theirEnergy = wireNeighbor.energy.getEnergyStored();
                if (myEnergy > theirEnergy) {
                    int diff = (myEnergy - theirEnergy) / 2;
                    int toTransfer = Math.min(diff, MAX_TRANSFER);
                    int extracted = energy.extractEnergy(toTransfer, false);
                    if (extracted > 0) {
                        wireNeighbor.energy.receiveEnergy(extracted, false);
                        wireNeighbor.setChanged();
                        changed = true;
                        if (verbose) {
                            LOGGER.debug("[Wire@{}]   equalized {}FE → wire at {}",
                                    worldPosition.toShortString(), extracted, neighborPos.toShortString());
                        }
                    }
                }
            }
        }

        if (verbose) {
            LOGGER.debug("[Wire@{}]   buffer after tick: {}/{}FE",
                    worldPosition.toShortString(), energy.getEnergyStored(), CAPACITY);
        }

        if (changed) setChanged();
    }
}
