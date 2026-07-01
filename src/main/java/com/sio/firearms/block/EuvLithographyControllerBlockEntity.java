package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.EuvLithographyMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.Map;

public class EuvLithographyControllerBlockEntity extends EnergyStorageBlock implements MenuProvider, IMultiblockPreview {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int CAPACITY = 5_000_000;
    public static final int MAX_RECEIVE = 10_000;
    public static final int FE_PER_TICK = 50_000;
    public static final int PROCESS_TIME = 1200;

    private int progress = 0;
    private boolean structureValid = false;
    private boolean cleanRoom = false;

    public final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    public final FluidTank photoresistTank = new FluidTank(10_000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return !stack.isEmpty() && stack.getFluid().isSame(ModFluids.PHOTORESIST_STILL.get());
        }
        @Override
        protected void onContentsChanged() { setChanged(); }
    };

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> progress;
                case 3 -> PROCESS_TIME;
                case 4 -> structureValid ? 1 : 0;
                case 5 -> cleanRoom ? 1 : 0;
                case 6 -> photoresistTank.getFluidAmount();
                case 7 -> 10_000;
                default -> 0;
            };
        }
        @Override
        public void set(int index, int value) { if (index == 2) progress = value; }
        @Override
        public int getCount() { return 8; }
    };

    public EuvLithographyControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EUV_LITHOGRAPHY_CONTROLLER.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory() { return inventory; }
    public FluidTank getPhotoresistTank()  { return photoresistTank; }
    public boolean isStructureValid()      { return structureValid; }
    public boolean isCleanRoom()           { return cleanRoom; }

    // Fill-only wrapper — pipes and fluid ports push photoresist in; machine drains internally
    public final IFluidHandler fillOnlyPhotoresistHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int tank) { return photoresistTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int tank) { return photoresistTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int tank, FluidStack stack) { return photoresistTank.isFluidValid(0, stack); }
        @Override public int fill(FluidStack resource, FluidAction action) { return photoresistTank.fill(resource, action); }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    };

    public IFluidHandler getPhotoresistInputHandler() { return fillOnlyPhotoresistHandler; }

    // Input-only machine for fluid; fullAccessHandler delegates entirely to fillOnlyPhotoresistHandler
    public final IFluidHandler fullAccessHandler = fillOnlyPhotoresistHandler;

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.euv_lithography_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new EuvLithographyMenu(id, inv, inventory, data);
    }

    private boolean stackIs(ItemStack stack, String id) {
        if (stack.isEmpty()) return false;
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(id);
    }

    private boolean canOutput(ItemStack result, ItemStack outputSlot) {
        if (outputSlot.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(outputSlot, result)) return false;
        return outputSlot.getCount() + result.getCount() <= outputSlot.getMaxStackSize();
    }

    public void serverTick() {
        if (level == null) return;
        if (level.getGameTime() % 40 == 0) checkStructure();
        if (!structureValid || !cleanRoom) {
            if (progress > 0) { progress = 0; setChanged(); }
            return;
        }

        ItemStack in0  = inventory.getStackInSlot(0);
        ItemStack mask = inventory.getStackInSlot(1);
        ItemStack out  = inventory.getStackInSlot(2);
        ItemStack result = new ItemStack(ModItems.PATTERNED_WAFER.get());

        boolean hasMask = !mask.isEmpty() && stackIs(mask, "firearms:photomask")
                && mask.getDamageValue() < mask.getMaxDamage();
        boolean hasResist = photoresistTank.getFluidAmount() >= 500;
        boolean canProcess = stackIs(in0, "firearms:coated_wafer") && hasMask && hasResist
                && canOutput(result, out) && energy.getEnergyStored() >= FE_PER_TICK;

        if (canProcess) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            if (progress >= PROCESS_TIME) {
                in0.shrink(1);
                photoresistTank.drain(500, IFluidHandler.FluidAction.EXECUTE);
                mask.setDamageValue(mask.getDamageValue() + 1);
                if (mask.getDamageValue() >= mask.getMaxDamage()) inventory.setStackInSlot(1, ItemStack.EMPTY);
                if (out.isEmpty()) inventory.setStackInSlot(2, result.copy());
                else out.grow(1);
                progress = 0;
            }
            setChanged();

            if (level instanceof ServerLevel serverLevel && level.getGameTime() % 5 == 0) {
                double cx = worldPosition.getX() + 0.5;
                double cy = worldPosition.getY() + 2.5;
                double cz = worldPosition.getZ() + 0.5;
                serverLevel.sendParticles(ParticleTypes.WITCH, cx, cy, cz, 3, 0.5, 0.5, 0.5, 0.05);
            }
        } else if (progress > 0) { progress = 0; setChanged(); }
    }

    public boolean checkStructure() {
        if (level == null) return false;
        LOGGER.info("[EUV] checkStructure() called, controller at {}", worldPosition);

        // Try all 25 positions of the 5x5 grid as the controller's grid position (anywhere
        // in the footprint, not just the border). origin is the SW corner (min x, min z);
        // structure extends +X, +Z from there.
        for (int ox = 0; ox <= 4; ox++) {
            for (int oz = 0; oz <= 4; oz++) {
                BlockPos origin = worldPosition.offset(-ox, 0, -oz);
                LOGGER.info("[EUV] Trying origin {} (ox={} oz={})", origin, ox, oz);
                if (isValidAt(origin)) {
                    structureValid = true;
                    checkCleanRoom(origin);
                    LOGGER.info("[EUV] Structure VALID — origin {}", origin);
                    return true;
                }
            }
        }

        LOGGER.info("[EUV] Structure INVALID — no valid origin found");
        structureValid = false;
        cleanRoom = false;
        return false;
    }

    private boolean isPort(Block b) {
        return b == ModBlocks.ENERGY_PORT.get() || b == ModBlocks.FLUID_PORT.get();
    }

    private boolean isValidAt(BlockPos origin) {
        // Layer 0: early-exit per position to quickly discard wrong origin candidates
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                Block b = level.getBlockState(origin.offset(x, 0, z)).getBlock();
                if (b != ModBlocks.EUV_BASE.get() && b != ModBlocks.EUV_LITHOGRAPHY_CONTROLLER.get()
                        && !isPort(b)) {
                    LOGGER.info("[EUV]   FAIL y=0 x={} z={} expected euv_base/controller/port, found {}",
                            x, z, BuiltInRegistries.BLOCK.getKey(b));
                    return false;
                }
            }
        }
        LOGGER.info("[EUV]   PASS y=0 base layer for origin {}", origin);

        // Layers 1-2: border (16 positions) must be euv_wall or a port; interior 3x3 may be air
        boolean ok = true;
        for (int y = 1; y <= 2; y++) {
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    if (x > 0 && x < 4 && z > 0 && z < 4) continue;
                    Block b = level.getBlockState(origin.offset(x, y, z)).getBlock();
                    if (b != ModBlocks.EUV_WALL.get() && !isPort(b)) {
                        LOGGER.info("[EUV]   FAIL y={} x={} z={} expected euv_wall/port, found {}",
                                y, x, z, BuiltInRegistries.BLOCK.getKey(b));
                        ok = false;
                    }
                }
            }
        }
        if (!ok) return false;
        LOGGER.info("[EUV]   PASS y=1-2 wall layers");

        // Layers 3-4: 4 corners = euv_mirror_array, 12 border non-corner positions = euv_wall,
        // interior 3x3 may be air; ports are valid at any border position
        ok = true;
        for (int y = 3; y <= 4; y++) {
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    boolean corner = (x == 0 || x == 4) && (z == 0 || z == 4);
                    boolean interior = (x > 0 && x < 4) && (z > 0 && z < 4);
                    if (interior) continue;
                    Block b = level.getBlockState(origin.offset(x, y, z)).getBlock();
                    if (isPort(b)) continue;
                    if (corner) {
                        if (b != ModBlocks.EUV_MIRROR_ARRAY.get()) {
                            LOGGER.info("[EUV]   FAIL y={} x={} z={} expected euv_mirror_array/port, found {}",
                                    y, x, z, BuiltInRegistries.BLOCK.getKey(b));
                            ok = false;
                        }
                    } else {
                        if (b != ModBlocks.EUV_WALL.get()) {
                            LOGGER.info("[EUV]   FAIL y={} x={} z={} expected euv_wall/port, found {}",
                                    y, x, z, BuiltInRegistries.BLOCK.getKey(b));
                            ok = false;
                        }
                    }
                }
            }
        }
        if (!ok) return false;
        LOGGER.info("[EUV]   PASS y=3-4 mirror layers");

        // Layers 5-6: all 25 positions = euv_emitter_housing or a port
        ok = true;
        for (int y = 5; y <= 6; y++) {
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    Block b = level.getBlockState(origin.offset(x, y, z)).getBlock();
                    if (b != ModBlocks.EUV_EMITTER_HOUSING.get() && !isPort(b)) {
                        LOGGER.info("[EUV]   FAIL y={} x={} z={} expected euv_emitter_housing/port, found {}",
                                y, x, z, BuiltInRegistries.BLOCK.getKey(b));
                        ok = false;
                    }
                }
            }
        }
        if (!ok) return false;
        LOGGER.info("[EUV]   PASS y=5-6 emitter layers");

        return true;
    }

    private void checkCleanRoom(BlockPos origin) {
        cleanRoom = true;
        for (int x = -2; x < 7; x++) {
            for (int z = -2; z < 7; z++) {
                for (int y = 1; y <= 6; y++) {
                    if (x >= 0 && x < 5 && z >= 0 && z < 5) continue;
                    if (x < -2 || x > 6 || z < -2 || z > 6) continue;
                    BlockPos check = origin.offset(x, y, z);
                    Block b = level.getBlockState(check).getBlock();
                    if (!level.getBlockState(check).isAir() && b != ModBlocks.EUV_WALL.get()) {
                        LOGGER.info("[EUV]   clean room FAIL at {}: found {}", check,
                                BuiltInRegistries.BLOCK.getKey(b));
                        cleanRoom = false;
                        return;
                    }
                }
            }
        }
        LOGGER.info("[EUV]   PASS clean room check");
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

    // Canonical layout: origin is the front-center cell at ground level (the 5×5
    // footprint), with the structure extending back (+z) and to the sides.
    @Override
    public Map<BlockPos, Block> getPreviewPositions(BlockPos origin) {
        Map<BlockPos, Block> map = new HashMap<>();
        Block base   = ModBlocks.EUV_BASE.get();
        Block wall   = ModBlocks.EUV_WALL.get();
        Block mirror = ModBlocks.EUV_MIRROR_ARRAY.get();
        Block housing = ModBlocks.EUV_EMITTER_HOUSING.get();
        for (int x = -2; x <= 2; x++) {
            for (int z = 0; z <= 4; z++) {
                BlockPos p = origin.offset(x, 0, z);
                if (!p.equals(origin)) map.put(p, base);
            }
        }
        for (int y = 1; y <= 2; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = 0; z <= 4; z++) {
                    if (x > -2 && x < 2 && z > 0 && z < 4) continue;
                    map.put(origin.offset(x, y, z), wall);
                }
            }
        }
        for (int y = 3; y <= 4; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = 0; z <= 4; z++) {
                    boolean corner = (x == -2 || x == 2) && (z == 0 || z == 4);
                    boolean interior = (x > -2 && x < 2) && (z > 0 && z < 4);
                    if (interior) continue;
                    map.put(origin.offset(x, y, z), corner ? mirror : wall);
                }
            }
        }
        for (int y = 5; y <= 6; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = 0; z <= 4; z++) {
                    map.put(origin.offset(x, y, z), housing);
                }
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

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
        tag.putBoolean("StructureValid", structureValid);
        tag.putBoolean("CleanRoom", cleanRoom);
        tag.put("PhotoresistTank", photoresistTank.writeToNBT(registries, new CompoundTag()));
        tag.putBoolean("PreviewActive", previewActive);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
        structureValid = tag.getBoolean("StructureValid");
        cleanRoom = tag.getBoolean("CleanRoom");
        if (tag.contains("PhotoresistTank")) photoresistTank.readFromNBT(registries, tag.getCompound("PhotoresistTank"));
        previewActive = tag.getBoolean("PreviewActive");
    }
}
