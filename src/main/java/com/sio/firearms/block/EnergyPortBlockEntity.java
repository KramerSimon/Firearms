package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class EnergyPortBlockEntity extends EnergyStorageBlock {

    private static final int CAPACITY = 2_000;
    private static final int MAX_RECEIVE = 1_000;
    private static final int MAX_BFS_DEPTH = 20;
    private static final int RESCAN_INTERVAL = 100;

    private int tickCount = 0;
    private BlockPos cachedControllerPos = null;

    public EnergyPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_PORT.get(), pos, state, CAPACITY, MAX_RECEIVE, MAX_RECEIVE);
    }

    public void serverTick() {
        if (level == null || energy.getEnergyStored() <= 0) return;

        tickCount++;
        if (tickCount % RESCAN_INTERVAL == 0) {
            cachedControllerPos = findControllerBFS();
        }

        if (cachedControllerPos == null) return;

        BlockEntity be = level.getBlockEntity(cachedControllerPos);
        if (be == null) {
            cachedControllerPos = null;
            return;
        }

        IEnergyStorage target = getControllerEnergy(be);
        if (target != null && target.canReceive()) {
            int toExtract = energy.extractEnergy(MAX_RECEIVE, true);
            int received = target.receiveEnergy(toExtract, false);
            if (received > 0) {
                energy.extractEnergy(received, false);
                setChanged();
            }
        }
    }

    private IEnergyStorage getControllerEnergy(BlockEntity be) {
        if (be instanceof OilDerrickControllerBlockEntity derrick) return derrick.getEnergyStorage();
        if (be instanceof RefineryControllerBlockEntity refinery) return refinery.getEnergyStorage();
        if (be instanceof EBFControllerBlockEntity ebf) return ebf.getEnergyStorage();
        return null;
    }

    private BlockPos findControllerBFS() {
        if (level == null) return null;

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        visited.add(worldPosition);
        queue.add(worldPosition);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (visited.contains(neighbor)) continue;
                if (neighbor.distManhattan(worldPosition) > MAX_BFS_DEPTH) continue;
                visited.add(neighbor);

                BlockEntity be = level.getBlockEntity(neighbor);
                if (be instanceof OilDerrickControllerBlockEntity
                        || be instanceof RefineryControllerBlockEntity
                        || be instanceof EBFControllerBlockEntity) {
                    return neighbor;
                }

                if (isStructureBlock(level.getBlockState(neighbor).getBlock())) {
                    queue.add(neighbor);
                }
            }
        }
        return null;
    }

    private static boolean isStructureBlock(Block block) {
        return block == ModBlocks.OIL_DERRICK_BASE.get()
                || block == ModBlocks.OIL_DERRICK_PILLAR.get()
                || block == ModBlocks.OIL_DERRICK_CONTROLLER.get()
                || block == ModBlocks.REFINERY_BASE.get()
                || block == ModBlocks.REFINERY_WALL.get()
                || block == ModBlocks.REFINERY_TOP.get()
                || block == ModBlocks.REFINERY_CONTROLLER.get()
                || block == ModBlocks.EBF_BASE.get()
                || block == ModBlocks.EBF_WALL.get()
                || block == ModBlocks.EBF_TOP.get()
                || block == ModBlocks.EBF_CONTROLLER.get()
                || block == ModBlocks.ENERGY_PORT.get()
                || block == ModBlocks.FLUID_PORT.get();
    }
}
