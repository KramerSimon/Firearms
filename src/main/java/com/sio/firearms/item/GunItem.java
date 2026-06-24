package com.sio.firearms.item;

import com.sio.firearms.entity.BulletEntity;
import com.sio.firearms.registry.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GunItem extends Item {

    private final int damage;
    private final int fireRate;
    private final int maxAmmo;
    private final SoundEvent soundEvent;

    public GunItem(Properties properties, int damage, int fireRate, int maxAmmo, SoundEvent soundEvent) {
        super(properties);
        this.damage = damage;
        this.fireRate = fireRate;
        this.maxAmmo = maxAmmo;
        this.soundEvent = soundEvent;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public int getAmmo(ItemStack stack) {
        Integer ammo = stack.get(ModDataComponents.AMMO.get());
        return ammo != null ? ammo : 0;
    }

    public void setAmmo(ItemStack stack, int ammo) {
        stack.set(ModDataComponents.AMMO.get(), ammo);
    }

    public boolean isAiming(ItemStack stack) {
        Boolean aiming = stack.get(ModDataComponents.IS_AIMING.get());
        return aiming != null && aiming;
    }

    public void setAiming(ItemStack stack, boolean aiming) {
        stack.set(ModDataComponents.IS_AIMING.get(), aiming);
    }

    public void shoot(Player player, Level level) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof GunItem)) return;

        int currentAmmo = getAmmo(stack);

        if (currentAmmo <= 0) {
            player.sendSystemMessage(Component.literal("Gun is empty! Press R to reload."));
            return;
        }

        setAmmo(stack, currentAmmo - 1);

        BulletEntity bullet = new BulletEntity(level, player, damage);
        bullet.setPos(player.getEyePosition());
        bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 0.0F);
        level.addFreshEntity(bullet);

        level.playSound(null, player.blockPosition(), soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);

        player.getCooldowns().addCooldown(this, fireRate);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            boolean aiming = isAiming(stack);
            setAiming(stack, !aiming);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
