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

public class NapalmFirePatchEntity extends Entity {

    private int life = 0;
    private static final int MAX_LIFE = 300; // 15 seconds at 20 tps

    public NapalmFirePatchEntity(EntityType<?> type, Level level) {
        super(type, level);
        setNoGravity(true);
        noPhysics = true;
    }

    public NapalmFirePatchEntity(Level level, double x, double y, double z) {
        this(ModEntities.NAPALM_FIRE_PATCH.get(), level);
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

            // 3 HP fire damage every 10 ticks
            if (life % 10 == 0) {
                AABB box = new AABB(getX() - 1.5, getY() - 0.5, getZ() - 1.5,
                                    getX() + 1.5, getY() + 1.5, getZ() + 1.5);
                for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, box)) {
                    e.hurt(damageSources().onFire(), 3.0f);
                    e.igniteForSeconds(3);
                }
            }

            // Flame and thick smoke particles from server every 4 ticks
            if (life % 4 == 0 && level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.FLAME,
                        getX(), getY() + 0.1, getZ(),
                        5, 1.2, 0.1, 1.2, 0.06);
                sl.sendParticles(ParticleTypes.LARGE_SMOKE,
                        getX(), getY() + 0.5, getZ(),
                        3, 0.8, 0.2, 0.8, 0.03);
            }
        }
    }

    @Override public boolean isPickable()  { return false; }
    @Override public boolean isPushable()  { return false; }
    @Override public boolean isInvisible() { return true; }
}
