package com.sio.firearms.item;

import com.sio.firearms.config.FirearmsConfig;
import com.sio.firearms.entity.BulletEntity;
import com.sio.firearms.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

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

    protected int getFireRate() {
        return fireRate;
    }

    protected SoundEvent getSoundEvent() {
        return soundEvent;
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

        boolean ap = Boolean.TRUE.equals(stack.get(ModDataComponents.ARMOR_PIERCING.get()));
        boolean refined = Boolean.TRUE.equals(stack.get(ModDataComponents.USING_REFINED_AMMO.get()));
        boolean cordite = Boolean.TRUE.equals(stack.get(ModDataComponents.USING_CORDITE_AMMO.get()));
        boolean explosive = Boolean.TRUE.equals(stack.get(ModDataComponents.USING_EXPLOSIVE_AMMO.get()));
        int actualDamage = (int) ((ap ? 20 : (cordite ? 14 : (refined ? 10 : damage))) * FirearmsConfig.GUN_DAMAGE_MULTIPLIER.get());
        BulletEntity bullet = new BulletEntity(level, player, actualDamage);
        bullet.setArmorPiercing(ap);
        bullet.setPartialArmorPiercing(cordite);
        bullet.setExplosive(explosive);
        bullet.setShooterGun(stack);
        bullet.setPos(player.getEyePosition());
        bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 0.0F);
        level.addFreshEntity(bullet);

        level.playSound(null, player.blockPosition(), soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);

        if (player instanceof ServerPlayer serverPlayer) {
            stack.hurtAndBreak(1, serverPlayer.serverLevel(), serverPlayer,
                    item -> serverPlayer.onEquippedItemBroken(item, EquipmentSlot.MAINHAND));
        }

        player.getCooldowns().addCooldown(this, fireRate);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        boolean refined = Boolean.TRUE.equals(stack.get(ModDataComponents.USING_REFINED_AMMO.get()));
        tooltipComponents.add(Component.literal("Damage: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.valueOf(damage)).withStyle(ChatFormatting.WHITE)));
        if (refined) {
            tooltipComponents.add(Component.literal("Ammo: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Refined (+2 dmg)").withStyle(ChatFormatting.AQUA)));
        }
        tooltipComponents.add(Component.literal("Fire Rate: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(fireRate + " ticks").withStyle(ChatFormatting.WHITE)));
        tooltipComponents.add(Component.literal("Ammo: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(getAmmo(stack) + " / " + maxAmmo).withStyle(ChatFormatting.WHITE)));
        tooltipComponents.add(Component.literal("Durability: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal((stack.getMaxDamage() - stack.getDamageValue()) + " / " + stack.getMaxDamage()).withStyle(ChatFormatting.WHITE)));

        Integer kills = stack.get(ModDataComponents.KILL_COUNT.get());
        tooltipComponents.add(Component.literal("Kills: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.valueOf(kills != null ? kills : 0)).withStyle(ChatFormatting.YELLOW)));

        String attachment = stack.get(ModDataComponents.ATTACHMENT.get());
        if (attachment != null && !attachment.isEmpty()) {
            tooltipComponents.add(Component.literal("Sight: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(attachment).withStyle(ChatFormatting.WHITE)));
        }

        String underbarrel = stack.get(ModDataComponents.UNDERBARREL_ATTACHMENT.get());
        if (underbarrel != null && !underbarrel.isEmpty()) {
            tooltipComponents.add(Component.literal("Underbarrel: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(underbarrel).withStyle(ChatFormatting.WHITE)));
        }
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
