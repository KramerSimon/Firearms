package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Firearms.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FIREARMS_TAB =
            CREATIVE_MODE_TABS.register("firearms", () -> CreativeModeTab.builder()
                    .title(Component.literal("Firearms"))
                    .icon(() -> ModItems.STEEL_INGOT.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.STEEL_INGOT.get());
                        output.accept(ModItems.GUN_BARREL.get());
                        output.accept(ModItems.GUN_GRIP.get());
                        output.accept(ModItems.TRIGGER_ASSEMBLY.get());
                        output.accept(ModItems.MAGAZINE.get());
                        output.accept(ModItems.BULLET.get());
                        output.accept(ModItems.GUNSMITH_TABLE.get());
                    })
                    .build());
}
