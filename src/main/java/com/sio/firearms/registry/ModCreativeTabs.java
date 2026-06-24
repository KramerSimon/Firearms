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
                        output.accept(ModItems.HARDENED_STEEL_INGOT.get());
                        output.accept(ModItems.PROPELLANT_POWDER.get());
                        output.accept(ModItems.STEEL_ROD.get());
                        output.accept(ModItems.GUN_BARREL_BLANK.get());
                        output.accept(ModItems.FIRING_MECHANISM.get());
                        output.accept(ModItems.BULLET_CASING.get());
                        output.accept(ModItems.LEAD_INGOT.get());
                        output.accept(ModItems.PISTOL.get());
                        output.accept(ModItems.RIFLE.get());
                        output.accept(ModItems.RED_DOT.get());
                        output.accept(ModItems.HOLO_SIGHT.get());
                        output.accept(ModItems.LASER.get());
                        output.accept(ModItems.FLASHLIGHT.get());
                        output.accept(ModItems.SCOPE_4X.get());
                        output.accept(ModItems.SCOPE_8X.get());
                        output.accept(ModItems.GUNSMITH_TABLE.get());
                        output.accept(ModItems.METAL_PRESS.get());
                        output.accept(ModItems.GUN_MODIFICATION_TABLE.get());
                        output.accept(ModItems.CARBON_STEEL.get());
                        output.accept(ModItems.SPRING.get());
                        output.accept(ModItems.FIRING_PIN.get());
                        output.accept(ModItems.COAL_GENERATOR.get());
                        output.accept(ModItems.HEAT_TREATMENT_FURNACE.get());
                        output.accept(ModItems.LATHE.get());
                        output.accept(ModItems.ASSEMBLY_BENCH.get());
                        output.accept(ModItems.ENERGY_PYLON.get());
                        output.accept(ModItems.COPPER_WIRE.get());
                        output.accept(ModItems.BATTERY.get());
                        output.accept(ModItems.RUBBER_SHEET.get());
                        output.accept(ModItems.GUN_OIL.get());
                        output.accept(ModItems.OIL_BUCKET.get());
                        output.accept(ModItems.FUEL_BUCKET.get());
                        output.accept(ModItems.REFINERY_BASE.get());
                        output.accept(ModItems.REFINERY_WALL.get());
                        output.accept(ModItems.REFINERY_TOP.get());
                        output.accept(ModItems.REFINERY_CONTROLLER.get());
                        output.accept(ModItems.ENERGY_PORT.get());
                        output.accept(ModItems.FLUID_PORT.get());
                        output.accept(ModItems.FLUID_PIPE.get());
                        output.accept(ModItems.OIL_DERRICK_BASE.get());
                        output.accept(ModItems.OIL_DERRICK_PILLAR.get());
                        output.accept(ModItems.OIL_DERRICK_CONTROLLER.get());
                    })
                    .build());
}
