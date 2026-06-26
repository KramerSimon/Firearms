package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.world.MilitaryBunkerStructure;
import com.sio.firearms.world.OilVeinFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Firearms.MOD_ID);

    public static final DeferredHolder<Feature<?>, OilVeinFeature> OIL_VEIN =
            FEATURES.register("oil_vein", OilVeinFeature::new);

    public static final DeferredHolder<Feature<?>, MilitaryBunkerStructure> MILITARY_BUNKER =
            FEATURES.register("military_bunker", MilitaryBunkerStructure::new);
}
