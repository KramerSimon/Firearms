package com.sio.firearms;

import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModCreativeTabs;
import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModFeatures;
import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModMenuTypes;
import com.sio.firearms.registry.ModSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Firearms.MOD_ID)
public class Firearms {

    public static final String MOD_ID = "firearms";

    public Firearms(IEventBus modEventBus) {
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
    }
}
