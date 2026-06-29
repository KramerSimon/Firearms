package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.menu.RefineryMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.slf4j.Logger;

public class RefineryControllerBlockEntity extends BlockEntity implements MenuProvider {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int ENERGY_CAPACITY  = 50_000;
    private static final int MAX_RECEIVE       = 1_000;
    private static final int FE_PER_TICK       = 500;
    private static final int PROCESS_TIME      = 200;
    private static final int OIL_PER_CYCLE     = 1_000;
    private static final int OIL_TANK_CAP      = 10_000;
    private static final int OUT_TANK_CAP      = 5_000;

    private static final int BUTANE_MB           = 50;
    private static final int GASOLINE_MB         = 150;
    private static final int NAPHTHA_MB          = 100;
    private static final int KEROSENE_MB         = 100;
    private static final int DIESEL_MB           = 150;
    private static final int HEAVY_GAS_OIL_MB    = 100;
    private static final int RESIDUAL_FUEL_MB    = 150;

    private final EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY, MAX_RECEIVE, 0);

    private final FluidTank oilTank = new FluidTank(OIL_TANK_CAP,
            s -> s.getFluid().isSame(ModFluids.OIL_STILL.get()));

    private final FluidTank butaneTank =
            new FluidTank(OUT_TANK_CAP, s -> s.getFluid().isSame(ModFluids.BUTANE_STILL.get()));
    private final FluidTank gasolineTank =
            new FluidTank(OUT_TANK_CAP, s -> s.getFluid().isSame(ModFluids.GASOLINE_STILL.get()));
    private final FluidTank naphthaTank =
            new FluidTank(OUT_TANK_CAP, s -> s.getFluid().isSame(ModFluids.NAPHTHA_STILL.get()));
    private final FluidTank keroseneTank =
            new FluidTank(OUT_TANK_CAP, s -> s.getFluid().isSame(ModFluids.KEROSENE_STILL.get()));
    private final FluidTank dieselTank =
            new FluidTank(OUT_TANK_CAP, s -> s.getFluid().isSame(ModFluids.DIESEL_STILL.get()));
    private final FluidTank heavyGasOilTank =
            new FluidTank(OUT_TANK_CAP, s -> s.getFluid().isSame(ModFluids.HEAVY_GAS_OIL_STILL.get()));
    private final FluidTank residualFuelOilTank =
            new FluidTank(OUT_TANK_CAP, s -> s.getFluid().isSame(ModFluids.RESIDUAL_FUEL_OIL_STILL.get()));

    private final FluidTank[] outputTanks = {
            butaneTank, gasolineTank, naphthaTank, keroseneTank,
            dieselTank, heavyGasOilTank, residualFuelOilTank
    };

    private final IFluidHandler oilInputHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int t) { return oilTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t) { return oilTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack s) { return oilTank.isFluidValid(0, s); }
        @Override public int fill(FluidStack r, FluidAction a) { return oilTank.fill(r, a); }
        @Override public FluidStack drain(FluidStack r, FluidAction a) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int max, FluidAction a) { return FluidStack.EMPTY; }
    };

    private final IFluidHandler combinedOutputHandler = new IFluidHandler() {
        @Override public int getTanks() { return 7; }
        @Override public FluidStack getFluidInTank(int t) {
            return (t >= 0 && t < 7) ? outputTanks[t].getFluidInTank(0) : FluidStack.EMPTY;
        }
        @Override public int getTankCapacity(int t) {
            return (t >= 0 && t < 7) ? outputTanks[t].getTankCapacity(0) : 0;
        }
        @Override public boolean isFluidValid(int t, FluidStack s) {
            return (t >= 0 && t < 7) && outputTanks[t].isFluidValid(0, s);
        }
        @Override public int fill(FluidStack r, FluidAction a) { return 0; }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) {
            for (FluidTank t : outputTanks) {
                if (!t.isEmpty() && t.getFluid().getFluid().isSame(resource.getFluid())) {
                    return t.drain(resource, action);
                }
            }
            return FluidStack.EMPTY;
        }
        @Override public FluidStack drain(int maxDrain, FluidAction action) {
            for (FluidTank t : outputTanks) {
                FluidStack d = t.drain(maxDrain, action);
                if (!d.isEmpty()) return d;
            }
            return FluidStack.EMPTY;
        }
    };

    // Full-access: fill→oil input, drain→product outputs. Registered on all sides.
    public final IFluidHandler fullAccessHandler = new IFluidHandler() {
        @Override public int getTanks() { return 8; }
        @Override public FluidStack getFluidInTank(int t) {
            if (t == 0) return oilTank.getFluidInTank(0);
            return (t >= 1 && t <= 7) ? outputTanks[t - 1].getFluidInTank(0) : FluidStack.EMPTY;
        }
        @Override public int getTankCapacity(int t) {
            if (t == 0) return oilTank.getTankCapacity(0);
            return (t >= 1 && t <= 7) ? outputTanks[t - 1].getTankCapacity(0) : 0;
        }
        @Override public boolean isFluidValid(int t, FluidStack s) {
            return t == 0 && oilTank.isFluidValid(0, s);
        }
        @Override public int fill(FluidStack resource, FluidAction a) { return oilTank.fill(resource, a); }
        @Override public FluidStack drain(FluidStack resource, FluidAction a) { return combinedOutputHandler.drain(resource, a); }
        @Override public FluidStack drain(int maxDrain, FluidAction a) { return combinedOutputHandler.drain(maxDrain, a); }
    };

    // Slot 0: rubber sheet output   Slot 1: gun oil output
    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    private int progress       = 0;
    private int cycleCount     = 0;
    private boolean structureValid   = false;
    private BlockPos structureCenter = null;

    // ContainerData indices:
    // 0: energy stored   1: energy max   2: oil amount   3: structure valid
    // 4: progress        5: max progress
    // 6-12: output tank amounts (butane, gasoline, naphtha, kerosene, diesel, hgo, rfo)
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case 0  -> energy.getEnergyStored();
                case 1  -> energy.getMaxEnergyStored();
                case 2  -> oilTank.getFluidAmount();
                case 3  -> structureValid ? 1 : 0;
                case 4  -> progress;
                case 5  -> PROCESS_TIME;
                case 6  -> butaneTank.getFluidAmount();
                case 7  -> gasolineTank.getFluidAmount();
                case 8  -> naphthaTank.getFluidAmount();
                case 9  -> keroseneTank.getFluidAmount();
                case 10 -> dieselTank.getFluidAmount();
                case 11 -> heavyGasOilTank.getFluidAmount();
                case 12 -> residualFuelOilTank.getFluidAmount();
                default -> 0;
            };
        }
        @Override public void set(int i, int v) { if (i == 4) progress = v; }
        @Override public int getCount() { return 13; }
    };

    public RefineryControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFINERY_CONTROLLER.get(), pos, state);
    }

    public EnergyStorage getEnergyStorage()   { return energy; }
    public FluidTank getOilTank()             { return oilTank; }
    public FluidTank[] getOutputTanks()       { return outputTanks; }
    public IFluidHandler getOilInputHandler() { return oilInputHandler; }
    public IFluidHandler getOutputHandler()   { return combinedOutputHandler; }
    public ItemStackHandler getInventory()    { return inventory; }

    public FluidTank getOutputTank(String fluidName) {
        return switch (fluidName) {
            case "butane"            -> butaneTank;
            case "gasoline"          -> gasolineTank;
            case "naphtha"           -> naphthaTank;
            case "kerosene"          -> keroseneTank;
            case "diesel"            -> dieselTank;
            case "heavy_gas_oil"     -> heavyGasOilTank;
            case "residual_fuel_oil" -> residualFuelOilTank;
            default                  -> null;
        };
    }

    @Override public Component getDisplayName() {
        return Component.translatable("block.firearms.refinery_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new RefineryMenu(id, inv, inventory, data);
    }

    // ── Structure validation (5×5×6) ─────────────────────────────────────────
    public boolean checkStructure() {
        if (level == null) return false;
        LOGGER.info("[Refinery] checkStructure() called, controller at {}", worldPosition);

        structureCenter = findCenter();
        if (structureCenter == null) {
            LOGGER.info("[Refinery] Structure INVALID — no valid base layer found");
            structureValid = false;
            return false;
        }
        BlockPos center = structureCenter;
        LOGGER.info("[Refinery]   PASS y=0 base layer, center = {}", center);

        // Layers 1-4 (y=1 to y=4): border (16 positions) must be refinery_wall; interior 3×3 may be air
        boolean ok = true;
        for (int y = 1; y <= 4; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (Math.abs(x) < 2 && Math.abs(z) < 2) continue;
                    BlockPos p = center.offset(x, y, z);
                    if (!isValidStructureBlock(p, ModBlocks.REFINERY_WALL.get())) {
                        Block found = level.getBlockState(p).getBlock();
                        LOGGER.info("[Refinery]   FAIL y={} dx={} dz={} expected refinery_wall, found {}",
                                y, x, z, BuiltInRegistries.BLOCK.getKey(found));
                        ok = false;
                    }
                }
            }
        }
        if (!ok) { structureValid = false; return false; }
        LOGGER.info("[Refinery]   PASS y=1-4 wall layers");

        // Top layer (y=5): 5×5 refinery_top
        ok = true;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos p = center.offset(x, 5, z);
                if (!isValidStructureBlock(p, ModBlocks.REFINERY_TOP.get())) {
                    Block found = level.getBlockState(p).getBlock();
                    LOGGER.info("[Refinery]   FAIL y=5 dx={} dz={} expected refinery_top, found {}",
                            x, z, BuiltInRegistries.BLOCK.getKey(found));
                    ok = false;
                }
            }
        }
        if (!ok) { structureValid = false; return false; }
        LOGGER.info("[Refinery]   PASS y=5 top layer");

        LOGGER.info("[Refinery] Structure VALID — center {}", center);
        structureValid = true;
        return true;
    }

    // Controller is on the outer border of the 5×5 (|dx|=2 or |dz|=2 from center).
    // isValidCenter() is used purely to find the right center; failures are not logged
    // individually here because all 16 candidates are tried before giving up.
    private BlockPos findCenter() {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) != 2 && Math.abs(dz) != 2) continue;
                BlockPos candidate = worldPosition.offset(-dx, 0, -dz);
                LOGGER.info("[Refinery]   Trying center {} (dx={} dz={})", candidate, dx, dz);
                if (isValidCenter(candidate)) return candidate;
            }
        }
        return null;
    }

    private boolean isValidCenter(BlockPos center) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos p = center.offset(x, 0, z);
                if (p.equals(worldPosition)) continue;
                if (!isValidStructureBlock(p, ModBlocks.REFINERY_BASE.get())) {
                    Block found = level.getBlockState(p).getBlock();
                    LOGGER.info("[Refinery]     base FAIL dx={} dz={} expected refinery_base, found {}",
                            x, z, BuiltInRegistries.BLOCK.getKey(found));
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidStructureBlock(BlockPos pos, Block expected) {
        BlockState st = level.getBlockState(pos);
        return st.is(expected) || st.is(ModBlocks.ENERGY_PORT.get()) || st.is(ModBlocks.FLUID_PORT.get());
    }

    // ── Server tick ───────────────────────────────────────────────────────────
    public void serverTick() {
        if (level == null) return;
        boolean changed = false;

        if (level.getGameTime() % 40 == 0) checkStructure();

        if (!structureValid) {
            if (progress > 0) { progress = 0; changed = true; }
            if (changed) setChanged();
            return;
        }

        boolean canProcess = energy.getEnergyStored() >= FE_PER_TICK
                && oilTank.getFluidAmount() >= OIL_PER_CYCLE
                && hasOutputSpace();

        if (canProcess) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            changed = true;

            if (progress >= PROCESS_TIME) {
                oilTank.drain(OIL_PER_CYCLE, IFluidHandler.FluidAction.EXECUTE);
                butaneTank.fill(         new FluidStack(ModFluids.BUTANE_STILL.get(),              BUTANE_MB),        IFluidHandler.FluidAction.EXECUTE);
                gasolineTank.fill(       new FluidStack(ModFluids.GASOLINE_STILL.get(),            GASOLINE_MB),      IFluidHandler.FluidAction.EXECUTE);
                naphthaTank.fill(        new FluidStack(ModFluids.NAPHTHA_STILL.get(),             NAPHTHA_MB),       IFluidHandler.FluidAction.EXECUTE);
                keroseneTank.fill(       new FluidStack(ModFluids.KEROSENE_STILL.get(),            KEROSENE_MB),      IFluidHandler.FluidAction.EXECUTE);
                dieselTank.fill(         new FluidStack(ModFluids.DIESEL_STILL.get(),              DIESEL_MB),        IFluidHandler.FluidAction.EXECUTE);
                heavyGasOilTank.fill(    new FluidStack(ModFluids.HEAVY_GAS_OIL_STILL.get(),      HEAVY_GAS_OIL_MB), IFluidHandler.FluidAction.EXECUTE);
                residualFuelOilTank.fill(new FluidStack(ModFluids.RESIDUAL_FUEL_OIL_STILL.get(),  RESIDUAL_FUEL_MB), IFluidHandler.FluidAction.EXECUTE);

                cycleCount++;
                if (cycleCount % 5  == 0) tryOutputItem(ModItems.RUBBER_SHEET.get(), 0);
                if (cycleCount % 10 == 0) tryOutputItem(ModItems.GUN_OIL.get(), 1);
                progress = 0;
            }
        } else if (progress > 0) {
            progress = 0;
            changed = true;
        }

        if (changed) setChanged();
    }

    private boolean hasOutputSpace() {
        return butaneTank.getFluidAmount()          + BUTANE_MB        <= OUT_TANK_CAP
            && gasolineTank.getFluidAmount()        + GASOLINE_MB      <= OUT_TANK_CAP
            && naphthaTank.getFluidAmount()         + NAPHTHA_MB       <= OUT_TANK_CAP
            && keroseneTank.getFluidAmount()        + KEROSENE_MB      <= OUT_TANK_CAP
            && dieselTank.getFluidAmount()          + DIESEL_MB        <= OUT_TANK_CAP
            && heavyGasOilTank.getFluidAmount()     + HEAVY_GAS_OIL_MB <= OUT_TANK_CAP
            && residualFuelOilTank.getFluidAmount() + RESIDUAL_FUEL_MB <= OUT_TANK_CAP;
    }

    private void tryOutputItem(Item item, int slot) {
        ItemStack s = inventory.getStackInSlot(slot);
        if (s.isEmpty()) inventory.setStackInSlot(slot, new ItemStack(item));
        else if (s.is(item) && s.getCount() < s.getMaxStackSize()) s.grow(1);
    }

    // ── NBT ───────────────────────────────────────────────────────────────────
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy",   energy.serializeNBT(registries));
        tag.put("OilTank",  oilTank.writeToNBT(registries, new CompoundTag()));
        tag.put("ButaneTank",          butaneTank.writeToNBT(registries, new CompoundTag()));
        tag.put("GasolineTank",        gasolineTank.writeToNBT(registries, new CompoundTag()));
        tag.put("NaphthaTank",         naphthaTank.writeToNBT(registries, new CompoundTag()));
        tag.put("KeroseneTank",        keroseneTank.writeToNBT(registries, new CompoundTag()));
        tag.put("DieselTank",          dieselTank.writeToNBT(registries, new CompoundTag()));
        tag.put("HeavyGasOilTank",     heavyGasOilTank.writeToNBT(registries, new CompoundTag()));
        tag.put("ResidualFuelOilTank", residualFuelOilTank.writeToNBT(registries, new CompoundTag()));
        tag.put("Inventory",  inventory.serializeNBT(registries));
        tag.putInt("Progress",   progress);
        tag.putInt("CycleCount", cycleCount);
        tag.putBoolean("StructureValid", structureValid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Energy"))   energy.deserializeNBT(registries, tag.get("Energy"));
        if (tag.contains("OilTank"))  oilTank.readFromNBT(registries, tag.getCompound("OilTank"));
        if (tag.contains("ButaneTank"))          butaneTank.readFromNBT(registries, tag.getCompound("ButaneTank"));
        if (tag.contains("GasolineTank"))        gasolineTank.readFromNBT(registries, tag.getCompound("GasolineTank"));
        if (tag.contains("NaphthaTank"))         naphthaTank.readFromNBT(registries, tag.getCompound("NaphthaTank"));
        if (tag.contains("KeroseneTank"))        keroseneTank.readFromNBT(registries, tag.getCompound("KeroseneTank"));
        if (tag.contains("DieselTank"))          dieselTank.readFromNBT(registries, tag.getCompound("DieselTank"));
        if (tag.contains("HeavyGasOilTank"))     heavyGasOilTank.readFromNBT(registries, tag.getCompound("HeavyGasOilTank"));
        if (tag.contains("ResidualFuelOilTank")) residualFuelOilTank.readFromNBT(registries, tag.getCompound("ResidualFuelOilTank"));
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        progress       = tag.getInt("Progress");
        cycleCount     = tag.getInt("CycleCount");
        structureValid = tag.getBoolean("StructureValid");
    }
}
