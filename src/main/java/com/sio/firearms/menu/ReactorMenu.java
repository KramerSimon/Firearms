package com.sio.firearms.menu;

import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ReactorMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public ReactorMenu(int id, Inventory inv) {
        this(id, inv, new ItemStackHandler(6), new SimpleContainerData(10));
    }

    public ReactorMenu(int id, Inventory inv, ItemStackHandler machineItems, ContainerData data) {
        super(ModMenuTypes.REACTOR_MENU.get(), id);
        this.data = data;

        // Fuel rod slots 0–3: 2×2 grid starting at (10, 20), 18px apart
        addSlot(new SlotItemHandler(machineItems, 0, 10, 20));
        addSlot(new SlotItemHandler(machineItems, 1, 28, 20));
        addSlot(new SlotItemHandler(machineItems, 2, 10, 38));
        addSlot(new SlotItemHandler(machineItems, 3, 28, 38));

        // Control rod slots 4–5: below fuel rods at (10, 74)
        addSlot(new SlotItemHandler(machineItems, 4, 10, 74));
        addSlot(new SlotItemHandler(machineItems, 5, 28, 74));

        // Player inventory (3 rows × 9 cols) starting at (27, 120)
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 27 + col * 18, 120 + row * 18));

        // Hotbar starting at (27, 178)
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(inv, col, 27 + col * 18, 178));

        addDataSlots(data);
    }

    public int getEnergyStored()      { return data.get(0); }
    public int getMaxEnergy()         { return data.get(1); }
    public int getFeRate()            { return data.get(2); }
    public int getTemperature()       { return data.get(3); }
    public int getWaterAmount()       { return data.get(4); }
    public int getWaterMax()          { return data.get(5); }
    public int getSteamAmount()       { return data.get(6); }
    public int getSteamMax()          { return data.get(7); }
    public boolean isStructureValid() { return data.get(8) == 1; }
    public int getMeltdownTicks()     { return data.get(9); }

    @Override public ItemStack quickMoveStack(Player p, int i) { return ItemStack.EMPTY; }
    @Override public boolean stillValid(Player p) { return true; }
}
