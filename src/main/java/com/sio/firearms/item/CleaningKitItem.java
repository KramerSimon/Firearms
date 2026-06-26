package com.sio.firearms.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class CleaningKitItem extends Item {

    private static final int RESTORE_AMOUNT = 50;

    public CleaningKitItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(player.getItemInHand(hand));

        ItemStack stack = player.getMainHandItem();
        ItemStack offhand = player.getOffhandItem();

        if (!(offhand.getItem() instanceof GunItem)) {
            return InteractionResultHolder.pass(stack);
        }

        if (!offhand.isDamaged()) {
            if (!level.isClientSide()) {
                player.sendSystemMessage(Component.literal("Weapon is already clean!")
                        .withStyle(ChatFormatting.YELLOW));
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        if (!level.isClientSide()) {
            offhand.setDamageValue(Math.max(0, offhand.getDamageValue() - RESTORE_AMOUNT));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.sendSystemMessage(Component.literal("Weapon cleaned! (+50 durability)")
                    .withStyle(ChatFormatting.GREEN));
            level.playSound(null, player.blockPosition(),
                    SoundEvents.GRAVEL_PLACE, SoundSource.PLAYERS, 0.8F, 1.3F);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        components.add(Component.literal("Right-click with gun in offhand to restore 50 durability")
                .withStyle(ChatFormatting.GRAY));
    }
}
