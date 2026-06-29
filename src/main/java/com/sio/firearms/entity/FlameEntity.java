package com.sio.firearms.entity;

import com.sio.firearms.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class FlameEntity extends Projectile {

    private int life = 0;
    private static final int MAX_LIFE = 20;

    public FlameEntity(EntityType<? extends FlameEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public FlameEntity(Level level, LivingEntity shooter) {
        super(ModEntities.FLAME.get(), level);
        setOwner(shooter);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        if (++life > MAX_LIFE) { discard(); return; }

        Vec3 dm = getDeltaMovement();

        // Client: spawn visual particles
        if (level().isClientSide()) {
            level().addParticle(ParticleTypes.FLAME,
                    getX(), getY(), getZ(),
                    dm.x * 0.2, dm.y * 0.2, dm.z * 0.2);
            if (level().random.nextInt(3) == 0) {
                level().addParticle(ParticleTypes.LARGE_SMOKE,
                        getX(), getY(), getZ(), 0, 0.02, 0);
            }
        }

        // Move (passing through blocks intentionally for flame effect)
        setPos(getX() + dm.x, getY() + dm.y, getZ() + dm.z);

        if (!level().isClientSide()) {
            // Damage nearby living entities
            Entity owner = getOwner();
            AABB box = getBoundingBox().inflate(0.5);
            for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, box)) {
                if (target == owner) continue;
                target.hurt(damageSources().onFire(), 3.0f);
                target.igniteForSeconds(3);
            }

            // Ignite the block at current position if it's air above a solid surface
            BlockPos pos = BlockPos.containing(getX(), getY(), getZ());
            if (level().getBlockState(pos).isAir() && level().isLoaded(pos)) {
                BlockPos below = pos.below();
                BlockState belowState = level().getBlockState(below);
                if (!belowState.isAir() && BaseFireBlock.canBePlacedAt(level(), pos, Direction.UP)) {
                    level().setBlock(pos, BaseFireBlock.getState(level(), pos), 11);
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        // Ignite block face on contact but don't stop the flame entity
        if (!level().isClientSide()) {
            BlockPos above = result.getBlockPos().relative(result.getDirection());
            if (level().getBlockState(above).isAir() && level().isLoaded(above)) {
                if (BaseFireBlock.canBePlacedAt(level(), above, result.getDirection())) {
                    level().setBlock(above, BaseFireBlock.getState(level(), above), 11);
                }
            }
        }
        // Do not call super — flame passes through blocks
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide()) {
            Entity target = result.getEntity();
            target.hurt(damageSources().onFire(), 3.0f);
            if (target instanceof LivingEntity le) le.igniteForSeconds(3);
        }
        // Do not call super — flame passes through entities
    }
}
