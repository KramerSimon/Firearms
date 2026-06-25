package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, Firearms.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> PISTOL_SHOOT =
            SOUND_EVENTS.register("pistol_shoot", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "pistol_shoot")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RIFLE_SHOOT =
            SOUND_EVENTS.register("rifle_shoot", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "rifle_shoot")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BULLET_FLYBY =
            SOUND_EVENTS.register("bullet_flyby", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "bullet_flyby")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BANDAGE =
            SOUND_EVENTS.register("bandage", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "bandage")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SNIPER_RIFLE_SHOOT =
            SOUND_EVENTS.register("sniper_rifle_shoot", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "sniper_rifle_shoot")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SHOTGUN_SHOOT =
            SOUND_EVENTS.register("shotgun_shoot", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "shotgun_shoot")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SMG_SHOOT =
            SOUND_EVENTS.register("smg_shoot", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "smg_shoot")));

    public static final DeferredHolder<SoundEvent, SoundEvent> GRENADE_PIN =
            SOUND_EVENTS.register("grenade_pin", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "grenade_pin")));
}
