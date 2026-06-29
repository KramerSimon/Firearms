package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.AcidBathMenu;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

public class AcidBathBlockEntity extends EnergyStorageBlock implements MenuProvider {

    public static final int CAPACITY     = 20_000;
    public static final int MAX_RECEIVE  = 400;
    public static final int FE_PER_TICK  = 60;
    public static final int PROCESS_TIME = 300;
    public static final int TANK_SIZE    = 5_000;

    // slots: 0=input, 1=output
    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    public final FluidTank acidTank = new FluidTank(TANK_SIZE) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return !stack.isEmpty() && stack.getFluid().isSame(ModFluids.SULFURIC_ACID_STILL.get());
        }
        @Override
        protected void onContentsChanged() { setChanged(); }
    };

    private int progress = 0;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> progress;
                case 3 -> PROCESS_TIME;
                case 4 -> acidTank.getFluidAmount();
                case 5 -> TANK_SIZE;
                default -> 0;
            };
        }
        @Override
        public void set(int i, int v) { if (i == 2) progress = v; }
        @Override
        public int getCount() { return 6; }
    };

    public AcidBathBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ACID_BATH.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    // Fill-only: accept sulfuric acid input, no drain
    public final IFluidHandler fillOnlyHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int t) { return acidTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t) { return acidTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack s) { return acidTank.isFluidValid(0, s); }
        @Override public int fill(FluidStack resource, FluidAction a) { return acidTank.fill(resource, a); }
        @Override public FluidStack drain(FluidStack resource, FluidAction a) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int maxDrain, FluidAction a) { return FluidStack.EMPTY; }
    };

    // Input-only machine; fullAccessHandler delegates entirely to fillOnlyHandler
    public final IFluidHandler fullAccessHandler = fillOnlyHandler;

    public ItemStackHandler getInventory() { return inventory; }
    public FluidTank getAcidTank()         { return acidTank; }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.acid_bath");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new AcidBathMenu(id, inv, inventory, data);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static String itemKey(ItemStack stack) {
        if (stack.isEmpty()) return "";
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private boolean itemIs(ItemStack stack, String id) {
        return !stack.isEmpty() && itemKey(stack).equals(id);
    }

    private boolean hasAcid(int mb) {
        return acidTank.getFluidAmount() >= mb;
    }

    private boolean canOutput(ItemStack current, ItemStack result) {
        if (current.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(current, result)
            && current.getCount() + result.getCount() <= current.getMaxStackSize();
    }

    // ── Recipe ───────────────────────────────────────────────────────────────

    private record AcidRecipe(String inputId, ItemStack result) {}

    private AcidRecipe findRecipe() {
        ItemStack input  = inventory.getStackInSlot(0);
        ItemStack output = inventory.getStackInSlot(1);
        if (!hasAcid(250)) return null;

        if (itemIs(input, "firearms:steel_ingot") && canOutput(output, new ItemStack(ModItems.ETCHED_STEEL.get())))
            return new AcidRecipe("firearms:steel_ingot", new ItemStack(ModItems.ETCHED_STEEL.get()));

        if (itemIs(input, "minecraft:copper_ingot") && canOutput(output, new ItemStack(ModItems.ETCHED_COPPER.get())))
            return new AcidRecipe("minecraft:copper_ingot", new ItemStack(ModItems.ETCHED_COPPER.get()));

        if (itemIs(input, "minecraft:iron_ingot") && canOutput(output, new ItemStack(ModItems.ETCHED_IRON.get())))
            return new AcidRecipe("minecraft:iron_ingot", new ItemStack(ModItems.ETCHED_IRON.get()));

        return null;
    }

    // ── Tick ─────────────────────────────────────────────────────────────────

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;

        AcidRecipe recipe = findRecipe();

        if (recipe != null && energy.getEnergyStored() >= FE_PER_TICK) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            changed = true;

            if (progress >= PROCESS_TIME) {
                inventory.getStackInSlot(0).shrink(1);
                acidTank.drain(250, IFluidHandler.FluidAction.EXECUTE);

                ItemStack outSlot = inventory.getStackInSlot(1);
                if (outSlot.isEmpty()) inventory.setStackInSlot(1, recipe.result().copy());
                else outSlot.grow(1);

                progress = 0;
            }
        } else if (progress > 0) {
            progress = 0;
            changed = true;
        }

        if (changed) setChanged();
    }

    // ── NBT ──────────────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory",   inventory.serializeNBT(registries));
        tag.put("AcidTank",    acidTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("AcidTank"))  acidTank.readFromNBT(registries, tag.getCompound("AcidTank"));
        progress = tag.getInt("Progress");
    }
}
