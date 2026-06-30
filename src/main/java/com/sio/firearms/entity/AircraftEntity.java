package com.sio.firearms.entity;

import com.sio.firearms.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AircraftEntity extends Entity {

    // ── Constants ─────────────────────────────────────────────────────────────
    public static final int   MAX_FUEL        = 15_000;  // mB
    public static final float MAX_HEALTH      = 300.0f;
    public static final float MAX_SPEED       = 0.4f;    // blocks/tick
    public static final float TAKEOFF_SPEED   = 0.2f;    // min speed for lift
    public static final float ACCELERATION    = 0.005f;
    public static final float DECELERATION    = 0.003f;
    public static final float PITCH_RATE      = 1.5f;    // degrees/tick
    public static final float YAW_RATE        = 1.2f;    // degrees/tick
    public static final float GRAVITY         = 0.04f;
    public static final int   FIRE_COOLDOWN   = 10;      // ticks between shots

    // ── Synced data ───────────────────────────────────────────────────────────
    private static final EntityDataAccessor<Integer> DATA_FUEL =
            SynchedEntityData.defineId(AircraftEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_HEALTH =
            SynchedEntityData.defineId(AircraftEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PITCH =
            SynchedEntityData.defineId(AircraftEntity.class, EntityDataSerializers.FLOAT);

    // ── Server-side input state (set by AircraftInputPayload) ─────────────────
    private boolean inputForward, inputBack, inputLeft, inputRight;
    private boolean inputUp, inputDown, inputFire;

    private float   currentSpeed  = 0f;
    private int     fireCooldown  = 0;
    private int     fuelTick      = 0;

    public AircraftEntity(EntityType<? extends AircraftEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FUEL,   0);
        builder.define(DATA_HEALTH, MAX_HEALTH);
        builder.define(DATA_PITCH,  0.0f);
    }

    // ── Input setter called by AircraftInputPayload ──────────────────────────

    public void setInputState(boolean forward, boolean back, boolean left, boolean right,
                              boolean up, boolean down, boolean fire) {
        this.inputForward = forward;
        this.inputBack    = back;
        this.inputLeft    = left;
        this.inputRight   = right;
        this.inputUp      = up;
        this.inputDown    = down;
        this.inputFire    = fire;
    }

    // ── Tick ──────────────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) serverTick();
    }

    private void serverTick() {
        if (fireCooldown > 0) fireCooldown--;

        boolean hasPassenger = getFirstPassenger() != null;
        int fuel = getFuel();

        if (hasPassenger && fuel > 0) {
            // Yaw (A/D)
            if (inputLeft)  setYRot(getYRot() - YAW_RATE);
            if (inputRight) setYRot(getYRot() + YAW_RATE);

            // Pitch (Space=pitch up / Shift=pitch down)
            float pitch = getAircraftPitch();
            if (inputUp)   pitch = Math.max(pitch - PITCH_RATE, -45f);
            if (inputDown) pitch = Math.min(pitch + PITCH_RATE,  30f);
            setAircraftPitch(pitch);

            // Throttle (W/S)
            if (inputForward) currentSpeed = Math.min(currentSpeed + ACCELERATION, MAX_SPEED);
            else if (inputBack) currentSpeed = Math.max(currentSpeed - DECELERATION * 2, 0f);
            else currentSpeed = Math.max(currentSpeed - DECELERATION, 0f);

            // Fuel consumption: 2 mB/tick when engine running
            if (currentSpeed > 0.01f) {
                if (++fuelTick >= 1) {
                    fuelTick = 0;
                    setFuel(Math.max(0, fuel - 2));
                }
            }

            // Fire weapon
            if (inputFire && fireCooldown == 0 && fuel > 0) {
                fireWeapon();
                fireCooldown = FIRE_COOLDOWN;
            }
        } else if (!hasPassenger) {
            // No passenger — decelerate
            currentSpeed = Math.max(currentSpeed - DECELERATION, 0f);
        }

        // ── Movement physics ─────────────────────────────────────────────────
        if (currentSpeed > 0.01f) {
            double yawRad   = Math.toRadians(getYRot());
            double pitchRad = Math.toRadians(getAircraftPitch());

            double vx = -Math.sin(yawRad) * Math.cos(pitchRad) * currentSpeed;
            double vy =  Math.sin(-pitchRad) * currentSpeed;
            double vz =  Math.cos(yawRad)  * Math.cos(pitchRad) * currentSpeed;

            // If flying fast enough, negate gravity
            if (currentSpeed >= TAKEOFF_SPEED) {
                vy = Math.max(vy, 0.02);  // minimum lift
            }

            setDeltaMovement(vx, vy, vz);
        } else {
            // Gravity when slow / stopped
            Vec3 dm = getDeltaMovement();
            if (!onGround()) {
                setDeltaMovement(dm.x * 0.9, dm.y - GRAVITY, dm.z * 0.9);
            } else {
                setDeltaMovement(dm.x * 0.8, 0, dm.z * 0.8);
            }
        }

        move(MoverType.SELF, getDeltaMovement());
    }

    private void fireWeapon() {
        if (level() == null) return;
        Player pilot = level().getPlayerByUUID(
                getFirstPassenger() instanceof Player p ? p.getUUID() : java.util.UUID.randomUUID());
        if (pilot == null && getFirstPassenger() instanceof Player p) pilot = p;
        if (pilot == null) return;

        // Forward direction
        double yawRad   = Math.toRadians(getYRot());
        double pitchRad = Math.toRadians(getAircraftPitch());
        double vx = -Math.sin(yawRad) * Math.cos(pitchRad);
        double vy =  Math.sin(-pitchRad);
        double vz =  Math.cos(yawRad)  * Math.cos(pitchRad);

        float bulletSpeed = 3.0f;
        BulletEntity bullet = new BulletEntity(level(), pilot, 25.0f);
        bullet.setPos(getX() + vx, getY() + 1.0, getZ() + vz);
        bullet.setDeltaMovement(vx * bulletSpeed, vy * bulletSpeed, vz * bulletSpeed);
        level().addFreshEntity(bullet);
    }

    // ── Passenger riding ──────────────────────────────────────────────────────

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!level().isClientSide() && !isVehicle()) {
            player.startRiding(this);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        double yawRad = Math.toRadians(getYRot());
        double ox = -Math.sin(yawRad) * 0.5;
        double oz =  Math.cos(yawRad) * 0.5;
        callback.accept(passenger,
                getX() + ox,
                getY() + 1.5,
                getZ() + oz);
    }

    public double getPassengersRidingOffset() { return 1.5; }

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected boolean canAddPassenger(Entity passenger) { return getPassengers().isEmpty(); }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public int   getFuel()            { return entityData.get(DATA_FUEL);    }
    public void  setFuel(int fuel)    { entityData.set(DATA_FUEL, Math.max(0, Math.min(MAX_FUEL, fuel))); }
    public float getHealth()          { return entityData.get(DATA_HEALTH);  }
    public void  setHealth(float h)   { entityData.set(DATA_HEALTH, Math.max(0, h)); }
    public float getAircraftPitch()   { return entityData.get(DATA_PITCH);   }
    public void  setAircraftPitch(float p) { entityData.set(DATA_PITCH, p); }
    public float getCurrentSpeed()    { return currentSpeed; }

    public boolean hurtAircraft(float dmg) {
        float h = getHealth() - dmg;
        setHealth(h);
        return h <= 0;
    }

    // ── NBT ──────────────────────────────────────────────────────────────────

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setFuel(tag.getInt("Fuel"));
        setHealth(tag.getFloat("AircraftHealth"));
        currentSpeed = tag.getFloat("Speed");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Fuel", getFuel());
        tag.putFloat("AircraftHealth", getHealth());
        tag.putFloat("Speed", currentSpeed);
    }
}
