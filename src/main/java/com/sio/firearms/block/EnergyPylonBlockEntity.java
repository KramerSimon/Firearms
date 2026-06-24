package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EnergyPylonBlockEntity extends EnergyStorageBlock {

    public EnergyPylonBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_PYLON.get(), pos, state, 500_000, 500, 500);
    }
}
