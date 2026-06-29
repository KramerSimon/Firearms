package com.sio.firearms.event;

import com.sio.firearms.Firearms;
import com.sio.firearms.config.FirearmsConfig;
import com.sio.firearms.entity.BulletEntity;
import com.sio.firearms.item.BatteryItem;
import com.sio.firearms.item.BulletproofVestItem;
import com.sio.firearms.item.ChainsawItem;
import com.sio.firearms.item.NightVisionGogglesItem;
import com.sio.firearms.item.NitroglycerinItem;
import com.sio.firearms.item.RiotShieldItem;
import com.sio.firearms.item.RubberBootsItem;
import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ModGameEvents {

    @SubscribeEvent
    public static void onLivingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        // Bulletproof vest: 60% damage reduction from bullets
        if (event.getSource().getDirectEntity() instanceof BulletEntity) {
            if (entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof BulletproofVestItem) {
                event.setAmount(event.getAmount() * 0.4f);
            }
        }

        // Riot Shield: 80% damage reduction from bullets and melee while blocking
        if (entity instanceof Player player) {
            ItemStack offhand = player.getOffhandItem();
            ItemStack mainhand = player.getMainHandItem();
            ItemStack shieldStack = offhand.getItem() instanceof RiotShieldItem ? offhand
                    : (mainhand.getItem() instanceof RiotShieldItem ? mainhand : ItemStack.EMPTY);

            if (!shieldStack.isEmpty() && RiotShieldItem.isBlocking(shieldStack)) {
                boolean isBullet = event.getSource().getDirectEntity() instanceof BulletEntity;
                boolean isMelee = event.getSource().is(DamageTypes.PLAYER_ATTACK)
                        || event.getSource().is(DamageTypes.MOB_ATTACK);
                if (isBullet || isMelee) {
                    event.setAmount(event.getAmount() * 0.2f);
                }
            }
        }

        // Rubber Boots: 50% reduction from explosion damage
        if (event.getSource().is(DamageTypes.EXPLOSION) || event.getSource().is(DamageTypes.PLAYER_EXPLOSION)) {
            if (entity.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof RubberBootsItem) {
                event.setAmount(event.getAmount() * 0.5f);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        // Night Vision Goggles
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.getItem() instanceof NightVisionGogglesItem) {
            ItemStack batteryStack = null;
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() instanceof BatteryItem) {
                    int energy = stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
                    if (energy > 0) {
                        batteryStack = stack;
                        break;
                    }
                }
            }
            if (batteryStack != null) {
                int current = batteryStack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
                batteryStack.set(ModDataComponents.ENERGY.get(), Math.max(0, current - 5));
                MobEffectInstance existing = player.getEffect(MobEffects.NIGHT_VISION);
                if (existing == null || existing.getDuration() <= 200) {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false));
                }
            } else {
                player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }

        // Rubber Boots: no ice slipping
        if (player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof RubberBootsItem) {
            net.minecraft.core.BlockPos feetPos = player.blockPosition();
            net.minecraft.world.level.block.Block below = player.level().getBlockState(feetPos.below()).getBlock();
            boolean isIcy = below == Blocks.ICE || below == Blocks.PACKED_ICE
                    || below == Blocks.BLUE_ICE || below == Blocks.FROSTED_ICE;
            if (isIcy) {
                net.minecraft.world.phys.Vec3 mv = player.getDeltaMovement();
                player.setDeltaMovement(mv.x * 0.6, mv.y, mv.z * 0.6);
            }
        }

        // Riot Shield: remove blindness/nausea while blocking
        ItemStack offhand = player.getOffhandItem();
        ItemStack mainhand = player.getMainHandItem();
        boolean shieldBlocking =
                (offhand.getItem() instanceof RiotShieldItem && RiotShieldItem.isBlocking(offhand)) ||
                (mainhand.getItem() instanceof RiotShieldItem && RiotShieldItem.isBlocking(mainhand));
        if (shieldBlocking) {
            player.removeEffect(MobEffects.BLINDNESS);
            player.removeEffect(MobEffects.CONFUSION);
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getSlot() != EquipmentSlot.HEAD) return;
        if (!(event.getFrom().getItem() instanceof NightVisionGogglesItem)) return;
        player.removeEffect(MobEffects.NIGHT_VISION);
    }

    @SubscribeEvent
    public static void onPlayerFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        // Rubber Boots: immunity to fall damage
        if (player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof RubberBootsItem) {
            event.setCanceled(true);
            return;
        }

        if (event.getDistance() <= 3f) return;

        boolean holding = player.getMainHandItem().getItem() instanceof NitroglycerinItem
                       || player.getOffhandItem().getItem() instanceof NitroglycerinItem;
        if (!holding) return;

        float power = 3.0f * (float) FirearmsConfig.EXPLOSION_DAMAGE_MULTIPLIER.get().doubleValue();
        player.level().explode(null, player.getX(), player.getY(), player.getZ(),
                power, false, Level.ExplosionInteraction.TNT);
    }

    @SubscribeEvent
    public static void onKnockBack(LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof RubberBootsItem) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult().getType() != HitResult.Type.ENTITY) return;
        if (!(event.getRayTraceResult() instanceof EntityHitResult ehr)) return;
        if (!(ehr.getEntity() instanceof ItemEntity itemEntity)) return;
        if (!(itemEntity.getItem().getItem() instanceof NitroglycerinItem)) return;
        if (itemEntity.level().isClientSide()) return;

        float power = 3.0f * (float) FirearmsConfig.EXPLOSION_DAMAGE_MULTIPLIER.get().doubleValue();
        itemEntity.level().explode(null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                power, false, Level.ExplosionInteraction.TNT);
        itemEntity.discard();
    }

    @SubscribeEvent
    public static void onChainsawAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        ItemStack mainhand = player.getMainHandItem();
        if (!(mainhand.getItem() instanceof ChainsawItem chainsaw)) return;
        if (!chainsaw.isRunning(mainhand)) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;

        event.setCanceled(true);

        target.hurt(player.damageSources().playerAttack(player), 15.0f);
        target.addEffect(new MobEffectInstance(ModEffects.BLEEDING, 200, 0));

        int fuel = mainhand.getOrDefault(ModDataComponents.CHAINSAW_FUEL.get(), 0);
        mainhand.set(ModDataComponents.CHAINSAW_FUEL.get(), Math.max(0, fuel - 20));

        mainhand.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

}
