package com.sio.firearms.item;

import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

public class SMGItem extends GunItem {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float SPREAD = 0.1f;

    public SMGItem(Properties properties, int damage, int fireRate, int maxAmmo, SoundEvent soundEvent) {
        super(properties, damage, fireRate, maxAmmo, soundEvent);
    }

    @Override
    public void shoot(Player player, Level level) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof SMGItem)) return;

        int currentAmmo = getAmmo(stack);

        if (currentAmmo <= 0) {
            return;
        }

        setAmmo(stack, currentAmmo - 1);

        boolean matchGrade = Boolean.TRUE.equals(stack.get(ModDataComponents.USING_MATCH_GRADE_AMMO.get()));
        float inaccuracy = matchGrade ? 0.0F : SPREAD;
        LOGGER.debug("[SMG] Loaded ammo type={} inaccuracy={}", matchGrade ? "Match Grade" : "Normal", inaccuracy);
        BulletEntity bullet = new BulletEntity(level, player, 5);
        bullet.setShooterGun(stack);
        bullet.setMatchGrade(matchGrade);
        bullet.setPos(player.getEyePosition());
        bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, inaccuracy);
        level.addFreshEntity(bullet);

        level.playSound(null, player.blockPosition(), getSoundEvent(), SoundSource.PLAYERS, 0.7F, 1.0F + level.getRandom().nextFloat() * 0.2F);

        if (player instanceof ServerPlayer serverPlayer) {
            stack.hurtAndBreak(1, serverPlayer.serverLevel(), serverPlayer,
                    item -> serverPlayer.onEquippedItemBroken(item, EquipmentSlot.MAINHAND));
        }

        player.getCooldowns().addCooldown(this, getFireRate());
    }
}
