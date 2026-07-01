package com.sio.firearms.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Wall of the Nuclear Reactor multiblock; swaps to a seamless texture once the
 *  controller validates the structure (see {@link ModBlockStateProperties#CONNECTED}). */
public class ReactorWallBlock extends Block {

    public ReactorWallBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ModBlockStateProperties.CONNECTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ModBlockStateProperties.CONNECTED);
    }
}
