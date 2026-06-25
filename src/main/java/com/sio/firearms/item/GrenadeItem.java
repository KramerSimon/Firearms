package com.sio.firearms.item;

import com.sio.firearms.entity.GrenadeEntity;
import com.sio.firearms.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GrenadeItem extends Item {

    public GrenadeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            GrenadeEntity grenade = new GrenadeEntity(level, player);
            grenade.setPos(player.getEyePosition());
            grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 1.5f, 1.0f);
            level.addFreshEntity(grenade);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.GRENADE_PIN.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }

        stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
