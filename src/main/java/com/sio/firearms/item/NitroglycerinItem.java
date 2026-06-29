package com.sio.firearms.item;

import com.sio.firearms.config.FirearmsConfig;
import com.sio.firearms.entity.NitroglycerinEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class NitroglycerinItem extends Item {

    public NitroglycerinItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            NitroglycerinEntity entity = new NitroglycerinEntity(level, player);
            entity.setPos(player.getEyePosition());
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), -20f, 1.0f, 1.0f);
            level.addFreshEntity(entity);
            level.playSound(null, player.blockPosition(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5f, 1.0f);
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide() && entity.isOnFire()) {
            float power = 3.0f * (float) FirearmsConfig.EXPLOSION_DAMAGE_MULTIPLIER.get().doubleValue();
            entity.level().explode(null, entity.getX(), entity.getY(), entity.getZ(),
                    power, false, Level.ExplosionInteraction.TNT);
            entity.discard();
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("⚠ UNSTABLE — Handle with care").withStyle(ChatFormatting.RED));
    }
}
