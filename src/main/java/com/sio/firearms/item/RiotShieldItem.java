package com.sio.firearms.item;

import com.sio.firearms.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;

public class RiotShieldItem extends Item {

    private static final int USE_DURATION = 72000;
    private static final double SPEED_PENALTY = 0.3;

    public RiotShieldItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide()) {
            stack.set(ModDataComponents.SHIELD_BLOCKING.get(), true);
        }
        // Speed penalty while blocking
        if (entity instanceof Player) {
            entity.setDeltaMovement(
                    entity.getDeltaMovement().x * (1.0 - SPEED_PENALTY),
                    entity.getDeltaMovement().y,
                    entity.getDeltaMovement().z * (1.0 - SPEED_PENALTY)
            );
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!level.isClientSide()) {
            stack.set(ModDataComponents.SHIELD_BLOCKING.get(), false);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    public static boolean isBlocking(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(ModDataComponents.SHIELD_BLOCKING.get()));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        boolean blocking = isBlocking(stack);
        tooltip.add(Component.literal("Right-click and hold to raise shield").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Blocks 80% of bullet and melee damage").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("Immune to blindness effects while blocking").withStyle(ChatFormatting.YELLOW));
        if (blocking) {
            tooltip.add(Component.literal("BLOCKING").withStyle(ChatFormatting.GREEN));
        }
    }
}
