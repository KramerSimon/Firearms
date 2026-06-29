package com.sio.firearms.block;

import com.sio.firearms.menu.CokeOvenMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModFluids;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CokeOvenControllerBlockEntity extends BlockEntity implements MenuProvider {

    public static final int MAX_PROCESS_TIME = 600;
    public static final int CREOSOTE_PER_CYCLE = 100;
    public static final int TANK_CAPACITY = 10_000;

    private int progress = 0;
    private boolean structureValid = false;

    public final FluidTank creosoteTank = new FluidTank(TANK_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().isSame(ModFluids.CREOSOTE_OIL_STILL.get());
        }
    };

    // Drain-only: creosote output only, no fill accepted
    public final IFluidHandler drainOnlyHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int t) { return creosoteTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t) { return creosoteTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack s) { return false; }
        @Override public int fill(FluidStack r, FluidAction a) { return 0; }
        @Override public FluidStack drain(FluidStack resource, FluidAction a) { return creosoteTank.drain(resource, a); }
        @Override public FluidStack drain(int maxDrain, FluidAction a) { return creosoteTank.drain(maxDrain, a); }
    };

    // Output-only machine; fullAccessHandler delegates entirely to drainOnlyHandler
    public final IFluidHandler fullAccessHandler = drainOnlyHandler;

    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> MAX_PROCESS_TIME;
                case 2 -> creosoteTank.getFluidAmount();
                case 3 -> TANK_CAPACITY;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) progress = value;
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public CokeOvenControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COKE_OVEN_CONTROLLER.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public FluidTank getCreosoteTank() {
        return creosoteTank;
    }

    public boolean isStructureValid() {
        return structureValid;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.coke_oven_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CokeOvenMenu(containerId, playerInventory, inventory, data);
    }

    public boolean checkStructure() {
        if (level == null) return false;
        for (int ox = -2; ox <= 0; ox++)
            for (int oy = -2; oy <= 0; oy++)
                for (int oz = -2; oz <= 0; oz++) {
                    if (isValidAt(worldPosition.offset(ox, oy, oz))) {
                        structureValid = true;
                        return true;
                    }
                }
        structureValid = false;
        return false;
    }

    private boolean isValidAt(BlockPos origin) {
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                for (int z = 0; z < 3; z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (pos.equals(worldPosition)) continue;
                    if (!level.getBlockState(pos).is(ModBlocks.COKE_OVEN_BRICK.get())) return false;
                }
        return true;
    }

    public void serverTick() {
        if (level == null) return;

        if (level.getGameTime() % 40 == 0) checkStructure();

        if (!structureValid) {
            if (progress > 0) { progress = 0; setChanged(); }
            return;
        }

        ItemStack input = inventory.getStackInSlot(0);
        ItemStack output = inventory.getStackInSlot(1);

        boolean canProcess = input.is(Items.COAL)
                && (output.isEmpty() || (output.is(ModItems.COAL_COKE.get())
                        && output.getCount() < output.getMaxStackSize()))
                && creosoteTank.getFluidAmount() + CREOSOTE_PER_CYCLE <= TANK_CAPACITY;

        if (canProcess) {
            progress++;
            if (progress >= MAX_PROCESS_TIME) {
                input.shrink(1);
                if (output.isEmpty()) {
                    inventory.setStackInSlot(1, new ItemStack(ModItems.COAL_COKE.get()));
                } else {
                    output.grow(1);
                }
                creosoteTank.fill(
                        new FluidStack(ModFluids.CREOSOTE_OIL_STILL.get(), CREOSOTE_PER_CYCLE),
                        IFluidHandler.FluidAction.EXECUTE);
                progress = 0;
            }
            setChanged();
        } else if (progress > 0) {
            progress = 0;
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.put("CreosoteTank", creosoteTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("Progress", progress);
        tag.putBoolean("StructureValid", structureValid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("CreosoteTank")) creosoteTank.readFromNBT(registries, tag.getCompound("CreosoteTank"));
        progress = tag.getInt("Progress");
        structureValid = tag.getBoolean("StructureValid");
    }
}
