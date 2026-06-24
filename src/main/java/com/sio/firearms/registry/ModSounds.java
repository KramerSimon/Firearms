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
}
