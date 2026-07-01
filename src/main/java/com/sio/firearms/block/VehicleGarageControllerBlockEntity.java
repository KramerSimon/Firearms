package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.entity.TankEntity;
import com.sio.firearms.menu.VehicleGarageMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class VehicleGarageControllerBlockEntity extends EnergyStorageBlock implements MenuProvider {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int ENERGY_CAPACITY  = 100_000;
    public static final int MAX_FE_IN        = 10_000;
    public static final int BUILD_ENERGY     = 50_000;
    public static final int BUILD_TICKS      = 200;
    public static final int ENERGY_PER_TICK  = BUILD_ENERGY / BUILD_TICKS;  // 250 FE/tick
    public static final int SCAN_INTERVAL    = 100;

    public final SimpleContainer inputSlots = new SimpleContainer(10) {
        @Override public void setChanged() {
            super.setChanged();
            VehicleGarageControllerBlockEntity.this.setChanged();
        }
    };

    private boolean structureValid  = false;
    private BlockPos structureOrigin = null;
    private int buildProgress       = 0;   // 0 = idle, 1-200 = building
    private int scanTick            = 0;
    private Boolean lastValidState  = null;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int i) {
            return switch (i) {
                case 0 -> Math.min(energy.getEnergyStored(), Short.MAX_VALUE);
                case 1 -> Math.min(ENERGY_CAPACITY, Short.MAX_VALUE);
                case 2 -> buildProgress;
                case 3 -> structureValid ? 1 : 0;
                default -> 0;
            };
        }
        @Override public void set(int i, int v) {}
        @Override public int getCount() { return 4; }
    };

    public VehicleGarageControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VEHICLE_GARAGE_CONTROLLER.get(), pos, state, ENERGY_CAPACITY, MAX_FE_IN, 0);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.garage_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new VehicleGarageMenu(id, inv, inputSlots, data);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VehicleGarageControllerBlockEntity be) {
        be.serverTick();
    }

    public void serverTick() {
        if (level == null) return;

        if (scanTick % 40 == 0) LOGGER.info("[VehicleGarage] ticking at {}", worldPosition);

        scanTick++;
        if (scanTick >= SCAN_INTERVAL) {
            scanTick = 0;
            boolean valid = checkStructure();
            if (valid != structureValid) {
                structureValid = valid;
                setChanged();
            }
        }

        if (!structureValid) {
            if (buildProgress > 0) { buildProgress = 0; setChanged(); }
            return;
        }

        // Auto-start build when structure valid and energy available and all required items present
        if (buildProgress == 0 && energy.getEnergyStored() >= ENERGY_PER_TICK && hasRequiredItems()) {
            buildProgress = 1;
            setChanged();
        }

        if (buildProgress > 0) {
            if (energy.getEnergyStored() >= ENERGY_PER_TICK) {
                energy.extractEnergy(ENERGY_PER_TICK, false);
                buildProgress++;
                if (buildProgress > BUILD_TICKS) {
                    onBuildComplete();
                    buildProgress = 0;
                }
                setChanged();
            }
        }
    }

    /** All 7 required components must be present to start assembly. */
    private boolean hasRequiredItems() {
        boolean ok = slotMatches(0, ModItems.TANK_HULL.get(),         1)
            && slotMatches(1, ModItems.TANK_TRACKS.get(),       2)
            && slotMatches(2, ModItems.TANK_TURRET.get(),       1)
            && slotMatches(3, ModItems.TANK_CANNON.get(),       1)
            && slotMatches(4, ModItems.DIESEL_ENGINE.get(),     1)
            && slotMatches(5, ModItems.ADVANCED_MICROCHIP.get(),2)
            && slotMatches(6, ModItems.DIESEL_BUCKET.get(),     1);

        if (!ok) {
            LOGGER.debug("[VehicleGarage@{}] hasRequiredItems=false — slot0(hull)={}/1 need tank_hull, "
                            + "slot1(tracks)={}/2 need tank_tracks, slot2(turret)={}/1 need tank_turret, "
                            + "slot3(cannon)={}/1 need tank_cannon, slot4(engine)={}/1 need diesel_engine, "
                            + "slot5(chip)={}/2 need advanced_microchip, slot6(bucket)={}/1 need diesel_bucket",
                    worldPosition.toShortString(),
                    describeSlot(0), describeSlot(1), describeSlot(2), describeSlot(3),
                    describeSlot(4), describeSlot(5), describeSlot(6));
        }
        return ok;
    }

    private String describeSlot(int slot) {
        ItemStack s = inputSlots.getItem(slot);
        return s.isEmpty() ? "empty" : (BuiltInRegistries.ITEM.getKey(s.getItem()) + "x" + s.getCount());
    }

    private boolean slotMatches(int slot, Item item, int minCount) {
        ItemStack s = inputSlots.getItem(slot);
        return s.getItem() == item && s.getCount() >= minCount;
    }

    private void onBuildComplete() {
        if (level == null || structureOrigin == null) return;
        if (!hasRequiredItems()) return;

        // Interior centre: 2-layer floor → spawn at y+2; 9-wide → centre at +4.5
        double cx = structureOrigin.getX() + 4.5;
        double cy = structureOrigin.getY() + 2.0;
        double cz = structureOrigin.getZ() + 4.5;

        TankEntity tank = new TankEntity(ModEntities.TANK.get(), level);
        tank.setPos(cx, cy, cz);
        tank.setInitialFacing(findDoorFacing());
        tank.setFuel(5000);  // initial fill from diesel bucket in slot 6

        level.addFreshEntity(tank);

        // Consume all required items
        inputSlots.removeItem(0, 1);  // tank_hull
        inputSlots.removeItem(1, 2);  // tank_tracks
        inputSlots.removeItem(2, 1);  // tank_turret
        inputSlots.removeItem(3, 1);  // tank_cannon
        inputSlots.removeItem(4, 1);  // diesel_engine
        inputSlots.removeItem(5, 2);  // advanced_microchip
        inputSlots.removeItem(6, 1);  // diesel_bucket — return empty bucket
        Block.popResource(level, worldPosition, new ItemStack(Items.BUCKET));
        setChanged();

        LOGGER.info("[VehicleGarage@{}] Spawned M1 Abrams tank at ({}, {}, {})",
                worldPosition.toShortString(), (int) cx, (int) cy, (int) cz);
    }

    private Direction findDoorFacing() {
        if (structureOrigin == null) return Direction.NORTH;
        for (int y = 2; y <= 4; y++) {
            for (int x = 0; x < 9; x++) {
                for (int z = 0; z < 9; z++) {
                    BlockState bs = level.getBlockState(structureOrigin.offset(x, y, z));
                    if (bs.getBlock() instanceof GarageDoorBlock) {
                        return bs.getValue(GarageDoorBlock.FACING);
                    }
                }
            }
        }
        return Direction.NORTH;
    }

    // ── Structure validation: 9×9×6 ─────────────────────────────────────────

    public boolean checkStructure() {
        if (level == null) return false;
        String firstFail = null;

        // West and east faces (dx=0 and dx=8), all dz positions 0-8
        for (int dz = 0; dz <= 8; dz++) {
            BlockPos o; String fail;

            o = worldPosition.offset(0, 0, -dz);
            LOGGER.info("[VehicleGarage@{}] Trying origin {} (ctrl at local dx=0, dz={})",
                    worldPosition.toShortString(), o.toShortString(), dz);
            fail = validateAt(o);
            if (fail == null) return onValidOrigin(o);
            if (firstFail == null) firstFail = fail;

            o = worldPosition.offset(-8, 0, -dz);
            LOGGER.info("[VehicleGarage@{}] Trying origin {} (ctrl at local dx=8, dz={})",
                    worldPosition.toShortString(), o.toShortString(), dz);
            fail = validateAt(o);
            if (fail == null) return onValidOrigin(o);
            if (firstFail == null) firstFail = fail;
        }

        // North and south faces (dz=0 and dz=8), all dx positions 0-8
        for (int dx = 0; dx <= 8; dx++) {
            BlockPos o; String fail;

            o = worldPosition.offset(-dx, 0, 0);
            LOGGER.info("[VehicleGarage@{}] Trying origin {} (ctrl at local dx={}, dz=0)",
                    worldPosition.toShortString(), o.toShortString(), dx);
            fail = validateAt(o);
            if (fail == null) return onValidOrigin(o);
            if (firstFail == null) firstFail = fail;

            o = worldPosition.offset(-dx, 0, -8);
            LOGGER.info("[VehicleGarage@{}] Trying origin {} (ctrl at local dx={}, dz=8)",
                    worldPosition.toShortString(), o.toShortString(), dx);
            fail = validateAt(o);
            if (fail == null) return onValidOrigin(o);
            if (firstFail == null) firstFail = fail;
        }

        structureOrigin = null;
        if (!Boolean.FALSE.equals(lastValidState)) {
            LOGGER.info("[VehicleGarage@{}] Structure INVALID — {}",
                    worldPosition.toShortString(),
                    firstFail != null ? firstFail : "no valid origin found");
            lastValidState = false;
        }
        return false;
    }

    private boolean onValidOrigin(BlockPos origin) {
        structureOrigin = origin;
        if (!Boolean.TRUE.equals(lastValidState)) {
            LOGGER.info("[VehicleGarage@{}] Structure VALID (origin={})",
                    worldPosition.toShortString(), origin.toShortString());
            lastValidState = true;
        }
        return true;
    }

    public void toggleDoors() {
        if (level == null || structureOrigin == null || !structureValid) return;

        // Sample the first door's OPEN state to determine toggle direction
        boolean currentlyOpen = false;
        search:
        for (int y = 2; y <= 4; y++) {
            for (int x = 0; x < 9; x++) {
                for (int z = 0; z < 9; z++) {
                    BlockState bs = level.getBlockState(structureOrigin.offset(x, y, z));
                    if (bs.getBlock() instanceof GarageDoorBlock) {
                        currentlyOpen = bs.getValue(GarageDoorBlock.OPEN);
                        break search;
                    }
                }
            }
        }

        boolean newOpen = !currentlyOpen;
        for (int y = 2; y <= 4; y++) {
            for (int x = 0; x < 9; x++) {
                for (int z = 0; z < 9; z++) {
                    BlockPos bp = structureOrigin.offset(x, y, z);
                    BlockState bs = level.getBlockState(bp);
                    if (bs.getBlock() instanceof GarageDoorBlock) {
                        level.setBlock(bp, bs.setValue(GarageDoorBlock.OPEN, newOpen), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    private String validateAt(BlockPos origin) {
        String tag = "[VehicleGarage@" + worldPosition.toShortString() + " origin=" + origin.toShortString() + "]";

        // Layers 0–1: 9×9 garage_floor (controller counts as floor on border)
        for (int y = 0; y <= 1; y++) {
            for (int x = 0; x < 9; x++) {
                for (int z = 0; z < 9; z++) {
                    BlockPos fp = origin.offset(x, y, z);
                    Block b = level.getBlockState(fp).getBlock();
                    if (!isFloor(b)) {
                        String fail = String.format("layer %d (%d,%d) abs(%d,%d,%d): expected floor, got %s",
                                y, x, z, fp.getX(), fp.getY(), fp.getZ(), blockName(b));
                        LOGGER.info("{} FAIL {}", tag, fail);
                        return fail;
                    }
                }
            }
            LOGGER.info("{} layer {} floor OK", tag, y);
        }

        // Layers 2–4: 9×9 wall border, 7×7 interior air/port
        for (int y = 2; y <= 4; y++) {
            for (int x = 0; x < 9; x++) {
                for (int z = 0; z < 9; z++) {
                    boolean border = x == 0 || x == 8 || z == 0 || z == 8;
                    BlockPos bp = origin.offset(x, y, z);
                    Block b = level.getBlockState(bp).getBlock();
                    if (border) {
                        if (!isWall(b)) {
                            String fail = String.format("layer %d border (%d,%d) abs(%d,%d,%d): expected wall, got %s",
                                    y, x, z, bp.getX(), bp.getY(), bp.getZ(), blockName(b));
                            LOGGER.info("{} FAIL {}", tag, fail);
                            return fail;
                        }
                    } else {
                        if (!level.getBlockState(bp).isAir() && !isPort(b)) {
                            String fail = String.format("layer %d interior (%d,%d) abs(%d,%d,%d): expected air/port, got %s",
                                    y, x, z, bp.getX(), bp.getY(), bp.getZ(), blockName(b));
                            LOGGER.info("{} FAIL {}", tag, fail);
                            return fail;
                        }
                    }
                }
            }
            LOGGER.info("{} layer {} wall/interior OK", tag, y);
        }

        // Layer 5: 9×9 garage_roof
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 9; z++) {
                BlockPos rp = origin.offset(x, 5, z);
                Block b = level.getBlockState(rp).getBlock();
                if (!isRoof(b)) {
                    String fail = String.format("layer 5 (%d,%d) abs(%d,%d,%d): expected roof, got %s",
                            x, z, rp.getX(), rp.getY(), rp.getZ(), blockName(b));
                    LOGGER.info("{} FAIL {}", tag, fail);
                    return fail;
                }
            }
        }
        LOGGER.info("{} layer 5 roof OK — Structure VALID", tag);
        return null;
    }

    private boolean isFloor(Block b) {
        return b == ModBlocks.GARAGE_FLOOR.get()
                || b == ModBlocks.GARAGE_CONTROLLER.get()
                || b instanceof GarageDoorBlock
                || isPort(b);
    }

    private boolean isWall(Block b) {
        return b == ModBlocks.GARAGE_WALL.get()
                || b instanceof GarageDoorBlock          // open OR closed
                || b == ModBlocks.GARAGE_CONTROLLER.get()
                || isPort(b);
    }

    private boolean isRoof(Block b) {
        return b == ModBlocks.GARAGE_ROOF.get()
                || isPort(b);
    }

    private boolean isPort(Block b) {
        return b == ModBlocks.ENERGY_PORT.get() || b == ModBlocks.FLUID_PORT.get();
    }

    private String blockName(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).toString();
    }

    // ── NBT ──────────────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag itemsTag = new CompoundTag();
        for (int i = 0; i < inputSlots.getContainerSize(); i++) {
            ItemStack stack = inputSlots.getItem(i);
            if (!stack.isEmpty()) {
                itemsTag.put("Slot" + i, stack.save(registries, new CompoundTag()));
            }
        }
        tag.put("Items", itemsTag);
        tag.putInt("BuildProgress", buildProgress);
        tag.putBoolean("StructureValid", structureValid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Items")) {
            CompoundTag itemsTag = tag.getCompound("Items");
            for (int i = 0; i < inputSlots.getContainerSize(); i++) {
                String key = "Slot" + i;
                if (itemsTag.contains(key)) {
                    inputSlots.setItem(i, ItemStack.parseOptional(registries, itemsTag.getCompound(key)));
                }
            }
        }
        buildProgress  = tag.getInt("BuildProgress");
        structureValid = tag.getBoolean("StructureValid");
    }
}
