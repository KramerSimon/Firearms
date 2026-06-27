package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class WireBlockEntity extends EnergyStorageBlock {

    private static final int CAPACITY = 500;
    private static final int MAX_TRANSFER = 500;

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COPPER_WIRE.get(), pos, state, CAPACITY, MAX_TRANSFER, MAX_TRANSFER);
    }

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;
        BlockState wireState = level.getBlockState(worldPosition);

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
                }
            }
        }

        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() <= 0) break;
            if (wireState.getValue(WireBlock.blockedPropFor(dir))) continue;
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity be = level.getBlockEntity(neighborPos);
            if (be == null || be instanceof WireBlockEntity) continue;
            IEnergyStorage neighbor = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
            if (neighbor != null && neighbor.canReceive()) {
                int toExtract = energy.extractEnergy(MAX_TRANSFER, true);
                int received = neighbor.receiveEnergy(toExtract, false);
                if (received > 0) {
                    energy.extractEnergy(received, false);
                    changed = true;
                }
            }
        }

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
                    }
                }
            }
        }

        if (changed) setChanged();
    }
}
