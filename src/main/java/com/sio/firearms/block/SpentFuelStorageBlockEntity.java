package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.menu.SpentFuelStorageMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModEffects;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import org.slf4j.Logger;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class SpentFuelStorageBlockEntity extends BlockEntity implements MenuProvider {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SLOTS = 9;
    private static final int RADIATION_RADIUS = 5;
    private static final int RADIATION_INTERVAL = 40;

    public final ItemStackHandler inventory = new ItemStackHandler(SLOTS) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() == ModItems.SPENT_FUEL_ROD.get();
        }
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    private boolean structureValid = false;
    private int tickCounter = 0;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> structureValid ? 1 : 0;
                case 1 -> countRods();
                default -> 0;
            };
        }
        @Override
        public void set(int index, int value) {
            if (index == 0) structureValid = value == 1;
        }
        @Override
        public int getCount() { return 2; }
    };

    public SpentFuelStorageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPENT_FUEL_STORAGE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SpentFuelStorageBlockEntity be) {
        be.tickCounter++;
        if (be.tickCounter % 100 == 0) {
            be.structureValid = be.checkStructure();
        }
        if (be.tickCounter % RADIATION_INTERVAL == 0 && be.countRods() > 0) {
            be.emitRadiation();
        }
        if (be.tickCounter >= 200) be.tickCounter = 0;
    }

    private int countRods() {
        int count = 0;
        for (int i = 0; i < SLOTS; i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) count++;
        }
        return count;
    }

    private void emitRadiation() {
        if (level == null) return;
        BlockPos pos = getBlockPos();
        double r = RADIATION_RADIUS;
        net.minecraft.world.phys.AABB box = new net.minecraft.world.phys.AABB(
                pos.getX() - r, pos.getY() - r, pos.getZ() - r,
                pos.getX() + r, pos.getY() + r, pos.getZ() + r);
        List<Player> players = level.getEntitiesOfClass(Player.class, box);
        for (Player p : players) {
            p.addEffect(new MobEffectInstance(ModEffects.RADIATION, 80, 0, false, false));
        }
    }

    private boolean checkStructure() {
        if (level == null) return false;
        BlockPos origin = getBlockPos();
        // Controller sits in the wall layer (dy=1 relative to corner = layer 1 = base).
        // Layer 0 base is 1 below controller, so corner Y = controllerY - 1.
        LOGGER.info("[SpentFuelStorage@{}] checkStructure — scanning 25 corner candidates",
                origin.toShortString());

        for (int offX = -4; offX <= 0; offX++) {
            for (int offZ = -4; offZ <= 0; offZ++) {
                BlockPos corner = origin.offset(offX, -1, offZ);
                if (validateStructureAt(corner)) {
                    LOGGER.info("[SpentFuelStorage@{}] structure VALID — corner={}", origin.toShortString(), corner.toShortString());
                    return true;
                }
            }
        }
        LOGGER.info("[SpentFuelStorage@{}] structure INVALID — no valid corner found", origin.toShortString());
        return false;
    }

    private boolean validateStructureAt(BlockPos corner) {
        if (level == null) return false;

        // Layer 0 (cornerY = controllerY - 1): full 5x5 base — no controller here
        for (int dx = 0; dx < 5; dx++) {
            for (int dz = 0; dz < 5; dz++) {
                BlockPos p = corner.offset(dx, 0, dz);
                BlockState st = level.getBlockState(p);
                if (!st.is(ModBlocks.SPENT_FUEL_STORAGE_BASE.get())) {
                    LOGGER.info("[SpentFuelStorage] corner={} FAIL layer=0 ({},{}) — expected base, found {}",
                            corner.toShortString(), dx, dz,
                            net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(st.getBlock()));
                    return false;
                }
            }
        }
        LOGGER.info("[SpentFuelStorage] corner={} layer 0 base OK", corner.toShortString());

        // Layer 1 (cornerY + 1 = controllerY): full 5x5 base — controller replaces one base block here
        for (int dx = 0; dx < 5; dx++) {
            for (int dz = 0; dz < 5; dz++) {
                BlockPos p = corner.offset(dx, 1, dz);
                BlockState st = level.getBlockState(p);
                boolean isBase = st.is(ModBlocks.SPENT_FUEL_STORAGE_BASE.get());
                boolean isController = st.is(ModBlocks.SPENT_FUEL_STORAGE_CONTROLLER.get());
                if (!isBase && !isController) {
                    LOGGER.info("[SpentFuelStorage] corner={} FAIL layer=1 ({},{}) — expected base/controller, found {}",
                            corner.toShortString(), dx, dz,
                            net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(st.getBlock()));
                    return false;
                }
            }
        }
        LOGGER.info("[SpentFuelStorage] corner={} layer 1 base OK", corner.toShortString());

        // Layers 2 and 3 (cornerY + 2/3 = controllerY + 1/2): hollow 5x5 wall edges only
        for (int dy = 2; dy <= 3; dy++) {
            for (int dx = 0; dx < 5; dx++) {
                for (int dz = 0; dz < 5; dz++) {
                    if (dx > 0 && dx < 4 && dz > 0 && dz < 4) continue; // interior open
                    BlockPos p = corner.offset(dx, dy, dz);
                    BlockState st = level.getBlockState(p);
                    if (!st.is(ModBlocks.SPENT_FUEL_STORAGE_WALL.get())) {
                        LOGGER.info("[SpentFuelStorage] corner={} FAIL layer={} ({},{}) — expected wall, found {}",
                                corner.toShortString(), dy, dx, dz,
                                net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(st.getBlock()));
                        return false;
                    }
                }
            }
            LOGGER.info("[SpentFuelStorage] corner={} layer {} wall OK", corner.toShortString(), dy);
        }

        return true;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.spent_fuel_storage_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SpentFuelStorageMenu(containerId, playerInventory, inventory, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putBoolean("StructureValid", structureValid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        structureValid = tag.getBoolean("StructureValid");
    }
}
