package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.CoolingTowerMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoolingTowerControllerBlockEntity extends EnergyStorageBlock implements MenuProvider, IMultiblockPreview {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int CAPACITY        = 100_000;
    public static final int MAX_FE_OUT      = 10_000;
    public static final int STEAM_CAPACITY  = 100_000;
    public static final int WATER_CAPACITY  = 50_000;
    public static final int STEAM_PER_TICK  = 100;   // per turbine per tick
    public static final int FE_PER_TURBINE  = 400;   // FE per turbine per tick
    public static final int MAX_TURBINES    = 4;

    public final FluidTank steamTank = new FluidTank(STEAM_CAPACITY,
            fs -> fs.getFluid().isSame(ModFluids.STEAM_STILL.get())) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    public final FluidTank waterTank = new FluidTank(WATER_CAPACITY) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    // Fill-only: external pipes fill steam in
    public final IFluidHandler steamInputHandler = new IFluidHandler() {
        @Override public int getTanks()                               { return 1; }
        @Override public FluidStack getFluidInTank(int t)            { return steamTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t)                   { return steamTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack s)    { return steamTank.isFluidValid(0, s); }
        @Override public int fill(FluidStack r, FluidAction a)        { return steamTank.fill(r, a); }
        @Override public FluidStack drain(FluidStack r, FluidAction a){ return FluidStack.EMPTY; }
        @Override public FluidStack drain(int max, FluidAction a)     { return FluidStack.EMPTY; }
    };

    // Full-access: fill() routes to steam input, drain() routes from water output.
    // Registered as the single FluidHandler capability so pipes can both supply steam and pull water.
    public final IFluidHandler fullAccessHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int t) { return waterTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t) { return STEAM_CAPACITY; }
        @Override public boolean isFluidValid(int t, FluidStack s) { return steamTank.isFluidValid(0, s); }
        @Override public int fill(FluidStack r, FluidAction a) { return steamTank.fill(r, a); }
        @Override public FluidStack drain(FluidStack r, FluidAction a) { return waterTank.drain(r, a); }
        @Override public FluidStack drain(int max, FluidAction a) { return waterTank.drain(max, a); }
    };

    // Drain-only: external pipes pull condensed water out
    public final IFluidHandler waterOutputHandler = new IFluidHandler() {
        @Override public int getTanks()                               { return 1; }
        @Override public FluidStack getFluidInTank(int t)            { return waterTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t)                   { return waterTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack s)    { return waterTank.isFluidValid(0, s); }
        @Override public int fill(FluidStack r, FluidAction a)        { return 0; }
        @Override public FluidStack drain(FluidStack r, FluidAction a){ return waterTank.drain(r, a); }
        @Override public FluidStack drain(int max, FluidAction a)     { return waterTank.drain(max, a); }
    };

    private int feRate     = 0;
    private int turbineCount = 0;
    private boolean structureValid = false;
    private int scanTick = 0;
    private Boolean lastValidState = null;

    private final List<BlockPos> turbinePositions = new ArrayList<>();

    private final ContainerData data = new ContainerData() {
        @Override public int get(int i) {
            return switch (i) {
                case 0 -> energy.getEnergyStored();
                case 1 -> CAPACITY;
                case 2 -> Math.min(steamTank.getFluidAmount(), Short.MAX_VALUE);
                case 3 -> Math.min(STEAM_CAPACITY, Short.MAX_VALUE);
                case 4 -> Math.min(waterTank.getFluidAmount(), Short.MAX_VALUE);
                case 5 -> Math.min(WATER_CAPACITY, Short.MAX_VALUE);
                case 6 -> feRate;
                case 7 -> turbineCount;
                case 8 -> structureValid ? 1 : 0;
                default -> 0;
            };
        }
        @Override public void set(int i, int v) {}
        @Override public int getCount() { return 9; }
    };

    public CoolingTowerControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COOLING_TOWER_CONTROLLER.get(), pos, state, CAPACITY, MAX_FE_OUT, MAX_FE_OUT);
    }

    public FluidTank getSteamTank()                { return steamTank; }
    public FluidTank getWaterTank()                { return waterTank; }
    public List<BlockPos> getTurbinePositions()    { return Collections.unmodifiableList(turbinePositions); }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.cooling_tower_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new CoolingTowerMenu(id, inv, data);
    }

    public void serverTick() {
        if (level == null) return;

        scanTick++;
        if (scanTick >= 100) {
            scanTick = 0;
            boolean valid = checkStructure();
            if (valid != structureValid) {
                structureValid = valid;
                setChanged();
            }
        }

        LOGGER.debug("[CoolingTower@{}] buffer={}/{}FE  steam={}/{}mB  water={}/{}mB  turbines={}  valid={}",
                worldPosition.toShortString(),
                energy.getEnergyStored(), CAPACITY,
                steamTank.getFluidAmount(), STEAM_CAPACITY,
                waterTank.getFluidAmount(), WATER_CAPACITY,
                turbineCount, structureValid);

        if (!structureValid) {
            if (feRate != 0) { feRate = 0; setChanged(); }
            return;
        }

        if (turbineCount == 0) {
            LOGGER.debug("[CoolingTower@{}] Valid structure but 0 turbines — no generation", worldPosition.toShortString());
            if (feRate != 0) { feRate = 0; setChanged(); }
            pushEnergyToNeighbors();
            return;
        }

        // Consume steam → generate FE → condense water
        int activeTurbines = Math.min(turbineCount, MAX_TURBINES);
        int steamNeeded = activeTurbines * STEAM_PER_TICK;
        int steamAvail  = steamTank.getFluidAmount();
        int energyRoom  = CAPACITY - energy.getEnergyStored();

        LOGGER.debug("[CoolingTower@{}] tick — turbines={} steamAvail={} steamNeeded={} energyRoom={}",
                worldPosition.toShortString(), activeTurbines, steamAvail, steamNeeded, energyRoom);

        if (steamAvail >= steamNeeded && energyRoom > 0) {
            steamTank.drain(steamNeeded, IFluidHandler.FluidAction.EXECUTE);
            int generated = activeTurbines * FE_PER_TURBINE;
            energy.receiveEnergy(generated, false);
            feRate = generated;

            // Condense steam → water (1:1 mB)
            FluidStack waterOut = new FluidStack(net.minecraft.world.level.material.Fluids.WATER, steamNeeded);
            waterTank.fill(waterOut, IFluidHandler.FluidAction.EXECUTE);
            setChanged();

            LOGGER.debug("[CoolingTower@{}] generated {}FE, consumed {}mB steam, condensed {}mB water",
                    worldPosition.toShortString(), generated, steamNeeded, steamNeeded);
        } else {
            if (feRate != 0) { feRate = 0; setChanged(); }
            LOGGER.debug("[CoolingTower@{}] generation stalled — steam={}/{} energyRoom={}",
                    worldPosition.toShortString(), steamAvail, steamNeeded, energyRoom);
        }

        pushEnergyToNeighbors();
    }

    private void pushEnergyToNeighbors() {
        if (level == null) return;
        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() <= 0) return;
            BlockPos nb = worldPosition.relative(dir);
            IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, nb, dir.getOpposite());
            if (cap == null || !cap.canReceive()) continue;
            int toSend = energy.extractEnergy(MAX_FE_OUT, true);
            if (toSend <= 0) continue;
            int accepted = cap.receiveEnergy(toSend, false);
            if (accepted > 0) {
                energy.extractEnergy(accepted, false);
                setChanged();
                LOGGER.debug("[CoolingTower@{}] pushed {}FE to {} ({})",
                        worldPosition.toShortString(), accepted, dir, nb.toShortString());
            }
        }
    }

    // ── Structure validation: 5×5×8 ─────────────────────────────────────────

    public boolean checkStructure() {
        if (level == null) return false;
        String firstFail = null;

        for (int dx = 0; dx <= 4; dx++) {
            for (int dz = 0; dz <= 4; dz++) {
                BlockPos origin = worldPosition.offset(-dx, 0, -dz);

                if (!quickBaseCheck(origin)) continue;

                String fail = validateAt(origin);
                if (fail == null) {
                    countAndStoreTurbines(origin);
                    turbineCount = turbinePositions.size();
                    if (!Boolean.TRUE.equals(lastValidState)) {
                        LOGGER.info("[CoolingTower@{}] Structure VALID — origin={} turbines={}",
                                worldPosition.toShortString(), origin.toShortString(), turbineCount);
                        lastValidState = true;
                    }
                    return true;
                }
                if (firstFail == null) firstFail = fail;
            }
        }

        if (!Boolean.FALSE.equals(lastValidState)) {
            turbinePositions.clear();
            turbineCount = 0;
            LOGGER.info("[CoolingTower@{}] Structure INVALID — {}",
                    worldPosition.toShortString(), firstFail != null ? firstFail : "no valid base found");
            lastValidState = false;
        }
        return false;
    }

    private boolean quickBaseCheck(BlockPos origin) {
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                Block b = level.getBlockState(origin.offset(x, 0, z)).getBlock();
                if (!isBaseBlock(b) && !isPort(b)) return false;
            }
        }
        return true;
    }

    private String validateAt(BlockPos origin) {
        // Layer 0: 5×5 cooling_tower_base (controller counts as base)
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                Block b = level.getBlockState(origin.offset(x, 0, z)).getBlock();
                if (!isBaseBlock(b) && !isPort(b))
                    return String.format("layer 0 (%d,%d): expected base/port, got %s", x, z, blockName(b));
            }
        }

        // Layers 1–6: 5×5 cooling_tower_wall border + air/steam_turbine/port interior
        for (int y = 1; y <= 6; y++) {
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    boolean border = x == 0 || x == 4 || z == 0 || z == 4;
                    BlockPos bp = origin.offset(x, y, z);
                    Block b = level.getBlockState(bp).getBlock();
                    if (border) {
                        if (b != ModBlocks.COOLING_TOWER_WALL.get() && !isPort(b))
                            return String.format("layer %d border (%d,%d): expected wall/port, got %s", y, x, z, blockName(b));
                    } else {
                        // Interior: air, steam_turbine, or port allowed
                        if (b != ModBlocks.STEAM_TURBINE.get()
                                && !level.getBlockState(bp).isAir()
                                && !isPort(b))
                            return String.format("layer %d interior (%d,%d): expected air/steam_turbine/port, got %s", y, x, z, blockName(b));
                    }
                }
            }
        }

        // Layer 7: 5×5 cooling_tower_vent
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                Block b = level.getBlockState(origin.offset(x, 7, z)).getBlock();
                if (b != ModBlocks.COOLING_TOWER_VENT.get() && !isPort(b))
                    return String.format("layer 7 (%d,%d): expected vent/port, got %s", x, z, blockName(b));
            }
        }

        return null;
    }

    private void countAndStoreTurbines(BlockPos origin) {
        turbinePositions.clear();
        // Interior of 5×5 is x=1..3, z=1..3 (border = 0 and 4); layers 1–6
        for (int y = 1; y <= 6; y++) {
            for (int x = 1; x < 4; x++) {
                for (int z = 1; z < 4; z++) {
                    BlockPos bp = origin.offset(x, y, z);
                    Block found = level.getBlockState(bp).getBlock();
                    if (found == ModBlocks.STEAM_TURBINE.get()) {
                        turbinePositions.add(bp.immutable());
                        LOGGER.info("[CoolingTower@{}] turbine found at {} (layer {} interior x={} z={})",
                                worldPosition.toShortString(), bp.toShortString(), y, x, z);
                    } else {
                        LOGGER.debug("[CoolingTower@{}] scan ({},{},{}) → {}",
                                worldPosition.toShortString(), x, y, z,
                                BuiltInRegistries.BLOCK.getKey(found));
                    }
                }
            }
        }
        LOGGER.info("[CoolingTower@{}] turbine scan complete — found {} turbine(s) at origin {}",
                worldPosition.toShortString(), turbinePositions.size(), origin.toShortString());
    }

    private boolean isBaseBlock(Block b) {
        return b == ModBlocks.COOLING_TOWER_BASE.get() || b == ModBlocks.COOLING_TOWER_CONTROLLER.get();
    }

    private boolean isPort(Block b) {
        return b == ModBlocks.ENERGY_PORT.get() || b == ModBlocks.FLUID_PORT.get();
    }

    private String blockName(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).toString();
    }

    // ── Multiblock preview ghost ────────────────────────────────────────────────
    private boolean previewActive = false;

    @Override
    public boolean isPreviewActive() { return previewActive; }

    @Override
    public void setPreviewActive(boolean active) {
        previewActive = active;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // Canonical layout: origin is always the centre of the 5×5 footprint (the controller
    // can be placed at any of the 25 base positions, so the preview centres on it).
    @Override
    public Map<BlockPos, Block> getPreviewPositions(BlockPos origin) {
        Map<BlockPos, Block> map = new HashMap<>();
        Block base = ModBlocks.COOLING_TOWER_BASE.get();
        Block wall = ModBlocks.COOLING_TOWER_WALL.get();
        Block vent = ModBlocks.COOLING_TOWER_VENT.get();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos p = origin.offset(x, 0, z);
                if (!p.equals(origin)) map.put(p, base);
            }
        }
        for (int y = 1; y <= 6; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    boolean border = x == -2 || x == 2 || z == -2 || z == 2;
                    if (border) map.put(origin.offset(x, y, z), wall);
                }
            }
        }
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                map.put(origin.offset(x, 7, z), vent);
            }
        }
        return map;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ── NBT ─────────────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("SteamTank", steamTank.writeToNBT(registries, new CompoundTag()));
        tag.put("WaterTank", waterTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("FeRate", feRate);
        tag.putInt("TurbineCount", turbineCount);
        tag.putBoolean("StructureValid", structureValid);
        tag.putBoolean("PreviewActive", previewActive);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("SteamTank"))  steamTank.readFromNBT(registries, tag.getCompound("SteamTank"));
        if (tag.contains("WaterTank"))  waterTank.readFromNBT(registries, tag.getCompound("WaterTank"));
        feRate         = tag.getInt("FeRate");
        turbineCount   = tag.getInt("TurbineCount");
        structureValid = tag.getBoolean("StructureValid");
        previewActive  = tag.getBoolean("PreviewActive");
    }
}
