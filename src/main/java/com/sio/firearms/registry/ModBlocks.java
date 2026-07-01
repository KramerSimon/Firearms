package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.AcidBathBlock;
import com.sio.firearms.block.CoolingTowerControllerBlock;
import com.sio.firearms.block.FluidTankBlock;
import com.sio.firearms.block.ReactorControllerBlock;
import com.sio.firearms.block.SteamTurbineBlock;
import com.sio.firearms.block.GasCentrifugeBlock;
import com.sio.firearms.block.CrystalGrowthControllerBlock;
import com.sio.firearms.block.EuvLithographyControllerBlock;
import com.sio.firearms.block.WaferCuttingMachineBlock;
import com.sio.firearms.block.DepositionChamberBlock;
import com.sio.firearms.block.PlasmaEtcherBlock;
import com.sio.firearms.block.IonImplanterBlock;
import com.sio.firearms.block.MetallizationChamberBlock;
import com.sio.firearms.block.WaferTesterBlock;
import com.sio.firearms.block.DicingSawBlock;
import com.sio.firearms.block.ChipPackagingMachineBlock;
import com.sio.firearms.block.CoilBlock;
import com.sio.firearms.block.AssemblyBenchBlock;
import com.sio.firearms.block.ChemicalMixerBlock;
import com.sio.firearms.block.ChemicalMixerControllerBlock;
import com.sio.firearms.block.EBFControllerBlock;
import com.sio.firearms.block.EbfImportBusBlock;
import com.sio.firearms.block.EbfOutputBusBlock;
import com.sio.firearms.block.ElectrolysisMachineBlock;
import com.sio.firearms.block.ItemPipeBlock;
import com.sio.firearms.block.UraniumOreBlock;
import com.sio.firearms.block.WaterPumpBlock;
import com.sio.firearms.block.WeaponRackBlock;
import com.sio.firearms.block.LandMineBlock;
import com.sio.firearms.block.AutoTurretBlock;
import com.sio.firearms.block.CoalGeneratorBlock;
import com.sio.firearms.block.EnergyPortBlock;
import com.sio.firearms.block.EnergyPylonBlock;
import com.sio.firearms.block.FluidPipeBlock;
import com.sio.firearms.block.FluidPortBlock;
import com.sio.firearms.block.FuelGeneratorBlock;
import com.sio.firearms.block.OilDerrickControllerBlock;
import com.sio.firearms.block.RefineryControllerBlock;
import com.sio.firearms.block.HeatTreatmentFurnaceBlock;
import com.sio.firearms.block.LatheBlock;
import com.sio.firearms.block.GunModificationTableBlock;
import com.sio.firearms.block.MetalPressBlock;
import com.sio.firearms.block.GarageDoorBlock;
import com.sio.firearms.block.VehicleGarageControllerBlock;
import com.sio.firearms.block.HangarDoorBlock;
import com.sio.firearms.block.HangarControllerBlock;
import com.sio.firearms.block.CrateBlock;
import com.sio.firearms.block.PoppyPlantBlock;
import com.sio.firearms.block.WireBlock;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Firearms.MOD_ID);

    public static final DeferredBlock<Block> METAL_PRESS =
            BLOCKS.register("metal_press",
                    () -> new MetalPressBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> GUN_MODIFICATION_TABLE =
            BLOCKS.register("gun_modification_table",
                    () -> new GunModificationTableBlock(BlockBehaviour.Properties.of()
                            .strength(2.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> COAL_GENERATOR =
            BLOCKS.register("coal_generator",
                    () -> new CoalGeneratorBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> HEAT_TREATMENT_FURNACE =
            BLOCKS.register("heat_treatment_furnace",
                    () -> new HeatTreatmentFurnaceBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> LATHE =
            BLOCKS.register("lathe",
                    () -> new LatheBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> ASSEMBLY_BENCH =
            BLOCKS.register("assembly_bench",
                    () -> new AssemblyBenchBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> ENERGY_PYLON =
            BLOCKS.register("energy_pylon",
                    () -> new EnergyPylonBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> COPPER_WIRE =
            BLOCKS.register("copper_wire",
                    () -> new WireBlock(BlockBehaviour.Properties.of()
                            .strength(0.5f)
                            .noOcclusion()));

    public static final DeferredBlock<Block> OIL_DERRICK_BASE =
            BLOCKS.register("oil_derrick_base",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> OIL_DERRICK_PILLAR =
            BLOCKS.register("oil_derrick_pillar",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> OIL_DERRICK_CONTROLLER =
            BLOCKS.register("oil_derrick_controller",
                    () -> new OilDerrickControllerBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> REFINERY_BASE =
            BLOCKS.register("refinery_base",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> REFINERY_WALL =
            BLOCKS.register("refinery_wall",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> REFINERY_TOP =
            BLOCKS.register("refinery_top",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> REFINERY_CONTROLLER =
            BLOCKS.register("refinery_controller",
                    () -> new RefineryControllerBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> FUEL_GENERATOR =
            BLOCKS.register("fuel_generator",
                    () -> new FuelGeneratorBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> AUTO_TURRET =
            BLOCKS.register("auto_turret",
                    () -> new AutoTurretBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> FLUID_PIPE =
            BLOCKS.register("fluid_pipe",
                    () -> new FluidPipeBlock(BlockBehaviour.Properties.of()
                            .strength(0.5f)
                            .noOcclusion()));

    public static final DeferredBlock<Block> ENERGY_PORT =
            BLOCKS.register("energy_port",
                    () -> new EnergyPortBlock(BlockBehaviour.Properties.of()
                            .strength(2.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> FLUID_PORT =
            BLOCKS.register("fluid_port",
                    () -> new FluidPortBlock(BlockBehaviour.Properties.of()
                            .strength(2.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> LAND_MINE =
            BLOCKS.register("land_mine",
                    () -> new LandMineBlock(BlockBehaviour.Properties.of()
                            .strength(0.5f)
                            .noOcclusion()
                            .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> COKE_OVEN_BRICK =
            BLOCKS.register("coke_oven_brick",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> COKE_OVEN_CONTROLLER =
            BLOCKS.register("coke_oven_controller",
                    () -> new com.sio.firearms.block.CokeOvenControllerBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    // Item input hatch — feeds material/additive into the formed furnace.
    public static final DeferredBlock<Block> EBF_IMPORT_BUS =
            BLOCKS.register("ebf_import_bus",
                    () -> new EbfImportBusBlock(BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()));

    // Item output hatch — extracts smelted results from the formed furnace.
    public static final DeferredBlock<Block> EBF_OUTPUT_BUS =
            BLOCKS.register("ebf_output_bus",
                    () -> new EbfOutputBusBlock(BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> EBF_CONTROLLER =
            BLOCKS.register("ebf_controller",
                    () -> new EBFControllerBlock(BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()
                            // Small green status indicators on the controller face.
                            .lightLevel(state -> 7)));

    // Unified dark-steel blast furnace casing used for the floor, outer walls and roof
    // of the 5x5x5 Electric Blast Furnace multiblock.
    public static final DeferredBlock<Block> BLAST_FURNACE_CASING =
            BLOCKS.register("blast_furnace_casing",
                    () -> new com.sio.firearms.block.EbfPartBlock(BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()));

    // Industrial exhaust / muffler hatch placed at the centre of the roof.
    public static final DeferredBlock<Block> MUFFLER_HATCH =
            BLOCKS.register("muffler_hatch",
                    () -> new com.sio.firearms.block.EbfPartBlock(BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> CHEMICAL_MIXER =
            BLOCKS.register("chemical_mixer",
                    () -> new ChemicalMixerBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> CHEMICAL_MIXER_BASE =
            BLOCKS.register("chemical_mixer_base",
                    () -> new com.sio.firearms.block.ChemicalMixerBaseBlock(BlockBehaviour.Properties.of()
                            .strength(5.0f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> CHEMICAL_MIXER_WALL =
            BLOCKS.register("chemical_mixer_wall",
                    () -> new com.sio.firearms.block.ChemicalMixerWallBlock(BlockBehaviour.Properties.of()
                            .strength(5.0f)
                            .noOcclusion()
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> CHEMICAL_MIXER_CONTROLLER =
            BLOCKS.register("chemical_mixer_controller",
                    () -> new ChemicalMixerControllerBlock(BlockBehaviour.Properties.of()
                            .strength(5.0f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> ACID_BATH =
            BLOCKS.register("acid_bath",
                    () -> new AcidBathBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> WATER_PUMP =
            BLOCKS.register("water_pump",
                    () -> new WaterPumpBlock(BlockBehaviour.Properties.of()
                            .strength(3.0f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> WEAPON_RACK =
            BLOCKS.register("weapon_rack",
                    () -> new WeaponRackBlock(BlockBehaviour.Properties.of()
                            .strength(2.0f)
                            .noOcclusion()));

    public static final DeferredBlock<Block> ITEM_PIPE =
            BLOCKS.register("item_pipe",
                    () -> new ItemPipeBlock(BlockBehaviour.Properties.of()
                            .strength(0.5f)
                            .noOcclusion()));

    // ── Ore blocks ───────────────────────────────────────────────────────────

    public static final DeferredBlock<Block> SULFUR_ORE =
            BLOCKS.register("sulfur_ore",
                    () -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.of()
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> SALTPETER_ORE =
            BLOCKS.register("saltpeter_ore",
                    () -> new DropExperienceBlock(UniformInt.of(0, 1), BlockBehaviour.Properties.of()
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> TUNGSTEN_ORE =
            BLOCKS.register("tungsten_ore",
                    () -> new DropExperienceBlock(UniformInt.of(1, 3), BlockBehaviour.Properties.of()
                            .strength(3.0f, 3.0f)
                            .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> FLUORITE_ORE =
            BLOCKS.register("fluorite_ore",
                    () -> new DropExperienceBlock(UniformInt.of(1, 2), BlockBehaviour.Properties.of()
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> URANIUM_ORE =
            BLOCKS.register("uranium_ore",
                    () -> new UraniumOreBlock(BlockBehaviour.Properties.of()
                            .strength(2.5f, 3.0f)
                            .sound(SoundType.STONE)
                            .lightLevel(state -> 2)));

    public static final DeferredBlock<Block> CHROMIUM_ORE =
            BLOCKS.register("chromium_ore",
                    () -> new DropExperienceBlock(UniformInt.of(0, 1), BlockBehaviour.Properties.of()
                            .strength(3.0f, 3.0f)
                            .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> NICKEL_ORE =
            BLOCKS.register("nickel_ore",
                    () -> new DropExperienceBlock(UniformInt.of(0, 1), BlockBehaviour.Properties.of()
                            .strength(3.0f, 3.0f)
                            .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> BAUXITE_ORE =
            BLOCKS.register("bauxite_ore",
                    () -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.of()
                            .strength(2.5f, 3.0f)
                            .sound(SoundType.STONE)));

    // ── Coil blocks ──────────────────────────────────────────────────────────

    // Coil light levels rise with tier so the inner chamber glows hotter:
    // Kanthal (orange) < Nichrome (orange-white) < Tungsten (white-yellow).
    public static final DeferredBlock<CoilBlock> KANTHAL_COIL =
            BLOCKS.register("kanthal_coil",
                    () -> new CoilBlock(800, BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()
                            .lightLevel(state -> 10)));

    public static final DeferredBlock<CoilBlock> NICHROME_COIL =
            BLOCKS.register("nichrome_coil",
                    () -> new CoilBlock(1200, BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()
                            .lightLevel(state -> 12)));

    public static final DeferredBlock<CoilBlock> TUNGSTEN_COIL =
            BLOCKS.register("tungsten_coil",
                    () -> new CoilBlock(2000, BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()
                            .lightLevel(state -> 15)));

    public static final DeferredBlock<LiquidBlock> CREOSOTE_OIL_FLUID =
            BLOCKS.register("creosote_oil_fluid",
                    () -> new LiquidBlock(ModFluids.CREOSOTE_OIL_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable()
                            .noCollission()
                            .strength(-1.0f)
                            .liquid()
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()));

    public static final DeferredBlock<LiquidBlock> OIL_FLUID =
            BLOCKS.register("oil_fluid",
                    () -> new LiquidBlock(ModFluids.OIL_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable()
                            .noCollission()
                            .strength(-1.0f)
                            .liquid()
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()));

    public static final DeferredBlock<LiquidBlock> FUEL_FLUID =
            BLOCKS.register("fuel_fluid",
                    () -> new LiquidBlock(ModFluids.FUEL_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable()
                            .noCollission()
                            .strength(-1.0f)
                            .liquid()
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()));

    public static final DeferredBlock<LiquidBlock> SULFURIC_ACID_FLUID =
            BLOCKS.register("sulfuric_acid_fluid",
                    () -> new LiquidBlock(ModFluids.SULFURIC_ACID_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable()
                            .noCollission()
                            .strength(-1.0f)
                            .liquid()
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()));

    public static final DeferredBlock<LiquidBlock> NITRIC_ACID_FLUID =
            BLOCKS.register("nitric_acid_fluid",
                    () -> new LiquidBlock(ModFluids.NITRIC_ACID_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable()
                            .noCollission()
                            .strength(-1.0f)
                            .liquid()
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()));

    public static final DeferredBlock<LiquidBlock> SYNTHETIC_RUBBER_FLUID =
            BLOCKS.register("synthetic_rubber_fluid",
                    () -> new LiquidBlock(ModFluids.SYNTHETIC_RUBBER_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable()
                            .noCollission()
                            .strength(-1.0f)
                            .liquid()
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()));

    // ── Electrolysis Machine ─────────────────────────────────────────────────

    public static final DeferredBlock<Block> ELECTROLYSIS_MACHINE =
            BLOCKS.register("electrolysis_machine",
                    () -> new ElectrolysisMachineBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    // ── New fluid blocks ─────────────────────────────────────────────────────

    public static final DeferredBlock<LiquidBlock> HYDROGEN_GAS_FLUID =
            BLOCKS.register("hydrogen_gas_fluid",
                    () -> new LiquidBlock(ModFluids.HYDROGEN_GAS_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));

    public static final DeferredBlock<LiquidBlock> OXYGEN_GAS_FLUID =
            BLOCKS.register("oxygen_gas_fluid",
                    () -> new LiquidBlock(ModFluids.OXYGEN_GAS_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));

    public static final DeferredBlock<LiquidBlock> FLUORINE_GAS_FLUID =
            BLOCKS.register("fluorine_gas_fluid",
                    () -> new LiquidBlock(ModFluids.FLUORINE_GAS_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));

    public static final DeferredBlock<LiquidBlock> CHLORINE_GAS_FLUID =
            BLOCKS.register("chlorine_gas_fluid",
                    () -> new LiquidBlock(ModFluids.CHLORINE_GAS_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));

    public static final DeferredBlock<LiquidBlock> NITRATE_SOLUTION_FLUID =
            BLOCKS.register("nitrate_solution_fluid",
                    () -> new LiquidBlock(ModFluids.NITRATE_SOLUTION_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));

    public static final DeferredBlock<LiquidBlock> PVC_RESIN_FLUID =
            BLOCKS.register("pvc_resin_fluid",
                    () -> new LiquidBlock(ModFluids.PVC_RESIN_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));

    // ── Distillation product fluid blocks ────────────────────────────────────
    public static final DeferredBlock<LiquidBlock> BUTANE_FLUID =
            BLOCKS.register("butane_fluid",
                    () -> new LiquidBlock(ModFluids.BUTANE_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));
    public static final DeferredBlock<LiquidBlock> GASOLINE_FLUID =
            BLOCKS.register("gasoline_fluid",
                    () -> new LiquidBlock(ModFluids.GASOLINE_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));
    public static final DeferredBlock<LiquidBlock> NAPHTHA_FLUID =
            BLOCKS.register("naphtha_fluid",
                    () -> new LiquidBlock(ModFluids.NAPHTHA_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));
    public static final DeferredBlock<LiquidBlock> KEROSENE_FLUID =
            BLOCKS.register("kerosene_fluid",
                    () -> new LiquidBlock(ModFluids.KEROSENE_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));
    public static final DeferredBlock<LiquidBlock> DIESEL_FLUID =
            BLOCKS.register("diesel_fluid",
                    () -> new LiquidBlock(ModFluids.DIESEL_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));
    public static final DeferredBlock<LiquidBlock> HEAVY_GAS_OIL_FLUID =
            BLOCKS.register("heavy_gas_oil_fluid",
                    () -> new LiquidBlock(ModFluids.HEAVY_GAS_OIL_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));
    public static final DeferredBlock<LiquidBlock> RESIDUAL_FUEL_OIL_FLUID =
            BLOCKS.register("residual_fuel_oil_fluid",
                    () -> new LiquidBlock(ModFluids.RESIDUAL_FUEL_OIL_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));

    // ── Photoresist fluid block ──────────────────────────────────────────────
    public static final DeferredBlock<LiquidBlock> PHOTORESIST_FLUID =
            BLOCKS.register("photoresist_fluid",
                    () -> new LiquidBlock(ModFluids.PHOTORESIST_STILL.get(), BlockBehaviour.Properties.of()
                            .replaceable().noCollission().strength(-1.0f).liquid()
                            .pushReaction(PushReaction.DESTROY).noLootTable()));

    // ── Crystal Growth Chamber blocks ────────────────────────────────────────
    public static final DeferredBlock<Block> CRYSTAL_GROWTH_BASE =
            BLOCKS.register("crystal_growth_base",
                    () -> new Block(BlockBehaviour.Properties.of().strength(4.0f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CRYSTAL_GROWTH_WALL =
            BLOCKS.register("crystal_growth_wall",
                    () -> new Block(BlockBehaviour.Properties.of().strength(4.0f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CRYSTAL_GROWTH_TOP =
            BLOCKS.register("crystal_growth_top",
                    () -> new Block(BlockBehaviour.Properties.of().strength(4.0f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CRYSTAL_GROWTH_CONTROLLER =
            BLOCKS.register("crystal_growth_controller",
                    () -> new CrystalGrowthControllerBlock(BlockBehaviour.Properties.of().strength(4.0f).requiresCorrectToolForDrops()));

    // ── EUV Lithography Machine blocks ───────────────────────────────────────
    public static final DeferredBlock<Block> EUV_BASE =
            BLOCKS.register("euv_base",
                    () -> new Block(BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> EUV_WALL =
            BLOCKS.register("euv_wall",
                    () -> new Block(BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> EUV_LENS_HOUSING =
            BLOCKS.register("euv_lens_housing",
                    () -> new Block(BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> EUV_MIRROR_ARRAY =
            BLOCKS.register("euv_mirror_array",
                    () -> new Block(BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> EUV_EMITTER_HOUSING =
            BLOCKS.register("euv_emitter_housing",
                    () -> new Block(BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> EUV_LITHOGRAPHY_CONTROLLER =
            BLOCKS.register("euv_lithography_controller",
                    () -> new EuvLithographyControllerBlock(BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops()));

    // ── Stage 2 microchip fabrication machines ───────────────────────────────
    public static final DeferredBlock<Block> WAFER_CUTTING_MACHINE =
            BLOCKS.register("wafer_cutting_machine",
                    () -> new WaferCuttingMachineBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DEPOSITION_CHAMBER =
            BLOCKS.register("deposition_chamber",
                    () -> new DepositionChamberBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> PLASMA_ETCHER =
            BLOCKS.register("plasma_etcher",
                    () -> new PlasmaEtcherBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> ION_IMPLANTER =
            BLOCKS.register("ion_implanter",
                    () -> new IonImplanterBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> METALLIZATION_CHAMBER =
            BLOCKS.register("metallization_chamber",
                    () -> new MetallizationChamberBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> WAFER_TESTER =
            BLOCKS.register("wafer_tester",
                    () -> new WaferTesterBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DICING_SAW =
            BLOCKS.register("dicing_saw",
                    () -> new DicingSawBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CHIP_PACKAGING_MACHINE =
            BLOCKS.register("chip_packaging_machine",
                    () -> new ChipPackagingMachineBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f).requiresCorrectToolForDrops()));

    // ── Nuclear Reactor Stage 1 ──────────────────────────────────────────────
    public static final DeferredBlock<Block> ZIRCONITE_ORE =
            BLOCKS.register("zirconite_ore",
                    () -> new DropExperienceBlock(UniformInt.of(1, 3),
                            BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.STONE)
                                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> GAS_CENTRIFUGE =
            BLOCKS.register("gas_centrifuge",
                    () -> new GasCentrifugeBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f).requiresCorrectToolForDrops()));

    // ── Nuclear fluid blocks ──────────────────────────────────────────────────
    public static final DeferredBlock<LiquidBlock> URANIUM_HEXAFLUORIDE_FLUID =
            BLOCKS.register("uranium_hexafluoride_fluid",
                    () -> new LiquidBlock(ModFluids.URANIUM_HEXAFLUORIDE_STILL.get(),
                            BlockBehaviour.Properties.of().noCollission().strength(100f).noLootTable()
                                    .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<LiquidBlock> ENRICHED_UF6_FLUID =
            BLOCKS.register("enriched_uf6_fluid",
                    () -> new LiquidBlock(ModFluids.ENRICHED_UF6_STILL.get(),
                            BlockBehaviour.Properties.of().noCollission().strength(100f).noLootTable()
                                    .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<LiquidBlock> DEPLETED_UF6_FLUID =
            BLOCKS.register("depleted_uf6_fluid",
                    () -> new LiquidBlock(ModFluids.DEPLETED_UF6_STILL.get(),
                            BlockBehaviour.Properties.of().noCollission().strength(100f).noLootTable()
                                    .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<LiquidBlock> HEAVY_WATER_FLUID =
            BLOCKS.register("heavy_water_fluid",
                    () -> new LiquidBlock(ModFluids.HEAVY_WATER_STILL.get(),
                            BlockBehaviour.Properties.of().noCollission().strength(100f).noLootTable()
                                    .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<LiquidBlock> STEAM_FLUID =
            BLOCKS.register("steam_fluid",
                    () -> new LiquidBlock(ModFluids.STEAM_STILL.get(),
                            BlockBehaviour.Properties.of().noCollission().strength(100f).noLootTable()
                                    .pushReaction(PushReaction.DESTROY)));

    // ── Fluid storage ────────────────────────────────────────────────────────
    public static final DeferredBlock<Block> FLUID_TANK =
            BLOCKS.register("fluid_tank",
                    () -> new FluidTankBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    // ── Nuclear Reactor Stage 2 ──────────────────────────────────────────────
    public static final DeferredBlock<Block> REACTOR_BASE =
            BLOCKS.register("reactor_base",
                    () -> new com.sio.firearms.block.ReactorBaseBlock(BlockBehaviour.Properties.of()
                            .strength(10.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> REACTOR_WALL =
            BLOCKS.register("reactor_wall",
                    () -> new com.sio.firearms.block.ReactorWallBlock(BlockBehaviour.Properties.of()
                            .strength(8.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> REACTOR_TOP =
            BLOCKS.register("reactor_top",
                    () -> new com.sio.firearms.block.ReactorTopBlock(BlockBehaviour.Properties.of()
                            .strength(8.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> REACTOR_CONTROL_ROD_HOUSING =
            BLOCKS.register("reactor_control_rod_housing",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(8.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> LEAD_BLOCK =
            BLOCKS.register("lead_block",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(3.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> REACTOR_CONTROLLER =
            BLOCKS.register("reactor_controller",
                    () -> new ReactorControllerBlock(BlockBehaviour.Properties.of()
                            .strength(8.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> STEAM_TURBINE =
            BLOCKS.register("steam_turbine",
                    () -> new SteamTurbineBlock(BlockBehaviour.Properties.of()
                            .strength(5.0f)
                            .requiresCorrectToolForDrops()));

    // ── Cooling Tower ────────────────────────────────────────────────────────
    public static final DeferredBlock<Block> COOLING_TOWER_BASE =
            BLOCKS.register("cooling_tower_base",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(8.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> COOLING_TOWER_WALL =
            BLOCKS.register("cooling_tower_wall",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(6.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> COOLING_TOWER_VENT =
            BLOCKS.register("cooling_tower_vent",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> COOLING_TOWER_CONTROLLER =
            BLOCKS.register("cooling_tower_controller",
                    () -> new CoolingTowerControllerBlock(BlockBehaviour.Properties.of()
                            .strength(6.0f)
                            .requiresCorrectToolForDrops()));

    // ── Vehicle Garage ───────────────────────────────────────────────────────
    public static final DeferredBlock<Block> GARAGE_FLOOR =
            BLOCKS.register("garage_floor",
                    () -> new com.sio.firearms.block.GarageFloorBlock(BlockBehaviour.Properties.of()
                            .strength(8.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> GARAGE_WALL =
            BLOCKS.register("garage_wall",
                    () -> new com.sio.firearms.block.GarageWallBlock(BlockBehaviour.Properties.of()
                            .strength(8.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> GARAGE_ROOF =
            BLOCKS.register("garage_roof",
                    () -> new com.sio.firearms.block.GarageRoofBlock(BlockBehaviour.Properties.of()
                            .strength(8.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> GARAGE_DOOR =
            BLOCKS.register("garage_door",
                    () -> new GarageDoorBlock(BlockBehaviour.Properties.of()
                            .strength(6.0f)
                            .noOcclusion()
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> GARAGE_CONTROLLER =
            BLOCKS.register("garage_controller",
                    () -> new VehicleGarageControllerBlock(BlockBehaviour.Properties.of()
                            .strength(6.0f)
                            .requiresCorrectToolForDrops()));

    // ── Aircraft Hangar ───────────────────────────────────────────────────────
    public static final DeferredBlock<Block> HANGAR_FLOOR =
            BLOCKS.register("hangar_floor",
                    () -> new com.sio.firearms.block.HangarFloorBlock(BlockBehaviour.Properties.of()
                            .strength(8.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> HANGAR_WALL =
            BLOCKS.register("hangar_wall",
                    () -> new com.sio.firearms.block.HangarWallBlock(BlockBehaviour.Properties.of()
                            .strength(7.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> HANGAR_ROOF =
            BLOCKS.register("hangar_roof",
                    () -> new com.sio.firearms.block.HangarRoofBlock(BlockBehaviour.Properties.of()
                            .strength(7.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> HANGAR_DOOR =
            BLOCKS.register("hangar_door",
                    () -> new HangarDoorBlock(BlockBehaviour.Properties.of()
                            .strength(7.0f)
                            .noOcclusion()
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> HANGAR_CONTROLLER =
            BLOCKS.register("hangar_controller",
                    () -> new HangarControllerBlock(BlockBehaviour.Properties.of()
                            .strength(6.0f)
                            .requiresCorrectToolForDrops()));

    // ── Utility ───────────────────────────────────────────────────────────────
    public static final DeferredBlock<Block> CRATE =
            BLOCKS.register("crate",
                    () -> new CrateBlock(BlockBehaviour.Properties.of()
                            .strength(2.5f)
                            .sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> TRASH_CAN =
            BLOCKS.register("trash_can",
                    () -> new com.sio.firearms.block.TrashCanBlock(BlockBehaviour.Properties.of()
                            .strength(2.0f)
                            .requiresCorrectToolForDrops()));

    // ── New ore blocks ────────────────────────────────────────────────────────

    public static final DeferredBlock<Block> TITANIUM_ORE =
            BLOCKS.register("titanium_ore",
                    () -> new DropExperienceBlock(UniformInt.of(1, 2), BlockBehaviour.Properties.of()
                            .strength(3.0f, 3.0f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> IRIDIUM_ORE =
            BLOCKS.register("iridium_ore",
                    () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of()
                            .strength(4.0f, 4.0f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> OSMIUM_ORE =
            BLOCKS.register("osmium_ore",
                    () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of()
                            .strength(4.0f, 4.0f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<CoilBlock> IRIDIUM_COIL =
            BLOCKS.register("iridium_coil",
                    () -> new CoilBlock(5000, BlockBehaviour.Properties.of()
                            .strength(5.0f)
                            .requiresCorrectToolForDrops()
                            .lightLevel(state -> 15)));

    // ── Pharmaceutical ────────────────────────────────────────────────────────
    public static final DeferredBlock<Block> POPPY_PLANT =
            BLOCKS.register("poppy_plant",
                    () -> new PoppyPlantBlock(BlockBehaviour.Properties.of()
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .sound(SoundType.CROP)
                            .noOcclusion()
                            .pushReaction(PushReaction.DESTROY)));

    // ── New ore blocks ────────────────────────────────────────────────────────

    public static final DeferredBlock<Block> NEODYMIUM_ORE =
            BLOCKS.register("neodymium_ore",
                    () -> new DropExperienceBlock(UniformInt.of(1, 2), BlockBehaviour.Properties.of()
                            .strength(3.0f, 3.0f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()));

    // ── Spent Fuel Storage ────────────────────────────────────────────────────
    public static final DeferredBlock<Block> SPENT_FUEL_STORAGE_BASE =
            BLOCKS.register("spent_fuel_storage_base",
                    () -> new com.sio.firearms.block.SpentFuelStorageBaseBlock(BlockBehaviour.Properties.of()
                            .strength(8.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SPENT_FUEL_STORAGE_WALL =
            BLOCKS.register("spent_fuel_storage_wall",
                    () -> new com.sio.firearms.block.SpentFuelStorageWallBlock(BlockBehaviour.Properties.of()
                            .strength(8.0f)
                            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SPENT_FUEL_STORAGE_CONTROLLER =
            BLOCKS.register("spent_fuel_storage_controller",
                    () -> new com.sio.firearms.block.SpentFuelStorageControllerBlock(BlockBehaviour.Properties.of()
                            .strength(8.0f)
                            .requiresCorrectToolForDrops()));
}
