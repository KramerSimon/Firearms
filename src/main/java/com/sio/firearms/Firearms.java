package com.sio.firearms;

import com.sio.firearms.config.FirearmsConfig;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModCreativeTabs;
import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModFeatures;
import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import com.sio.firearms.registry.ModArmorMaterials;
import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModEffects;
import com.sio.firearms.registry.ModMenuTypes;
import com.sio.firearms.registry.ModSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Firearms.MOD_ID)
public class Firearms {

    public static final String MOD_ID = "firearms";

    public Firearms(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, FirearmsConfig.SPEC, "firearms-common.toml");
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
    }
}
