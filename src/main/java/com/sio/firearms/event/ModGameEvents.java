package com.sio.firearms.event;

import com.sio.firearms.Firearms;
import com.sio.firearms.entity.BulletEntity;
import com.sio.firearms.item.BatteryItem;
import com.sio.firearms.item.BulletproofVestItem;
import com.sio.firearms.item.NightVisionGogglesItem;
import com.sio.firearms.registry.ModDataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ModGameEvents {

    @SubscribeEvent
    public static void onLivingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof BulletEntity)) return;

        LivingEntity entity = event.getEntity();
        if (entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof BulletproofVestItem) {
            event.setAmount(event.getAmount() * 0.4f);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof NightVisionGogglesItem)) return;

        // Find first battery with charge in main inventory
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

            // Refresh night vision before it hits the flicker threshold (200 ticks)
            MobEffectInstance existing = player.getEffect(MobEffects.NIGHT_VISION);
            if (existing == null || existing.getDuration() <= 200) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false));
            }
        } else {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getSlot() != EquipmentSlot.HEAD) return;
        if (!(event.getFrom().getItem() instanceof NightVisionGogglesItem)) return;

        // NVG removed — immediately cancel night vision
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
