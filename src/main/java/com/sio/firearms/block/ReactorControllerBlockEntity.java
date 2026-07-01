package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.item.HazmatSuitItem;
import com.sio.firearms.menu.ReactorMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModEffects;
import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactorControllerBlockEntity extends EnergyStorageBlock implements MenuProvider, IMultiblockPreview {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int CAPACITY            = 100_000;
    public static final int MAX_FE_PER_TICK     = 2_000;
    public static final int FE_PER_ROD_TICK     = 500;
    public static final int WATER_CAPACITY      = 50_000;
    public static final int STEAM_CAPACITY      = 50_000;
    public static final int WATER_PER_TICK      = 100;
    public static final int STEAM_PER_TICK      = 100;
    public static final int FUEL_ROD_TICKS      = 144_000;
    public static final int MELTDOWN_THRESHOLD  = 200;
    public static final int MAX_TEMPERATURE     = 1000;
    public static final int TEMP_RISE_PER_TICK  = 5;
    public static final int TEMP_FALL_PER_TICK  = 2;
    public static final float EXPLOSION_RADIUS  = 8.0f;
    public static final int RADIATION_RADIUS    = 10;

    private boolean structureValid = false;
    private Boolean lastValidState = null;
    private final List<BlockPos> controlRodHousingPositions = new ArrayList<>();
    private int temperature  = 0;
    private int meltdownTicks = 0;
    private int feOutputRate  = 0;
    private int operationalTicks = 0;

    // 0-3 = fuel_rod_assembly, 4-5 = control_rod
    public final ItemStackHandler inventory = new ItemStackHandler(6) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot < 4) return stack.is(ModItems.FUEL_ROD_ASSEMBLY.get());
            return stack.is(ModItems.CONTROL_ROD.get());
        }
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    public final FluidTank waterTank = new FluidTank(WATER_CAPACITY,
            fs -> fs.getFluid().isSame(Fluids.WATER)) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    public final FluidTank steamTank = new FluidTank(STEAM_CAPACITY,
            fs -> fs.getFluid().isSame(ModFluids.STEAM_STILL.get())) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    // Fill-only (water input)
    public final IFluidHandler waterInputHandler = new IFluidHandler() {
        @Override public int getTanks()                              { return 1; }
        @Override public FluidStack getFluidInTank(int t)           { return waterTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t)                  { return waterTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack s)   { return waterTank.isFluidValid(0, s); }
        @Override public int fill(FluidStack r, FluidAction a)       { return waterTank.fill(r, a); }
        @Override public FluidStack drain(FluidStack r, FluidAction a) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int max, FluidAction a)    { return FluidStack.EMPTY; }
    };

    // Drain-only (steam output)
    public final IFluidHandler steamOutputHandler = new IFluidHandler() {
        @Override public int getTanks()                              { return 1; }
        @Override public FluidStack getFluidInTank(int t)           { return steamTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t)                  { return steamTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack s)   { return false; }
        @Override public int fill(FluidStack r, FluidAction a)       { return 0; }
        @Override public FluidStack drain(FluidStack r, FluidAction a) { return steamTank.drain(r, a); }
        @Override public FluidStack drain(int max, FluidAction a)    { return steamTank.drain(max, a); }
    };

    private final ContainerData data = new ContainerData() {
        @Override public int get(int i) {
            return switch (i) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> feOutputRate;
                case 3 -> temperature;
                case 4 -> waterTank.getFluidAmount();
                case 5 -> WATER_CAPACITY;
                case 6 -> steamTank.getFluidAmount();
                case 7 -> STEAM_CAPACITY;
                case 8 -> structureValid ? 1 : 0;
                case 9 -> meltdownTicks;
                default -> 0;
            };
        }
        @Override public void set(int i, int v) {}
        @Override public int getCount() { return 10; }
    };

    public ReactorControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REACTOR_CONTROLLER.get(), pos, state, CAPACITY, MAX_FE_PER_TICK, MAX_FE_PER_TICK);
    }

    public ItemStackHandler getInventory()              { return inventory; }
    public FluidTank getWaterTank()                    { return waterTank; }
    public FluidTank getSteamTank()                    { return steamTank; }
    public boolean isStructureValid()                  { return structureValid; }
    public int getTemperature()                        { return temperature; }
    public List<BlockPos> getControlRodHousingPositions() {
        return Collections.unmodifiableList(controlRodHousingPositions);
    }

    public int getControlRodCount() {
        int n = 0;
        if (!inventory.getStackInSlot(4).isEmpty()) n++;
        if (!inventory.getStackInSlot(5).isEmpty()) n++;
        return n;
    }

    public int getFuelRodCount() {
        int n = 0;
        for (int i = 0; i < 4; i++) if (!inventory.getStackInSlot(i).isEmpty()) n++;
        return n;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.reactor_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ReactorMenu(id, inv, inventory, data);
    }

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;

        if (level.getGameTime() % 100 == 0) {
            boolean was = structureValid;
            structureValid = checkStructure();
            if (was != structureValid) changed = true;
        }

        if (!structureValid) {
            if (temperature > 0) { temperature = Math.max(0, temperature - TEMP_FALL_PER_TICK); changed = true; }
            meltdownTicks = 0;
            feOutputRate  = 0;
            if (changed) setChanged();
            return;
        }

        int fuelRods    = getFuelRodCount();
        int controlRods = getControlRodCount();
        boolean canRun  = fuelRods > 0
                && waterTank.getFluidAmount() >= WATER_PER_TICK
                && steamTank.getFluidAmount() + STEAM_PER_TICK <= STEAM_CAPACITY;

        if (canRun) {
            // Power with rod count only; control rods reduce by 25% each
            float mult = Math.max(0.0f, 1.0f - controlRods * 0.25f);
            feOutputRate = (int)(fuelRods * FE_PER_ROD_TICK * mult);

            energy.receiveEnergy(feOutputRate, false);
            waterTank.drain(WATER_PER_TICK, IFluidHandler.FluidAction.EXECUTE);
            steamTank.fill(new FluidStack(ModFluids.STEAM_STILL.get(), STEAM_PER_TICK), IFluidHandler.FluidAction.EXECUTE);
            changed = true;

            // Degrade one fuel rod assembly every FUEL_ROD_TICKS operational ticks
            operationalTicks++;
            if (operationalTicks >= FUEL_ROD_TICKS) {
                operationalTicks = 0;
                for (int i = 0; i < 4; i++) {
                    if (!inventory.getStackInSlot(i).isEmpty()
                            && inventory.getStackInSlot(i).is(ModItems.FUEL_ROD_ASSEMBLY.get())) {
                        inventory.setStackInSlot(i, new ItemStack(ModItems.SPENT_FUEL_ROD.get()));
                        break;
                    }
                }
            }

            // Meltdown logic: full power with 0 control rods raises temperature
            if (controlRods == 0) {
                meltdownTicks++;
                if (meltdownTicks > MELTDOWN_THRESHOLD) {
                    temperature = Math.min(MAX_TEMPERATURE, temperature + TEMP_RISE_PER_TICK);
                    if (temperature >= MAX_TEMPERATURE) {
                        triggerMeltdown();
                        return;
                    }
                }
            } else {
                meltdownTicks = Math.max(0, meltdownTicks - 1);
                temperature   = Math.max(0, temperature - TEMP_FALL_PER_TICK);
            }

            // Radiation to nearby players when unshielded
            if (controlRods == 0 && level.getGameTime() % 20 == 0) {
                applyRadiationToNearbyPlayers();
            }

        } else {
            feOutputRate  = 0;
            meltdownTicks = Math.max(0, meltdownTicks - 1);
            if (temperature > 0) { temperature = Math.max(0, temperature - TEMP_FALL_PER_TICK); changed = true; }
        }

        if (energy.getEnergyStored() > 0 && pushEnergyToNeighbors()) changed = true;

        if (changed) setChanged();
    }

    private void triggerMeltdown() {
        if (level != null) {
            level.explode(null,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5,
                    EXPLOSION_RADIUS, Level.ExplosionInteraction.TNT);
        }
        temperature   = 0;
        meltdownTicks = 0;
    }

    private void applyRadiationToNearbyPlayers() {
        AABB box = new AABB(worldPosition).inflate(RADIATION_RADIUS);
        List<Player> nearby = level.getEntitiesOfClass(Player.class, box);
        for (Player p : nearby) {
            if (!HazmatSuitItem.hasFullSet(p)) {
                p.addEffect(new MobEffectInstance(ModEffects.RADIATION, 100, 0, false, true));
            }
        }
    }

    private boolean pushEnergyToNeighbors() {
        boolean pushed = false;
        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() <= 0) break;
            BlockPos nb = worldPosition.relative(dir);
            IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, nb, dir.getOpposite());
            if (cap != null && cap.canReceive()) {
                int sim = energy.extractEnergy(MAX_FE_PER_TICK, true);
                int rec = cap.receiveEnergy(sim, false);
                if (rec > 0) { energy.extractEnergy(rec, false); pushed = true; }
            }
        }
        return pushed;
    }

    // ── Structure validation: 7×7×9 multiblock ────────────────────────────────
    // Layers 0–1 : REACTOR_BASE (full 7×7; REACTOR_CONTROLLER counts)
    // Layers 2–6 : REACTOR_WALL on border, air or LEAD_BLOCK inside
    // Layer  7   : REACTOR_WALL on border, REACTOR_CONTROL_ROD_HOUSING at x=3 z=1..5
    // Layer  8   : REACTOR_TOP (full 7×7)
    // EnergyPort / FluidPort substitute for any structural block at any position.
    //
    // Origin = minimum-X, minimum-Z corner of the 7×7 base.
    // (dx, dz) = controller's grid-offset from that origin, which must be on the border.
    //   dx=0 → west face;  dx=6 → east face
    //   dz=0 → north face (origin.z == controller.z);  dz=6 → south face
    public boolean checkStructure() {
        if (level == null) return false;

        String firstFail = null;

        // Try all 49 positions of a 7×7 grid as the controller's possible location within
        // the grid (anywhere in the base layer, not just the border).
        // Origin = worldPosition.offset(-dx, 0, -dz).
        for (int dx = 0; dx <= 6; dx++) {
            for (int dz = 0; dz <= 6; dz++) {
                BlockPos origin = worldPosition.offset(-dx, 0, -dz);

                LOGGER.debug("[Reactor@{}] Trying origin {} (controller at dx={} dz={})",
                        worldPosition.toShortString(), origin.toShortString(), dx, dz);

                // Fast pre-check: all 49 positions at controller Y must be base/controller/port.
                // This avoids a full 9-layer scan when the base layer already fails.
                if (!quickBaseCheck(origin)) {
                    LOGGER.debug("[Reactor@{}]   origin {} — base layer mismatch, skipping",
                            worldPosition.toShortString(), origin.toShortString());
                    continue;
                }

                String fail = validateAt(origin);
                if (fail == null) {
                    scanControlRodHousings(origin);
                    if (!Boolean.TRUE.equals(lastValidState)) {
                        LOGGER.info("[Reactor@{}] Structure VALID — origin={} controller-offset=({},{}) housings={}",
                                worldPosition.toShortString(), origin.toShortString(), dx, dz,
                                controlRodHousingPositions.size());
                        lastValidState = true;
                    }
                    return true;
                }
                LOGGER.debug("[Reactor@{}]   origin {} — FAILED: {}",
                        worldPosition.toShortString(), origin.toShortString(), fail);
                if (firstFail == null) firstFail = fail;
            }
        }

        if (!Boolean.FALSE.equals(lastValidState)) {
            controlRodHousingPositions.clear();
            LOGGER.info("[Reactor@{}] Structure INVALID — {}",
                    worldPosition.toShortString(), firstFail != null ? firstFail : "no valid base found");
            lastValidState = false;
        }
        return false;
    }

    /** Populates controlRodHousingPositions from the 5×5 interior of layer 7. */
    private void scanControlRodHousings(BlockPos origin) {
        controlRodHousingPositions.clear();
        for (int x = 1; x <= 5; x++) {
            for (int z = 1; z <= 5; z++) {
                BlockPos bp = origin.offset(x, 7, z);
                if (level.getBlockState(bp).getBlock() == ModBlocks.REACTOR_CONTROL_ROD_HOUSING.get())
                    controlRodHousingPositions.add(bp.immutable());
            }
        }
        LOGGER.debug("[Reactor@{}] Found {} control rod housing(s) in layer 7: {}",
                worldPosition.toShortString(), controlRodHousingPositions.size(),
                controlRodHousingPositions.stream().map(BlockPos::toShortString).toList());
    }

    /** All 49 positions at controller Y (layer 0 relative to origin) must be base/controller/port. */
    private boolean quickBaseCheck(BlockPos origin) {
        for (int x = 0; x < 7; x++)
            for (int z = 0; z < 7; z++) {
                Block b = level.getBlockState(origin.offset(x, 0, z)).getBlock();
                if (!isBaseBlock(b) && !isPort(b)) return false;
            }
        return true;
    }

    private boolean isPort(Block b) {
        return b == ModBlocks.ENERGY_PORT.get() || b == ModBlocks.FLUID_PORT.get();
    }

    private boolean isBaseBlock(Block b) {
        return b == ModBlocks.REACTOR_BASE.get() || b == ModBlocks.REACTOR_CONTROLLER.get();
    }

    private String blockName(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).toString();
    }

    /** Returns null if valid at this origin, or a description of the first failure. */
    private String validateAt(BlockPos origin) {
        // Layers 0–1: full 7×7 of reactor_base / controller / port
        for (int y = 0; y <= 1; y++) {
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 7; z++) {
                    Block b = level.getBlockState(origin.offset(x, y, z)).getBlock();
                    if (!isBaseBlock(b) && !isPort(b))
                        return String.format("layer %d (%d,%d): expected reactor_base/controller, got %s", y, x, z, blockName(b));
                }
            }
        }
        // Layers 2–6: wall on border, air/lead/port inside
        for (int y = 2; y <= 6; y++) {
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 7; z++) {
                    boolean interior = x > 0 && x < 6 && z > 0 && z < 6;
                    BlockPos bp = origin.offset(x, y, z);
                    Block b = level.getBlockState(bp).getBlock();
                    if (interior) {
                        if (b != ModBlocks.LEAD_BLOCK.get() && !level.getBlockState(bp).isAir() && !isPort(b))
                            return String.format("layer %d interior (%d,%d): expected air/lead/port, got %s", y, x, z, blockName(b));
                    } else {
                        if (b != ModBlocks.REACTOR_WALL.get() && !isPort(b))
                            return String.format("layer %d border (%d,%d): expected reactor_wall/port, got %s", y, x, z, blockName(b));
                    }
                }
            }
        }
        // Layer 7: wall on border; interior (5×5) freely accepts housing/air/lead/port
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                boolean interior = x > 0 && x < 6 && z > 0 && z < 6;
                BlockPos bp = origin.offset(x, 7, z);
                Block b = level.getBlockState(bp).getBlock();
                if (!interior) {
                    if (b != ModBlocks.REACTOR_WALL.get() && !isPort(b))
                        return String.format("layer 7 border (%d,%d): expected reactor_wall/port, got %s", x, z, blockName(b));
                } else {
                    if (b != ModBlocks.REACTOR_CONTROL_ROD_HOUSING.get()
                            && b != ModBlocks.LEAD_BLOCK.get()
                            && !level.getBlockState(bp).isAir()
                            && !isPort(b))
                        return String.format("layer 7 interior (%d,%d): expected housing/air/lead/port, got %s", x, z, blockName(b));
                }
            }
        }
        // Layer 8: full 7×7 of reactor_top / port
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                Block b = level.getBlockState(origin.offset(x, 8, z)).getBlock();
                if (b != ModBlocks.REACTOR_TOP.get() && !isPort(b))
                    return String.format("layer 8 (%d,%d): expected reactor_top/port, got %s", x, z, blockName(b));
            }
        }
        return null; // valid
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

    // Canonical layout: origin is the front-center cell at ground level (the 7×7
    // footprint), with the structure extending back (+z) and to the sides.
    @Override
    public Map<BlockPos, Block> getPreviewPositions(BlockPos origin) {
        Map<BlockPos, Block> map = new HashMap<>();
        Block base = ModBlocks.REACTOR_BASE.get();
        Block wall = ModBlocks.REACTOR_WALL.get();
        Block top  = ModBlocks.REACTOR_TOP.get();
        for (int y = 0; y <= 1; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = 0; z <= 6; z++) {
                    BlockPos p = origin.offset(x, y, z);
                    if (!p.equals(origin)) map.put(p, base);
                }
            }
        }
        for (int y = 2; y <= 7; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = 0; z <= 6; z++) {
                    boolean interior = x > -3 && x < 3 && z > 0 && z < 6;
                    if (interior) continue;
                    map.put(origin.offset(x, y, z), wall);
                }
            }
        }
        for (int x = -3; x <= 3; x++) {
            for (int z = 0; z <= 6; z++) {
                map.put(origin.offset(x, 8, z), top);
            }
        }
        return map;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.put("WaterTank", waterTank.writeToNBT(registries, new CompoundTag()));
        tag.put("SteamTank", steamTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("Temperature", temperature);
        tag.putBoolean("PreviewActive", previewActive);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.put("WaterTank", waterTank.writeToNBT(registries, new CompoundTag()));
        tag.put("SteamTank", steamTank.writeToNBT(registries, new CompoundTag()));
        tag.putBoolean("StructureValid", structureValid);
        tag.putInt("Temperature",     temperature);
        tag.putInt("MeltdownTicks",   meltdownTicks);
        tag.putInt("FeOutputRate",    feOutputRate);
        tag.putInt("OperationalTicks", operationalTicks);
        tag.putBoolean("PreviewActive", previewActive);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory"))  inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("WaterTank"))  waterTank.readFromNBT(registries, tag.getCompound("WaterTank"));
        if (tag.contains("SteamTank"))  steamTank.readFromNBT(registries, tag.getCompound("SteamTank"));
        structureValid    = tag.getBoolean("StructureValid");
        temperature       = tag.getInt("Temperature");
        meltdownTicks     = tag.getInt("MeltdownTicks");
        feOutputRate      = tag.getInt("FeOutputRate");
        operationalTicks  = tag.getInt("OperationalTicks");
        previewActive     = tag.getBoolean("PreviewActive");
    }
}
