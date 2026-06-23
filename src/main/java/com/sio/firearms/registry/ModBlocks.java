package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.GunsmithTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Firearms.MOD_ID);

    public static final DeferredBlock<Block> GUNSMITH_TABLE =
            BLOCKS.register("gunsmith_table",
                    () -> new GunsmithTableBlock(BlockBehaviour.Properties.of()
                            .strength(2.5f)
                            .requiresCorrectToolForDrops()));
}
