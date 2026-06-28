package com.sio.firearms.item;

import com.sio.firearms.block.FluidPipeBlock;
import com.sio.firearms.block.FluidPipeBlockEntity;
import com.sio.firearms.block.FluidPortBlock;
import com.sio.firearms.block.FluidPortBlockEntity;
import com.sio.firearms.block.ItemPipeBlock;
import com.sio.firearms.block.ItemPipeBlockEntity;
import com.sio.firearms.block.WireBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class WrenchItem extends Item {

    public WrenchItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockPos pos = ctx.getClickedPos();
        Direction face = ctx.getClickedFace();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof ItemPipeBlock) {
            if (level.getBlockEntity(pos) instanceof ItemPipeBlockEntity pipe) {
                ItemPipeBlockEntity.SideMode newMode = pipe.cycleSideMode(face);
                pipe.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                String msg = switch (newMode) {
                    case EXTRACT -> "Extract from " + face.getSerializedName();
                    case INSERT  -> "Insert into "  + face.getSerializedName();
                    case NONE    -> face.getSerializedName() + ": None";
                };
                ctx.getPlayer().displayClientMessage(Component.literal(msg), true);
            }
            return InteractionResult.SUCCESS;
        }

        if (block instanceof FluidPipeBlock) {
            BooleanProperty blockedProp = FluidPipeBlock.blockedPropFor(face);
            boolean wasBlocked = state.getValue(blockedProp);
            BlockState withToggle = state.setValue(blockedProp, !wasBlocked);
            level.setBlock(pos, withToggle, 3);
            // Recompute connections by triggering the block's own updateShape logic
            BlockState recomputed = withToggle.updateShape(face,
                    level.getBlockState(pos.relative(face)), level, pos, pos.relative(face));
            if (recomputed != withToggle) level.setBlock(pos, recomputed, 3);
            String msg = !wasBlocked
                    ? "Pipe " + face.getSerializedName() + ": blocked"
                    : "Pipe " + face.getSerializedName() + ": open";
            String lockMsg = "";
            if (level.getBlockEntity(pos) instanceof FluidPipeBlockEntity pipe) {
                ResourceLocation lf = pipe.getLockedFluid();
                lockMsg = " | locked: " + (lf != null ? lf : "none");
            }
            ctx.getPlayer().displayClientMessage(Component.literal(msg + lockMsg), true);
            return InteractionResult.SUCCESS;
        }

        if (block instanceof WireBlock) {
            BooleanProperty blockedProp = WireBlock.blockedPropFor(face);
            boolean wasBlocked = state.getValue(blockedProp);
            BlockState withToggle = state.setValue(blockedProp, !wasBlocked);
            level.setBlock(pos, withToggle, 3);
            BlockState recomputed = withToggle.updateShape(face,
                    level.getBlockState(pos.relative(face)), level, pos, pos.relative(face));
            if (recomputed != withToggle) level.setBlock(pos, recomputed, 3);
            String msg = !wasBlocked
                    ? "Wire " + face.getSerializedName() + ": blocked"
                    : "Wire " + face.getSerializedName() + ": open";
            ctx.getPlayer().displayClientMessage(Component.literal(msg), true);
            return InteractionResult.SUCCESS;
        }

        if (block instanceof FluidPortBlock && ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown()) {
            if (level.getBlockEntity(pos) instanceof FluidPortBlockEntity port) {
                port.cycleTargetFluid();
                ctx.getPlayer().displayClientMessage(
                        Component.literal("Fluid Port: Targeting " + port.getTargetFluidDisplayName()), true);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
