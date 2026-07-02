package com.sio.firearms.menu;

import com.sio.firearms.block.RefuelStationBlockEntity;
import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RefuelStationMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public RefuelStationMenu(int id, Inventory inv) {
        this(id, inv, new SimpleContainerData(4));
    }

    public RefuelStationMenu(int id, Inventory inv, ContainerData data) {
        super(ModMenuTypes.REFUEL_STATION_MENU.get(), id);
        this.data = data;

        // Player inventory (no machine slots — the station stores fluid, not items)
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(inv, col, 8 + col * 18, 142));

        addDataSlots(data);
    }

    public int getFluidAmount()  { return data.get(0); }
    public int getCapacity()     { return data.get(1); }
    public int getFluidTypeId()  { return data.get(2); }
    public int getStatus()       { return data.get(3); }

    public String getStatusText() {
        return switch (getStatus()) {
            case RefuelStationBlockEntity.STATUS_TANK -> "Fueling: Tank";
            case RefuelStationBlockEntity.STATUS_AIRCRAFT -> "Fueling: Aircraft";
            default -> "No vehicle nearby";
        };
    }

    @Override
    public ItemStack quickMoveStack(Player p, int i) { return ItemStack.EMPTY; }

    @Override
    public boolean stillValid(Player p) { return true; }
}
