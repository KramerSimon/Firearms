package com.sio.firearms.menu;

import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ElectrolysisMachineMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public ElectrolysisMachineMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(1), new SimpleContainerData(10));
    }

    public ElectrolysisMachineMenu(int containerId, Inventory playerInventory,
                                   IItemHandler handler, ContainerData data) {
        super(ModMenuTypes.ELECTROLYSIS_MACHINE_MENU.get(), containerId);
        this.data = data;

        // Item input slot at x=80, y=35
        addSlot(new SlotItemHandler(handler, 0, 80, 35));

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
    public int getProgress()      { return data.get(2); }
    public int getMaxProgress()   { return data.get(3); }
    public int getFluidIn()       { return data.get(4); }
    public int getFluidInMax()    { return data.get(5); }
    public int getFluidOut1()     { return data.get(6); }
    public int getFluidOut1Max()  { return data.get(7); }
    public int getFluidOut2()     { return data.get(8); }
    public int getFluidOut2Max()  { return data.get(9); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        // slot 0: item input
        if (index < 1) {
            if (!moveItemStackTo(stack, 1, 37, false)) return ItemStack.EMPTY;
        } else if (index < 28) {
            if (!moveItemStackTo(stack, 0, 1, false)
                    && !moveItemStackTo(stack, 28, 37, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, 1, false)
                    && !moveItemStackTo(stack, 1, 28, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) { return true; }
}
