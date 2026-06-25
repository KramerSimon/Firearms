package com.sio.firearms.entity;

import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SmokeGrenadeEntity extends ThrowableItemProjectile {

    private static final int FUSE_TIME = 40;
    private static final int SMOKE_DURATION = 200;
    private static final double SMOKE_RADIUS = 5.0;

    private int fuseTicks = 0;
    private int smokeTicks = -1;

    public SmokeGrenadeEntity(EntityType<? extends SmokeGrenadeEntity> type, Level level) {
        super(type, level);
    }

    public SmokeGrenadeEntity(Level level, LivingEntity shooter) {
        super(ModEntities.SMOKE_GRENADE.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SMOKE_GRENADE.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            if (smokeTicks >= 0) {
                tickSmoke();
                smokeTicks++;
                if (smokeTicks >= SMOKE_DURATION) {
                    discard();
                }
            } else {
                fuseTicks++;
                if (fuseTicks >= FUSE_TIME) {
                    detonate();
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide() && smokeTicks < 0) {
            detonate();
        }
    }

    private void detonate() {
        smokeTicks = 0;
        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
    }

    private void tickSmoke() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        Vec3 pos = position();

        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                pos.x, pos.y + 0.5, pos.z, 12, 2.5, 1.0, 2.5, 0.01);

        // Re-apply blindness every second so players entering the cloud are affected
        if (smokeTicks % 20 == 0) {
            level().getEntitiesOfClass(Player.class,
                            new AABB(pos.x - SMOKE_RADIUS, pos.y - SMOKE_RADIUS, pos.z - SMOKE_RADIUS,
                                     pos.x + SMOKE_RADIUS, pos.y + SMOKE_RADIUS, pos.z + SMOKE_RADIUS))
                    .forEach(player -> player.addEffect(
                            new MobEffectInstance(MobEffects.BLINDNESS, 100, 0)));
        }
    }
}
