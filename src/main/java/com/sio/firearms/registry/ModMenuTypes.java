package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.menu.GunsmithTableMenu;
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
}