package com.sio.firearms.registry;

import com.sio.firearms.screen.AssemblyBenchScreen;
import com.sio.firearms.screen.CoalGeneratorScreen;
import com.sio.firearms.screen.OilDerrickScreen;
import com.sio.firearms.screen.RefineryScreen;
import com.sio.firearms.screen.HeatTreatmentFurnaceScreen;
import com.sio.firearms.screen.LatheScreen;
import com.sio.firearms.screen.GunModificationTableScreen;
import com.sio.firearms.screen.GunsmithTableScreen;
import com.sio.firearms.screen.MetalPressScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "firearms", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModScreens {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.GUNSMITH_TABLE_MENU.get(), GunsmithTableScreen::new);
        event.register(ModMenuTypes.METAL_PRESS_MENU.get(), MetalPressScreen::new);
        event.register(ModMenuTypes.GUN_MODIFICATION_TABLE_MENU.get(), GunModificationTableScreen::new);
        event.register(ModMenuTypes.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
        event.register(ModMenuTypes.HEAT_TREATMENT_FURNACE_MENU.get(), HeatTreatmentFurnaceScreen::new);
        event.register(ModMenuTypes.LATHE_MENU.get(), LatheScreen::new);
        event.register(ModMenuTypes.ASSEMBLY_BENCH_MENU.get(), AssemblyBenchScreen::new);
        event.register(ModMenuTypes.OIL_DERRICK_MENU.get(), OilDerrickScreen::new);
        event.register(ModMenuTypes.REFINERY_MENU.get(), RefineryScreen::new);
    }
}