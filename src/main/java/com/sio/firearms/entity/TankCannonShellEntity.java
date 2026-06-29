package com.sio.firearms.entity;

import com.sio.firearms.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TankCannonShellEntity extends Projectile {

    private int life = 0;

    public TankCannonShellEntity(EntityType<? extends TankCannonShellEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public TankCannonShellEntity(Level level, Entity owner, double dx, double dy, double dz) {
        super(ModEntities.TANK_CANNON_SHELL.get(), level);
        setOwner(owner);
        setNoGravity(true);
        setDeltaMovement(dx * 3.0, dy * 3.0, dz * 3.0);
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {}

    @Override
    protected boolean canHitEntity(Entity entity) {
        // Don't hit the tank the shooter is riding
        if (getOwner() instanceof Player player && entity == player.getVehicle()) return false;
        return super.canHitEntity(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (++life > 200) { discard(); return; }

        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            onHit(hit);
            return;
        }

        Vec3 dm = getDeltaMovement();
        setPos(getX() + dm.x, getY() + dm.y, getZ() + dm.z);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide()) {
            Vec3 loc = result.getLocation();
            explodeAt(loc.x, loc.y, loc.z);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!level().isClientSide()) {
            Vec3 loc = result.getLocation();
            explodeAt(loc.x, loc.y, loc.z);
        }
    }

    private void explodeAt(double x, double y, double z) {
        level().playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0f, 0.9f + level().random.nextFloat() * 0.2f);

        // 50 damage to all entities within 6 blocks, falling off with distance
        AABB damageBox = new AABB(x - 6, y - 6, z - 6, x + 6, y + 6, z + 6);
        DamageSource source = damageSources().explosion(this, getOwner());
        for (Entity e : level().getEntitiesOfClass(Entity.class, damageBox)) {
            if (e == this) continue;
            double dist = e.position().distanceTo(new Vec3(x, y, z));
            if (dist < 6.0) {
                float falloff = (float)(1.0 - dist / 6.0);
                e.hurt(source, 50.0f * falloff);
            }
        }

        // Break blocks in radius 2
        BlockPos center = BlockPos.containing(x, y, z);
        for (BlockPos bp : BlockPos.betweenClosed(center.offset(-2, -2, -2), center.offset(2, 2, 2))) {
            if (center.distSqr(bp) > 4.0) continue;
            if (!level().getBlockState(bp).isAir()
                    && !level().getBlockState(bp).is(BlockTags.WITHER_IMMUNE)) {
                level().destroyBlock(bp, false);
            }
        }

        discard();
    }
}
