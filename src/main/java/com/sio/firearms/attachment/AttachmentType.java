package com.sio.firearms.attachment;

public enum AttachmentType {
    RED_DOT("red_dot", false, 0.85f, 0.1f, 0.0f),
    HOLO_SIGHT("holo_sight", false, 0.80f, 0.2f, 0.0f),
    LASER("laser", false, 0.90f, 0.3f, 0.1f),
    FLASHLIGHT("flashlight", false, 1.0f, 0.4f, 0.2f),
    SCOPE_4X("scope_4x", true, 0.25f, 0.5f, 0.0f),
    SCOPE_8X("scope_8x", true, 0.12f, 0.6f, 0.0f);

    private final String name;
    private final boolean rifleOnly;
    private final float fovMultiplier;
    private final float predicateValue;
    private final float underbarrelValue;

    AttachmentType(String name, boolean rifleOnly, float fovMultiplier, float predicateValue, float underbarrelValue) {
        this.name = name;
        this.rifleOnly = rifleOnly;
        this.fovMultiplier = fovMultiplier;
        this.predicateValue = predicateValue;
        this.underbarrelValue = underbarrelValue;
    }

    public String getName() {
        return name;
    }

    public boolean isRifleOnly() {
        return rifleOnly;
    }

    public float getFovMultiplier() {
        return fovMultiplier;
    }

    public float getPredicateValue() {
        return predicateValue;
    }

    public float getUnderbarrelValue() {
        return underbarrelValue;
    }

    public static AttachmentType fromName(String name) {
        for (AttachmentType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }
}
