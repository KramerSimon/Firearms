package com.sio.firearms.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CoagulantSyringeItem extends Item {

    public CoagulantSyringeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            CoagulantItem.applyCoagulantEffects(player);
            stack.hurtAndBreak(1, serverLevel, serverPlayer, item -> {});
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
