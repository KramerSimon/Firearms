package com.sio.firearms.item;

import com.mojang.logging.LogUtils;
import com.sio.firearms.entity.BulletEntity;
import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModItems;
import net.minecraft.ChatFormatting;
import org.slf4j.Logger;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MingunItem extends Item {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int MAX_ENERGY  = 50_000;
    public static final int MAX_BULLETS = 500;
    private static final int SPIN_UP_TICKS  = 40;
    private static final int FIRE_INTERVAL  = 2;   // ticks between shots (10/sec)
    private static final int DAMAGE_PER_BULLET = 6;
    private static final int FE_PER_BULLET  = 100;
    private static final int MAX_USE_DURATION = 72000;
    private static final double SPREAD_HALF_ANGLE = Math.toRadians(15.0);

    public MingunItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.pass(player.getItemInHand(hand));

        ItemStack stack = player.getMainHandItem();

        // Offhand battery → charge minigun up to 10,000 FE per click
        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() == ModItems.BATTERY.get()) {
            if (!level.isClientSide()) {
                int batteryStored = offhand.getOrDefault(ModDataComponents.ENERGY.get(), 0);
                int minigunStored = stack.getOrDefault(ModDataComponents.MINIGUN_ENERGY.get(), 0);
                int canAbsorb     = Math.min(10_000, MAX_ENERGY - minigunStored);
                int transfer      = Math.min(canAbsorb, batteryStored);
                if (transfer > 0) {
                    stack.set(ModDataComponents.MINIGUN_ENERGY.get(), minigunStored + transfer);
                    offhand.set(ModDataComponents.ENERGY.get(), batteryStored - transfer);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.6f, 1.4f);
                } else {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8f, 1.0f);
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        // Offhand bullet → load into minigun
        if (isBulletItem(offhand)) {
            if (!level.isClientSide()) {
                int current = stack.getOrDefault(ModDataComponents.MINIGUN_BULLETS.get(), 0);
                int canLoad = Math.min(MAX_BULLETS - current, offhand.getCount());
                if (canLoad > 0) {
                    stack.set(ModDataComponents.MINIGUN_BULLETS.get(), current + canLoad);
                    offhand.shrink(canLoad);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.8f, 1.2f);
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        int energy  = player.isCreative() ? MAX_ENERGY : stack.getOrDefault(ModDataComponents.MINIGUN_ENERGY.get(), 0);
        int bullets = player.isCreative() ? MAX_BULLETS : stack.getOrDefault(ModDataComponents.MINIGUN_BULLETS.get(), 0);
        if (energy < FE_PER_BULLET || bullets <= 0) {
            if (!level.isClientSide()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 1.2f);
            }
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        LOGGER.debug("[Minigun] onUseTick called — client={} entityType={}", level.isClientSide(), entity.getClass().getSimpleName());
        if (level.isClientSide() || !(entity instanceof Player player)) return;

        int ticksUsed = MAX_USE_DURATION - remainingUseDuration;
        int spin = Math.min(ticksUsed, SPIN_UP_TICKS);
        stack.set(ModDataComponents.MINIGUN_SPIN.get(), spin);

        LOGGER.debug("[Minigun] ticksUsed={} spin={}/{} energy={} bullets={}",
                ticksUsed, spin, SPIN_UP_TICKS,
                stack.getOrDefault(ModDataComponents.MINIGUN_ENERGY.get(), 0),
                stack.getOrDefault(ModDataComponents.MINIGUN_BULLETS.get(), 0));

        // Movement penalty while spinning
        if (spin > 0) {
            Vec3 mv = player.getDeltaMovement();
            player.setDeltaMovement(mv.x * 0.5, mv.y, mv.z * 0.5);
        }

        // Not yet spun up
        if (ticksUsed < SPIN_UP_TICKS) return;

        // Fire every FIRE_INTERVAL ticks
        if ((ticksUsed - SPIN_UP_TICKS) % FIRE_INTERVAL != 0) return;

        boolean creative = player.isCreative();
        int energy  = creative ? MAX_ENERGY : stack.getOrDefault(ModDataComponents.MINIGUN_ENERGY.get(), 0);
        int bullets = creative ? MAX_BULLETS : stack.getOrDefault(ModDataComponents.MINIGUN_BULLETS.get(), 0);

        if (energy < FE_PER_BULLET || bullets <= 0) {
            LOGGER.debug("[Minigun] FIRE BLOCKED — energy={} bullets={}", energy, bullets);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8f, 1.2f);
            player.releaseUsingItem();
            return;
        }

        LOGGER.debug("[Minigun] FIRING — energy={} bullets={}", energy, bullets);
        fireBullet(level, player, stack);
        if (!creative) {
            stack.set(ModDataComponents.MINIGUN_ENERGY.get(), energy - FE_PER_BULLET);
            stack.set(ModDataComponents.MINIGUN_BULLETS.get(), bullets - 1);
        }
    }

    private void fireBullet(Level level, Player player, ItemStack stack) {
        Vec3 look = player.getLookAngle();
        double sinSpread = Math.sin(SPREAD_HALF_ANGLE);
        double dx = look.x + (level.random.nextDouble() - 0.5) * 2 * sinSpread;
        double dy = look.y + (level.random.nextDouble() - 0.5) * 2 * sinSpread;
        double dz = look.z + (level.random.nextDouble() - 0.5) * 2 * sinSpread;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);

        BulletEntity bullet = new BulletEntity(level, player, DAMAGE_PER_BULLET);
        bullet.setShooterGun(stack);
        bullet.setPos(player.getEyePosition());
        bullet.setDeltaMovement(dx / len * 3.0, dy / len * 3.0, dz / len * 3.0);
        level.addFreshEntity(bullet);

        level.playSound(null, player.blockPosition(),
                net.minecraft.sounds.SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 0.6f, 1.8f);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        stack.set(ModDataComponents.MINIGUN_SPIN.get(), 0);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return MAX_USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    private static boolean isBulletItem(ItemStack stack) {
        return stack.getItem() == ModItems.BULLET.get()
                || stack.getItem() == ModItems.REFINED_BULLET.get()
                || stack.getItem() == ModItems.ARMOR_PIERCING_BULLET.get()
                || stack.getItem() == ModItems.CORDITE_BULLET.get()
                || stack.getItem() == ModItems.EXPLOSIVE_BULLET.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int energy  = stack.getOrDefault(ModDataComponents.MINIGUN_ENERGY.get(), 0);
        int bullets = stack.getOrDefault(ModDataComponents.MINIGUN_BULLETS.get(), 0);
        int spin    = stack.getOrDefault(ModDataComponents.MINIGUN_SPIN.get(), 0);

        ChatFormatting eColor = energy > 15000 ? ChatFormatting.AQUA : ChatFormatting.RED;
        ChatFormatting bColor = bullets > 100   ? ChatFormatting.WHITE : ChatFormatting.RED;

        tooltip.add(Component.literal("Energy: " + energy + " / " + MAX_ENERGY + " FE").withStyle(eColor));
        tooltip.add(Component.literal("Ammo: " + bullets + " / " + MAX_BULLETS).withStyle(bColor));
        if (spin > 0) {
            tooltip.add(Component.literal("Spin: " + spin + " / " + SPIN_UP_TICKS).withStyle(ChatFormatting.YELLOW));
        }
        tooltip.add(Component.literal("Hold right-click to spin up and fire").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Bullets in offhand to load | Battery in offhand to charge").withStyle(ChatFormatting.GRAY));
    }
}
