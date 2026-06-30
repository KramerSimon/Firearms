package com.sio.firearms.entity;

import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThermiteGrenadeEntity extends ThrowableItemProjectile {

    public ThermiteGrenadeEntity(EntityType<? extends ThermiteGrenadeEntity> type, Level level) {
        super(type, level);
    }

    public ThermiteGrenadeEntity(Level level, LivingEntity thrower) {
        super(ModEntities.THERMITE_GRENADE.get(), thrower, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.THERMITE_GRENADE.get();
    }

    @Override
    protected void onHit(HitResult result) {
        if (!level().isClientSide()) {
            Vec3 pos = position();
            BlockPos center = BlockPos.containing(pos);

            // Burn through 3x3x3 blocks; skip unbreakable (hardness < 0, e.g. bedrock, end portal frame)
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos bp = center.offset(dx, dy, dz);
                        BlockState bs = level().getBlockState(bp);
                        if (!bs.isAir() && bs.getDestroySpeed(level(), bp) >= 0) {
                            level().setBlock(bp, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }

            // Lingering fire patch for 5 seconds in crater (FirePatchEntity MAX_LIFE = 100 ticks)
            FirePatchEntity patch = new FirePatchEntity(level(), pos.x, pos.y + 0.1, pos.z);
            level().addFreshEntity(patch);

            // 40 damage (armor-piercing via magic damage type) + 5-second ignite
            AABB box = new AABB(pos.x - 2, pos.y - 2, pos.z - 2,
                                pos.x + 2, pos.y + 2, pos.z + 2);
            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, box)) {
                e.hurt(damageSources().magic(), 40.0f);
                e.igniteForSeconds(5);
            }

            // Bright white/orange particle effect
            if (level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.FLASH,
                        pos.x, pos.y, pos.z, 3, 0.5, 0.5, 0.5, 0.0);
                sl.sendParticles(ParticleTypes.FLAME,
                        pos.x, pos.y, pos.z, 50, 1.5, 1.0, 1.5, 0.25);
                sl.sendParticles(ParticleTypes.LARGE_SMOKE,
                        pos.x, pos.y + 1, pos.z, 20, 1.0, 0.5, 1.0, 0.05);
            }

            // Metal sizzle (fire extinguish hiss) + high-pitched explosion
            level().playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1.5f, 1.3f);
            level().playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 2.0f, 0.4f);

            discard();
        }
    }
}
