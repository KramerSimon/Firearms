package com.sio.firearms.menu;

import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WaterPumpMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public WaterPumpMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainerData(4));
    }

    public WaterPumpMenu(int containerId, Inventory playerInventory, ContainerData data) {
        super(ModMenuTypes.WATER_PUMP_MENU.get(), containerId);
        this.data = data;

        // No item slots — this machine only handles fluids and energy.

        // Player inventory
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));

        addDataSlots(data);
    }

    public int getEnergyStored()  { return data.get(0); }
    public int getMaxEnergy()     { return data.get(1); }
    public int getWaterAmount()   { return data.get(2); }
    public int getWaterMax()      { return data.get(3); }

    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
    @Override public boolean stillValid(Player player) { return true; }
}
