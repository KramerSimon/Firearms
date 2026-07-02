package com.sio.firearms.block;

import com.sio.firearms.entity.AircraftEntity;
import com.sio.firearms.entity.TankEntity;
import com.sio.firearms.menu.RefuelStationMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class RefuelStationBlockEntity extends BlockEntity implements MenuProvider {

    public static final int CAPACITY         = 10_000;
    private static final int SCAN_RADIUS     = 3;
    private static final int SCAN_INTERVAL   = 20;
    private static final int TRANSFER_RATE   = 100; // mB/tick

    public static final int STATUS_NONE     = 0;
    public static final int STATUS_TANK     = 1;
    public static final int STATUS_AIRCRAFT = 2;

    public final FluidTank fluidTank = new FluidTank(CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().isSame(ModFluids.DIESEL_STILL.get())
                    || stack.getFluid().isSame(ModFluids.KEROSENE_STILL.get());
        }
        @Override
        protected void onContentsChanged() { setChanged(); }
    };

    // FluidTank itself implements IFluidHandler with full fill+drain access
    public final IFluidHandler fullAccessHandler = fluidTank;

    private int scanTick = 0;
    private Entity cachedVehicle = null;
    private int status = STATUS_NONE;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int i) {
            return switch (i) {
                case 0 -> fluidTank.getFluidAmount();
                case 1 -> CAPACITY;
                case 2 -> fluidTank.isEmpty() ? 0 : BuiltInRegistries.FLUID.getId(fluidTank.getFluid().getFluid());
                case 3 -> status;
                default -> 0;
            };
        }
        @Override public void set(int i, int v) {}
        @Override public int getCount() { return 4; }
    };

    public RefuelStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFUEL_STATION.get(), pos, state);
    }

    public FluidTank getFluidTank() { return fluidTank; }

    public String getFluidName() {
        if (fluidTank.isEmpty()) return "Empty";
        return BuiltInRegistries.FLUID.getKey(fluidTank.getFluid().getFluid()).toString();
    }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, RefuelStationBlockEntity be) {
        be.serverTick();
    }

    private void serverTick() {
        if (level == null) return;

        scanTick++;
        if (scanTick >= SCAN_INTERVAL || cachedVehicle == null || !cachedVehicle.isAlive()) {
            scanTick = 0;
            cachedVehicle = findNearbyVehicle();
        }

        if (cachedVehicle instanceof TankEntity tank) {
            status = STATUS_TANK;
            if (tank.getFuel() < TankEntity.MAX_FUEL && !fluidTank.isEmpty()
                    && fluidTank.getFluid().getFluid().isSame(ModFluids.DIESEL_STILL.get())) {
                int want = Math.min(TRANSFER_RATE, TankEntity.MAX_FUEL - tank.getFuel());
                FluidStack drained = fluidTank.drain(want, IFluidHandler.FluidAction.EXECUTE);
                if (!drained.isEmpty()) {
                    tank.setFuel(tank.getFuel() + drained.getAmount());
                }
            }
        } else if (cachedVehicle instanceof AircraftEntity aircraft) {
            status = STATUS_AIRCRAFT;
            if (aircraft.getFuel() < AircraftEntity.MAX_FUEL && !fluidTank.isEmpty()
                    && fluidTank.getFluid().getFluid().isSame(ModFluids.KEROSENE_STILL.get())) {
                int want = Math.min(TRANSFER_RATE, AircraftEntity.MAX_FUEL - aircraft.getFuel());
                FluidStack drained = fluidTank.drain(want, IFluidHandler.FluidAction.EXECUTE);
                if (!drained.isEmpty()) {
                    aircraft.setFuel(aircraft.getFuel() + drained.getAmount());
                }
            }
        } else {
            status = STATUS_NONE;
        }
    }

    private Entity findNearbyVehicle() {
        AABB area = new AABB(worldPosition).inflate(SCAN_RADIUS);
        Entity closest = null;
        double closestDistSq = Double.MAX_VALUE;
        for (Entity e : level.getEntitiesOfClass(Entity.class, area,
                e -> e instanceof TankEntity || e instanceof AircraftEntity)) {
            double distSq = e.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = e;
            }
        }
        return closest;
    }

    public int getStatus() { return status; }

    public String getStatusText() {
        return switch (status) {
            case STATUS_TANK -> "Fueling: Tank";
            case STATUS_AIRCRAFT -> "Fueling: Aircraft";
            default -> "No vehicle nearby";
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.refuel_station");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new RefuelStationMenu(id, inv, data);
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
