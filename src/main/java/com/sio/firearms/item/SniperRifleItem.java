package com.sio.firearms.item;

import com.sio.firearms.entity.BulletEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SniperRifleItem extends GunItem {

    public SniperRifleItem(Properties properties, int damage, int fireRate, int maxAmmo, SoundEvent soundEvent) {
        super(properties, damage, fireRate, maxAmmo, soundEvent);
    }

    @Override
    public void shoot(Player player, Level level) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof SniperRifleItem)) return;

        int currentAmmo = getAmmo(stack);

        if (currentAmmo <= 0) {
            player.sendSystemMessage(Component.literal("Gun is empty! Press R to reload."));
            return;
        }

        setAmmo(stack, currentAmmo - 1);

        BulletEntity bullet = new BulletEntity(level, player, 24);
        bullet.setShooterGun(stack);
        bullet.setPiercingCount(3);
        bullet.setPos(player.getEyePosition());
        bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 6.0F, 0.0F);
        level.addFreshEntity(bullet);

        level.playSound(null, player.blockPosition(), getSoundEvent(), SoundSource.PLAYERS, 1.0F, 0.6F);

        player.getCooldowns().addCooldown(this, getFireRate());
    }
}
