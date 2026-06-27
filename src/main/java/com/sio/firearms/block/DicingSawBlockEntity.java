package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.DicingSawMenu;
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

public class DicingSawBlockEntity extends EnergyStorageBlock implements MenuProvider {

    public static final int CAPACITY     = 25_000;
    public static final int MAX_RECEIVE  = 250;
    public static final int FE_PER_TICK  = 80;
    public static final int PROCESS_TIME = 250;

    public final ItemStackHandler inventory = new ItemStackHandler(3) {
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

    public DicingSawBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DICING_SAW.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory() { return inventory; }

    @Override public Component getDisplayName() { return Component.translatable("block.firearms.dicing_saw"); }

    @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new DicingSawMenu(id, inv, inventory, data);
    }

    private boolean stackIs(ItemStack s, String id) {
        if (s.isEmpty()) return false;
        return BuiltInRegistries.ITEM.getKey(s.getItem()).toString().equals(id);
    }

    private boolean canOutput(ItemStack result, ItemStack out) {
        if (out.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(out, result)) return false;
        return out.getCount() + result.getCount() <= out.getMaxStackSize();
    }

    public void serverTick() {
        if (level == null) return;
        ItemStack in0   = inventory.getStackInSlot(0);
        ItemStack blade = inventory.getStackInSlot(1);
        ItemStack out   = inventory.getStackInSlot(2);
        ItemStack result = new ItemStack(ModItems.SILICON_DIE.get(), 8);

        boolean hasBlade = !blade.isEmpty() && stackIs(blade, "firearms:diamond_saw_blade")
                && blade.getDamageValue() < blade.getMaxDamage();
        boolean canProcess = stackIs(in0, "firearms:tested_wafer") && hasBlade
                && energy.getEnergyStored() >= FE_PER_TICK && canOutput(result, out);

        if (canProcess) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            if (progress >= PROCESS_TIME) {
                in0.shrink(1);
                blade.setDamageValue(blade.getDamageValue() + 1);
                if (blade.getDamageValue() >= blade.getMaxDamage()) inventory.setStackInSlot(1, ItemStack.EMPTY);
                if (out.isEmpty()) inventory.setStackInSlot(2, result.copy());
                else out.grow(result.getCount());
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
