package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.BlockItem;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Firearms.MOD_ID);

    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.registerSimpleItem("steel_ingot");
    public static final DeferredItem<Item> GUN_BARREL = ITEMS.registerSimpleItem("gun_barrel");
    public static final DeferredItem<Item> GUN_GRIP = ITEMS.registerSimpleItem("gun_grip");
    public static final DeferredItem<Item> TRIGGER_ASSEMBLY = ITEMS.registerSimpleItem("trigger_assembly");
    public static final DeferredItem<Item> MAGAZINE = ITEMS.registerSimpleItem("magazine");
    public static final DeferredItem<Item> BULLET = ITEMS.registerSimpleItem("bullet");

    public static final DeferredItem<BlockItem> GUNSMITH_TABLE = ITEMS.registerSimpleBlockItem(ModBlocks.GUNSMITH_TABLE);
}