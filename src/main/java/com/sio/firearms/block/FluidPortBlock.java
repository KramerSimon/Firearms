package com.sio.firearms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class FluidPortBlock extends Block implements EntityBlock {

    public FluidPortBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidPortBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof FluidPortBlockEntity port) {
                port.serverTick();
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof FluidPortBlockEntity port) {
                if (player.isShiftKeyDown()) {
                    // Sneak + right-click: cycle target fluid
                    port.cycleTargetFluid();
                    player.displayClientMessage(
                            Component.literal("Fluid Port: Targeting " + port.getTargetFluidDisplayName()), true);
                } else {
                    // Right-click: cycle INPUT / OUTPUT mode
                    port.toggleMode();
                    String modeStr = port.getMode() == FluidPortBlockEntity.Mode.INPUT ? "INPUT" : "OUTPUT";
                    player.displayClientMessage(
                            Component.literal("Fluid Port: " + modeStr + " mode"), true);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
