package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.entity.AircraftEntity;
import com.sio.firearms.menu.HangarMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class HangarControllerBlockEntity extends EnergyStorageBlock implements MenuProvider {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int ENERGY_CAPACITY = 200_000;
    public static final int MAX_FE_IN       = 20_000;
    public static final int BUILD_ENERGY    = 80_000;
    public static final int BUILD_TICKS     = 400;
    public static final int ENERGY_PER_TICK = BUILD_ENERGY / BUILD_TICKS;
    public static final int SCAN_INTERVAL   = 100;

    // 10 input slots for aircraft components
    public final SimpleContainer inputSlots = new SimpleContainer(10) {
        @Override
        public void setChanged() {
            super.setChanged();
            HangarControllerBlockEntity.this.setChanged();
        }
    };

    private boolean structureValid   = false;
    private BlockPos structureOrigin = null;
    private int buildProgress        = 0;
    private int scanTick             = 0;
    private Boolean lastValidState   = null;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
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

    public HangarControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HANGAR_CONTROLLER.get(), pos, state, ENERGY_CAPACITY, MAX_FE_IN, 0);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.hangar_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new HangarMenu(id, inv, inputSlots, data);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, HangarControllerBlockEntity be) {
        be.serverTick();
    }

    private void serverTick() {
        if (level == null) return;

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

    private boolean hasRequiredItems() {
        return slotMatches(0, ModItems.AIRCRAFT_FUSELAGE.get(), 1)
            && slotMatches(1, ModItems.AIRCRAFT_WINGS.get(),    2)
            && slotMatches(2, ModItems.JET_ENGINE.get(),        2)
            && slotMatches(3, ModItems.COCKPIT_AVIONICS.get(),  1)
            && slotMatches(4, ModItems.KEROSENE_BUCKET.get(),   1);
    }

    private boolean slotMatches(int slot, Item item, int minCount) {
        ItemStack s = inputSlots.getItem(slot);
        return s.getItem() == item && s.getCount() >= minCount;
    }

    private void onBuildComplete() {
        if (level == null || structureOrigin == null) return;
        if (!hasRequiredItems()) return;

        // Spawn at centre of 9×9×4 interior (origin + 1 offset, interior at y=1-4)
        double cx = structureOrigin.getX() + 5.5;
        double cy = structureOrigin.getY() + 2.0;
        double cz = structureOrigin.getZ() + 5.5;

        AircraftEntity aircraft = new AircraftEntity(ModEntities.AIRCRAFT.get(), level);
        aircraft.setPos(cx, cy, cz);
        aircraft.setFuel(1000);  // initial fuel from kerosene bucket

        level.addFreshEntity(aircraft);

        // Consume required items
        inputSlots.removeItem(0, 1);  // fuselage
        inputSlots.removeItem(1, 2);  // wings
        inputSlots.removeItem(2, 2);  // jet engines
        inputSlots.removeItem(3, 1);  // avionics
        inputSlots.removeItem(4, 1);  // kerosene bucket — return empty bucket
        Block.popResource(level, worldPosition, new ItemStack(Items.BUCKET));
        setChanged();

        LOGGER.info("[HangarController@{}] Spawned F-22 aircraft at ({}, {}, {})",
                worldPosition.toShortString(), (int) cx, (int) cy, (int) cz);
    }

    // ── Structure validation: 11×11×6 ────────────────────────────────────────

    public boolean checkStructure() {
        if (level == null) return false;

        // Try controller at any position on the outer border of the 11×11 floor.
        // Outer border: x=0 or x=10, OR z=0 or z=10 (controller at y=0).
        for (int dz = 0; dz <= 10; dz++) {
            BlockPos o;
            String fail;

            // Controller on west face (dx=0)
            o = worldPosition.offset(0, 0, -dz);
            fail = validateAt(o);
            if (fail == null) return onValidOrigin(o);

            // Controller on east face (dx=10)
            o = worldPosition.offset(-10, 0, -dz);
            fail = validateAt(o);
            if (fail == null) return onValidOrigin(o);
        }
        for (int dx = 1; dx <= 9; dx++) {
            BlockPos o;
            String fail;

            // Controller on north face (dz=0)
            o = worldPosition.offset(-dx, 0, 0);
            fail = validateAt(o);
            if (fail == null) return onValidOrigin(o);

            // Controller on south face (dz=10)
            o = worldPosition.offset(-dx, 0, -10);
            fail = validateAt(o);
            if (fail == null) return onValidOrigin(o);
        }

        structureOrigin = null;
        if (!Boolean.FALSE.equals(lastValidState)) {
            LOGGER.info("[HangarController@{}] Structure INVALID", worldPosition.toShortString());
            lastValidState = false;
        }
        return false;
    }

    private boolean onValidOrigin(BlockPos origin) {
        structureOrigin = origin;
        if (!Boolean.TRUE.equals(lastValidState)) {
            LOGGER.info("[HangarController@{}] Structure VALID (origin={})",
                    worldPosition.toShortString(), origin.toShortString());
            lastValidState = true;
        }
        return true;
    }

    private String validateAt(BlockPos origin) {
        // Layer 0: full 11×11 hangar_floor (controller and doors count as floor on border)
        for (int x = 0; x < 11; x++) {
            for (int z = 0; z < 11; z++) {
                BlockPos fp = origin.offset(x, 0, z);
                Block b = level.getBlockState(fp).getBlock();
                if (!isFloor(b)) {
                    return "layer 0 (" + x + "," + z + "): expected floor, got " + blockName(b);
                }
            }
        }

        // Layers 1-4: 11×11 outer border = wall/door/controller, 9×9 interior = air/port
        for (int y = 1; y <= 4; y++) {
            for (int x = 0; x < 11; x++) {
                for (int z = 0; z < 11; z++) {
                    boolean border = x == 0 || x == 10 || z == 0 || z == 10;
                    BlockPos bp = origin.offset(x, y, z);
                    Block b = level.getBlockState(bp).getBlock();
                    if (border) {
                        if (!isWall(b)) {
                            return "layer " + y + " border (" + x + "," + z + "): expected wall, got " + blockName(b);
                        }
                    } else {
                        if (!level.getBlockState(bp).isAir() && !isPort(b)) {
                            return "layer " + y + " interior (" + x + "," + z + "): expected air/port, got " + blockName(b);
                        }
                    }
                }
            }
        }

        // Layer 5: full 11×11 hangar_roof
        for (int x = 0; x < 11; x++) {
            for (int z = 0; z < 11; z++) {
                BlockPos rp = origin.offset(x, 5, z);
                Block b = level.getBlockState(rp).getBlock();
                if (!isRoof(b)) {
                    return "layer 5 (" + x + "," + z + "): expected roof, got " + blockName(b);
                }
            }
        }

        return null;  // valid
    }

    private boolean isFloor(Block b) {
        return b == ModBlocks.HANGAR_FLOOR.get()
            || b == ModBlocks.HANGAR_CONTROLLER.get()
            || b instanceof HangarDoorBlock
            || isPort(b);
    }

    private boolean isWall(Block b) {
        return b == ModBlocks.HANGAR_WALL.get()
            || b instanceof HangarDoorBlock
            || b == ModBlocks.HANGAR_CONTROLLER.get()
            || isPort(b);
    }

    private boolean isRoof(Block b) {
        return b == ModBlocks.HANGAR_ROOF.get() || isPort(b);
    }

    private boolean isPort(Block b) {
        return b == ModBlocks.ENERGY_PORT.get() || b == ModBlocks.FLUID_PORT.get();
    }

    private String blockName(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).toString();
    }

    // ── Door toggle ───────────────────────────────────────────────────────────

    public void toggleDoors() {
        if (level == null || structureOrigin == null || !structureValid) return;

        boolean currentlyOpen = false;
        outer:
        for (int y = 1; y <= 4; y++) {
            for (int x = 0; x < 11; x++) {
                for (int z = 0; z < 11; z++) {
                    BlockState bs = level.getBlockState(structureOrigin.offset(x, y, z));
                    if (bs.getBlock() instanceof HangarDoorBlock) {
                        currentlyOpen = bs.getValue(HangarDoorBlock.OPEN);
                        break outer;
                    }
                }
            }
        }

        boolean newOpen = !currentlyOpen;
        for (int y = 1; y <= 4; y++) {
            for (int x = 0; x < 11; x++) {
                for (int z = 0; z < 11; z++) {
                    BlockPos bp = structureOrigin.offset(x, y, z);
                    BlockState bs = level.getBlockState(bp);
                    if (bs.getBlock() instanceof HangarDoorBlock) {
                        level.setBlock(bp, bs.setValue(HangarDoorBlock.OPEN, newOpen), Block.UPDATE_ALL);
                    }
                }
            }
        }
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
