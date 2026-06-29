package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.SteamTurbineMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class SteamTurbineBlockEntity extends EnergyStorageBlock implements MenuProvider {

    public static final int CAPACITY       = 100_000;
    public static final int MAX_FE_OUT     = 400;
    public static final int STEAM_CAPACITY = 50_000;
    public static final int STEAM_PER_TICK = 100;
    public static final int FE_PER_TICK    = 400;

    private int feOutputRate = 0;

    public final FluidTank steamTank = new FluidTank(STEAM_CAPACITY,
            fs -> fs.getFluid().isSame(ModFluids.STEAM_STILL.get())) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    // Fill-only handler exposed to fluid system
    public final IFluidHandler steamInputHandler = new IFluidHandler() {
        @Override public int getTanks()                               { return 1; }
        @Override public FluidStack getFluidInTank(int t)            { return steamTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int t)                   { return steamTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int t, FluidStack s)    { return steamTank.isFluidValid(0, s); }
        @Override public int fill(FluidStack r, FluidAction a)        { return steamTank.fill(r, a); }
        @Override public FluidStack drain(FluidStack r, FluidAction a){ return FluidStack.EMPTY; }
        @Override public FluidStack drain(int max, FluidAction a)     { return FluidStack.EMPTY; }
    };

    private final ContainerData data = new ContainerData() {
        @Override public int get(int i) {
            return switch (i) {
                case 0 -> energy.getEnergyStored();
                case 1 -> CAPACITY;
                case 2 -> steamTank.getFluidAmount();
                case 3 -> STEAM_CAPACITY;
                case 4 -> feOutputRate;
                default -> 0;
            };
        }
        @Override public void set(int i, int v) {}
        @Override public int getCount() { return 5; }
    };

    public SteamTurbineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STEAM_TURBINE.get(), pos, state, CAPACITY, MAX_FE_OUT, MAX_FE_OUT);
    }

    public FluidTank getSteamTank()               { return steamTank; }
    public IFluidHandler getSteamInputHandler()    { return steamInputHandler; }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.steam_turbine");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new SteamTurbineMenu(id, inv, data);
    }

    public void serverTick() {
        // Standalone generation removed — this block is now a structural component of the Cooling Tower.
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("SteamTank", steamTank.writeToNBT(registries, new CompoundTag()));
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("SteamTank", steamTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("FeOutputRate", feOutputRate);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("SteamTank")) steamTank.readFromNBT(registries, tag.getCompound("SteamTank"));
        feOutputRate = tag.getInt("FeOutputRate");
    }
}
