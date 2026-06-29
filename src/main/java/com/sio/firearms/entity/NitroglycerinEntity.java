package com.sio.firearms.entity;

import com.sio.firearms.config.FirearmsConfig;
import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class NitroglycerinEntity extends ThrowableItemProjectile {

    public NitroglycerinEntity(EntityType<? extends NitroglycerinEntity> type, Level level) {
        super(type, level);
    }

    public NitroglycerinEntity(Level level, LivingEntity thrower) {
        super(ModEntities.NITROGLYCERIN.get(), thrower, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.NITROGLYCERIN.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide()) {
            float power = 2.5f * (float) FirearmsConfig.EXPLOSION_DAMAGE_MULTIPLIER.get().doubleValue();
            level().explode(null, getX(), getY(), getZ(), power, false, Level.ExplosionInteraction.TNT);
            discard();
        }
    }
}
