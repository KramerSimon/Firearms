package com.sio.firearms.entity;

import com.sio.firearms.config.FirearmsConfig;
import com.sio.firearms.item.WrenchItem;
import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TankEntity extends Entity {

    public static final int MAX_AMMO = 20;

    private static final EntityDataAccessor<Float>   DATA_TURRET_YAW   = SynchedEntityData.defineId(TankEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float>   DATA_TURRET_PITCH = SynchedEntityData.defineId(TankEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_FUEL         = SynchedEntityData.defineId(TankEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float>   DATA_HEALTH       = SynchedEntityData.defineId(TankEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_AMMO         = SynchedEntityData.defineId(TankEntity.class, EntityDataSerializers.INT);

    // Server-side input state, set by TankInputPayload
    private boolean inputForward, inputBack, inputLeft, inputRight, firePressed;
    private int fireCooldown = 0;
    private int fuelTick     = 0;

    public TankEntity(EntityType<? extends TankEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_TURRET_YAW,   0.0f);
        builder.define(DATA_TURRET_PITCH, 0.0f);
        builder.define(DATA_FUEL,         0);
        builder.define(DATA_HEALTH,       500.0f);
        builder.define(DATA_AMMO,         MAX_AMMO);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) serverTick();
    }

    private void serverTick() {
        if (fireCooldown > 0) fireCooldown--;

        boolean moving = false;

        if (this.getFirstPassenger() != null) {
            int fuel = getFuel();

            // Rotate body
            if (inputLeft)  setYRot(getYRot() - (float) Math.toDegrees(0.03));
            if (inputRight) setYRot(getYRot() + (float) Math.toDegrees(0.03));

            // Forward / reverse — only if there's fuel
            if ((inputForward || inputBack) && fuel > 0) {
                double cfgSpeed = FirearmsConfig.TANK_SPEED.get();
                double speed    = inputForward ? cfgSpeed : -(cfgSpeed * 0.533);
                double yawRad   = Math.toRadians(getYRot());
                setDeltaMovement(
                    -Math.sin(yawRad) * speed,
                    getDeltaMovement().y,
                     Math.cos(yawRad) * speed
                );
                moving = true;
            }

            // Fire cannon
            if (firePressed && fireCooldown == 0 && fuel > 0 && getAmmo() > 0) {
                fireCannon(this.getFirstPassenger());
                setAmmo(getAmmo() - 1);
                fireCooldown = 100;
            }

            // Consume 1 mB every 20 ticks of movement
            if (moving) {
                if (++fuelTick >= 20) {
                    fuelTick = 0;
                    setFuel(Math.max(0, fuel - 1));
                }
            } else {
                fuelTick = 0;
            }
        }

        // Gravity
        if (!onGround()) {
            setDeltaMovement(getDeltaMovement().add(0, -0.08, 0));
        } else if (!moving) {
            // Ground friction
            Vec3 dm = getDeltaMovement();
            setDeltaMovement(dm.x * 0.7, dm.y, dm.z * 0.7);
        }

        move(MoverType.SELF, getDeltaMovement());

        // Kill tiny residual movement to avoid drift
        Vec3 dm = getDeltaMovement();
        if (Math.abs(dm.x) < 0.003 && Math.abs(dm.z) < 0.003) {
            setDeltaMovement(0, dm.y, 0);
        }
    }

    private void fireCannon(Entity shooter) {
        double bodyYawRad   = Math.toRadians(getYRot() + getTurretYaw());
        double pitchRad     = Math.toRadians(getTurretPitch());
        double dx = -Math.sin(bodyYawRad) * Math.cos(pitchRad);
        double dy = -Math.sin(pitchRad);
        double dz =  Math.cos(bodyYawRad) * Math.cos(pitchRad);

        TankCannonShellEntity shell = new TankCannonShellEntity(level(), shooter, dx, dy, dz);
        // Place shell at turret muzzle (3 m above entity origin, 4 m ahead)
        shell.setPos(getX() + dx * 4, getY() + 3.5, getZ() + dz * 4);
        level().addFreshEntity(shell);
    }

    // ── Rideable ─────────────────────────────────────────────────────────────

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        // Seat the passenger directly above the tank's origin, in the hatch/turret area
        callback.accept(passenger, getX(), getY() + 2.0, getZ());
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity entity) {
        // Turret/hatch area, used by vanilla dismount and passenger-offset logic
        return position().add(0, 2.5, 0);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        // getEyeHeight() is final in Entity; eye height is derived from dimensions instead
        return super.getDimensions(pose).withEyeHeight(2.5f);
    }

    /** Interpolated body yaw, used by the client to lock the first-person camera to the hull. */
    public float getBodyYawInterpolated(float partialTick) {
        return Mth.rotLerp(partialTick, yRotO, getYRot());
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!level().isClientSide()) {
            net.minecraft.world.item.ItemStack held = player.getItemInHand(hand);
            // Wrench + no passengers → dismantle and return schematic
            if (held.getItem() instanceof WrenchItem && this.getFirstPassenger() == null) {
                level().playSound(null, getX(), getY(), getZ(),
                        SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0f, 0.8f);
                player.addItem(new net.minecraft.world.item.ItemStack(ModItems.TANK_SCHEMATIC.get()));
                discard();
                return InteractionResult.SUCCESS;
            }
            // Diesel bucket → refuel 1000 mB, return empty bucket
            if (held.getItem() == ModItems.DIESEL_BUCKET.get()) {
                int current = getFuel();
                if (current < 10000) {
                    setFuel(Math.min(10000, current + 1000));
                    if (!player.isCreative()) {
                        held.shrink(1);
                        player.addItem(new net.minecraft.world.item.ItemStack(Items.BUCKET));
                    }
                    level().playSound(null, getX(), getY(), getZ(),
                            SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0f, 1.0f);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.FAIL;
            }
            return player.startRiding(this) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }

    // ── Input (set by server via TankInputPayload) ────────────────────────────

    public void setInputState(boolean forward, boolean back, boolean left, boolean right, boolean fire) {
        this.inputForward = forward;
        this.inputBack    = back;
        this.inputLeft    = left;
        this.inputRight   = right;
        this.firePressed  = fire;
    }

    // ── Synced data accessors ─────────────────────────────────────────────────

    public float getTurretYaw()   { return entityData.get(DATA_TURRET_YAW); }
    public float getTurretPitch() { return entityData.get(DATA_TURRET_PITCH); }
    public int   getFuel()        { return entityData.get(DATA_FUEL); }
    public float getHealth()      { return entityData.get(DATA_HEALTH); }
    public int   getAmmo()        { return entityData.get(DATA_AMMO); }

    public void setTurretYaw(float v)   { entityData.set(DATA_TURRET_YAW, v); }
    public void setTurretPitch(float v) { entityData.set(DATA_TURRET_PITCH, v); }
    public void setFuel(int v)          { entityData.set(DATA_FUEL, v); }
    public void setHealth(float v)      { entityData.set(DATA_HEALTH, v); }
    public void setAmmo(int v)          { entityData.set(DATA_AMMO, v); }

    // ── Damage ────────────────────────────────────────────────────────────────

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (level().isClientSide() || isInvulnerableTo(source)) return false;
        float hp = getHealth() - amount;
        if (hp <= 0) {
            level().explode(this, getX(), getY(), getZ(), 3.0f, Level.ExplosionInteraction.MOB);
            discard();
        } else {
            setHealth(hp);
        }
        return true;
    }

    @Override public boolean isPickable() { return true; }
    @Override public boolean isPushable() { return false; }

    // Initial facing set when spawned from garage
    public void setInitialFacing(Direction dir) {
        setYRot(dir.toYRot());
        yRotO = getYRot();
    }

    // ── NBT ──────────────────────────────────────────────────────────────────

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setFuel(tag.getInt("Fuel"));
        setHealth(tag.getFloat("Health"));
        setTurretYaw(tag.getFloat("TurretYaw"));
        setAmmo(tag.contains("Ammo") ? tag.getInt("Ammo") : MAX_AMMO);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Fuel",       getFuel());
        tag.putFloat("Health",   getHealth());
        tag.putFloat("TurretYaw", getTurretYaw());
        tag.putInt("Ammo",       getAmmo());
    }
}
