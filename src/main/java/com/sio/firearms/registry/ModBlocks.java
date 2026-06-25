package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.AssemblyBenchBlock;
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
import com.sio.firearms.block.GunsmithTableBlock;
import com.sio.firearms.block.MetalPressBlock;
import com.sio.firearms.block.WireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Firearms.MOD_ID);

    public static final DeferredBlock<Block> GUNSMITH_TABLE =
            BLOCKS.register("gunsmith_table",
                    () -> new GunsmithTableBlock(BlockBehaviour.Properties.of()
                            .strength(2.5f)
                            .requiresCorrectToolForDrops()));

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
}
