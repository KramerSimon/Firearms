package com.sio.firearms.item;

import com.sio.firearms.entity.SeaMineEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class SeaMineItem extends Item {

    public SeaMineItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        HitResult hitResult = player.pick(5.0, 1.0f, false);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            if (level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("Sea mines can only be placed in water!").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos hitPos = blockHit.getBlockPos();
        Direction clickedFace = blockHit.getDirection();

        boolean hitIsWater    = level.getFluidState(hitPos).is(Fluids.WATER);
        boolean aboveIsWater  = level.getFluidState(hitPos.above()).is(Fluids.WATER);
        boolean clickedTopFace = clickedFace == Direction.UP;

        if (!hitIsWater && !aboveIsWater && !clickedTopFace) {
            if (level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("Sea mines can only be placed in water!").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        // Spawn at the top surface of the relevant water block
        BlockPos waterPos = hitIsWater ? hitPos : hitPos.above();

        if (!level.isClientSide()) {
            double x = waterPos.getX() + 0.5;
            double y = waterPos.getY() + 1.0;
            double z = waterPos.getZ() + 0.5;

            SeaMineEntity mine = new SeaMineEntity(level, player, x, y, z);
            level.addFreshEntity(mine);

            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
