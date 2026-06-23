package com.sio.firearms;

import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModCreativeTabs;
import com.sio.firearms.registry.ModItems;
import com.sio.firearms.registry.ModMenuTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Firearms.MOD_ID)
public class Firearms {

    public static final String MOD_ID = "firearms";

    public Firearms(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
    }
}
