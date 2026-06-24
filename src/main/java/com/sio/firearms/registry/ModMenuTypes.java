package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.menu.AssemblyBenchMenu;
import com.sio.firearms.menu.CoalGeneratorMenu;
import com.sio.firearms.menu.OilDerrickMenu;
import com.sio.firearms.menu.RefineryMenu;
import com.sio.firearms.menu.HeatTreatmentFurnaceMenu;
import com.sio.firearms.menu.LatheMenu;
import com.sio.firearms.menu.GunModificationTableMenu;
import com.sio.firearms.menu.GunsmithTableMenu;
import com.sio.firearms.menu.MetalPressMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, Firearms.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<GunsmithTableMenu>> GUNSMITH_TABLE_MENU =
            MENU_TYPES.register("gunsmith_table",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new GunsmithTableMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<MetalPressMenu>> METAL_PRESS_MENU =
            MENU_TYPES.register("metal_press",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new MetalPressMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<GunModificationTableMenu>> GUN_MODIFICATION_TABLE_MENU =
            MENU_TYPES.register("gun_modification_table",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new GunModificationTableMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<CoalGeneratorMenu>> COAL_GENERATOR_MENU =
            MENU_TYPES.register("coal_generator",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new CoalGeneratorMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<HeatTreatmentFurnaceMenu>> HEAT_TREATMENT_FURNACE_MENU =
            MENU_TYPES.register("heat_treatment_furnace",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new HeatTreatmentFurnaceMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<LatheMenu>> LATHE_MENU =
            MENU_TYPES.register("lathe",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new LatheMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<AssemblyBenchMenu>> ASSEMBLY_BENCH_MENU =
            MENU_TYPES.register("assembly_bench",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new AssemblyBenchMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<RefineryMenu>> REFINERY_MENU =
            MENU_TYPES.register("refinery",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new RefineryMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<OilDerrickMenu>> OIL_DERRICK_MENU =
            MENU_TYPES.register("oil_derrick",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new OilDerrickMenu(windowId, inv)));
}