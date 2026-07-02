package com.sio.firearms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

public class RefuelStationBlock extends Block implements EntityBlock {

    public RefuelStationBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RefuelStationBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof RefuelStationBlockEntity station) {
                RefuelStationBlockEntity.serverTick(lvl, pos, st, station);
            }
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return stack.getCapability(Capabilities.FluidHandler.ITEM) != null
                    ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.getBlockEntity(pos) instanceof RefuelStationBlockEntity station) {
            boolean interacted = FluidUtil.interactWithFluidHandler(player, hand, station.fullAccessHandler);
            if (interacted) return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof RefuelStationBlockEntity station) {
                if (player.isShiftKeyDown()) {
                    int amount = station.getFluidTank().getFluidAmount();
                    String msg = amount == 0
                            ? "Refuel Station: Empty (0 / " + RefuelStationBlockEntity.CAPACITY + " mB) — " + station.getStatusText()
                            : "Refuel Station: " + station.getFluidName() + " — " + amount
                              + " / " + RefuelStationBlockEntity.CAPACITY + " mB — " + station.getStatusText();
                    player.displayClientMessage(Component.literal(msg), true);
                } else {
                    player.openMenu(station);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
