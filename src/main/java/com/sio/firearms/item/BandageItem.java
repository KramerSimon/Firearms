package com.sio.firearms.item;

import com.sio.firearms.registry.ModEffects;
import com.sio.firearms.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BandageItem extends Item {

    public BandageItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.hasEffect(ModEffects.BLEEDING)) {
            if (!level.isClientSide()) {
                player.removeEffect(ModEffects.BLEEDING);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.BANDAGE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            stack.shrink(1);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return InteractionResultHolder.pass(stack);
    }
}
