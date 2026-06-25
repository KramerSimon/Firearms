package com.sio.firearms.entity;

import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class GrenadeEntity extends ThrowableItemProjectile {

    private static final int FUSE_TIME = 60;
    private static final float EXPLOSION_RADIUS = 4.0f;

    private int fuseTicks = 0;

    public GrenadeEntity(EntityType<? extends GrenadeEntity> type, Level level) {
        super(type, level);
    }

    public GrenadeEntity(Level level, LivingEntity shooter) {
        super(ModEntities.GRENADE.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.GRENADE.get();
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            fuseTicks++;
            if (fuseTicks >= FUSE_TIME) {
                explode();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide()) {
            explode();
        }
    }

    private void explode() {
        if (!level().isClientSide()) {
            Entity owner = getOwner();
            level().explode(owner, getX(), getY(), getZ(), EXPLOSION_RADIUS,
                    Level.ExplosionInteraction.NONE);

            if (level() instanceof ServerLevel serverLevel) {
                Vec3 pos = position();
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                        pos.x, pos.y, pos.z, 20, 0.5, 0.5, 0.5, 0.05);
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        pos.x, pos.y, pos.z, 10, 0.3, 0.3, 0.3, 0.05);
            }

            level().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1.0f, 1.0f);

            discard();
        }
    }
}
