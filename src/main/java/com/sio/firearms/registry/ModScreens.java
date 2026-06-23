package com.sio.firearms.registry;

import com.sio.firearms.screen.GunsmithTableScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "firearms", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModScreens {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.GUNSMITH_TABLE_MENU.get(), GunsmithTableScreen::new);
    }
}