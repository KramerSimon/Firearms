package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.ChipPackagingMachineMenu;
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

public class ChipPackagingMachineBlockEntity extends EnergyStorageBlock implements MenuProvider {

    public static final int CAPACITY     = 30_000;
    public static final int MAX_RECEIVE  = 300;
    public static final int FE_PER_TICK  = 100;
    public static final int PROCESS_TIME = 300;

    public final ItemStackHandler inventory = new ItemStackHandler(4) {
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

    public ChipPackagingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHIP_PACKAGING_MACHINE.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory() { return inventory; }

    @Override public Component getDisplayName() { return Component.translatable("block.firearms.chip_packaging_machine"); }

    @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ChipPackagingMachineMenu(id, inv, inventory, data);
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

    private record Recipe(ItemStack result, int dieCount, boolean needsEtchedCopper) {}

    private Recipe findRecipe() {
        ItemStack in0 = inventory.getStackInSlot(0);
        ItemStack in1 = inventory.getStackInSlot(1);
        ItemStack in2 = inventory.getStackInSlot(2);
        ItemStack out = inventory.getStackInSlot(3);

        if (stackIs(in0, "firearms:silicon_die") && in0.getCount() >= 8
                && stackIs(in1, "firearms:gold_foil")
                && stackIs(in2, "firearms:etched_copper")) {
            ItemStack result = new ItemStack(ModItems.ADVANCED_MICROCHIP.get());
            if (canOutput(result, out)) return new Recipe(result, 8, true);
        }

        if (stackIs(in0, "firearms:silicon_die") && in0.getCount() >= 4
                && stackIs(in1, "firearms:gold_foil")
                && in2.isEmpty()) {
            ItemStack result = new ItemStack(ModItems.BASIC_MICROCHIP.get());
            if (canOutput(result, out)) return new Recipe(result, 4, false);
        }

        return null;
    }

    public void serverTick() {
        if (level == null) return;
        Recipe recipe = findRecipe();

        if (recipe != null && energy.getEnergyStored() >= FE_PER_TICK) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            if (progress >= PROCESS_TIME) {
                inventory.getStackInSlot(0).shrink(recipe.dieCount());
                inventory.getStackInSlot(1).shrink(1);
                if (recipe.needsEtchedCopper()) inventory.getStackInSlot(2).shrink(1);
                ItemStack out = inventory.getStackInSlot(3);
                if (out.isEmpty()) inventory.setStackInSlot(3, recipe.result().copy());
                else out.grow(recipe.result().getCount());
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
