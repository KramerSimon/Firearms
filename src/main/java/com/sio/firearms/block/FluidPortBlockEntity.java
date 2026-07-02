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
import net.minecraft.world.level.material.Fluids;
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
        "any", "oil", "fuel",
        "butane", "gasoline", "naphtha", "kerosene", "diesel", "heavy_gas_oil", "residual_fuel_oil",
        "creosote_oil", "synthetic_rubber", "pvc_resin",
        "sulfuric_acid", "nitric_acid",
        "chlorine_gas", "hydrogen_gas", "oxygen_gas", "fluorine_gas", "nitrate_solution",
        "photoresist",
        "uranium_hexafluoride", "enriched_uf6", "depleted_uf6",
        "heavy_water", "steam",
        "water"
    };
    private static final String[] FLUID_DISPLAY = {
        "Any", "Oil", "Fuel",
        "Butane", "Gasoline", "Naphtha", "Kerosene", "Diesel", "Heavy Gas Oil", "Residual Fuel Oil",
        "Creosote Oil", "Synthetic Rubber", "PVC Resin",
        "Sulfuric Acid", "Nitric Acid",
        "Chlorine Gas", "Hydrogen Gas", "Oxygen Gas", "Fluorine Gas", "Nitrate Solution",
        "Photoresist",
        "Uranium Hexafluoride", "Enriched UF6", "Depleted UF6",
        "Heavy Water", "Steam",
        "Water"
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

    public void setTargetFluid(String key) {
        this.targetFluid = key;
        setChanged();
    }

    public void setMode(Mode newMode) {
        this.mode = newMode;
        setChanged();
    }

    /** Returns the FLUID_CYCLE key for the given fluid, or "any" if not found. */
    public String findCycleKeyForFluid(Fluid fluid) {
        if (fluid == null) return "any";
        for (String key : FLUID_CYCLE) {
            Fluid f = getFluidByName(key);
            if (f != null && f.isSame(fluid)) return key;
        }
        return "any";
    }

    public IFluidHandler getExposedHandler() {
        return mode == Mode.INPUT ? fillOnlyHandler : drainOnlyHandler;
    }

    private Fluid getFluidByName(String name) {
        return switch (name) {
            case "oil"               -> ModFluids.OIL_STILL.get();
            case "fuel"              -> ModFluids.FUEL_STILL.get();
            case "butane"            -> ModFluids.BUTANE_STILL.get();
            case "gasoline"          -> ModFluids.GASOLINE_STILL.get();
            case "naphtha"           -> ModFluids.NAPHTHA_STILL.get();
            case "kerosene"          -> ModFluids.KEROSENE_STILL.get();
            case "diesel"            -> ModFluids.DIESEL_STILL.get();
            case "heavy_gas_oil"     -> ModFluids.HEAVY_GAS_OIL_STILL.get();
            case "residual_fuel_oil" -> ModFluids.RESIDUAL_FUEL_OIL_STILL.get();
            case "creosote_oil"      -> ModFluids.CREOSOTE_OIL_STILL.get();
            case "synthetic_rubber"  -> ModFluids.SYNTHETIC_RUBBER_STILL.get();
            case "pvc_resin"         -> ModFluids.PVC_RESIN_STILL.get();
            case "sulfuric_acid"     -> ModFluids.SULFURIC_ACID_STILL.get();
            case "nitric_acid"       -> ModFluids.NITRIC_ACID_STILL.get();
            case "chlorine_gas"      -> ModFluids.CHLORINE_GAS_STILL.get();
            case "hydrogen_gas"      -> ModFluids.HYDROGEN_GAS_STILL.get();
            case "oxygen_gas"        -> ModFluids.OXYGEN_GAS_STILL.get();
            case "fluorine_gas"      -> ModFluids.FLUORINE_GAS_STILL.get();
            case "nitrate_solution"  -> ModFluids.NITRATE_SOLUTION_STILL.get();
            case "photoresist"           -> ModFluids.PHOTORESIST_STILL.get();
            case "uranium_hexafluoride"  -> ModFluids.URANIUM_HEXAFLUORIDE_STILL.get();
            case "enriched_uf6"          -> ModFluids.ENRICHED_UF6_STILL.get();
            case "depleted_uf6"          -> ModFluids.DEPLETED_UF6_STILL.get();
            case "heavy_water"           -> ModFluids.HEAVY_WATER_STILL.get();
            case "steam"                 -> ModFluids.STEAM_STILL.get();
            case "water"                 -> Fluids.WATER;
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
                LOGGER.debug("FluidPort@{} [{}] target={}: BFS rescan → controller={}",
                        worldPosition.toShortString(), mode, targetFluid,
                        cachedControllerPos != null ? cachedControllerPos.toShortString() : "none");
            }
        }

        if (cachedControllerPos == null) {
            if (shouldLog && mode == Mode.OUTPUT) {
                LOGGER.debug("[FluidPort OUTPUT]@{} target={} — 0 controllers found (BFS cache empty)",
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
                    target = mixer.combinedFluidInputHandler;
                } else if (be instanceof ChemicalMixerControllerBlockEntity ctrl) {
                    target = ctrl.combinedFluidInputHandler;
                } else if (be instanceof EuvLithographyControllerBlockEntity euv) {
                    target = euv.getPhotoresistInputHandler();
                } else if (be instanceof GasCentrifugeBlockEntity gc) {
                    target = gc.fluidInputHandler;
                } else if (be instanceof ElectrolysisMachineBlockEntity em) {
                    target = em.fluidInputHandler;
                } else if (be instanceof AcidBathBlockEntity ab) {
                    target = ab.fillOnlyHandler;
                } else if (be instanceof PlasmaEtcherBlockEntity pe) {
                    target = pe.fillOnlyHandler;
                } else if (be instanceof FluidTankBlockEntity ft) {
                    target = ft.fullAccessHandler;
                } else if (be instanceof ReactorControllerBlockEntity reactor) {
                    target = reactor.waterInputHandler;
                } else if (be instanceof SteamTurbineBlockEntity turbine) {
                    target = turbine.getSteamInputHandler();
                } else if (be instanceof CoolingTowerControllerBlockEntity ct) {
                    target = ct.steamInputHandler;
                } else if (be instanceof RefuelStationBlockEntity rs) {
                    target = rs.fullAccessHandler;
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
                LOGGER.debug("[FluidPort OUTPUT]@{} target={} buffer={}/{}mB — pulling from {} at {}",
                        worldPosition.toShortString(), targetFluid,
                        tank.getFluidAmount(), tank.getCapacity(),
                        controllerType, cachedControllerPos.toShortString());
            }

            if (be instanceof OilDerrickControllerBlockEntity derrick) {
                FluidTank source = derrick.getFluidTank();
                if (shouldLog) {
                    LOGGER.debug("[FluidPort OUTPUT]@{} OilDerrick tank: {}mB of {}",
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
                            if (shouldLog) LOGGER.debug("[FluidPort OUTPUT]@{} transferred {}mB from OilDerrick",
                                    worldPosition.toShortString(), actual.getAmount());
                        }
                    } else if (shouldLog) {
                        LOGGER.debug("[FluidPort OUTPUT]@{} buffer full ({}/{}mB) — cannot accept more",
                                worldPosition.toShortString(), tank.getFluidAmount(), tank.getCapacity());
                    }
                } else if (shouldLog) {
                    LOGGER.debug("[FluidPort OUTPUT]@{} OilDerrick tank empty — nothing to transfer",
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
                    LOGGER.debug("[FluidPort OUTPUT]@{} Refinery tank[{}]: {}mB available",
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
                            if (shouldLog) LOGGER.debug("[FluidPort OUTPUT]@{} transferred {}mB {} from Refinery",
                                    worldPosition.toShortString(), actual.getAmount(), actual.getFluid());
                        }
                    } else if (shouldLog) {
                        LOGGER.debug("[FluidPort OUTPUT]@{} buffer full ({}/{}mB) — cannot accept more",
                                worldPosition.toShortString(), tank.getFluidAmount(), tank.getCapacity());
                    }
                } else if (shouldLog) {
                    LOGGER.debug("[FluidPort OUTPUT]@{} Refinery tank[{}] empty — nothing to transfer",
                            worldPosition.toShortString(), targetFluid);
                }

            } else if (be instanceof GasCentrifugeBlockEntity gc) {
                // Pick the specific output tank when targeting a known product, else use combined handler
                IFluidHandler outputHandler = switch (targetFluid) {
                    case "enriched_uf6" -> gc.fluidOutputTank1;
                    case "depleted_uf6" -> gc.fluidOutputTank2;
                    default             -> gc.fluidOutputHandler;
                };
                FluidStack toDrain = outputHandler.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                if (!toDrain.isEmpty()) {
                    boolean fluidMatches = "any".equals(targetFluid)
                            || "enriched_uf6".equals(targetFluid)
                            || "depleted_uf6".equals(targetFluid);
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
                    LOGGER.debug("[FluidPort OUTPUT]@{} ChemicalMixer output tank: {}mB of {}",
                            worldPosition.toShortString(), outputTank.getFluidAmount(),
                            outputTank.isEmpty() ? "empty" : outputTank.getFluid().getFluid());
                }
                if (!outputTank.isEmpty()) {
                    boolean fluidMatches = "any".equals(targetFluid);
                    if (!fluidMatches) {
                        Fluid expected = getFluidByName(targetFluid);
                        fluidMatches = expected != null && outputTank.getFluid().getFluid().isSame(expected);
                        if (!fluidMatches && shouldLog) {
                            LOGGER.debug("[FluidPort OUTPUT]@{} ChemicalMixer fluid mismatch: expected={} actual={}",
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
                                    if (shouldLog) LOGGER.debug("[FluidPort OUTPUT]@{} transferred {}mB from ChemicalMixer",
                                            worldPosition.toShortString(), actual.getAmount());
                                }
                            } else if (shouldLog) {
                                LOGGER.debug("[FluidPort OUTPUT]@{} buffer full ({}/{}mB) — cannot accept more",
                                        worldPosition.toShortString(), tank.getFluidAmount(), tank.getCapacity());
                            }
                        }
                    }
                } else if (shouldLog) {
                    LOGGER.debug("[FluidPort OUTPUT]@{} ChemicalMixer output tank empty — nothing to transfer",
                            worldPosition.toShortString());
                }

            } else if (be instanceof ChemicalMixerControllerBlockEntity ctrl) {
                FluidTank outputTank = ctrl.getFluidOutputTank();
                if (!outputTank.isEmpty()) {
                    boolean fluidMatches = "any".equals(targetFluid);
                    if (!fluidMatches) {
                        Fluid expected = getFluidByName(targetFluid);
                        fluidMatches = expected != null && outputTank.getFluid().getFluid().isSame(expected);
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
                                }
                            }
                        }
                    }
                }

            } else if (be instanceof CokeOvenControllerBlockEntity coke) {
                if ("any".equals(targetFluid) || "creosote_oil".equals(targetFluid)) {
                    FluidStack toDrain = coke.creosoteTank.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                    if (!toDrain.isEmpty()) {
                        int accepted = tank.fill(toDrain, IFluidHandler.FluidAction.SIMULATE);
                        if (accepted > 0) {
                            FluidStack actual = coke.creosoteTank.drain(
                                    new FluidStack(toDrain.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
                            if (!actual.isEmpty()) {
                                tank.fill(actual, IFluidHandler.FluidAction.EXECUTE);
                                changed = true;
                            }
                        }
                    }
                }

            } else if (be instanceof WaterPumpBlockEntity wp) {
                if ("any".equals(targetFluid) || "water".equals(targetFluid)) {
                    FluidStack toDrain = wp.waterTank.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                    if (!toDrain.isEmpty()) {
                        int accepted = tank.fill(toDrain, IFluidHandler.FluidAction.SIMULATE);
                        if (accepted > 0) {
                            FluidStack actual = wp.waterTank.drain(
                                    new FluidStack(toDrain.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
                            if (!actual.isEmpty()) {
                                tank.fill(actual, IFluidHandler.FluidAction.EXECUTE);
                                changed = true;
                            }
                        }
                    }
                }

            } else if (be instanceof ElectrolysisMachineBlockEntity em) {
                // Select the output tank that contains the target fluid
                FluidTank sourceTank;
                if ("any".equals(targetFluid)) {
                    sourceTank = !em.fluidOutputTank1.isEmpty() ? em.fluidOutputTank1 : em.fluidOutputTank2;
                } else {
                    Fluid expected = getFluidByName(targetFluid);
                    if (expected != null && !em.fluidOutputTank1.isEmpty()
                            && em.fluidOutputTank1.getFluid().getFluid().isSame(expected)) {
                        sourceTank = em.fluidOutputTank1;
                    } else if (expected != null && !em.fluidOutputTank2.isEmpty()
                            && em.fluidOutputTank2.getFluid().getFluid().isSame(expected)) {
                        sourceTank = em.fluidOutputTank2;
                    } else {
                        sourceTank = null;
                    }
                }
                if (sourceTank != null) {
                    FluidStack toDrain = sourceTank.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                    if (!toDrain.isEmpty()) {
                        int accepted = tank.fill(toDrain, IFluidHandler.FluidAction.SIMULATE);
                        if (accepted > 0) {
                            FluidStack actual = sourceTank.drain(
                                    new FluidStack(toDrain.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
                            if (!actual.isEmpty()) {
                                tank.fill(actual, IFluidHandler.FluidAction.EXECUTE);
                                changed = true;
                            }
                        }
                    }
                }

            } else if (be instanceof FluidTankBlockEntity ft) {
                FluidTank sourceTank = ft.getFluidTank();
                if (!sourceTank.isEmpty()) {
                    boolean fluidMatches = "any".equals(targetFluid);
                    if (!fluidMatches) {
                        Fluid expected = getFluidByName(targetFluid);
                        fluidMatches = expected != null && sourceTank.getFluid().getFluid().isSame(expected);
                    }
                    if (fluidMatches) {
                        FluidStack toDrain = sourceTank.drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                        if (!toDrain.isEmpty()) {
                            int accepted = tank.fill(toDrain, IFluidHandler.FluidAction.SIMULATE);
                            if (accepted > 0) {
                                FluidStack actual = sourceTank.drain(
                                        new FluidStack(toDrain.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
                                if (!actual.isEmpty()) {
                                    tank.fill(actual, IFluidHandler.FluidAction.EXECUTE);
                                    changed = true;
                                }
                            }
                        }
                    }
                }

            } else if (be instanceof ReactorControllerBlockEntity reactor) {
                // Pull steam from reactor
                if ("any".equals(targetFluid) || "steam".equals(targetFluid)) {
                    FluidStack toDrain = reactor.getSteamTank().drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                    if (!toDrain.isEmpty()) {
                        int accepted = tank.fill(toDrain, IFluidHandler.FluidAction.SIMULATE);
                        if (accepted > 0) {
                            FluidStack actual = reactor.getSteamTank().drain(
                                    new FluidStack(toDrain.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
                            if (!actual.isEmpty()) {
                                tank.fill(actual, IFluidHandler.FluidAction.EXECUTE);
                                changed = true;
                            }
                        }
                    }
                }

            } else if (be instanceof CoolingTowerControllerBlockEntity ct) {
                // Pull condensed water from cooling tower
                if ("any".equals(targetFluid) || "water".equals(targetFluid)) {
                    if (shouldLog) {
                        LOGGER.debug("[FluidPort OUTPUT]@{} CoolingTower waterTank: {}mB available, buffer={}/{}mB",
                                worldPosition.toShortString(), ct.getWaterTank().getFluidAmount(),
                                tank.getFluidAmount(), tank.getCapacity());
                    }
                    FluidStack toDrain = ct.getWaterTank().drain(MAX_TRANSFER, IFluidHandler.FluidAction.SIMULATE);
                    if (!toDrain.isEmpty()) {
                        int accepted = tank.fill(toDrain, IFluidHandler.FluidAction.SIMULATE);
                        if (accepted > 0) {
                            FluidStack actual = ct.getWaterTank().drain(
                                    new FluidStack(toDrain.getFluid(), accepted), IFluidHandler.FluidAction.EXECUTE);
                            if (!actual.isEmpty()) {
                                tank.fill(actual, IFluidHandler.FluidAction.EXECUTE);
                                changed = true;
                                if (shouldLog) LOGGER.debug("[FluidPort OUTPUT]@{} drained {}mB water from CoolingTower",
                                        worldPosition.toShortString(), actual.getAmount());
                            }
                        } else if (shouldLog) {
                            LOGGER.debug("[FluidPort OUTPUT]@{} buffer full ({}/{}mB) — cannot accept more water",
                                    worldPosition.toShortString(), tank.getFluidAmount(), tank.getCapacity());
                        }
                    } else if (shouldLog) {
                        LOGGER.debug("[FluidPort OUTPUT]@{} CoolingTower waterTank empty — nothing to drain",
                                worldPosition.toShortString());
                    }
                }
            }
        }

        if (changed) setChanged();
    }

    private BlockPos findControllerBFS(boolean verbose) {
        if (level == null) return null;

        LOGGER.debug("[FluidPort BFS]@{} starting search (maxDepth={}, verbose={})",
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
                    LOGGER.debug("[FluidPort BFS]@{} visit#{} {} — block={} hasBE={}",
                            worldPosition.toShortString(), visitCount,
                            neighbor.toShortString(),
                            net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block),
                            be != null);
                }

                if (be instanceof OilDerrickControllerBlockEntity
                        || be instanceof RefineryControllerBlockEntity
                        || be instanceof ChemicalMixerBlockEntity
                        || be instanceof ChemicalMixerControllerBlockEntity
                        || be instanceof EuvLithographyControllerBlockEntity
                        || be instanceof GasCentrifugeBlockEntity
                        || be instanceof ElectrolysisMachineBlockEntity
                        || be instanceof CokeOvenControllerBlockEntity
                        || be instanceof WaterPumpBlockEntity
                        || be instanceof AcidBathBlockEntity
                        || be instanceof PlasmaEtcherBlockEntity
                        || be instanceof FluidTankBlockEntity
                        || be instanceof ReactorControllerBlockEntity
                        || be instanceof CoolingTowerControllerBlockEntity
                        || be instanceof RefuelStationBlockEntity) {
                    LOGGER.info("[FluidPort BFS]@{} FOUND controller: {} at {} (after {} visits)",
                            worldPosition.toShortString(),
                            be.getClass().getSimpleName(),
                            neighbor.toShortString(),
                            visitCount);
                    return neighbor;
                }

                if (isStructureBlock(block)) {
                    if (verbose) {
                        LOGGER.debug("[FluidPort BFS]@{} queuing structure block {} at {}",
                                worldPosition.toShortString(),
                                net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block),
                                neighbor.toShortString());
                    }
                    queue.add(neighbor);
                }
            }
        }

        LOGGER.debug("[FluidPort BFS]@{} search complete — visited {} blocks, no controller found",
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
                || block == ModBlocks.COKE_OVEN_BRICK.get()
                || block == ModBlocks.COKE_OVEN_CONTROLLER.get()
                || block == ModBlocks.EUV_BASE.get()
                || block == ModBlocks.EUV_WALL.get()
                || block == ModBlocks.EUV_LENS_HOUSING.get()
                || block == ModBlocks.EUV_MIRROR_ARRAY.get()
                || block == ModBlocks.EUV_EMITTER_HOUSING.get()
                || block == ModBlocks.EUV_LITHOGRAPHY_CONTROLLER.get()
                || block == ModBlocks.CHEMICAL_MIXER.get()
                || block == ModBlocks.CHEMICAL_MIXER_BASE.get()
                || block == ModBlocks.CHEMICAL_MIXER_WALL.get()
                || block == ModBlocks.CHEMICAL_MIXER_CONTROLLER.get()
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
                || block == ModBlocks.GARAGE_CONTROLLER.get();
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
