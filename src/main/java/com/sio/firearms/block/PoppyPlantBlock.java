package com.sio.firearms.block;

import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class PoppyPlantBlock extends CropBlock {

    // AGE_3 (0-3) overrides the parent CropBlock.AGE (AGE_7, 0-7).
    // We must also override createBlockStateDefinition() because Java static
    // fields are not polymorphic — CropBlock.createBlockStateDefinition() always
    // adds CropBlock.AGE (AGE_7), causing a property-not-found crash at runtime.
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    public PoppyPlantBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(AGE);  // registers AGE_3, not the parent's AGE_7
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.POPPY_SEEDS.get();
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.FARMLAND)
            || state.is(Blocks.GRASS_BLOCK)
            || state.is(Blocks.DIRT);
    }
}