package com.sio.firearms.item;

import net.minecraft.ChatFormatting;

public enum WeaponQuality {
    STANDARD("Standard", ChatFormatting.WHITE, 1.0f, 1.0f, 1.0f),
    REFINED("Refined", ChatFormatting.BLUE, 1.15f, 1.10f, 1.0f),
    MILITARY_GRADE("Military Grade", ChatFormatting.GOLD, 1.30f, 1.25f, 0.8f);

    private final String displayName;
    private final ChatFormatting color;
    private final float damageMultiplier;
    private final float durabilityMultiplier;
    private final float fireRateMultiplier;

    WeaponQuality(String displayName, ChatFormatting color,
                  float damageMultiplier, float durabilityMultiplier, float fireRateMultiplier) {
        this.displayName = displayName;
        this.color = color;
        this.damageMultiplier = damageMultiplier;
        this.durabilityMultiplier = durabilityMultiplier;
        this.fireRateMultiplier = fireRateMultiplier;
    }

    public String getDisplayName() { return displayName; }
    public ChatFormatting getColor() { return color; }
    public float getDamageMultiplier() { return damageMultiplier; }
    public float getDurabilityMultiplier() { return durabilityMultiplier; }
    public float getFireRateMultiplier() { return fireRateMultiplier; }

    public static WeaponQuality fromString(String name) {
        for (WeaponQuality q : values()) {
            if (q.name().equals(name)) return q;
        }
        return STANDARD;
    }
}
