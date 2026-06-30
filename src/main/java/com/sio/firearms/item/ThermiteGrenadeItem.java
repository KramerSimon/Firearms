package com.sio.firearms.item;

import com.sio.firearms.entity.ThermiteGrenadeEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThermiteGrenadeItem extends Item {

    public ThermiteGrenadeItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            ThermiteGrenadeEntity grenade = new ThermiteGrenadeEntity(level, player);
            grenade.setPos(player.getEyePosition());
            grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 1.5f, 1.0f);
            level.addFreshEntity(grenade);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5f, 0.8f);
        }

        stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
