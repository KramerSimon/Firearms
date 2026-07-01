package com.sio.firearms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A passive part of the EBF multiblock (casing / muffler / coil). Once the furnace is
 * formed, right-clicking any part flood-fills to the controller and opens its GUI, so
 * the whole shell acts as one machine you can access from anywhere.
 */
public class EbfPartBlock extends Block {

    public EbfPartBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ModBlockStateProperties.CONNECTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ModBlockStateProperties.CONNECTED);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        EBFControllerBlockEntity controller = EbfStructure.findController(level, pos);
        if (controller == null) return InteractionResult.PASS;

        // Structure validity is only tracked server-side, so only the server opens the menu.
        if (!level.isClientSide() && controller.isStructureValid()) {
            player.openMenu(controller);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
