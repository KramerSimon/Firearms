package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.PlasmaEtcherMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModFluids;
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
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

public class PlasmaEtcherBlockEntity extends EnergyStorageBlock implements MenuProvider {

    public static final int CAPACITY       = 35_000;
    public static final int MAX_RECEIVE    = 350;
    public static final int FE_PER_TICK    = 120;
    public static final int PROCESS_TIME   = 350;
    public static final int TANK_SIZE      = 2_000;
    public static final int FLUID_PER_CRAFT = 500;

    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };

    public final FluidTank fluidTank = new FluidTank(TANK_SIZE) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    private int progress = 0;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> progress;
                case 3 -> PROCESS_TIME;
                case 4 -> fluidTank.getFluidAmount();
                case 5 -> TANK_SIZE;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) { if (index == 2) progress = value; }
        @Override public int getCount() { return 6; }
    };

    public PlasmaEtcherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLASMA_ETCHER.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory() { return inventory; }
    public FluidTank getFluidTank()        { return fluidTank; }

    @Override public Component getDisplayName() { return Component.translatable("block.firearms.plasma_etcher"); }

    @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new PlasmaEtcherMenu(id, inv, inventory, data);
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
        ItemStack in0 = inventory.getStackInSlot(0);
        ItemStack out = inventory.getStackInSlot(1);
        ItemStack result = new ItemStack(ModItems.ETCHED_WAFER.get());

        boolean hasChlorine = fluidTank.getFluidAmount() >= FLUID_PER_CRAFT
                && !fluidTank.getFluid().isEmpty()
                && fluidTank.getFluid().getFluid() == ModFluids.CHLORINE_GAS_STILL.get();
        boolean canProcess = stackIs(in0, "firearms:patterned_wafer") && hasChlorine
                && energy.getEnergyStored() >= FE_PER_TICK && canOutput(result, out);

        if (canProcess) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            if (progress >= PROCESS_TIME) {
                in0.shrink(1);
                fluidTank.drain(FLUID_PER_CRAFT, IFluidHandler.FluidAction.EXECUTE);
                if (out.isEmpty()) inventory.setStackInSlot(1, result.copy());
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
        tag.put("FluidTank", fluidTank.writeToNBT(reg, new CompoundTag()));
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
        super.loadAdditional(tag, reg);
        if (tag.contains("Inventory")) inventory.deserializeNBT(reg, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
        if (tag.contains("FluidTank")) fluidTank.readFromNBT(reg, tag.getCompound("FluidTank"));
    }
}
