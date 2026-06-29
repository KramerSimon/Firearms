package com.sio.firearms.client;

import com.sio.firearms.Firearms;
import com.sio.firearms.item.GasMaskItem;
import com.sio.firearms.registry.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class GasMaskEffectHandler {

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof GasMaskItem)) return;
        if (helmet.getDamageValue() >= helmet.getMaxDamage()) return; // filter exhausted

        MobEffect effect = event.getEffectInstance().getEffect().value();
        boolean blocked = effect == MobEffects.BLINDNESS.value()
                       || effect == MobEffects.POISON.value()
                       || effect == ModEffects.RADIATION.get();

        if (blocked) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            // Degrade the filter each time an effect is blocked
            if (!entity.level().isClientSide() && entity instanceof ServerPlayer player
                    && !player.isCreative()) {
                helmet.hurtAndBreak(1, entity, EquipmentSlot.HEAD);
            }
        }
    }
}
