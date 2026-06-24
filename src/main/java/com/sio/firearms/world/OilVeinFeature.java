package com.sio.firearms.world;

import com.sio.firearms.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.FlowingFluid;

public class OilVeinFeature extends Feature<NoneFeatureConfiguration> {

    public OilVeinFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        FlowingFluid oil = ModFluids.OIL_STILL.get();
        if (oil == null) return false;

        BlockState oilBlock = oil.defaultFluidState().createLegacyBlock();
        if (oilBlock.isAir()) return false;

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int radius = 4 + random.nextInt(4);
        boolean placed = false;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        BlockPos pos = origin.offset(x, y, z);
                        if (level.getBlockState(pos).canBeReplaced()) {
                            level.setBlock(pos, oilBlock, 2);
                            placed = true;
                        }
                    }
                }
            }
        }
        return placed;
    }
}
