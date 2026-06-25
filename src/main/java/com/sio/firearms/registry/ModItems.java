package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.attachment.AttachmentType;
import com.sio.firearms.item.AttachmentItem;
import com.sio.firearms.item.BandageItem;
import com.sio.firearms.item.BatteryItem;
import com.sio.firearms.item.BulletproofVestItem;
import com.sio.firearms.item.CleaningKitItem;
import com.sio.firearms.item.GrenadeItem;
import com.sio.firearms.item.NightVisionGogglesItem;
import com.sio.firearms.item.SeaMineItem;
import com.sio.firearms.item.SmokeGrenadeItem;
import com.sio.firearms.item.GunItem;
import com.sio.firearms.item.MedikitItem;
import com.sio.firearms.item.ShotgunItem;
import com.sio.firearms.item.SMGItem;
import com.sio.firearms.item.SniperRifleItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
    public static final DeferredItem<Item> HARDENED_STEEL_INGOT = ITEMS.registerSimpleItem("hardened_steel_ingot");
    public static final DeferredItem<Item> PROPELLANT_POWDER = ITEMS.registerSimpleItem("propellant_powder");
    public static final DeferredItem<Item> STEEL_ROD = ITEMS.registerSimpleItem("steel_rod");
    public static final DeferredItem<Item> GUN_BARREL_BLANK = ITEMS.registerSimpleItem("gun_barrel_blank");
    public static final DeferredItem<Item> FIRING_MECHANISM = ITEMS.registerSimpleItem("firing_mechanism");
    public static final DeferredItem<Item> BULLET_CASING = ITEMS.registerSimpleItem("bullet_casing");
    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.registerSimpleItem("lead_ingot");

    public static final DeferredItem<Item> PISTOL = ITEMS.register("pistol",
            () -> new GunItem(new Item.Properties().stacksTo(1).durability(150), 8, 10, 7, ModSounds.PISTOL_SHOOT.get()));

    public static final DeferredItem<Item> RIFLE = ITEMS.register("rifle",
            () -> new GunItem(new Item.Properties().stacksTo(1).durability(200), 16, 25, 5, ModSounds.RIFLE_SHOOT.get()));

    public static final DeferredItem<Item> SHOTGUN = ITEMS.register("shotgun",
            () -> new ShotgunItem(new Item.Properties().stacksTo(1).durability(150), 4, 40, 2, ModSounds.SHOTGUN_SHOOT.get()));

    public static final DeferredItem<Item> SNIPER_RIFLE = ITEMS.register("sniper_rifle",
            () -> new SniperRifleItem(new Item.Properties().stacksTo(1).durability(200), 24, 60, 5, ModSounds.SNIPER_RIFLE_SHOOT.get()));

    public static final DeferredItem<Item> SMG = ITEMS.register("smg",
            () -> new SMGItem(new Item.Properties().stacksTo(1).durability(300), 5, 4, 30, ModSounds.SMG_SHOOT.get()));

    public static final DeferredItem<Item> RED_DOT = ITEMS.register("red_dot",
            () -> new AttachmentItem(new Item.Properties(), AttachmentType.RED_DOT));
    public static final DeferredItem<Item> HOLO_SIGHT = ITEMS.register("holo_sight",
            () -> new AttachmentItem(new Item.Properties(), AttachmentType.HOLO_SIGHT));
    public static final DeferredItem<Item> LASER = ITEMS.register("laser",
            () -> new AttachmentItem(new Item.Properties(), AttachmentType.LASER));
    public static final DeferredItem<Item> FLASHLIGHT = ITEMS.register("flashlight",
            () -> new AttachmentItem(new Item.Properties(), AttachmentType.FLASHLIGHT));
    public static final DeferredItem<Item> SCOPE_4X = ITEMS.register("scope_4x",
            () -> new AttachmentItem(new Item.Properties(), AttachmentType.SCOPE_4X));
    public static final DeferredItem<Item> SCOPE_8X = ITEMS.register("scope_8x",
            () -> new AttachmentItem(new Item.Properties(), AttachmentType.SCOPE_8X));

    public static final DeferredItem<Item> CARBON_STEEL = ITEMS.registerSimpleItem("carbon_steel");
    public static final DeferredItem<Item> SPRING = ITEMS.registerSimpleItem("spring");
    public static final DeferredItem<Item> FIRING_PIN = ITEMS.registerSimpleItem("firing_pin");

    public static final DeferredItem<Item> RUBBER_SHEET = ITEMS.registerSimpleItem("rubber_sheet");
    public static final DeferredItem<Item> GUN_OIL = ITEMS.registerSimpleItem("gun_oil");
    public static final DeferredItem<Item> BOLT = ITEMS.registerSimpleItem("bolt");
    public static final DeferredItem<Item> BUFFER_TUBE = ITEMS.registerSimpleItem("buffer_tube");
    public static final DeferredItem<Item> RUBBER_GRIP = ITEMS.register("rubber_grip",
            () -> new AttachmentItem(new Item.Properties(), AttachmentType.RUBBER_GRIP));
    public static final DeferredItem<Item> CLEANING_KIT = ITEMS.register("cleaning_kit",
            () -> new CleaningKitItem(new Item.Properties().stacksTo(8)));
    public static final DeferredItem<Item> INDUSTRIAL_LUBRICANT = ITEMS.registerSimpleItem("industrial_lubricant");

    public static final DeferredItem<Item> OIL_BUCKET = ITEMS.register("oil_bucket",
            () -> new BucketItem(ModFluids.OIL_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> FUEL_BUCKET = ITEMS.register("fuel_bucket",
            () -> new BucketItem(ModFluids.FUEL_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> BATTERY = ITEMS.register("battery",
            () -> new BatteryItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> BANDAGE = ITEMS.register("bandage",
            () -> new BandageItem(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> MEDIKIT = ITEMS.register("medikit",
            () -> new MedikitItem(new Item.Properties().stacksTo(4)));

    public static final DeferredItem<Item> KEVLAR_PLATE = ITEMS.registerSimpleItem("kevlar_plate");
    public static final DeferredItem<Item> CIRCUIT_BOARD = ITEMS.registerSimpleItem("circuit_board");
    public static final DeferredItem<Item> ELECTRONIC_TRIGGER = ITEMS.registerSimpleItem("electronic_trigger");
    public static final DeferredItem<Item> EXPLOSIVE_COMPOUND = ITEMS.registerSimpleItem("explosive_compound");

    public static final DeferredItem<Item> GRENADE = ITEMS.register("grenade",
            () -> new GrenadeItem(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> SMOKE_GRENADE = ITEMS.register("smoke_grenade",
            () -> new SmokeGrenadeItem(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> SEA_MINE = ITEMS.register("sea_mine",
            () -> new SeaMineItem(new Item.Properties().stacksTo(4)));

    public static final DeferredItem<Item> BULLETPROOF_VEST = ITEMS.register("bulletproof_vest",
            () -> new BulletproofVestItem(ModArmorMaterials.BULLETPROOF_VEST, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(ArmorItem.Type.CHESTPLATE.getDurability(25))));

    public static final DeferredItem<Item> NIGHT_VISION_GOGGLES = ITEMS.register("night_vision_goggles",
            () -> new NightVisionGogglesItem(ModArmorMaterials.NIGHT_VISION_GOGGLES,
                    new Item.Properties().stacksTo(1).durability(ArmorItem.Type.HELMET.getDurability(15))));

    public static final DeferredItem<BlockItem> LAND_MINE = ITEMS.registerSimpleBlockItem(ModBlocks.LAND_MINE);

    public static final DeferredItem<BlockItem> GUNSMITH_TABLE = ITEMS.registerSimpleBlockItem(ModBlocks.GUNSMITH_TABLE);
    public static final DeferredItem<BlockItem> METAL_PRESS = ITEMS.registerSimpleBlockItem(ModBlocks.METAL_PRESS);
    public static final DeferredItem<BlockItem> GUN_MODIFICATION_TABLE = ITEMS.registerSimpleBlockItem(ModBlocks.GUN_MODIFICATION_TABLE);
    public static final DeferredItem<BlockItem> COAL_GENERATOR = ITEMS.registerSimpleBlockItem(ModBlocks.COAL_GENERATOR);
    public static final DeferredItem<BlockItem> HEAT_TREATMENT_FURNACE = ITEMS.registerSimpleBlockItem(ModBlocks.HEAT_TREATMENT_FURNACE);
    public static final DeferredItem<BlockItem> LATHE = ITEMS.registerSimpleBlockItem(ModBlocks.LATHE);
    public static final DeferredItem<BlockItem> ASSEMBLY_BENCH = ITEMS.registerSimpleBlockItem(ModBlocks.ASSEMBLY_BENCH);
    public static final DeferredItem<BlockItem> ENERGY_PYLON = ITEMS.registerSimpleBlockItem(ModBlocks.ENERGY_PYLON);
    public static final DeferredItem<BlockItem> COPPER_WIRE = ITEMS.registerSimpleBlockItem(ModBlocks.COPPER_WIRE);
    public static final DeferredItem<BlockItem> REFINERY_BASE = ITEMS.registerSimpleBlockItem(ModBlocks.REFINERY_BASE);
    public static final DeferredItem<BlockItem> REFINERY_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.REFINERY_WALL);
    public static final DeferredItem<BlockItem> REFINERY_TOP = ITEMS.registerSimpleBlockItem(ModBlocks.REFINERY_TOP);
    public static final DeferredItem<BlockItem> REFINERY_CONTROLLER = ITEMS.registerSimpleBlockItem(ModBlocks.REFINERY_CONTROLLER);
    public static final DeferredItem<BlockItem> ENERGY_PORT = ITEMS.registerSimpleBlockItem(ModBlocks.ENERGY_PORT);
    public static final DeferredItem<BlockItem> FLUID_PORT = ITEMS.registerSimpleBlockItem(ModBlocks.FLUID_PORT);
    public static final DeferredItem<BlockItem> FUEL_GENERATOR = ITEMS.registerSimpleBlockItem(ModBlocks.FUEL_GENERATOR);
    public static final DeferredItem<BlockItem> AUTO_TURRET = ITEMS.registerSimpleBlockItem(ModBlocks.AUTO_TURRET);
    public static final DeferredItem<BlockItem> FLUID_PIPE = ITEMS.registerSimpleBlockItem(ModBlocks.FLUID_PIPE);
    public static final DeferredItem<BlockItem> OIL_DERRICK_BASE = ITEMS.registerSimpleBlockItem(ModBlocks.OIL_DERRICK_BASE);
    public static final DeferredItem<BlockItem> OIL_DERRICK_PILLAR = ITEMS.registerSimpleBlockItem(ModBlocks.OIL_DERRICK_PILLAR);
    public static final DeferredItem<BlockItem> OIL_DERRICK_CONTROLLER = ITEMS.registerSimpleBlockItem(ModBlocks.OIL_DERRICK_CONTROLLER);
}