package com.sio.firearms.menu;

import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SteamTurbineMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public SteamTurbineMenu(int id, Inventory inv) {
        this(id, inv, new SimpleContainerData(5));
    }

    public SteamTurbineMenu(int id, Inventory inv, ContainerData data) {
        super(ModMenuTypes.STEAM_TURBINE_MENU.get(), id);
        this.data = data;

        // Player inventory
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(inv, col, 8 + col * 18, 142));

        addDataSlots(data);
    }

    public int getEnergyStored() { return data.get(0); }
    public int getMaxEnergy()    { return data.get(1); }
    public int getSteamAmount()  { return data.get(2); }
    public int getSteamMax()     { return data.get(3); }
    public int getFeRate()       { return data.get(4); }

    @Override public ItemStack quickMoveStack(Player p, int i) { return ItemStack.EMPTY; }
    @Override public boolean stillValid(Player p) { return true; }
}
