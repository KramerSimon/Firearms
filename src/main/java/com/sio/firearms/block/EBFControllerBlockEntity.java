package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.EBFMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EBFControllerBlockEntity extends EnergyStorageBlock implements MenuProvider, IMultiblockPreview {

    public static final int MAX_PROCESS_TIME = 400;
    public static final int FE_PER_TICK = 200;
    public static final int CAPACITY = 100_000;
    public static final int MAX_RECEIVE = 1_000;

    private int progress = 0;
    private boolean structureValid = false;
    private int installedCoilTemp = 0;
    private int requiredTemp = 0;   // temperature the loaded recipe needs (0 = no recipe)
    private boolean active = false; // currently smelting
    private boolean enabled = false; // toggled by the Start/Stop button in the GUI

    // Slot 0 = material, slot 2 = output (slot 1 is legacy/unused — smelting is now
    // powered purely by FE, no additive). Fed and drained by the import/output buses.
    public final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> progress;
                case 3 -> MAX_PROCESS_TIME;
                case 4 -> structureValid ? 1 : 0;
                case 5 -> installedCoilTemp;
                case 6 -> requiredTemp;
                case 7 -> active ? 1 : 0;
                case 8 -> enabled ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 2) progress = value;
            else if (index == 8) enabled = value != 0;
        }

        @Override
        public int getCount() {
            return 9;
        }
    };

    public EBFControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EBF_CONTROLLER.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory() { return inventory; }

    public boolean isEnabled() { return enabled; }

    public void toggleEnabled() {
        enabled = !enabled;
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.ebf_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new EBFMenu(containerId, playerInventory, data, this);
    }

    // ── Item routing for the import / output buses ──────────────────────────────
    // Smelting is powered purely by FE now, so everything is material (slot 0).
    /** Insert from an import bus; returns the remainder that did not fit. */
    public ItemStack importItem(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        return inventory.insertItem(0, stack, false);
    }

    /** Pull finished product for an output bus. */
    public ItemStack extractOutput(int max, boolean simulate) {
        return inventory.extractItem(2, max, simulate);
    }

    private boolean stackIs(ItemStack stack, String id) {
        if (stack.isEmpty()) return false;
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(id);
    }

    private ItemStack getRecipeOutput(ItemStack in0, ItemStack in1) {
        if (in0.isEmpty()) return ItemStack.EMPTY;
        // ── Two-ingredient recipes (slot 0 + slot 1) ──────────────────────────
        if (stackIs(in0, "firearms:titanium_ore_raw") && stackIs(in1, "firearms:coal_coke"))
            return new ItemStack(ModItems.TITANIUM_INGOT.get());
        if (stackIs(in0, "firearms:iridium_ore_raw") && stackIs(in1, "firearms:osmium_ore_raw"))
            return new ItemStack(ModItems.IRIDIUM_ALLOY.get());
        // New-tier two-ingredient recipes — must come before single-ingredient checks for steel/copper
        if (stackIs(in0, "firearms:steel_ingot") && stackIs(in1, "firearms:chromium_ingot"))
            return new ItemStack(ModItems.STAINLESS_STEEL_INGOT.get(), 2);
        if (stackIs(in0, "firearms:steel_ingot") && stackIs(in1, "firearms:tungsten_ingot"))
            return new ItemStack(ModItems.TOOL_STEEL_INGOT.get(), 2);
        if (stackIs(in0, "minecraft:copper_ingot") && stackIs(in1, "firearms:fluorite_crystal"))
            return new ItemStack(ModItems.BERYLLIUM_COPPER_INGOT.get(), 2);
        if (stackIs(in0, "firearms:neodymium_ore_raw") && stackIs(in1, "firearms:coal_coke"))
            return new ItemStack(ModItems.NEODYMIUM_INGOT.get());
        // ── Single-ingredient recipes ─────────────────────────────────────────
        if (stackIs(in0, "minecraft:raw_iron"))         return new ItemStack(ModItems.STEEL_INGOT.get(), 2);
        if (stackIs(in0, "minecraft:iron_ingot"))       return new ItemStack(ModItems.STEEL_INGOT.get(), 3);
        if (stackIs(in0, "firearms:steel_ingot"))       return new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 1);
        if (stackIs(in0, "minecraft:raw_gold"))         return new ItemStack(Items.GOLD_INGOT, 2);
        if (stackIs(in0, "minecraft:copper_ingot"))     return new ItemStack(ModItems.CARBON_STEEL.get(), 1);
        if (stackIs(in0, "firearms:tungsten_ore_raw"))  return new ItemStack(ModItems.TUNGSTEN_INGOT.get(), 1);
        if (stackIs(in0, "firearms:titanium_ore_raw")) return new ItemStack(ModItems.TITANIUM_INGOT.get(), 1);
        if (stackIs(in0, "firearms:uranium_ore_raw"))   return new ItemStack(ModItems.URANIUM_INGOT.get(), 1);
        if (stackIs(in0, "minecraft:coal"))             return new ItemStack(ModItems.GRAPHITE_BLOCK_ITEM.get(), 2);
        if (stackIs(in0, "firearms:zirconium_ore_raw")) return new ItemStack(ModItems.ZIRCONIUM_INGOT.get(), 1);
        if (stackIs(in0, "firearms:tungsten_ingot"))    return new ItemStack(ModItems.TUNGSTEN_CARBIDE.get(), 2);
        return ItemStack.EMPTY;
    }

    private int getRequiredTemperature(ItemStack in0, ItemStack in1) {
        if (stackIs(in0, "firearms:titanium_ore_raw") && stackIs(in1, "firearms:coal_coke")) return 1200;
        if (stackIs(in0, "firearms:iridium_ore_raw") && stackIs(in1, "firearms:osmium_ore_raw")) return 2000;
        if (stackIs(in0, "firearms:steel_ingot") && stackIs(in1, "firearms:chromium_ingot")) return 1200;
        if (stackIs(in0, "firearms:steel_ingot") && stackIs(in1, "firearms:tungsten_ingot")) return 800;
        if (stackIs(in0, "minecraft:copper_ingot") && stackIs(in1, "firearms:fluorite_crystal")) return 800;
        if (stackIs(in0, "firearms:neodymium_ore_raw") && stackIs(in1, "firearms:coal_coke")) return 2000;
        if (stackIs(in0, "firearms:uranium_ore_raw"))  return 2000;
        if (stackIs(in0, "firearms:tungsten_ore_raw")) return 1200;
        return getRecipeOutput(in0, in1).isEmpty() ? 0 : 800;
    }

    private boolean consumesAdditive(ItemStack in0, ItemStack in1) {
        if (stackIs(in0, "firearms:titanium_ore_raw") && stackIs(in1, "firearms:coal_coke")) return true;
        if (stackIs(in0, "firearms:iridium_ore_raw") && stackIs(in1, "firearms:osmium_ore_raw")) return true;
        if (stackIs(in0, "firearms:steel_ingot") && stackIs(in1, "firearms:chromium_ingot")) return true;
        if (stackIs(in0, "firearms:steel_ingot") && stackIs(in1, "firearms:tungsten_ingot")) return true;
        if (stackIs(in0, "minecraft:copper_ingot") && stackIs(in1, "firearms:fluorite_crystal")) return true;
        if (stackIs(in0, "firearms:neodymium_ore_raw") && stackIs(in1, "firearms:coal_coke")) return true;
        return false;
    }

    private boolean canOutput(ItemStack result, ItemStack outputSlot) {
        if (outputSlot.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(outputSlot, result)) return false;
        return outputSlot.getCount() + result.getCount() <= outputSlot.getMaxStackSize();
    }

    public void serverTick() {
        if (level == null) return;

        if (level.getGameTime() % 40 == 0) checkStructure();

        if (!structureValid) {
            if (progress > 0) { progress = 0; setChanged(); }
            active = false;
            requiredTemp = 0;
            return;
        }

        ItemStack in0 = inventory.getStackInSlot(0);
        ItemStack in1 = inventory.getStackInSlot(1);
        ItemStack out = inventory.getStackInSlot(2);
        ItemStack result = getRecipeOutput(in0, in1);

        requiredTemp = getRequiredTemperature(in0, in1);
        boolean canProcess = enabled
                && !result.isEmpty()
                && canOutput(result, out)
                && energy.getEnergyStored() >= FE_PER_TICK
                && installedCoilTemp >= requiredTemp;
        active = canProcess;

        if (canProcess) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            if (progress >= MAX_PROCESS_TIME) {
                boolean useAdditive = consumesAdditive(in0, in1);
                in0.shrink(1);
                if (useAdditive) in1.shrink(1);
                if (out.isEmpty()) {
                    inventory.setStackInSlot(2, result.copy());
                } else {
                    out.grow(result.getCount());
                }
                progress = 0;
            }
            setChanged();
        } else if (progress > 0) {
            progress = 0;
            setChanged();
        }

        spawnEffects(canProcess);
    }

    // ── Atmosphere ─────────────────────────────────────────────────────────────
    // Heat glow rising up the central chamber shaft and smoke venting from the
    // muffler at the centre of the roof. Heavier while actively smelting.
    private void spawnEffects(boolean smelting) {
        if (!(level instanceof ServerLevel server) || structureBack == null) return;

        long t = level.getGameTime();
        // The central heat column sits at the structure's depth-centre (d=2, r=0), which
        // may be offset from the controller's own cell now that it can sit anywhere in the shell.
        Direction right = structureBack.getClockWise();
        BlockPos axis = worldPosition.relative(structureBack, 2 - controllerD)
                .relative(right, -controllerR)
                .relative(Direction.UP, -controllerU);
        double cx = axis.getX() + 0.5;
        double cz = axis.getZ() + 0.5;

        if (t % 5 == 0) {
            for (int u = -1; u <= 1; u++) {
                server.sendParticles(ParticleTypes.FLAME,
                        cx, axis.getY() + u + 0.2, cz, smelting ? 3 : 1,
                        0.22, 0.25, 0.22, 0.01);
            }
            server.sendParticles(ParticleTypes.LARGE_SMOKE,
                    cx, axis.getY() + 1.6, cz, 1, 0.18, 0.1, 0.18, 0.0);
        }

        // Muffler exhaust at the centre of the roof (two blocks above the axis).
        if (t % 8 == 0) {
            BlockPos m = axis.relative(Direction.UP, 2);
            server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    m.getX() + 0.5, m.getY() + 1.05, m.getZ() + 0.5,
                    smelting ? 4 : 1, 0.08, 0.02, 0.08, smelting ? 0.02 : 0.005);
        }
    }

    // ── Multiblock validation ───────────────────────────────────────────────────
    // The machine body is a 5x5x5 shell; the controller can be placed at any non-hollow
    // cell within it (front face, walls, floor, roof — anywhere but the open central
    // chamber). Validation tries all four horizontal orientations, and for each, every
    // candidate cell the controller could be occupying, so the structure forms wherever
    // and however the player built it. Local coordinates: d = depth into the machine
    // (0..4), r = right (-2..2), u = height (-2..2).
    private Direction structureBack = null;
    private int controllerD = 2, controllerR = 0, controllerU = 0;
    // Positions the shell currently occupies with CONNECTED=true, so we can un-connect
    // exactly the right blocks if the structure re-forms in a different orientation/cell.
    private Set<BlockPos> connectedBlocks = new HashSet<>();

    public boolean checkStructure() {
        if (level == null) return false;
        for (Direction back : Direction.Plane.HORIZONTAL) {
            for (int d = 0; d <= 4; d++) {
                for (int r = -2; r <= 2; r++) {
                    for (int u = -2; u <= 2; u++) {
                        if (cellType(d, r, u) == Cell.HOLLOW) continue;
                        CoilBlock coil = validateOriented(back, d, r, u);
                        if (coil != null) {
                            structureValid = true;
                            structureBack = back;
                            controllerD = d;
                            controllerR = r;
                            controllerU = u;
                            installedCoilTemp = coil.getTemperature();
                            setFormed(true);
                            connectedBlocks = ConnectedStructureHelper.apply(level, connectedBlocks,
                                    collectStructurePositions(back, d, r, u));
                            return true;
                        }
                    }
                }
            }
        }
        structureValid = false;
        structureBack = null;
        installedCoilTemp = 0;
        setFormed(false);
        connectedBlocks = ConnectedStructureHelper.clear(level, connectedBlocks);
        return false;
    }

    // Every shell position (casing / muffler / coil) for a given orientation and controller
    // cell, excluding the controller's own cell (it has no CONNECTED property).
    private Set<BlockPos> collectStructurePositions(Direction back, int cd, int cr, int cu) {
        Direction right = back.getClockWise();
        Set<BlockPos> positions = new HashSet<>();
        for (int u = -2; u <= 2; u++) {
            for (int d = 0; d <= 4; d++) {
                for (int r = -2; r <= 2; r++) {
                    if (cellType(d, r, u) == Cell.HOLLOW) continue;
                    if (d == cd && r == cr && u == cu) continue;
                    positions.add(worldPosition.relative(back, d - cd).relative(right, r - cr).relative(Direction.UP, u - cu));
                }
            }
        }
        return positions;
    }

    private CoilBlock validateOriented(Direction back, int cd, int cr, int cu) {
        Direction right = back.getClockWise();
        Block muffler = ModBlocks.MUFFLER_HATCH.get();
        CoilBlock coil = null;

        for (int u = -2; u <= 2; u++) {
            for (int d = 0; d <= 4; d++) {
                for (int r = -2; r <= 2; r++) {
                    if (d == cd && r == cr && u == cu) continue; // the controller's own cell
                    BlockPos p = worldPosition.relative(back, d - cd).relative(right, r - cr).relative(Direction.UP, u - cu);
                    switch (cellType(d, r, u)) {
                        case CONTROLLER, HOLLOW -> { /* nothing to verify */ }
                        case CASING  -> { if (!isCasing(p)) return null; }
                        case MUFFLER -> { if (level.getBlockState(p).getBlock() != muffler) return null; }
                        case COIL -> {
                            Block b = level.getBlockState(p).getBlock();
                            if (!(b instanceof CoilBlock cb)) return null;
                            if (coil == null) coil = cb;
                            else if (coil != cb) return null;
                        }
                    }
                }
            }
        }
        return coil;
    }

    private enum Cell { CASING, COIL, CONTROLLER, MUFFLER, HOLLOW }

    // The shape of the furnace at a given local cell (kept in sync with the JEI guide).
    // CONTROLLER is no longer tied to a fixed cell — cellType() only classifies the block
    // requirements of the shell itself; validateOriented() separately skips whichever cell
    // the controller currently occupies.
    private static Cell cellType(int d, int r, int u) {
        int dist = Math.max(Math.abs(d - 2), Math.abs(r));              // ring distance from the central axis
        if (u == 2)  return (d == 2 && r == 0) ? Cell.MUFFLER : Cell.CASING; // roof (+ muffler)
        if (u == -2) return Cell.CASING;                                // floor
        // wall heights (u = -1, 0, +1)
        if (dist == 2) return Cell.CASING;                              // outer shell
        if ((u == -1 || u == 0) && dist == 1) return Cell.COIL;         // two coil rings
        return Cell.HOLLOW;                                             // central chamber
    }

    private void setFormed(boolean formed) {
        if (level == null) return;
        BlockState st = getBlockState();
        if (st.hasProperty(EBFControllerBlock.FORMED) && st.getValue(EBFControllerBlock.FORMED) != formed) {
            level.setBlock(worldPosition, st.setValue(EBFControllerBlock.FORMED, formed), 3);
        }
    }

    // Casing positions accept the casing block plus energy/fluid ports, which blend
    // into the wall so input/output/energy/fluid hatches can replace any casing.
    private boolean isCasing(BlockPos pos) {
        Block b = level.getBlockState(pos).getBlock();
        return b == ModBlocks.BLAST_FURNACE_CASING.get()
                || b == ModBlocks.EBF_IMPORT_BUS.get()
                || b == ModBlocks.EBF_OUTPUT_BUS.get()
                || b == ModBlocks.ENERGY_PORT.get()
                || b == ModBlocks.FLUID_PORT.get();
    }

    public boolean isStructureValid() { return structureValid; }

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

    // Canonical layout: origin is the front-center cell at ground level (d=0, r=0, u=-2)
    // — the front face the player is looking at when placing/previewing — with the
    // structure extending back (+d) and up (+u) from there.
    @Override
    public Map<BlockPos, Block> getPreviewPositions(BlockPos origin) {
        Map<BlockPos, Block> map = new HashMap<>();
        Direction back = structureBack != null ? structureBack : Direction.NORTH;
        Direction right = back.getClockWise();
        Block casing = ModBlocks.BLAST_FURNACE_CASING.get();
        Block muffler = ModBlocks.MUFFLER_HATCH.get();
        Block coil = ModBlocks.KANTHAL_COIL.get();
        for (int u = -2; u <= 2; u++) {
            for (int d = 0; d <= 4; d++) {
                for (int r = -2; r <= 2; r++) {
                    BlockPos p = origin.relative(back, d).relative(right, r).relative(Direction.UP, u + 2);
                    if (p.equals(origin)) continue;
                    Block expected = switch (cellType(d, r, u)) {
                        case CASING -> casing;
                        case MUFFLER -> muffler;
                        case COIL -> coil;
                        default -> null;
                    };
                    if (expected != null) map.put(p, expected);
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
        tag.putInt("CoilTemp", installedCoilTemp);
        tag.putBoolean("Enabled", enabled);
        tag.putBoolean("PreviewActive", previewActive);
        ConnectedStructureHelper.writePositions(tag, "ConnectedBlocks", connectedBlocks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
        structureValid = tag.getBoolean("StructureValid");
        installedCoilTemp = tag.getInt("CoilTemp");
        enabled = tag.getBoolean("Enabled");
        previewActive = tag.getBoolean("PreviewActive");
        connectedBlocks = ConnectedStructureHelper.readPositions(tag, "ConnectedBlocks");
    }
}
