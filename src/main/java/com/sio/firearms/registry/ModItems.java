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
import com.sio.firearms.item.GeigerCounterItem;
import com.sio.firearms.item.HazmatSuitItem;
import com.sio.firearms.item.FlamethrowerItem;
import com.sio.firearms.item.GasMaskItem;
import com.sio.firearms.item.MolotovCocktailItem;
import com.sio.firearms.item.NitroglycerinItem;
import com.sio.firearms.item.WrenchItem;
import com.sio.firearms.item.ChainsawItem;
import com.sio.firearms.item.MingunItem;
import com.sio.firearms.item.AmmoBoxItem;
import com.sio.firearms.item.GunCaseItem;
import com.sio.firearms.item.RubberBootsItem;
import com.sio.firearms.item.AdrenalineItem;
import com.sio.firearms.item.AdrenalineSyringeItem;
import com.sio.firearms.item.CoagulantItem;
import com.sio.firearms.item.CoagulantSyringeItem;
import com.sio.firearms.item.MorphineItem;
import com.sio.firearms.item.MorphineSyringeItem;
import com.sio.firearms.item.RiotShieldItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.BlockItem;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Firearms.MOD_ID);

    public static final DeferredItem<Item> COAL_COKE = ITEMS.registerSimpleItem("coal_coke");

    public static final DeferredItem<Item> CREOSOTE_OIL_BUCKET = ITEMS.register("creosote_oil_bucket",
            () -> new BucketItem(ModFluids.CREOSOTE_OIL_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

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

    // ── Microchip production chain — Stage 1 ─────────────────────────────────
    public static final DeferredItem<Item> METALLURGICAL_SILICON   = ITEMS.registerSimpleItem("metallurgical_silicon");
    public static final DeferredItem<Item> ELECTRONIC_GRADE_SILICON = ITEMS.registerSimpleItem("electronic_grade_silicon");
    public static final DeferredItem<Item> SILICON_INGOT           = ITEMS.registerSimpleItem("silicon_ingot");
    public static final DeferredItem<Item> SILICON_WAFER           = ITEMS.registerSimpleItem("silicon_wafer");
    public static final DeferredItem<Item> COATED_WAFER            = ITEMS.registerSimpleItem("coated_wafer");
    public static final DeferredItem<Item> PATTERNED_WAFER         = ITEMS.registerSimpleItem("patterned_wafer");
    public static final DeferredItem<Item> ETCHED_WAFER            = ITEMS.registerSimpleItem("etched_wafer");
    public static final DeferredItem<Item> DOPED_WAFER             = ITEMS.registerSimpleItem("doped_wafer");
    public static final DeferredItem<Item> FINISHED_WAFER          = ITEMS.registerSimpleItem("finished_wafer");
    public static final DeferredItem<Item> TESTED_WAFER            = ITEMS.registerSimpleItem("tested_wafer");
    public static final DeferredItem<Item> DEFECTIVE_WAFER         = ITEMS.registerSimpleItem("defective_wafer");
    public static final DeferredItem<Item> SILICON_DIE             = ITEMS.registerSimpleItem("silicon_die");
    public static final DeferredItem<Item> BASIC_MICROCHIP         = ITEMS.registerSimpleItem("basic_microchip");
    public static final DeferredItem<Item> ADVANCED_MICROCHIP      = ITEMS.registerSimpleItem("advanced_microchip");
    public static final DeferredItem<Item> PHOTOMASK =
            ITEMS.register("photomask", () -> new Item(new Item.Properties().stacksTo(1).durability(10)));
    public static final DeferredItem<Item> GOLD_FOIL               = ITEMS.registerSimpleItem("gold_foil");
    public static final DeferredItem<Item> DIAMOND_SAW_BLADE =
            ITEMS.register("diamond_saw_blade", () -> new Item(new Item.Properties().stacksTo(1).durability(64)));
    public static final DeferredItem<Item> BORON              = ITEMS.registerSimpleItem("boron");
    public static final DeferredItem<Item> PHOSPHORUS         = ITEMS.registerSimpleItem("phosphorus");

    // ── PVC production chain ─────────────────────────────────────────────────
    public static final DeferredItem<Item> PVC_PELLETS        = ITEMS.registerSimpleItem("pvc_pellets");
    public static final DeferredItem<Item> PLASTIC_SHEET      = ITEMS.registerSimpleItem("plastic_sheet");
    public static final DeferredItem<Item> PIPE_FITTING       = ITEMS.registerSimpleItem("pipe_fitting");

    // ── New chemical items ───────────────────────────────────────────────────
    public static final DeferredItem<Item> QUARTZ_SAND        = ITEMS.registerSimpleItem("quartz_sand");
    public static final DeferredItem<Item> ETCHED_STEEL       = ITEMS.registerSimpleItem("etched_steel");
    public static final DeferredItem<Item> ETCHED_COPPER      = ITEMS.registerSimpleItem("etched_copper");
    public static final DeferredItem<Item> ETCHED_IRON        = ITEMS.registerSimpleItem("etched_iron");
    public static final DeferredItem<Item> SYNTHETIC_RUBBER   = ITEMS.registerSimpleItem("synthetic_rubber");

    public static final DeferredItem<Item> SULFURIC_ACID_BUCKET = ITEMS.register("sulfuric_acid_bucket",
            () -> new BucketItem(ModFluids.SULFURIC_ACID_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> NITRIC_ACID_BUCKET = ITEMS.register("nitric_acid_bucket",
            () -> new BucketItem(ModFluids.NITRIC_ACID_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> SYNTHETIC_RUBBER_BUCKET = ITEMS.register("synthetic_rubber_bucket",
            () -> new BucketItem(ModFluids.SYNTHETIC_RUBBER_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<BlockItem> CHEMICAL_MIXER            = ITEMS.registerSimpleBlockItem(ModBlocks.CHEMICAL_MIXER);
    public static final DeferredItem<BlockItem> CHEMICAL_MIXER_BASE       = ITEMS.registerSimpleBlockItem(ModBlocks.CHEMICAL_MIXER_BASE);
    public static final DeferredItem<BlockItem> CHEMICAL_MIXER_WALL       = ITEMS.registerSimpleBlockItem(ModBlocks.CHEMICAL_MIXER_WALL);
    public static final DeferredItem<BlockItem> CHEMICAL_MIXER_CONTROLLER = ITEMS.registerSimpleBlockItem(ModBlocks.CHEMICAL_MIXER_CONTROLLER);
    public static final DeferredItem<BlockItem> ACID_BATH      = ITEMS.registerSimpleBlockItem(ModBlocks.ACID_BATH);
    public static final DeferredItem<BlockItem> WATER_PUMP     = ITEMS.registerSimpleBlockItem(ModBlocks.WATER_PUMP);

    // ── Refined ammo ──────────────────────────────────────────────────────────
    public static final DeferredItem<Item> REFINED_GUNPOWDER    = ITEMS.registerSimpleItem("refined_gunpowder");
    public static final DeferredItem<Item> REFINED_BULLET       = ITEMS.registerSimpleItem("refined_bullet");

    // ── New chemical items ────────────────────────────────────────────────────
    public static final DeferredItem<Item> TUNGSTEN_ROD         = ITEMS.registerSimpleItem("tungsten_rod");
    public static final DeferredItem<Item> ARMOR_PIERCING_BULLET = ITEMS.registerSimpleItem("armor_piercing_bullet");
    public static final DeferredItem<Item> NITROCELLULOSE       = ITEMS.registerSimpleItem("nitrocellulose");

    public static final DeferredItem<Item> HYDROGEN_GAS_BUCKET = ITEMS.register("hydrogen_gas_bucket",
            () -> new BucketItem(ModFluids.HYDROGEN_GAS_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> OXYGEN_GAS_BUCKET = ITEMS.register("oxygen_gas_bucket",
            () -> new BucketItem(ModFluids.OXYGEN_GAS_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> FLUORINE_GAS_BUCKET = ITEMS.register("fluorine_gas_bucket",
            () -> new BucketItem(ModFluids.FLUORINE_GAS_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> CHLORINE_GAS_BUCKET = ITEMS.register("chlorine_gas_bucket",
            () -> new BucketItem(ModFluids.CHLORINE_GAS_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> NITRATE_SOLUTION_BUCKET = ITEMS.register("nitrate_solution_bucket",
            () -> new BucketItem(ModFluids.NITRATE_SOLUTION_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> PVC_RESIN_BUCKET = ITEMS.register("pvc_resin_bucket",
            () -> new BucketItem(ModFluids.PVC_RESIN_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<BlockItem> ELECTROLYSIS_MACHINE = ITEMS.registerSimpleBlockItem(ModBlocks.ELECTROLYSIS_MACHINE);

    // ── Ore drops & ingots ────────────────────────────────────────────────────
    public static final DeferredItem<Item> SULFUR           = ITEMS.registerSimpleItem("sulfur");
    public static final DeferredItem<Item> SALTPETER        = ITEMS.registerSimpleItem("saltpeter");
    public static final DeferredItem<Item> TUNGSTEN_ORE_RAW = ITEMS.registerSimpleItem("tungsten_ore_raw");
    public static final DeferredItem<Item> FLUORITE_CRYSTAL = ITEMS.registerSimpleItem("fluorite_crystal");
    public static final DeferredItem<Item> URANIUM_ORE_RAW  = ITEMS.registerSimpleItem("uranium_ore_raw");
    public static final DeferredItem<Item> TUNGSTEN_INGOT   = ITEMS.registerSimpleItem("tungsten_ingot");
    public static final DeferredItem<Item> URANIUM_INGOT    = ITEMS.registerSimpleItem("uranium_ingot");
    public static final DeferredItem<Item> CHROMIUM_INGOT   = ITEMS.registerSimpleItem("chromium_ingot");
    public static final DeferredItem<Item> NICKEL_INGOT     = ITEMS.registerSimpleItem("nickel_ingot");
    public static final DeferredItem<Item> BAUXITE_DUST     = ITEMS.registerSimpleItem("bauxite_dust");
    public static final DeferredItem<Item> ALUMINUM_INGOT   = ITEMS.registerSimpleItem("aluminum_ingot");
    public static final DeferredItem<Item> KANTHAL_ALLOY    = ITEMS.registerSimpleItem("kanthal_alloy");
    public static final DeferredItem<Item> KANTHAL_WIRE     = ITEMS.registerSimpleItem("kanthal_wire");
    public static final DeferredItem<Item> NICHROME_ALLOY   = ITEMS.registerSimpleItem("nichrome_alloy");
    public static final DeferredItem<Item> NICHROME_WIRE    = ITEMS.registerSimpleItem("nichrome_wire");
    public static final DeferredItem<Item> TUNGSTEN_CARBIDE = ITEMS.registerSimpleItem("tungsten_carbide");
    public static final DeferredItem<Item> TUNGSTEN_WIRE    = ITEMS.registerSimpleItem("tungsten_wire");

    public static final DeferredItem<BlockItem> SULFUR_ORE    = ITEMS.registerSimpleBlockItem(ModBlocks.SULFUR_ORE);
    public static final DeferredItem<BlockItem> SALTPETER_ORE = ITEMS.registerSimpleBlockItem(ModBlocks.SALTPETER_ORE);
    public static final DeferredItem<BlockItem> TUNGSTEN_ORE  = ITEMS.registerSimpleBlockItem(ModBlocks.TUNGSTEN_ORE);
    public static final DeferredItem<BlockItem> FLUORITE_ORE  = ITEMS.registerSimpleBlockItem(ModBlocks.FLUORITE_ORE);
    public static final DeferredItem<BlockItem> URANIUM_ORE   = ITEMS.registerSimpleBlockItem(ModBlocks.URANIUM_ORE);
    public static final DeferredItem<BlockItem> CHROMIUM_ORE  = ITEMS.registerSimpleBlockItem(ModBlocks.CHROMIUM_ORE);
    public static final DeferredItem<BlockItem> NICKEL_ORE    = ITEMS.registerSimpleBlockItem(ModBlocks.NICKEL_ORE);
    public static final DeferredItem<BlockItem> BAUXITE_ORE   = ITEMS.registerSimpleBlockItem(ModBlocks.BAUXITE_ORE);
    public static final DeferredItem<BlockItem> KANTHAL_COIL  = ITEMS.registerSimpleBlockItem(ModBlocks.KANTHAL_COIL);
    public static final DeferredItem<BlockItem> NICHROME_COIL = ITEMS.registerSimpleBlockItem(ModBlocks.NICHROME_COIL);
    public static final DeferredItem<BlockItem> TUNGSTEN_COIL = ITEMS.registerSimpleBlockItem(ModBlocks.TUNGSTEN_COIL);

    public static final DeferredItem<BlockItem> EBF_IMPORT_BUS = ITEMS.registerSimpleBlockItem(ModBlocks.EBF_IMPORT_BUS);
    public static final DeferredItem<BlockItem> EBF_OUTPUT_BUS = ITEMS.registerSimpleBlockItem(ModBlocks.EBF_OUTPUT_BUS);
    public static final DeferredItem<BlockItem> EBF_CONTROLLER = ITEMS.registerSimpleBlockItem(ModBlocks.EBF_CONTROLLER);
    public static final DeferredItem<BlockItem> BLAST_FURNACE_CASING = ITEMS.registerSimpleBlockItem(ModBlocks.BLAST_FURNACE_CASING);
    public static final DeferredItem<BlockItem> MUFFLER_HATCH        = ITEMS.registerSimpleBlockItem(ModBlocks.MUFFLER_HATCH);

    public static final DeferredItem<BlockItem> COKE_OVEN_BRICK  = ITEMS.registerSimpleBlockItem(ModBlocks.COKE_OVEN_BRICK);
    public static final DeferredItem<BlockItem> COKE_OVEN_CONTROLLER = ITEMS.registerSimpleBlockItem(ModBlocks.COKE_OVEN_CONTROLLER);

    public static final DeferredItem<BlockItem> WEAPON_RACK = ITEMS.registerSimpleBlockItem(ModBlocks.WEAPON_RACK);
    public static final DeferredItem<BlockItem> ITEM_PIPE   = ITEMS.registerSimpleBlockItem(ModBlocks.ITEM_PIPE);

    public static final DeferredItem<BlockItem> LAND_MINE = ITEMS.registerSimpleBlockItem(ModBlocks.LAND_MINE);

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

    public static final DeferredItem<Item> WRENCH =
            ITEMS.register("wrench", () -> new WrenchItem());

    // ── Distillation product buckets ─────────────────────────────────────────
    public static final DeferredItem<Item> BUTANE_BUCKET = ITEMS.register("butane_bucket",
            () -> new BucketItem(ModFluids.BUTANE_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> GASOLINE_BUCKET = ITEMS.register("gasoline_bucket",
            () -> new BucketItem(ModFluids.GASOLINE_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> NAPHTHA_BUCKET = ITEMS.register("naphtha_bucket",
            () -> new BucketItem(ModFluids.NAPHTHA_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> KEROSENE_BUCKET = ITEMS.register("kerosene_bucket",
            () -> new BucketItem(ModFluids.KEROSENE_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> DIESEL_BUCKET = ITEMS.register("diesel_bucket",
            () -> new BucketItem(ModFluids.DIESEL_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> HEAVY_GAS_OIL_BUCKET = ITEMS.register("heavy_gas_oil_bucket",
            () -> new BucketItem(ModFluids.HEAVY_GAS_OIL_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> RESIDUAL_FUEL_OIL_BUCKET = ITEMS.register("residual_fuel_oil_bucket",
            () -> new BucketItem(ModFluids.RESIDUAL_FUEL_OIL_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    // ── Photoresist bucket ────────────────────────────────────────────────────
    public static final DeferredItem<Item> PHOTORESIST_BUCKET = ITEMS.register("photoresist_bucket",
            () -> new BucketItem(ModFluids.PHOTORESIST_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    // ── Stage 2 machine block items ───────────────────────────────────────────
    public static final DeferredItem<BlockItem> WAFER_CUTTING_MACHINE    = ITEMS.registerSimpleBlockItem(ModBlocks.WAFER_CUTTING_MACHINE);
    public static final DeferredItem<BlockItem> DEPOSITION_CHAMBER       = ITEMS.registerSimpleBlockItem(ModBlocks.DEPOSITION_CHAMBER);
    public static final DeferredItem<BlockItem> PLASMA_ETCHER            = ITEMS.registerSimpleBlockItem(ModBlocks.PLASMA_ETCHER);
    public static final DeferredItem<BlockItem> ION_IMPLANTER            = ITEMS.registerSimpleBlockItem(ModBlocks.ION_IMPLANTER);
    public static final DeferredItem<BlockItem> METALLIZATION_CHAMBER    = ITEMS.registerSimpleBlockItem(ModBlocks.METALLIZATION_CHAMBER);
    public static final DeferredItem<BlockItem> WAFER_TESTER             = ITEMS.registerSimpleBlockItem(ModBlocks.WAFER_TESTER);
    public static final DeferredItem<BlockItem> DICING_SAW               = ITEMS.registerSimpleBlockItem(ModBlocks.DICING_SAW);
    public static final DeferredItem<BlockItem> CHIP_PACKAGING_MACHINE   = ITEMS.registerSimpleBlockItem(ModBlocks.CHIP_PACKAGING_MACHINE);

    // ── Multiblock block items ────────────────────────────────────────────────
    public static final DeferredItem<BlockItem> CRYSTAL_GROWTH_BASE       = ITEMS.registerSimpleBlockItem(ModBlocks.CRYSTAL_GROWTH_BASE);
    public static final DeferredItem<BlockItem> CRYSTAL_GROWTH_WALL       = ITEMS.registerSimpleBlockItem(ModBlocks.CRYSTAL_GROWTH_WALL);
    public static final DeferredItem<BlockItem> CRYSTAL_GROWTH_TOP        = ITEMS.registerSimpleBlockItem(ModBlocks.CRYSTAL_GROWTH_TOP);
    public static final DeferredItem<BlockItem> CRYSTAL_GROWTH_CONTROLLER = ITEMS.registerSimpleBlockItem(ModBlocks.CRYSTAL_GROWTH_CONTROLLER);
    public static final DeferredItem<BlockItem> EUV_BASE                  = ITEMS.registerSimpleBlockItem(ModBlocks.EUV_BASE);
    public static final DeferredItem<BlockItem> EUV_WALL                  = ITEMS.registerSimpleBlockItem(ModBlocks.EUV_WALL);
    public static final DeferredItem<BlockItem> EUV_LENS_HOUSING          = ITEMS.registerSimpleBlockItem(ModBlocks.EUV_LENS_HOUSING);
    public static final DeferredItem<BlockItem> EUV_MIRROR_ARRAY          = ITEMS.registerSimpleBlockItem(ModBlocks.EUV_MIRROR_ARRAY);
    public static final DeferredItem<BlockItem> EUV_EMITTER_HOUSING       = ITEMS.registerSimpleBlockItem(ModBlocks.EUV_EMITTER_HOUSING);
    public static final DeferredItem<BlockItem> EUV_LITHOGRAPHY_CONTROLLER = ITEMS.registerSimpleBlockItem(ModBlocks.EUV_LITHOGRAPHY_CONTROLLER);

    // ── Nuclear Reactor Stage 1 items ─────────────────────────────────────────
    public static final DeferredItem<Item> ZIRCONIUM_ORE_RAW          = ITEMS.registerSimpleItem("zirconium_ore_raw");
    public static final DeferredItem<Item> ZIRCONIUM_INGOT             = ITEMS.registerSimpleItem("zirconium_ingot");
    public static final DeferredItem<Item> URANIUM_HEXAFLUORIDE        = ITEMS.registerSimpleItem("uranium_hexafluoride");
    public static final DeferredItem<Item> ENRICHED_URANIUM_HEXAFLUORIDE = ITEMS.registerSimpleItem("enriched_uranium_hexafluoride");
    public static final DeferredItem<Item> DEPLETED_URANIUM_HEXAFLUORIDE = ITEMS.registerSimpleItem("depleted_uranium_hexafluoride");
    public static final DeferredItem<Item> URANIUM_DIOXIDE_POWDER      = ITEMS.registerSimpleItem("uranium_dioxide_powder");
    public static final DeferredItem<Item> URANIUM_DIOXIDE_PELLET      = ITEMS.registerSimpleItem("uranium_dioxide_pellet");
    public static final DeferredItem<Item> FUEL_ROD_CLADDING           = ITEMS.registerSimpleItem("fuel_rod_cladding");
    public static final DeferredItem<Item> FUEL_ROD                    = ITEMS.registerSimpleItem("fuel_rod");
    public static final DeferredItem<Item> FUEL_ROD_ASSEMBLY           = ITEMS.registerSimpleItem("fuel_rod_assembly");
    public static final DeferredItem<Item> SPENT_FUEL_ROD              = ITEMS.registerSimpleItem("spent_fuel_rod");
    public static final DeferredItem<Item> DEPLETED_URANIUM            = ITEMS.registerSimpleItem("depleted_uranium");
    public static final DeferredItem<Item> CONTROL_ROD                 = ITEMS.registerSimpleItem("control_rod");
    public static final DeferredItem<Item> BORON_CARBIDE               = ITEMS.registerSimpleItem("boron_carbide");
    public static final DeferredItem<Item> GRAPHITE_BLOCK_ITEM         = ITEMS.registerSimpleItem("graphite_block_item");

    // ── Nuclear fluid buckets ─────────────────────────────────────────────────
    public static final DeferredItem<Item> URANIUM_HEXAFLUORIDE_BUCKET = ITEMS.register("uranium_hexafluoride_bucket",
            () -> new BucketItem(ModFluids.URANIUM_HEXAFLUORIDE_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> ENRICHED_UF6_BUCKET = ITEMS.register("enriched_uf6_bucket",
            () -> new BucketItem(ModFluids.ENRICHED_UF6_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> DEPLETED_UF6_BUCKET = ITEMS.register("depleted_uf6_bucket",
            () -> new BucketItem(ModFluids.DEPLETED_UF6_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> HEAVY_WATER_BUCKET = ITEMS.register("heavy_water_bucket",
            () -> new BucketItem(ModFluids.HEAVY_WATER_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> STEAM_BUCKET = ITEMS.register("steam_bucket",
            () -> new BucketItem(ModFluids.STEAM_STILL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    // ── Nuclear block items ───────────────────────────────────────────────────
    public static final DeferredItem<BlockItem> ZIRCONITE_ORE  = ITEMS.registerSimpleBlockItem(ModBlocks.ZIRCONITE_ORE);
    public static final DeferredItem<BlockItem> GAS_CENTRIFUGE = ITEMS.registerSimpleBlockItem(ModBlocks.GAS_CENTRIFUGE);

    // ── Fluid storage ────────────────────────────────────────────────────────
    public static final DeferredItem<BlockItem> FLUID_TANK = ITEMS.registerSimpleBlockItem(ModBlocks.FLUID_TANK);

    // ── Nuclear Reactor Stage 2 blocks ────────────────────────────────────────
    public static final DeferredItem<BlockItem> REACTOR_BASE                = ITEMS.registerSimpleBlockItem(ModBlocks.REACTOR_BASE);
    public static final DeferredItem<BlockItem> REACTOR_WALL                = ITEMS.registerSimpleBlockItem(ModBlocks.REACTOR_WALL);
    public static final DeferredItem<BlockItem> REACTOR_TOP                 = ITEMS.registerSimpleBlockItem(ModBlocks.REACTOR_TOP);
    public static final DeferredItem<BlockItem> REACTOR_CONTROL_ROD_HOUSING = ITEMS.registerSimpleBlockItem(ModBlocks.REACTOR_CONTROL_ROD_HOUSING);
    public static final DeferredItem<BlockItem> LEAD_BLOCK                  = ITEMS.registerSimpleBlockItem(ModBlocks.LEAD_BLOCK);
    public static final DeferredItem<BlockItem> REACTOR_CONTROLLER          = ITEMS.registerSimpleBlockItem(ModBlocks.REACTOR_CONTROLLER);
    public static final DeferredItem<BlockItem> STEAM_TURBINE               = ITEMS.registerSimpleBlockItem(ModBlocks.STEAM_TURBINE);

    // ── Cooling Tower block items ─────────────────────────────────────────────
    public static final DeferredItem<BlockItem> COOLING_TOWER_BASE       = ITEMS.registerSimpleBlockItem(ModBlocks.COOLING_TOWER_BASE);
    public static final DeferredItem<BlockItem> COOLING_TOWER_WALL       = ITEMS.registerSimpleBlockItem(ModBlocks.COOLING_TOWER_WALL);
    public static final DeferredItem<BlockItem> COOLING_TOWER_VENT       = ITEMS.registerSimpleBlockItem(ModBlocks.COOLING_TOWER_VENT);
    public static final DeferredItem<BlockItem> COOLING_TOWER_CONTROLLER = ITEMS.registerSimpleBlockItem(ModBlocks.COOLING_TOWER_CONTROLLER);

    // ── Hazmat armor ─────────────────────────────────────────────────────────
    public static final DeferredItem<Item> HAZMAT_HELMET =
            ITEMS.register("hazmat_helmet",
                    () -> new HazmatSuitItem(ModArmorMaterials.HAZMAT, net.minecraft.world.item.ArmorItem.Type.HELMET,
                            new Item.Properties()));
    public static final DeferredItem<Item> HAZMAT_CHESTPLATE =
            ITEMS.register("hazmat_chestplate",
                    () -> new HazmatSuitItem(ModArmorMaterials.HAZMAT, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE,
                            new Item.Properties()));
    public static final DeferredItem<Item> HAZMAT_LEGGINGS =
            ITEMS.register("hazmat_leggings",
                    () -> new HazmatSuitItem(ModArmorMaterials.HAZMAT, net.minecraft.world.item.ArmorItem.Type.LEGGINGS,
                            new Item.Properties()));
    public static final DeferredItem<Item> HAZMAT_BOOTS =
            ITEMS.register("hazmat_boots",
                    () -> new HazmatSuitItem(ModArmorMaterials.HAZMAT, net.minecraft.world.item.ArmorItem.Type.BOOTS,
                            new Item.Properties()));

    // ── Radiation tools ───────────────────────────────────────────────────────
    public static final DeferredItem<Item> GEIGER_COUNTER =
            ITEMS.register("geiger_counter", GeigerCounterItem::new);

    public static final DeferredItem<BlockItem> TRASH_CAN = ITEMS.registerSimpleBlockItem(ModBlocks.TRASH_CAN);

    // ── Vehicle Garage ────────────────────────────────────────────────────────
    public static final DeferredItem<BlockItem> GARAGE_FLOOR      = ITEMS.registerSimpleBlockItem(ModBlocks.GARAGE_FLOOR);
    public static final DeferredItem<BlockItem> GARAGE_WALL       = ITEMS.registerSimpleBlockItem(ModBlocks.GARAGE_WALL);
    public static final DeferredItem<BlockItem> GARAGE_ROOF       = ITEMS.registerSimpleBlockItem(ModBlocks.GARAGE_ROOF);
    public static final DeferredItem<BlockItem> GARAGE_DOOR       = ITEMS.registerSimpleBlockItem(ModBlocks.GARAGE_DOOR);
    public static final DeferredItem<BlockItem> GARAGE_CONTROLLER = ITEMS.registerSimpleBlockItem(ModBlocks.GARAGE_CONTROLLER);
    public static final DeferredItem<Item>      TANK_SCHEMATIC    = ITEMS.registerSimpleItem("tank_schematic");

    // ── Incendiary / NBC / Explosives ────────────────────────────────────────
    public static final DeferredItem<Item> NITROGLYCERIN =
            ITEMS.register("nitroglycerin", () -> new NitroglycerinItem(new Item.Properties().stacksTo(8)));

    // ── Incendiary / NBC weapons ──────────────────────────────────────────────
    public static final DeferredItem<Item> FLAMETHROWER =
            ITEMS.register("flamethrower", () -> new FlamethrowerItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> MOLOTOV_COCKTAIL =
            ITEMS.register("molotov_cocktail", () -> new MolotovCocktailItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> GAS_MASK =
            ITEMS.register("gas_mask", () -> new GasMaskItem(ModArmorMaterials.GAS_MASK,
                    new Item.Properties().stacksTo(1).durability(500)));

    // ── Special weapons & containers ─────────────────────────────────────────
    public static final DeferredItem<Item> CHAINSAW = ITEMS.register("chainsaw", () ->
            new ChainsawItem(new Item.Properties().stacksTo(1).durability(500)
                    .attributes(ChainsawItem.createAttributes())));

    public static final DeferredItem<Item> MINIGUN =
            ITEMS.register("minigun", () -> new MingunItem(new Item.Properties().stacksTo(1).durability(800)));

    public static final DeferredItem<Item> AMMO_BOX =
            ITEMS.register("ammo_box", () -> new AmmoBoxItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> GUN_CASE =
            ITEMS.register("gun_case", () -> new GunCaseItem(new Item.Properties().stacksTo(1)));

    // ── Tank Production Chain ─────────────────────────────────────────────────
    public static final DeferredItem<Item> STEEL_PLATE   = ITEMS.registerSimpleItem("steel_plate");
    public static final DeferredItem<Item> TANK_HULL     = ITEMS.registerSimpleItem("tank_hull");
    public static final DeferredItem<Item> TANK_TRACKS   = ITEMS.registerSimpleItem("tank_tracks");
    public static final DeferredItem<Item> TANK_TURRET   = ITEMS.registerSimpleItem("tank_turret");
    public static final DeferredItem<Item> DIESEL_ENGINE = ITEMS.registerSimpleItem("diesel_engine");
    public static final DeferredItem<Item> TANK_CANNON   = ITEMS.registerSimpleItem("tank_cannon");

    // ── Rubber Boots ──────────────────────────────────────────────────────────
    public static final DeferredItem<Item> RUBBER_BOOTS =
            ITEMS.register("rubber_boots", () -> new RubberBootsItem(ModArmorMaterials.RUBBER_BOOTS,
                    new Item.Properties()));

    // ── Riot Shield ───────────────────────────────────────────────────────────
    public static final DeferredItem<Item> RIOT_SHIELD =
            ITEMS.register("riot_shield", () -> new RiotShieldItem(new Item.Properties().stacksTo(1).durability(500)));

    // ── Cordite & military ammo ───────────────────────────────────────────────
    public static final DeferredItem<Item> CORDITE          = ITEMS.registerSimpleItem("cordite");
    public static final DeferredItem<Item> CORDITE_BULLET   = ITEMS.registerSimpleItem("cordite_bullet");
    public static final DeferredItem<Item> EXPLOSIVE_BULLET = ITEMS.registerSimpleItem("explosive_bullet");

    // ── Pharmaceutical system ─────────────────────────────────────────────────
    public static final DeferredItem<Item> POPPY_SEEDS      = ITEMS.registerSimpleItem("poppy_seeds");
    public static final DeferredItem<Item> RAW_OPIUM        = ITEMS.registerSimpleItem("raw_opium");
    public static final DeferredItem<Item> REFINED_OPIUM    = ITEMS.registerSimpleItem("refined_opium");
    public static final DeferredItem<Item> MORPHINE =
            ITEMS.register("morphine", () -> new MorphineItem(new Item.Properties().stacksTo(8)));
    public static final DeferredItem<Item> ADRENALINE =
            ITEMS.register("adrenaline", () -> new AdrenalineItem(new Item.Properties().stacksTo(8)));
    public static final DeferredItem<Item> COAGULANT =
            ITEMS.register("coagulant", () -> new CoagulantItem(new Item.Properties().stacksTo(8)));
    public static final DeferredItem<Item> SYRINGE         = ITEMS.registerSimpleItem("syringe");
    public static final DeferredItem<Item> MORPHINE_SYRINGE =
            ITEMS.register("morphine_syringe",
                    () -> new MorphineSyringeItem(new Item.Properties().stacksTo(1).durability(5)));
    public static final DeferredItem<Item> ADRENALINE_SYRINGE =
            ITEMS.register("adrenaline_syringe",
                    () -> new AdrenalineSyringeItem(new Item.Properties().stacksTo(1).durability(5)));
    public static final DeferredItem<Item> COAGULANT_SYRINGE =
            ITEMS.register("coagulant_syringe",
                    () -> new CoagulantSyringeItem(new Item.Properties().stacksTo(1).durability(5)));

    // ── Titanium / Iridium / Osmium processing chain ──────────────────────────
    public static final DeferredItem<Item> TITANIUM_ORE_RAW = ITEMS.registerSimpleItem("titanium_ore_raw");
    public static final DeferredItem<Item> IRIDIUM_ORE_RAW  = ITEMS.registerSimpleItem("iridium_ore_raw");
    public static final DeferredItem<Item> OSMIUM_ORE_RAW   = ITEMS.registerSimpleItem("osmium_ore_raw");
    public static final DeferredItem<Item> TITANIUM_INGOT   = ITEMS.registerSimpleItem("titanium_ingot");
    public static final DeferredItem<Item> IRIDIUM_INGOT    = ITEMS.registerSimpleItem("iridium_ingot");
    public static final DeferredItem<Item> OSMIUM_INGOT     = ITEMS.registerSimpleItem("osmium_ingot");
    public static final DeferredItem<Item> IRIDIUM_ALLOY    = ITEMS.registerSimpleItem("iridium_alloy");
    public static final DeferredItem<Item> IRIDIUM_WIRE     = ITEMS.registerSimpleItem("iridium_wire");

    public static final DeferredItem<BlockItem> TITANIUM_ORE = ITEMS.registerSimpleBlockItem(ModBlocks.TITANIUM_ORE);
    public static final DeferredItem<BlockItem> IRIDIUM_ORE  = ITEMS.registerSimpleBlockItem(ModBlocks.IRIDIUM_ORE);
    public static final DeferredItem<BlockItem> OSMIUM_ORE   = ITEMS.registerSimpleBlockItem(ModBlocks.OSMIUM_ORE);
    public static final DeferredItem<BlockItem> IRIDIUM_COIL = ITEMS.registerSimpleBlockItem(ModBlocks.IRIDIUM_COIL);

    // ── Spent Fuel Storage block items ────────────────────────────────────────
    public static final DeferredItem<BlockItem> SPENT_FUEL_STORAGE_BASE       = ITEMS.registerSimpleBlockItem(ModBlocks.SPENT_FUEL_STORAGE_BASE);
    public static final DeferredItem<BlockItem> SPENT_FUEL_STORAGE_WALL       = ITEMS.registerSimpleBlockItem(ModBlocks.SPENT_FUEL_STORAGE_WALL);
    public static final DeferredItem<BlockItem> SPENT_FUEL_STORAGE_CONTROLLER = ITEMS.registerSimpleBlockItem(ModBlocks.SPENT_FUEL_STORAGE_CONTROLLER);
}