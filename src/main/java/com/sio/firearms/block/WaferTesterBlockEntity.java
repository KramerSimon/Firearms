package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.WaferTesterMenu;
import com.sio.firearms.registry.ModBlockEntities;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class WaferTesterBlockEntity extends EnergyStorageBlock implements MenuProvider {

    public static final int CAPACITY     = 20_000;
    public static final int MAX_RECEIVE  = 200;
    public static final int FE_PER_TICK  = 50;
    public static final int PROCESS_TIME = 200;

    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };

    private int progress = 0;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> progress;
                case 3 -> PROCESS_TIME;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) { if (index == 2) progress = value; }
        @Override public int getCount() { return 4; }
    };

    public WaferTesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WAFER_TESTER.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory() { return inventory; }

    @Override public Component getDisplayName() { return Component.translatable("block.firearms.wafer_tester"); }

    @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new WaferTesterMenu(id, inv, inventory, data);
    }

    private boolean stackIs(ItemStack s, String id) {
        if (s.isEmpty()) return false;
        return BuiltInRegistries.ITEM.getKey(s.getItem()).toString().equals(id);
    }

    public void serverTick() {
        if (level == null) return;
        ItemStack in0 = inventory.getStackInSlot(0);
        ItemStack out = inventory.getStackInSlot(1);

        boolean canProcess = stackIs(in0, "firearms:finished_wafer")
                && out.isEmpty()
                && energy.getEnergyStored() >= FE_PER_TICK;

        if (canProcess) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            if (progress >= PROCESS_TIME) {
                in0.shrink(1);
                ItemStack result = level.random.nextFloat() < 0.85f
                        ? new ItemStack(ModItems.TESTED_WAFER.get())
                        : new ItemStack(ModItems.DEFECTIVE_WAFER.get());
                inventory.setStackInSlot(1, result);
                progress = 0;
            }
            setChanged();
        } else if (progress > 0) { progress = 0; setChanged(); }
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
        super.saveAdditional(tag, reg);
        tag.put("Inventory", inventory.serializeNBT(reg));
        tag.putInt("Progress", progress);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
        super.loadAdditional(tag, reg);
        if (tag.contains("Inventory")) inventory.deserializeNBT(reg, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
    }
}
