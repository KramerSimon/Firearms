package com.sio.firearms.entity;

import com.sio.firearms.registry.ModEffects;
import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

public class SeaMineEntity extends Entity implements ItemSupplier {

    // A sea mine is only ever visually "hidden" while submerged — this mirrors LandMineBlock's
    // HIDDEN property so the metal detector can treat both the same way, and fully suppresses
    // rendering (via isInvisible()) instead of relying on murky water to obscure it.
    private static final EntityDataAccessor<Boolean> DATA_HIDDEN =
            SynchedEntityData.defineId(SeaMineEntity.class, EntityDataSerializers.BOOLEAN);

    private UUID placerUUID = null;
    private int placerImmuneTicks = 60; // 3-second placer immunity

    public SeaMineEntity(EntityType<? extends SeaMineEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public SeaMineEntity(Level level, Player placer, double x, double y, double z) {
        this(ModEntities.SEA_MINE.get(), level);
        this.setPos(x, y, z);
        this.placerUUID = placer.getUUID();
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(ModItems.SEA_MINE.get());
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            setDeltaMovement(0, 0, 0); // stay stationary on water surface

            boolean underwater = isUnderWater();
            if (entityData.get(DATA_HIDDEN) != underwater) {
                entityData.set(DATA_HIDDEN, underwater);
            }

            if (placerImmuneTicks > 0) placerImmuneTicks--;

            AABB detection = getBoundingBox().inflate(2.0);
            for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, detection, e -> !e.equals(this))) {
                if (isPlacerImmune(entity)) continue;
                triggerExplosion();
                return;
            }
        }
    }

    public boolean isHidden() {
        return entityData.get(DATA_HIDDEN);
    }

    @Override
    public boolean isInvisible() {
        return isHidden();
    }

    private boolean isPlacerImmune(Entity entity) {
        if (placerUUID == null || placerImmuneTicks <= 0) return false;
        return entity instanceof Player player && player.getUUID().equals(placerUUID);
    }

    private void triggerExplosion() {
        double cx = getX(), cy = getY(), cz = getZ();
        discard();

        level().explode(null, cx, cy, cz, 5.0f, Level.ExplosionInteraction.NONE);

        AABB blastArea = new AABB(cx - 5, cy - 5, cz - 5, cx + 5, cy + 5, cz + 5);
        level().getEntitiesOfClass(Player.class, blastArea)
                .forEach(player -> player.addEffect(
                        new MobEffectInstance(ModEffects.BLEEDING, 100, 1)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_HIDDEN, true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("placer")) placerUUID = tag.getUUID("placer");
        placerImmuneTicks = tag.getInt("placerImmuneTicks");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        if (placerUUID != null) tag.putUUID("placer", placerUUID);
        tag.putInt("placerImmuneTicks", placerImmuneTicks);
    }
}
