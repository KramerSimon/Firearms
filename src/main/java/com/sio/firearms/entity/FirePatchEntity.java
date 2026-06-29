package com.sio.firearms.entity;

import com.sio.firearms.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class FirePatchEntity extends Entity {

    private int life = 0;
    private static final int MAX_LIFE = 100; // 5 seconds

    public FirePatchEntity(EntityType<?> type, Level level) {
        super(type, level);
        setNoGravity(true);
        noPhysics = true;
    }

    public FirePatchEntity(Level level, double x, double y, double z) {
        this(ModEntities.FIRE_PATCH.get(), level);
        setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        life = tag.getInt("Life");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Life", life);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            if (++life >= MAX_LIFE) { discard(); return; }

            // Damage entities every 10 ticks (2 HP/tick * 1/10 = 0.2 HP/tick effective)
            if (life % 10 == 0) {
                AABB box = new AABB(getX() - 1.5, getY() - 0.5, getZ() - 1.5,
                                    getX() + 1.5, getY() + 1.5, getZ() + 1.5);
                for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, box)) {
                    e.hurt(damageSources().onFire(), 2.0f);
                    e.igniteForSeconds(3);
                }
            }

            // Broadcast flame particles from server every 4 ticks
            if (life % 4 == 0 && level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.FLAME,
                        getX(), getY() + 0.1, getZ(),
                        3, 1.2, 0.1, 1.2, 0.04);
                sl.sendParticles(ParticleTypes.SMOKE,
                        getX(), getY() + 0.3, getZ(),
                        2, 0.8, 0.2, 0.8, 0.02);
            }
        }
    }

    @Override public boolean isPickable()       { return false; }
    @Override public boolean isPushable()       { return false; }
    @Override public boolean isInvisible()      { return true; }
}
