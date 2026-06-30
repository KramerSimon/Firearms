package com.sio.firearms.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class MorphineItem extends Item {

    public MorphineItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            applyMorphineEffects(player);
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    static void applyMorphineEffects(Player player) {
        new ArrayList<>(player.getActiveEffects()).stream()
            .filter(e -> e.getEffect().value().getCategory() == MobEffectCategory.HARMFUL)
            .forEach(e -> player.removeEffect(e.getEffect()));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
    }
}
