package com.sio.firearms.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;

public abstract class EnergyStorageBlock extends BlockEntity {

    protected final EnergyStorage energy;

    protected EnergyStorageBlock(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                 int capacity, int maxReceive, int maxExtract) {
        super(type, pos, state);
        this.energy = new EnergyStorage(capacity, maxReceive, maxExtract);
    }

    public EnergyStorage getEnergyStorage() {
        return energy;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energy.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Energy")) {
            energy.deserializeNBT(registries, tag.get("Energy"));
        }
    }
}
