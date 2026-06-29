package com.sio.firearms.item;

import com.sio.firearms.entity.MolotovEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MolotovCocktailItem extends Item {

    public MolotovCocktailItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            MolotovEntity molotov = new MolotovEntity(level, player);
            molotov.setPos(player.getEyePosition());
            molotov.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0f, 1.2f, 1.0f);
            level.addFreshEntity(molotov);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS,
                    0.5f, 0.4f / (level.random.nextFloat() * 0.4f + 0.8f));
        }

        stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
