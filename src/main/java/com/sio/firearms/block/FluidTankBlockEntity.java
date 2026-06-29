package com.sio.firearms.block;

import com.sio.firearms.menu.FluidTankMenu;
import com.sio.firearms.registry.ModBlockEntities;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidTankBlockEntity extends BlockEntity implements MenuProvider {

    public static final int CAPACITY = 16_000;

    public final FluidTank fluidTank = new FluidTank(CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            if (!isEmpty()) {
                return getFluid().getFluid().isSame(stack.getFluid());
            }
            return !stack.isEmpty();
        }
        @Override
        protected void onContentsChanged() { setChanged(); }
    };

    // FluidTank itself implements IFluidHandler with full fill+drain access
    public final IFluidHandler fullAccessHandler = fluidTank;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int i) {
            return switch (i) {
                case 0 -> fluidTank.getFluidAmount();
                case 1 -> CAPACITY;
                default -> 0;
            };
        }
        @Override public void set(int i, int v) {}
        @Override public int getCount() { return 2; }
    };

    public FluidTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_TANK.get(), pos, state);
    }

    public FluidTank getFluidTank() { return fluidTank; }

    public String getFluidName() {
        if (fluidTank.isEmpty()) return "Empty";
        return BuiltInRegistries.FLUID.getKey(fluidTank.getFluid().getFluid()).toString();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.fluid_tank");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new FluidTankMenu(id, inv, data);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("FluidTank", fluidTank.writeToNBT(registries, new CompoundTag()));
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FluidTank", fluidTank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FluidTank")) fluidTank.readFromNBT(registries, tag.getCompound("FluidTank"));
    }
}
