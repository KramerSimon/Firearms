package com.sio.firearms.item;

import com.sio.firearms.entity.NapalmBombEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NapalmBombItem extends Item {

    public NapalmBombItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            NapalmBombEntity bomb = new NapalmBombEntity(level, player);
            bomb.setPos(player.getEyePosition());
            // Heavier arc (-20 pitch offset) and slower speed than a grenade (1.5f → 1.0f)
            bomb.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0f, 1.0f, 1.0f);
            level.addFreshEntity(bomb);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5f, 0.5f);
        }

        stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
