package com.sio.firearms.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class FirearmsConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue  GUN_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue BLEEDING_ENABLED;
    public static final ModConfigSpec.DoubleValue  BLEEDING_DAMAGE;
    public static final ModConfigSpec.DoubleValue  EXPLOSION_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue  TANK_SPEED;
    public static final ModConfigSpec.BooleanValue REACTOR_MELTDOWN_ENABLED;
    public static final ModConfigSpec.IntValue     OIL_GENERATION_FREQUENCY;
    public static final ModConfigSpec.IntValue     MILITARY_BUNKER_FREQUENCY;
    public static final ModConfigSpec.BooleanValue RADIATION_ENABLED;
    public static final ModConfigSpec.DoubleValue  AUTO_TURRET_RANGE;

    static {
        BUILDER.push("weapons");
        GUN_DAMAGE_MULTIPLIER = BUILDER
                .comment("Multiplies all gun damage output (1.0 = default, 2.0 = double damage)")
                .defineInRange("gunDamageMultiplier", 1.0, 0.1, 5.0);
        BLEEDING_ENABLED = BUILDER
                .comment("Enables the bleeding effect applied by bullets")
                .define("bleedingEnabled", true);
        BLEEDING_DAMAGE = BUILDER
                .comment("Damage dealt per bleeding tick (every 20 ticks, stacks with amplifier)")
                .defineInRange("bleedingDamage", 0.5, 0.0, 10.0);
        EXPLOSION_DAMAGE_MULTIPLIER = BUILDER
                .comment("Multiplies explosion power for grenades, shells, and nitroglycerin")
                .defineInRange("explosionDamageMultiplier", 1.0, 0.1, 5.0);
        BUILDER.pop();

        BUILDER.push("vehicles");
        TANK_SPEED = BUILDER
                .comment("Tank movement speed in blocks per tick")
                .defineInRange("tankSpeed", 0.15, 0.01, 2.0);
        BUILDER.pop();

        BUILDER.push("world");
        OIL_GENERATION_FREQUENCY = BUILDER
                .comment("Oil vein generation attempts per chunk chunk (higher = more oil veins)")
                .defineInRange("oilGenerationFrequency", 8, 1, 64);
        MILITARY_BUNKER_FREQUENCY = BUILDER
                .comment("Average chunk spacing between military bunker spawns")
                .defineInRange("militaryBunkerFrequency", 64, 16, 256);
        BUILDER.pop();

        BUILDER.push("nuclear");
        REACTOR_MELTDOWN_ENABLED = BUILDER
                .comment("Enables the reactor meltdown mechanic when overheated")
                .define("reactorMeltdownEnabled", true);
        RADIATION_ENABLED = BUILDER
                .comment("Enables the radiation damage and contamination system")
                .define("radiationEnabled", true);
        BUILDER.pop();

        BUILDER.push("machines");
        AUTO_TURRET_RANGE = BUILDER
                .comment("Detection range of auto turrets in blocks")
                .defineInRange("autoTurretRange", 16.0, 1.0, 64.0);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
