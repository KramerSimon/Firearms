package com.sio.firearms.item;

import com.sio.firearms.entity.SeaMineEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class SeaMineItem extends Item {

    public SeaMineItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        if (!state.getFluidState().is(Fluids.WATER)) {
            if (level.isClientSide() && player != null) {
                player.displayClientMessage(
                        Component.literal("Sea mines can only be placed in water!").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide() && player != null) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0; // top surface of water block
            double z = pos.getZ() + 0.5;

            SeaMineEntity mine = new SeaMineEntity(level, player, x, y, z);
            level.addFreshEntity(mine);

            context.getItemInHand().shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
