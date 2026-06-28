package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class FluidPortBlockEntity extends BlockEntity {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CAPACITY = 2_000;
    private static final int MAX_TRANSFER = 100;
    private static final int MAX_BFS_DEPTH = 20;
    private static final int RESCAN_INTERVAL = 100;

    public enum Mode { INPUT, OUTPUT }

    private static final String[] FLUID_CYCLE = {
        "any", "butane", "gasoline", "naphtha", "kerosene",
        "diesel", "heavy_gas_oil", "residual_fuel_oil", "oil", "photoresist",
        "uranium_hexafluoride", "enriched_uf6", "depleted_uf6", "heavy_water"
    };
    private static final String[] FLUID_DISPLAY = {
        "Any", "Butane", "Gasoline", "Naphtha", "Kerosene",
        "Diesel", "Heavy Gas Oil", "Residual Fuel Oil", "Oil", "Photoresist",
        "Uranium Hexafluoride", "Enriched UF6", "Depleted UF6", "Heavy Water"
    };

    private final FluidTank tank = new FluidTank(CAPACITY);
    private int tickCount = 0;
    private int ticksSinceSearch = RESCAN_INTERVAL; // start at max so BFS fires on first tick
    private BlockPos cachedControllerPos = null;
    private Mode mode = Mode.INPUT;
    private String targetFluid = "any";

    private final IFluidHandler fillOnlyHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }

        @Override public FluidStack getFluidInTank(int t) { return tank.getFluidInTank(0); }

        @Override public int getTankCapacity(int t) { return tank.getTankCapacity(0); }

        @Override
        public boolean isFluidValid(int t, FluidStack stack) {
            if (!"any".equals(targetFluid)) {
                Fluid expected = getFluidByName(targetFluid);
                if (expected == null || !stack.getFluid().isSame(expected)) return false;
            }
            return true;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!"any".equals(targetFluid)) {
                Fluid expected = getFluidByName(targetFluid);
                if (expected == null || !resource.getFluid().isSame(expected)) return 0;
            }
            return tank.fill(resource, action);
        }

        @Override public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    };

    private final IFluidHandler drainOnlyHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int t) { return tank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t) { return tank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack stack) { return tank.isFluidValid(0, stack); }
        @Override public int fill(FluidStack resource, FluidAction action) { return 0; }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) { return tank.drain(resource, action); }
        @Override public FluidStack drain(int maxDrain, FluidAction action) { return tank.drain(maxDrain, action); }
    };

    public FluidPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_PORT.get(), pos, state);
    }

    public FluidTank getFluidTank()  { return tank; }
    public Mode getMode()            { return mode; }
    public String getTargetFluid()   { return targetFluid; }

    public String getTargetFluidDisplayName() {
        for (int i = 0; i < FLUID_CYCLE.length; i++) {
            if (FLUID_CYCLE[i].equals(targetFluid)) return FLUID_DISPLAY[i];
        }
        return targetFluid;
    }

    public void toggleMode() {
        mode = mode == Mode.INPUT ? Mode.OUTPUT : Mode.INPUT;
        setChanged();
    }

    public void cycleTargetFluid() {
        int idx = 0;
        for (int i = 0; i < FLUID_CYCLE.length; i++) {
            if (FLUID_CYCLE[i].equals(targetFluid)) { idx = i; break; }
        }
        targetFluid = FLUID_CYCLE[(idx + 1) % FLUID_CYCLE.length];
        setChanged();
    }

    public IFluidHandler getExposedHandler() {
        return mode == Mode.INPUT ? fillOnlyHandler : drainOnlyHandler;
    }

    private Fluid getFluidByName(String name) {
        return switch (name) {
            case "butane"            -> ModFluids.BUTANE_STILL.get();
            case "gasoline"          -> ModFluids.GASOLINE_STILL.get();
            case "naphtha"           -> ModFluids.NAPHTHA_STILL.get();
            case "kerosene"          -> ModFluids.KEROSENE_STILL.get();
            case "diesel"            -> ModFluids.DIESEL_STILL.get();
            case "heavy_gas_oil"     -> ModFluids.HEAVY_GAS_OIL_STILL.get();
            case "residual_fuel_oil" -> ModFluids.RESIDUAL_FUEL_OIL_STILL.get();
            case "oil"               -> ModFluids.OIL_STILL.get();
            case "photoresist"           -> ModFluids.PHOTORESIST_STILL.get();
            case "uranium_hexafluoride"  -> ModFluids.URANIUM_HEXAFLUORIDE_STILL.get();
            case "enriched_uf6"          -> ModFluids.ENRICHED_UF6_STILL.get();
            case "depleted_uf6"          -> ModFluids.DEPLETED_UF6_STILL.get();
            case "heavy_water"           -> ModFluids.HEAVY_WATER_STILL.get();
            default                      -> null;
        };
    }

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;
        tickCount++;
        boolean shouldLog = tickCount % 40 == 0;

        ticksSinceSearch++;
        if (cachedControllerPos == null || ticksSinceSearch >= RESCAN_INTERVAL) {
            ticksSinceSearch = 0;
            cachedControllerPos = findControllerBFS(shouldLog);
            if (shouldLog) {
                LOGGER.info("FluidPort@{} [{}] target={}: BFS rescan → controller={}",
                        worldPosition.toShortString(), mode, targetFluid,
                        cachedControllerPos != null ? cachedControllerPos.toShortString() : "none");
            }
        }

        if (cachedControllerPos == null) {
            if (shouldLog && mode == Mode.OUTPUT) {
                LOGGER.info("[FluidPort OUTPUT]@{} target={} — 0 controllers found (BFS cache empty)",
                        worldPosition.toShortString(), targetFluid);
            }
            return;
        }

        BlockEntity be = level.getBlockEntity(cachedControllerPos);
        if (be == null) {
            cachedControllerPos = null;
            return;
        }

        if (mode == Mode.INPUT) {
            if (tank.getFluidAmount() > 0) {
                IFluidHandler target = null;
                if (be instanceof RefineryControllerBlockEntity refinery) {
                    target = refinery.getOilInputHandler();
                } else if (be instanceof ChemicalMixerBlockEntity mixer) {
                    // Route to the correct input tank based on the fluid being carried
                    FluidStack carrying = tank.getFluid();
                    target = mixer.getFluidInputTank2().isFluidValid(carrying)
                            ? mixer.fluidInputHandler2
                            : mixer.fluidInputHandler;
                } else if (be instanceof EuvLithographyControllerBlockEntity euv) {
                    target = euv.getPhotoresistInputHandler();
                } else if (be instanceof GasCentrifugeBlockEntity gc) {
                    target = gc.fluidInputHandler;
                }
                if (target != null) {
                    FluidStack toOffer = tank.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                    if (!toOffer.isEmpty()) {
                        int filled = target.fill(toOffer, IFluidHandler.FluidAction.EXECUTE);
                        if (filled > 0) {
                            tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                            changed = true;
                        }
                    }
                }
            }
        } else {
            // ── OUTPUT mode: pull from controller into local buffer each tick ─────
            // The buffer is exposed as drain-only to pipes via getExposedHandler().
            // tank.fill() caps the transfer naturally when the buffer is full.
            String controllerType = be instanceof OilDerrickControllerBlockEntity ? "OilDerrick"
                    : be instanceof RefineryControllerBlockEntity ? "Refinery"
                    : be instanceof ChemicalMixerBlockEntity ? "ChemicalMixer"
                    : be.getClass().getSimpleName();

            if (shouldLog) {
                LOGGER.info("[FluidPort OUTPUT]@{} target={} buffer={}/{}mB — pulling from {} at {}",
                        worldPosition.toShortString(), targetFluid,
                        tank.getFluidAmount(), tank.getCapacity(),
                        controllerType, cachedControllerPos.toShortString());
            }

            if (be instanceof OilDerrickControllerBlockEntity derrick) {
                FluidTank source = derrick.getFluidTank();
                if (shouldLog) {
                    LOGGER.info("[FluidPort OUTPUT]@{} OilDerrick tank: {}mB of {}",
                            worldPosition.toShortString(), source.getFluidAmount(),
                            source.isEmpty() ? "empty" : source.getFluid().getFluid());
                }
                FluidStack toDrain = source.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                if (!toDrain.isEmpty()) {
                    int accepted = tank.fill(toDrain, IFluidHandler.FluidAction.SIMULATE);
                    if (accepted > 0) {
                        FluidStack actual = source.drain(new FluidStack(toDrain.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
                        if (!actual.isEmpty()) {
                            tank.fill(actual, IFluidHandler.FluidAction.EXECUTE);
                            changed = true;
                            if (shouldLog) LOGGER.info("[FluidPort OUTPUT]@{} transferred {}mB from OilDerrick",
                                    worldPosition.toShortString(), actual.getAmount());
                        }
                    } else if (shouldLog) {
                        LOGGER.info("[FluidPort OUTPUT]@{} buffer full ({}/{}mB) — cannot accept more",
                                worldPosition.toShortString(), tank.getFluidAmount(), tank.getCapacity());
                    }
                } else if (shouldLog) {
                    LOGGER.info("[FluidPort OUTPUT]@{} OilDerrick tank empty — nothing to transfer",
                            worldPosition.toShortString());
                }

            } else if (be instanceof RefineryControllerBlockEntity refinery) {
                int availableMb;
                FluidStack toDrain;
                if ("any".equals(targetFluid)) {
                    toDrain = refinery.getOutputHandler().drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                    availableMb = 0;
                    for (FluidTank t : refinery.getOutputTanks()) availableMb += t.getFluidAmount();
                } else {
                    FluidTank specificTank = refinery.getOutputTank(targetFluid);
                    availableMb = specificTank != null ? specificTank.getFluidAmount() : 0;
                    toDrain = (specificTank != null && !specificTank.isEmpty())
                            ? specificTank.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE)
                            : FluidStack.EMPTY;
                }
                if (shouldLog) {
                    LOGGER.info("[FluidPort OUTPUT]@{} Refinery tank[{}]: {}mB available",
                            worldPosition.toShortString(), targetFluid, availableMb);
                }
                if (!toDrain.isEmpty()) {
                    int accepted = tank.fill(toDrain, IFluidHandler.FluidAction.SIMULATE);
                    if (accepted > 0) {
                        FluidStack actual;
                        if ("any".equals(targetFluid)) {
                            actual = refinery.getOutputHandler().drain(new FluidStack(toDrain.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
                        } else {
                            FluidTank specificTank = refinery.getOutputTank(targetFluid);
                            actual = (specificTank != null)
                                    ? specificTank.drain(new FluidStack(toDrain.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE)
                                    : FluidStack.EMPTY;
                        }
                        if (!actual.isEmpty()) {
                            tank.fill(actual, IFluidHandler.FluidAction.EXECUTE);
                            changed = true;
                            if (shouldLog) LOGGER.info("[FluidPort OUTPUT]@{} transferred {}mB {} from Refinery",
                                    worldPosition.toShortString(), actual.getAmount(), actual.getFluid());
                        }
                    } else if (shouldLog) {
                        LOGGER.info("[FluidPort OUTPUT]@{} buffer full ({}/{}mB) — cannot accept more",
                                worldPosition.toShortString(), tank.getFluidAmount(), tank.getCapacity());
                    }
                } else if (shouldLog) {
                    LOGGER.info("[FluidPort OUTPUT]@{} Refinery tank[{}] empty — nothing to transfer",
                            worldPosition.toShortString(), targetFluid);
                }

            } else if (be instanceof GasCentrifugeBlockEntity gc) {
                IFluidHandler outputHandler = gc.fluidOutputHandler;
                FluidStack toDrain = outputHandler.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                if (!toDrain.isEmpty()) {
                    boolean fluidMatches = "any".equals(targetFluid);
                    if (!fluidMatches) {
                        Fluid expected = getFluidByName(targetFluid);
                        fluidMatches = expected != null && toDrain.getFluid().isSame(expected);
                    }
                    if (fluidMatches) {
                        int accepted = tank.fill(toDrain, IFluidHandler.FluidAction.SIMULATE);
                        if (accepted > 0) {
                            FluidStack actual = outputHandler.drain(
                                    new FluidStack(toDrain.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
                            if (!actual.isEmpty()) {
                                tank.fill(actual, IFluidHandler.FluidAction.EXECUTE);
                                changed = true;
                            }
                        }
                    }
                }

            } else if (be instanceof ChemicalMixerBlockEntity mixer) {
                FluidTank outputTank = mixer.getFluidOutputTank();
                if (shouldLog) {
                    LOGGER.info("[FluidPort OUTPUT]@{} ChemicalMixer output tank: {}mB of {}",
                            worldPosition.toShortString(), outputTank.getFluidAmount(),
                            outputTank.isEmpty() ? "empty" : outputTank.getFluid().getFluid());
                }
                if (!outputTank.isEmpty()) {
                    boolean fluidMatches = "any".equals(targetFluid);
                    if (!fluidMatches) {
                        Fluid expected = getFluidByName(targetFluid);
                        fluidMatches = expected != null && outputTank.getFluid().getFluid().isSame(expected);
                        if (!fluidMatches && shouldLog) {
                            LOGGER.info("[FluidPort OUTPUT]@{} ChemicalMixer fluid mismatch: expected={} actual={}",
                                    worldPosition.toShortString(), targetFluid, outputTank.getFluid().getFluid());
                        }
                    }
                    if (fluidMatches) {
                        FluidStack toDrain = outputTank.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                        if (!toDrain.isEmpty()) {
                            int accepted = tank.fill(toDrain, IFluidHandler.FluidAction.SIMULATE);
                            if (accepted > 0) {
                                FluidStack actual = outputTank.drain(
                                        new FluidStack(toDrain.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
                                if (!actual.isEmpty()) {
                                    tank.fill(actual, IFluidHandler.FluidAction.EXECUTE);
                                    changed = true;
                                    if (shouldLog) LOGGER.info("[FluidPort OUTPUT]@{} transferred {}mB from ChemicalMixer",
                                            worldPosition.toShortString(), actual.getAmount());
                                }
                            } else if (shouldLog) {
                                LOGGER.info("[FluidPort OUTPUT]@{} buffer full ({}/{}mB) — cannot accept more",
                                        worldPosition.toShortString(), tank.getFluidAmount(), tank.getCapacity());
                            }
                        }
                    }
                } else if (shouldLog) {
                    LOGGER.info("[FluidPort OUTPUT]@{} ChemicalMixer output tank empty — nothing to transfer",
                            worldPosition.toShortString());
                }
            }
        }

        if (changed) setChanged();
    }

    private BlockPos findControllerBFS(boolean verbose) {
        if (level == null) return null;

        LOGGER.info("[FluidPort BFS]@{} starting search (maxDepth={}, verbose={})",
                worldPosition.toShortString(), MAX_BFS_DEPTH, verbose);

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        visited.add(worldPosition);
        queue.add(worldPosition);
        int visitCount = 0;

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (visited.contains(neighbor)) continue;
                if (neighbor.distManhattan(worldPosition) > MAX_BFS_DEPTH) continue;
                visited.add(neighbor);
                visitCount++;

                Block block = level.getBlockState(neighbor).getBlock();
                BlockEntity be = level.getBlockEntity(neighbor);

                if (verbose) {
                    LOGGER.info("[FluidPort BFS]@{} visit#{} {} — block={} hasBE={}",
                            worldPosition.toShortString(), visitCount,
                            neighbor.toShortString(),
                            net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block),
                            be != null);
                }

                if (be instanceof OilDerrickControllerBlockEntity
                        || be instanceof RefineryControllerBlockEntity
                        || be instanceof ChemicalMixerBlockEntity
                        || be instanceof EuvLithographyControllerBlockEntity
                        || be instanceof GasCentrifugeBlockEntity) {
                    LOGGER.info("[FluidPort BFS]@{} FOUND controller: {} at {} (after {} visits)",
                            worldPosition.toShortString(),
                            be.getClass().getSimpleName(),
                            neighbor.toShortString(),
                            visitCount);
                    return neighbor;
                }

                if (isStructureBlock(block)) {
                    if (verbose) {
                        LOGGER.info("[FluidPort BFS]@{} queuing structure block {} at {}",
                                worldPosition.toShortString(),
                                net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block),
                                neighbor.toShortString());
                    }
                    queue.add(neighbor);
                }
            }
        }

        LOGGER.info("[FluidPort BFS]@{} search complete — visited {} blocks, no controller found",
                worldPosition.toShortString(), visitCount);
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
                || block == ModBlocks.EUV_BASE.get()
                || block == ModBlocks.EUV_WALL.get()
                || block == ModBlocks.EUV_LENS_HOUSING.get()
                || block == ModBlocks.EUV_MIRROR_ARRAY.get()
                || block == ModBlocks.EUV_EMITTER_HOUSING.get()
                || block == ModBlocks.EUV_LITHOGRAPHY_CONTROLLER.get()
                || block == ModBlocks.CHEMICAL_MIXER.get()
                || block == ModBlocks.ENERGY_PORT.get()
                || block == ModBlocks.FLUID_PORT.get();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FluidTank", tank.writeToNBT(registries, new CompoundTag()));
        tag.putString("Mode", mode.name());
        tag.putString("TargetFluid", targetFluid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FluidTank")) tank.readFromNBT(registries, tag.getCompound("FluidTank"));
        if (tag.contains("Mode")) {
            try { mode = Mode.valueOf(tag.getString("Mode")); } catch (IllegalArgumentException ignored) {}
        }
        if (tag.contains("TargetFluid")) targetFluid = tag.getString("TargetFluid");
    }
}
