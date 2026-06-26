package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.AcidBathBlock;
import com.sio.firearms.block.AssemblyBenchBlock;
import com.sio.firearms.block.ChemicalMixerBlock;
import com.sio.firearms.block.EBFControllerBlock;
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

    public static final DeferredBlock<Block> EBF_BASE =
            BLOCKS.register("ebf_base",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> EBF_WALL =
            BLOCKS.register("ebf_wall",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> EBF_TOP =
            BLOCKS.register("ebf_top",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> EBF_CONTROLLER =
            BLOCKS.register("ebf_controller",
                    () -> new EBFControllerBlock(BlockBehaviour.Properties.of()
                            .strength(4.0f)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> CHEMICAL_MIXER =
            BLOCKS.register("chemical_mixer",
                    () -> new ChemicalMixerBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f)
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
}
