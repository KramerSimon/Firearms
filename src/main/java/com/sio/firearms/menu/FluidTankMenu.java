package com.sio.firearms.menu;

import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FluidTankMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public FluidTankMenu(int id, Inventory inv) {
        this(id, inv, new SimpleContainerData(2));
    }

    public FluidTankMenu(int id, Inventory inv, ContainerData data) {
        super(ModMenuTypes.FLUID_TANK_MENU.get(), id);
        this.data = data;

        // Player inventory (no machine slots — tank stores fluid, not items)
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(inv, col, 8 + col * 18, 142));

        addDataSlots(data);
    }

    public int getFluidAmount() { return data.get(0); }
    public int getCapacity()    { return data.get(1); }

    @Override
    public ItemStack quickMoveStack(Player p, int i) { return ItemStack.EMPTY; }

    @Override
    public boolean stillValid(Player p) { return true; }
}
