package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.effect.BleedingEffect;
import com.sio.firearms.effect.RadiationEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Firearms.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> BLEEDING =
            MOB_EFFECTS.register("bleeding", BleedingEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> RADIATION =
            MOB_EFFECTS.register("radiation", RadiationEffect::new);
}
