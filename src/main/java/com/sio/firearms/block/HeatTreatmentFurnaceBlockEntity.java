package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.HeatTreatmentFurnaceMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class HeatTreatmentFurnaceBlockEntity extends EnergyStorageBlock implements MenuProvider {

    private static final int CAPACITY = 20_000;
    private static final int MAX_RECEIVE = 500;
    private static final int FE_PER_TICK = 40;
    private static final int PROCESS_TIME = 200;

    private final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private int progress = 0;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> progress;
                case 3 -> PROCESS_TIME;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 2) progress = value;
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public HeatTreatmentFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HEAT_TREATMENT_FURNACE.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.heat_treatment_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HeatTreatmentFurnaceMenu(containerId, playerInventory, inventory, data);
    }

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;

        ItemStack primary = inventory.getStackInSlot(0);
        ItemStack secondary = inventory.getStackInSlot(1);
        ItemStack output = inventory.getStackInSlot(2);
        ItemStack recipe = getRecipeOutput(primary, secondary);

        if (!recipe.isEmpty() && energy.getEnergyStored() >= FE_PER_TICK && canOutput(output, recipe)) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            changed = true;

            if (progress >= PROCESS_TIME) {
                boolean consumeSecondary = !secondary.isEmpty()
                        && (secondary.is(Items.COAL) || secondary.is(ModItems.FLUORITE_CRYSTAL.get()));
                primary.shrink(1);
                if (consumeSecondary) {
                    secondary.shrink(1);
                }
                if (output.isEmpty()) {
                    inventory.setStackInSlot(2, recipe.copy());
                } else {
                    output.grow(recipe.getCount());
                }
                progress = 0;
            }
        } else if (progress > 0) {
            progress = 0;
            changed = true;
        }

        if (changed) setChanged();
    }

    private ItemStack getRecipeOutput(ItemStack primary, ItemStack secondary) {
        if (primary.isEmpty()) return ItemStack.EMPTY;

        if (primary.is(ModItems.STEEL_INGOT.get())) {
            if (!secondary.isEmpty() && secondary.is(Items.COAL)) {
                return new ItemStack(ModItems.CARBON_STEEL.get());
            }
            return new ItemStack(ModItems.HARDENED_STEEL_INGOT.get());
        }

        if (primary.is(Items.CLAY_BALL) && !secondary.isEmpty() && secondary.is(ModItems.FLUORITE_CRYSTAL.get())) {
            return new ItemStack(ModItems.CERAMIC_PLATE.get(), 2);
        }

        return ItemStack.EMPTY;
    }

    private boolean canOutput(ItemStack current, ItemStack result) {
        if (current.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(current, result)) return false;
        return current.getCount() + result.getCount() <= current.getMaxStackSize();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
    }
}
