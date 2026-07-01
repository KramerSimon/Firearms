package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.CrystalGrowthControllerMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.Map;

public class CrystalGrowthControllerBlockEntity extends EnergyStorageBlock implements MenuProvider, IMultiblockPreview {

    public static final int CAPACITY = 200_000;
    public static final int MAX_RECEIVE = 2000;
    public static final int FE_PER_TICK = 500;
    public static final int PROCESS_TIME = 1200;

    private int progress = 0;
    private boolean structureValid = false;

    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
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
                default -> 0;
            };
        }
        @Override
        public void set(int index, int value) { if (index == 2) progress = value; }
        @Override
        public int getCount() { return 5; }
    };

    public CrystalGrowthControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRYSTAL_GROWTH_CONTROLLER.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory() { return inventory; }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.crystal_growth_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new CrystalGrowthControllerMenu(id, inv, inventory, data);
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
        if (!structureValid) { if (progress > 0) { progress = 0; setChanged(); } return; }

        ItemStack in = inventory.getStackInSlot(0);
        ItemStack out = inventory.getStackInSlot(1);
        ItemStack result = new ItemStack(ModItems.SILICON_INGOT.get());

        boolean canProcess = stackIs(in, "firearms:electronic_grade_silicon")
                && canOutput(result, out)
                && energy.getEnergyStored() >= FE_PER_TICK;

        if (canProcess) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            if (progress >= PROCESS_TIME) {
                in.shrink(1);
                if (out.isEmpty()) inventory.setStackInSlot(1, result.copy());
                else out.grow(1);
                progress = 0;
            }
            setChanged();
        } else if (progress > 0) { progress = 0; setChanged(); }
    }

    public boolean checkStructure() {
        if (level == null) return false;
        for (int ox = 0; ox <= 2; ox++) {
            for (int oz = 0; oz <= 2; oz++) {
                BlockPos origin = worldPosition.offset(-ox, 0, -oz);
                if (isValidAt(origin)) {
                    structureValid = true;
                    return true;
                }
            }
        }
        structureValid = false;
        return false;
    }

    private boolean isValidAt(BlockPos origin) {
        for (int x = 0; x < 3; x++) for (int z = 0; z < 3; z++) {
            Block b = level.getBlockState(origin.offset(x, 0, z)).getBlock();
            if (b != ModBlocks.CRYSTAL_GROWTH_BASE.get()
                    && b != ModBlocks.CRYSTAL_GROWTH_CONTROLLER.get()
                    && b != ModBlocks.ENERGY_PORT.get()) return false;
        }
        for (int y = 1; y <= 2; y++) for (int x = 0; x < 3; x++) for (int z = 0; z < 3; z++) {
            Block b = level.getBlockState(origin.offset(x, y, z)).getBlock();
            if (b != ModBlocks.CRYSTAL_GROWTH_WALL.get() && b != ModBlocks.ENERGY_PORT.get()) return false;
        }
        for (int x = 0; x < 3; x++) for (int z = 0; z < 3; z++) {
            Block b = level.getBlockState(origin.offset(x, 3, z)).getBlock();
            if (b != ModBlocks.CRYSTAL_GROWTH_TOP.get() && b != ModBlocks.ENERGY_PORT.get()) return false;
        }
        return true;
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

    // Canonical layout: origin is always the centre of the 3×3 footprint (the controller
    // can be placed at any of the 9 base positions, so the preview centres on it).
    @Override
    public Map<BlockPos, Block> getPreviewPositions(BlockPos origin) {
        Map<BlockPos, Block> map = new HashMap<>();
        Block base = ModBlocks.CRYSTAL_GROWTH_BASE.get();
        Block wall = ModBlocks.CRYSTAL_GROWTH_WALL.get();
        Block top  = ModBlocks.CRYSTAL_GROWTH_TOP.get();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos p = origin.offset(x, 0, z);
                if (!p.equals(origin)) map.put(p, base);
            }
        }
        for (int y = 1; y <= 2; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    map.put(origin.offset(x, y, z), wall);
                }
            }
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                map.put(origin.offset(x, 3, z), top);
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
        tag.putBoolean("PreviewActive", previewActive);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
        structureValid = tag.getBoolean("StructureValid");
        previewActive = tag.getBoolean("PreviewActive");
    }
}
