package com.sio.firearms.block;

import com.sio.firearms.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Shared helpers for treating an assembled Electric Blast Furnace as a single machine:
 * any part of the shell can flood-fill across connected structure blocks to reach the
 * one controller that owns it. Used by the passive parts (casing / coil / muffler) and
 * the item hatches so the whole 5x5x5 behaves like one accessible entity.
 */
public final class EbfStructure {

    private static final int MAX_BFS_DEPTH = 12;

    private EbfStructure() {}

    /** Flood-fill from {@code origin} over connected EBF blocks; returns the controller, or null. */
    public static EBFControllerBlockEntity findController(Level level, BlockPos origin) {
        if (level == null) return null;

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos nb = current.relative(dir);
                if (!visited.add(nb)) continue;
                if (nb.distManhattan(origin) > MAX_BFS_DEPTH) continue;

                if (level.getBlockEntity(nb) instanceof EBFControllerBlockEntity controller) return controller;
                if (isStructureBlock(level.getBlockState(nb).getBlock())) queue.add(nb);
            }
        }
        return null;
    }

    public static boolean isStructureBlock(Block block) {
        return block == ModBlocks.BLAST_FURNACE_CASING.get()
                || block == ModBlocks.MUFFLER_HATCH.get()
                || block == ModBlocks.KANTHAL_COIL.get()
                || block == ModBlocks.NICHROME_COIL.get()
                || block == ModBlocks.TUNGSTEN_COIL.get()
                || block == ModBlocks.EBF_CONTROLLER.get()
                || block == ModBlocks.EBF_IMPORT_BUS.get()
                || block == ModBlocks.EBF_OUTPUT_BUS.get()
                || block == ModBlocks.ENERGY_PORT.get()
                || block == ModBlocks.FLUID_PORT.get();
    }
}
