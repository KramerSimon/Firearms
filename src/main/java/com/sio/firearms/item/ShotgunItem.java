package com.sio.firearms.item;

import com.sio.firearms.entity.BulletEntity;
import com.sio.firearms.registry.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShotgunItem extends GunItem {

    private static final int PELLET_COUNT = 6;
    private static final float SPREAD = 0.3f;

    public ShotgunItem(Properties properties, int damage, int fireRate, int maxAmmo, SoundEvent soundEvent) {
        super(properties, damage, fireRate, maxAmmo, soundEvent);
    }

    @Override
    public void shoot(Player player, Level level) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof ShotgunItem)) return;

        int currentAmmo = getAmmo(stack);

        if (currentAmmo <= 0) {
            player.sendSystemMessage(Component.literal("Gun is empty! Press R to reload."));
            return;
        }

        setAmmo(stack, currentAmmo - 1);

        boolean matchGrade = Boolean.TRUE.equals(stack.get(ModDataComponents.USING_MATCH_GRADE_AMMO.get()));
        float spread = matchGrade ? 0.0F : SPREAD;
        for (int i = 0; i < PELLET_COUNT; i++) {
            BulletEntity bullet = new BulletEntity(level, player, 4);
            bullet.setShooterGun(stack);
            bullet.setMatchGrade(matchGrade);
            bullet.setPos(player.getEyePosition());
            bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, spread);
            level.addFreshEntity(bullet);
        }

        level.playSound(null, player.blockPosition(), getSoundEvent(), SoundSource.PLAYERS, 1.0F, 0.8F);

        if (player instanceof ServerPlayer serverPlayer) {
            stack.hurtAndBreak(1, serverPlayer.serverLevel(), serverPlayer,
                    item -> serverPlayer.onEquippedItemBroken(item, EquipmentSlot.MAINHAND));
        }

        player.getCooldowns().addCooldown(this, getFireRate());
    }
}
