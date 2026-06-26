package com.sio.firearms.world;

import com.sio.firearms.Firearms;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootTable;

public class MilitaryBunkerStructure extends Feature<NoneFeatureConfiguration> {

    private static final ResourceKey<LootTable> BUNKER_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "chests/military_bunker"));

    public MilitaryBunkerStructure() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        // corner is the NW bottom corner; origin is the floor-center of the 9×9×5 room
        BlockPos corner = context.origin().offset(-4, 0, -4);

        BlockState stone  = Blocks.SMOOTH_STONE.defaultBlockState();
        BlockState bars   = Blocks.IRON_BARS.defaultBlockState();
        BlockState mossy  = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        BlockState air    = Blocks.AIR.defaultBlockState();

        // ── 1. Clear interior (x=1-7, y=1-3, z=1-7) ─────────────────────────
        for (int x = 1; x <= 7; x++)
            for (int y = 1; y <= 3; y++)
                for (int z = 1; z <= 7; z++)
                    level.setBlock(corner.offset(x, y, z), air, 2);

        // ── 2. Floor (y=0) and ceiling (y=4) ─────────────────────────────────
        for (int x = 0; x <= 8; x++)
            for (int z = 0; z <= 8; z++) {
                level.setBlock(corner.offset(x, 0, z), stone, 2);
                level.setBlock(corner.offset(x, 4, z), stone, 2);
            }

        // ── 3. Perimeter walls ────────────────────────────────────────────────
        for (int y = 1; y <= 3; y++) {
            for (int x = 0; x <= 8; x++) {
                level.setBlock(corner.offset(x, y, 0), stone, 2);
                level.setBlock(corner.offset(x, y, 8), stone, 2);
            }
            for (int z = 1; z <= 7; z++) {
                level.setBlock(corner.offset(0, y, z), stone, 2);
                level.setBlock(corner.offset(8, y, z), stone, 2);
            }
        }

        // ── 4. Iron-bar ventilation grates (y=2 in each wall) ────────────────
        for (int x : new int[]{2, 5}) {
            level.setBlock(corner.offset(x, 2, 0), bars, 2);
            level.setBlock(corner.offset(x, 2, 8), bars, 2);
        }
        for (int z : new int[]{2, 5}) {
            level.setBlock(corner.offset(0, 2, z), bars, 2);
            level.setBlock(corner.offset(8, 2, z), bars, 2);
        }

        // ── 5. Entrance opening (south wall z=8, centered at x=4) ─────────────
        level.setBlock(corner.offset(4, 1, 8), air, 2);
        level.setBlock(corner.offset(4, 2, 8), air, 2);

        BlockState doorBottom = Blocks.IRON_DOOR.defaultBlockState()
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT)
                .setValue(DoorBlock.OPEN, false);
        BlockState doorTop = doorBottom.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
        level.setBlock(corner.offset(4, 1, 8), doorBottom, 2);
        level.setBlock(corner.offset(4, 2, 8), doorTop, 2);

        // Stone pressure plate just inside the entrance
        level.setBlock(corner.offset(4, 1, 7), Blocks.STONE_PRESSURE_PLATE.defaultBlockState(), 2);

        // ── 6. Mossy cobblestone pillars at interior corners ──────────────────
        for (int[] p : new int[][]{{2, 2}, {2, 6}, {6, 2}, {6, 6}}) {
            level.setBlock(corner.offset(p[0], 1, p[1]), mossy, 2);
            level.setBlock(corner.offset(p[0], 2, p[1]), mossy, 2);
        }

        // ── 7. Lanterns on top of pillars (y=3) ───────────────────────────────
        BlockState lantern = Blocks.LANTERN.defaultBlockState()
                .setValue(LanternBlock.HANGING, false);
        for (int[] p : new int[][]{{2, 2}, {2, 6}, {6, 2}, {6, 6}}) {
            level.setBlock(corner.offset(p[0], 3, p[1]), lantern, 2);
        }

        // ── 8. Two chests with loot ───────────────────────────────────────────
        placeChest(level, corner.offset(2, 1, 3), random);
        placeChest(level, corner.offset(6, 1, 5), random);

        // ── 9. Cobwebs for atmosphere ──────────────────────────────────────────
        for (int[] spot : new int[][]{{1, 3, 1}, {7, 3, 1}, {1, 3, 7}, {7, 3, 7}, {3, 3, 4}, {5, 3, 4}}) {
            if (random.nextFloat() < 0.6f)
                level.setBlock(corner.offset(spot[0], spot[1], spot[2]), Blocks.COBWEB.defaultBlockState(), 2);
        }

        return true;
    }

    private void placeChest(WorldGenLevel level, BlockPos pos, RandomSource random) {
        level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 2);
        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            chest.setLootTable(BUNKER_LOOT, random.nextLong());
        }
    }
}
