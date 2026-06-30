package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class EnergyPortBlockEntity extends EnergyStorageBlock {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int CAPACITY       = 10_000;
    private static final int MAX_TRANSFER   = 2_000;
    private static final int MAX_BFS_DEPTH  = 20;
    private static final int RESCAN_INTERVAL = 100;

    private int tickCount = 0;
    private BlockPos cachedControllerPos = null;

    public EnergyPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_PORT.get(), pos, state, CAPACITY, MAX_TRANSFER, MAX_TRANSFER);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EnergyPortBlockEntity be) {
        be.tick();
    }

    public void tick() {
        if (level == null) return;
        tickCount++;

        if (tickCount % RESCAN_INTERVAL == 0) {
            cachedControllerPos = findControllerBFS();
        }

        boolean verbose = (tickCount % 40 == 0);
        if (verbose) {
            LOGGER.debug("[EnergyPort@{}] tick #{} — buffer={}/{}FE  controller={}",
                    worldPosition.toShortString(), tickCount,
                    energy.getEnergyStored(), CAPACITY,
                    cachedControllerPos != null ? cachedControllerPos.toShortString() : "none");
        }

        // Validate cached controller
        if (cachedControllerPos != null) {
            BlockEntity be = level.getBlockEntity(cachedControllerPos);
            if (be == null) {
                LOGGER.debug("[EnergyPort@{}] cached controller at {} is now null — clearing",
                        worldPosition.toShortString(), cachedControllerPos.toShortString());
                cachedControllerPos = null;
            } else if (!isValidController(be)) {
                LOGGER.warn("[EnergyPort@{}] cached pos {} holds unexpected BE type {} — clearing",
                        worldPosition.toShortString(), cachedControllerPos.toShortString(),
                        be.getClass().getName());
                cachedControllerPos = null;
            } else if (be instanceof CoolingTowerControllerBlockEntity) {
                // SOURCE mode: extract from CT into buffer this tick
                IEnergyStorage source = level.getCapability(Capabilities.EnergyStorage.BLOCK, cachedControllerPos, null);
                if (source != null && source.canExtract()) {
                    int room = CAPACITY - energy.getEnergyStored();
                    if (room > 0) {
                        int extracted = source.extractEnergy(Math.min(MAX_TRANSFER, room), false);
                        if (extracted > 0) {
                            energy.receiveEnergy(extracted, false);
                            setChanged();
                            LOGGER.debug("[EnergyPort@{}] extracted {}FE from CT@{} (CT={}FE port={}FE)",
                                    worldPosition.toShortString(), extracted,
                                    cachedControllerPos.toShortString(), source.getEnergyStored(),
                                    energy.getEnergyStored());
                        }
                    }
                }
            } else {
                // CONSUMER mode: push buffer into machine
                if (energy.getEnergyStored() > 0) {
                    IEnergyStorage target = getControllerEnergy(be);
                    if (target != null && target.canReceive()) {
                        int toExtract = energy.extractEnergy(MAX_TRANSFER, true);
                        int received  = target.receiveEnergy(toExtract, false);
                        if (received > 0) {
                            energy.extractEnergy(received, false);
                            setChanged();
                        }
                    }
                }
                return; // CONSUMER mode does not forward to wire neighbors
            }
        }

        // SOURCE mode and no-controller: push buffer to all neighbors every tick
        if (energy.getEnergyStored() > 0) {
            pushBufferToNeighbors(cachedControllerPos, verbose);
        }
    }

    private void pushBufferToNeighbors(BlockPos skipPos, boolean verbose) {
        int totalPushed = 0;
        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() <= 0) break;
            BlockPos nb = worldPosition.relative(dir);
            if (skipPos != null && nb.equals(skipPos)) continue;
            IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, nb, dir.getOpposite());
            if (cap == null || !cap.canReceive()) continue;
            int toSend = energy.extractEnergy(MAX_TRANSFER, true);
            if (toSend <= 0) break;
            int accepted = cap.receiveEnergy(toSend, false);
            if (accepted > 0) {
                energy.extractEnergy(accepted, false);
                setChanged();
                totalPushed += accepted;
                if (verbose) {
                    LOGGER.debug("[EnergyPort@{}] pushed {}FE → {} at {} [port={}FE]",
                            worldPosition.toShortString(), accepted, dir, nb.toShortString(),
                            energy.getEnergyStored());
                } else {
                    LOGGER.debug("[EnergyPort@{}] pushed {}FE → {} at {}",
                            worldPosition.toShortString(), accepted, dir, nb.toShortString());
                }
            }
        }
        if (verbose) {
            LOGGER.debug("[EnergyPort@{}] total pushed this tick: {}FE, buffer remaining: {}FE",
                    worldPosition.toShortString(), totalPushed, energy.getEnergyStored());
        }
    }

    private IEnergyStorage getControllerEnergy(BlockEntity be) {
        if (be instanceof OilDerrickControllerBlockEntity derrick) return derrick.getEnergyStorage();
        if (be instanceof RefineryControllerBlockEntity refinery) return refinery.getEnergyStorage();
        if (be instanceof EBFControllerBlockEntity ebf) return ebf.getEnergyStorage();
        if (be instanceof CrystalGrowthControllerBlockEntity cgc) return cgc.getEnergyStorage();
        if (be instanceof EuvLithographyControllerBlockEntity euv) return euv.getEnergyStorage();
        if (be instanceof ReactorControllerBlockEntity reactor) return reactor.getEnergyStorage();
        if (be instanceof VehicleGarageControllerBlockEntity garage) return garage.getEnergyStorage();
        if (be instanceof ChemicalMixerControllerBlockEntity ctrl) return ctrl.getEnergyStorage();
        return null;
    }

    private static boolean isValidController(BlockEntity be) {
        return be instanceof CoolingTowerControllerBlockEntity
                || be instanceof OilDerrickControllerBlockEntity
                || be instanceof RefineryControllerBlockEntity
                || be instanceof EBFControllerBlockEntity
                || be instanceof CrystalGrowthControllerBlockEntity
                || be instanceof EuvLithographyControllerBlockEntity
                || be instanceof ReactorControllerBlockEntity
                || be instanceof VehicleGarageControllerBlockEntity
                || be instanceof ChemicalMixerControllerBlockEntity;
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
                if (be != null) {
                    LOGGER.debug("[EnergyPort BFS@{}] BE at {} (dist {}) = {}",
                            worldPosition.toShortString(), neighbor.toShortString(),
                            neighbor.distManhattan(worldPosition), be.getClass().getName());
                    if (isValidController(be)) {
                        LOGGER.info("[EnergyPort BFS@{}] accepted controller {} at {} (dist {})",
                                worldPosition.toShortString(), be.getClass().getSimpleName(),
                                neighbor.toShortString(), neighbor.distManhattan(worldPosition));
                        return neighbor;
                    } else {
                        LOGGER.debug("[EnergyPort BFS@{}] rejected non-controller BE {} at {}",
                                worldPosition.toShortString(), be.getClass().getSimpleName(),
                                neighbor.toShortString());
                    }
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
                || block == ModBlocks.BLAST_FURNACE_CASING.get()
                || block == ModBlocks.MUFFLER_HATCH.get()
                || block == ModBlocks.KANTHAL_COIL.get()
                || block == ModBlocks.NICHROME_COIL.get()
                || block == ModBlocks.TUNGSTEN_COIL.get()
                || block == ModBlocks.EBF_IMPORT_BUS.get()
                || block == ModBlocks.EBF_OUTPUT_BUS.get()
                || block == ModBlocks.EBF_CONTROLLER.get()
                || block == ModBlocks.CRYSTAL_GROWTH_BASE.get()
                || block == ModBlocks.CRYSTAL_GROWTH_WALL.get()
                || block == ModBlocks.CRYSTAL_GROWTH_TOP.get()
                || block == ModBlocks.CRYSTAL_GROWTH_CONTROLLER.get()
                || block == ModBlocks.EUV_BASE.get()
                || block == ModBlocks.EUV_WALL.get()
                || block == ModBlocks.EUV_LENS_HOUSING.get()
                || block == ModBlocks.EUV_MIRROR_ARRAY.get()
                || block == ModBlocks.EUV_EMITTER_HOUSING.get()
                || block == ModBlocks.EUV_LITHOGRAPHY_CONTROLLER.get()
                || block == ModBlocks.REACTOR_BASE.get()
                || block == ModBlocks.REACTOR_WALL.get()
                || block == ModBlocks.REACTOR_TOP.get()
                || block == ModBlocks.REACTOR_CONTROL_ROD_HOUSING.get()
                || block == ModBlocks.REACTOR_CONTROLLER.get()
                || block == ModBlocks.LEAD_BLOCK.get()
                || block == ModBlocks.STEAM_TURBINE.get()
                || block == ModBlocks.COOLING_TOWER_BASE.get()
                || block == ModBlocks.COOLING_TOWER_WALL.get()
                || block == ModBlocks.COOLING_TOWER_VENT.get()
                || block == ModBlocks.COOLING_TOWER_CONTROLLER.get()
                || block == ModBlocks.ENERGY_PORT.get()
                || block == ModBlocks.FLUID_PORT.get()
                || block == ModBlocks.GARAGE_FLOOR.get()
                || block == ModBlocks.GARAGE_WALL.get()
                || block == ModBlocks.GARAGE_ROOF.get()
                || block == ModBlocks.GARAGE_DOOR.get()
                || block == ModBlocks.GARAGE_CONTROLLER.get()
                || block == ModBlocks.CHEMICAL_MIXER_BASE.get()
                || block == ModBlocks.CHEMICAL_MIXER_WALL.get()
                || block == ModBlocks.CHEMICAL_MIXER_CONTROLLER.get();
    }
}
