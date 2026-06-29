package com.sio.firearms.entity;

import com.sio.firearms.item.BulletproofVestItem;
import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModEffects;
import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModItems;
import com.sio.firearms.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BulletEntity extends Projectile implements ItemSupplier {

    private float damage = 8.0F;
    private int life = 0;
    private int piercingCount = 0;
    private boolean armorPiercing = false;
    private boolean partialArmorPiercing = false;
    private boolean explosive = false;
    private final Set<UUID> playersFlybyPlayed = new HashSet<>();
    private final Set<UUID> piercedEntities = new HashSet<>();
    private ItemStack shooterGun = ItemStack.EMPTY;

    public BulletEntity(EntityType<? extends BulletEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public BulletEntity(Level level, Entity owner, float damage) {
        super(ModEntities.BULLET.get(), level);
        this.setOwner(owner);
        this.setNoGravity(true);
        this.damage = damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setArmorPiercing(boolean ap) {
        this.armorPiercing = ap;
    }

    public void setPartialArmorPiercing(boolean partial) {
        this.partialArmorPiercing = partial;
    }

    public void setExplosive(boolean explosive) {
        this.explosive = explosive;
    }

    public void setShooterGun(ItemStack gun) {
        this.shooterGun = gun;
    }

    public void setPiercingCount(int count) {
        this.piercingCount = count;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !piercedEntities.contains(entity.getUUID());
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(ModItems.BULLET.get());
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();

        if (++life > 100) {
            discard();
            return;
        }

        Vec3 movement = getDeltaMovement();
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS) {
            onHit(hitResult);
        }

        setPos(getX() + movement.x, getY() + movement.y, getZ() + movement.z);

        if (!level().isClientSide()) {
            AABB flybyBox = getBoundingBox().inflate(3.0);
            List<Player> nearbyPlayers = level().getEntitiesOfClass(Player.class, flybyBox);
            Entity owner = getOwner();
            for (Player player : nearbyPlayers) {
                if (owner != null && player.getUUID().equals(owner.getUUID())) continue;
                if (playersFlybyPlayed.add(player.getUUID())) {
                    float pitch = 0.8f + level().getRandom().nextFloat() * 0.4f;
                    level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.BULLET_FLYBY.get(), SoundSource.PLAYERS, 1.0f, pitch);
                }
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide()) {
            Entity target = result.getEntity();
            Entity owner = getOwner();
            if (armorPiercing) {
                target.hurt(damageSources().magic(), damage);
            } else if (partialArmorPiercing) {
                // 30% bypasses armor as magic, 70% is reduced by armor
                target.hurt(damageSources().magic(), damage * 0.3f);
                target.hurt(damageSources().thrown(this, owner), damage * 0.7f);
            } else {
                target.hurt(damageSources().thrown(this, owner), damage);
            }
            if (explosive) {
                level().explode(null, getX(), getY(), getZ(), 1.0f, false,
                        net.minecraft.world.level.Level.ExplosionInteraction.NONE);
            }

            if (target instanceof LivingEntity living) {
                if (living.isDeadOrDying() && !shooterGun.isEmpty()) {
                    Integer kills = shooterGun.get(ModDataComponents.KILL_COUNT.get());
                    shooterGun.set(ModDataComponents.KILL_COUNT.get(), (kills != null ? kills : 0) + 1);
                }

                boolean wearingVest = living.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof BulletproofVestItem;
                if (!wearingVest || armorPiercing) {
                    MobEffectInstance existing = living.getEffect(ModEffects.BLEEDING);
                    int amplifier = (existing != null) ? Math.min(existing.getAmplifier() + 1, 2) : 0;
                    living.addEffect(new MobEffectInstance(ModEffects.BLEEDING, 200, amplifier));
                }
            }

            if (piercingCount > 0) {
                piercedEntities.add(target.getUUID());
                piercingCount--;
            } else {
                discard();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide()) {
            discard();
        }
    }
}
