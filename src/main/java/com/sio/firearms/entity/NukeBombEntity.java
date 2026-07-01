package com.sio.firearms.entity;

import com.sio.firearms.item.WrenchItem;
import com.sio.firearms.registry.ModEffects;
import com.sio.firearms.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NukeBombEntity extends Entity {

    public static final int FUSE_TIME = 200; // 10 seconds
    private static final float EXPLOSION_RADIUS = 50.0f;
    private static final float DESTROY_RADIUS = 30.0f;
    private static final float DAMAGE_RADIUS = 50.0f;
    private static final float RADIATION_RADIUS = 100.0f;
    private static final float MAX_DAMAGE = 500.0f;
    private static final float CRATER_RADIUS = 10.0f;

    private static final EntityDataAccessor<Boolean> DATA_ARMED =
            SynchedEntityData.defineId(NukeBombEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_COUNTDOWN =
            SynchedEntityData.defineId(NukeBombEntity.class, EntityDataSerializers.INT);

    private int tickSoundCooldown = 0;

    public NukeBombEntity(EntityType<? extends NukeBombEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public NukeBombEntity(Level level, double x, double y, double z) {
        this(ModEntities.NUKE_BOMB.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ARMED, false);
        builder.define(DATA_COUNTDOWN, FUSE_TIME);
    }

    public boolean isArmed() {
        return entityData.get(DATA_ARMED);
    }

    public int getCountdown() {
        return entityData.get(DATA_COUNTDOWN);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!(held.getItem() instanceof WrenchItem)) {
            return InteractionResult.PASS;
        }

        if (!level().isClientSide()) {
            boolean nowArmed = !isArmed();
            entityData.set(DATA_ARMED, nowArmed);
            entityData.set(DATA_COUNTDOWN, FUSE_TIME);
            tickSoundCooldown = 0;

            player.displayClientMessage(Component.literal(nowArmed
                    ? "☢ Nuclear device ARMED — 10 second countdown initiated"
                    : "Nuclear device DISARMED"), true);

            level().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 1.0f, nowArmed ? 0.7f : 1.3f);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(0, 0, 0);

        if (level().isClientSide() || !isArmed()) return;

        if (tickSoundCooldown > 0) {
            tickSoundCooldown--;
        } else {
            level().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.BLOCKS, 2.0f, 1.0f);
            tickSoundCooldown = 20;
        }

        int countdown = getCountdown();
        if (countdown <= 0) {
            detonate();
            return;
        }
        entityData.set(DATA_COUNTDOWN, countdown - 1);
    }

    private void detonate() {
        if (!(level() instanceof ServerLevel serverLevel)) return;

        double cx = getX(), cy = getY(), cz = getZ();
        BlockPos center = blockPosition();
        discard();

        serverLevel.playSound(null, cx, cy, cz,
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 20.0f, 0.6f);
        serverLevel.playSound(null, cx, cy, cz,
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 20.0f, 0.5f);

        // Vanilla explosion is used only for its light-touch visual/knockback flash;
        // all real damage and terrain effects below are fully custom so the radius-50
        // block sweep never has to run through vanilla's explosion resistance checks.
        serverLevel.explode(null, cx, cy, cz, 8.0f, Level.ExplosionInteraction.NONE);

        damageEntities(serverLevel, cx, cy, cz);
        reshapeTerrain(serverLevel, center);
        irradiateArea(serverLevel, cx, cy, cz);
        spawnMushroomCloud(serverLevel, cx, cy, cz);
        announceDetonation(serverLevel, center);
    }

    private void damageEntities(ServerLevel level, double cx, double cy, double cz) {
        Vec3 origin = new Vec3(cx, cy, cz);
        AABB area = new AABB(cx - DAMAGE_RADIUS, cy - DAMAGE_RADIUS, cz - DAMAGE_RADIUS,
                cx + DAMAGE_RADIUS, cy + DAMAGE_RADIUS, cz + DAMAGE_RADIUS);
        for (Entity entity : level.getEntities(this, area)) {
            double dist = entity.position().distanceTo(origin);
            if (dist > DAMAGE_RADIUS) continue;
            float falloff = (float) (1.0 - (dist / DAMAGE_RADIUS));
            float damage = MAX_DAMAGE * falloff;
            if (damage > 0) {
                entity.hurt(level.damageSources().explosion(this, null), damage);
            }
        }
    }

    private void reshapeTerrain(ServerLevel level, BlockPos center) {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight() - 1;
        RandomSource random = level.getRandom();

        int r = (int) EXPLOSION_RADIUS;
        double destroySq = DESTROY_RADIUS * DESTROY_RADIUS;
        double blastSq = EXPLOSION_RADIUS * EXPLOSION_RADIUS;

        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                for (int y = -r; y <= r; y++) {
                    int worldY = center.getY() + y;
                    if (worldY < minY || worldY > maxY) continue;

                    double distSq = (double) x * x + (double) y * y + (double) z * z;
                    if (distSq > blastSq) continue;

                    BlockPos pos = center.offset(x, y, z);
                    if (level.getBlockState(pos).is(Blocks.BEDROCK)) continue;

                    if (distSq <= destroySq) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    } else if (!level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, random.nextBoolean()
                                ? Blocks.OBSIDIAN.defaultBlockState()
                                : Blocks.GRAVEL.defaultBlockState(), 3);
                    }
                }
            }
        }

        // Central crater filled with lava — a bowl a few blocks below ground zero.
        BlockPos craterCenter = center.below(4);
        int cr = (int) CRATER_RADIUS;
        double craterSq = CRATER_RADIUS * CRATER_RADIUS;
        for (int x = -cr; x <= cr; x++) {
            for (int y = -cr; y <= cr; y++) {
                int worldY = craterCenter.getY() + y;
                if (worldY < minY || worldY > maxY) continue;
                for (int z = -cr; z <= cr; z++) {
                    double distSq = (double) x * x + (double) y * y + (double) z * z;
                    if (distSq > craterSq) continue;
                    BlockPos pos = craterCenter.offset(x, y, z);
                    if (level.getBlockState(pos).is(Blocks.BEDROCK)) continue;
                    level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
                }
            }
        }
    }

    private void irradiateArea(ServerLevel level, double cx, double cy, double cz) {
        AABB area = new AABB(cx - RADIATION_RADIUS, cy - RADIATION_RADIUS, cz - RADIATION_RADIUS,
                cx + RADIATION_RADIUS, cy + RADIATION_RADIUS, cz + RADIATION_RADIUS);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, area)) {
            player.addEffect(new MobEffectInstance(ModEffects.RADIATION, 300 * 20, 3, false, true));
        }
    }

    private void spawnMushroomCloud(ServerLevel level, double cx, double cy, double cz) {
        for (int i = 0; i < 40; i++) {
            double height = i * 1.5;
            double spread = 2.0 + height * 0.15;
            level.sendParticles(ParticleTypes.LARGE_SMOKE, cx, cy + height, cz, 6,
                    spread * 0.3, 0.4, spread * 0.3, 0.02);
            level.sendParticles(ParticleTypes.EXPLOSION, cx, cy + height, cz, 1,
                    spread * 0.2, 0.2, spread * 0.2, 0.0);
        }
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, cx, cy + 55, cz, 12, 8.0, 3.0, 8.0, 0.0);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, cx, cy + 55, cz, 200, 10.0, 4.0, 10.0, 0.05);
    }

    private void announceDetonation(ServerLevel level, BlockPos pos) {
        MinecraftServer server = level.getServer();
        if (server == null) return;
        Component message = Component.literal(String.format(
                "☢ NUCLEAR DETONATION at %d, %d, %d ☢", pos.getX(), pos.getY(), pos.getZ()));
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.displayClientMessage(message, false);
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.BLOCK;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(DATA_ARMED, tag.getBoolean("Armed"));
        entityData.set(DATA_COUNTDOWN, tag.contains("Countdown") ? tag.getInt("Countdown") : FUSE_TIME);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("Armed", isArmed());
        tag.putInt("Countdown", getCountdown());
    }
}
