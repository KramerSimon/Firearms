package com.sio.firearms.entity;

import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class NapalmBombEntity extends ThrowableItemProjectile {

    public NapalmBombEntity(EntityType<? extends NapalmBombEntity> type, Level level) {
        super(type, level);
    }

    public NapalmBombEntity(Level level, LivingEntity thrower) {
        super(ModEntities.NAPALM_BOMB.get(), thrower, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.NAPALM_BOMB.get();
    }

    @Override
    protected void onHit(HitResult result) {
        if (!level().isClientSide()) {
            Vec3 pos = position();
            BlockPos center = BlockPos.containing(pos);
            int radius = 4;

            // Ignite surface blocks in a circular footprint of radius 4
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius) continue;
                    BlockPos fp = center.offset(dx, 0, dz);
                    if (level().getBlockState(fp).isAir()
                            && !level().getBlockState(fp.below()).isAir()
                            && BaseFireBlock.canBePlacedAt(level(), fp, Direction.UP)) {
                        level().setBlock(fp, BaseFireBlock.getState(level(), fp), 11);
                    }
                }
            }

            // Spawn NapalmFirePatch entities on a 2-block grid within the radius (15-second burn)
            for (int dx = -radius; dx <= radius; dx += 2) {
                for (int dz = -radius; dz <= radius; dz += 2) {
                    if (dx * dx + dz * dz > radius * radius) continue;
                    NapalmFirePatchEntity patch = new NapalmFirePatchEntity(
                            level(), pos.x + dx, pos.y + 0.1, pos.z + dz);
                    level().addFreshEntity(patch);
                }
            }

            // Orange/black smoke particle burst
            if (level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.LARGE_SMOKE,
                        pos.x, pos.y + 1.5, pos.z, 60, 3.0, 1.5, 3.0, 0.08);
                sl.sendParticles(ParticleTypes.FLAME,
                        pos.x, pos.y, pos.z, 40, 2.5, 0.5, 2.5, 0.15);
            }

            // Explosion + fire sounds
            level().playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1.2f, 0.8f);
            level().playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.5f, 0.6f);

            discard();
        }
    }
}
