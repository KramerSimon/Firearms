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

public class ChipPackagingMachineMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public ChipPackagingMachineMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(4), new SimpleContainerData(4));
    }

    public ChipPackagingMachineMenu(int containerId, Inventory playerInventory, ItemStackHandler handler, ContainerData data) {
        super(ModMenuTypes.CHIP_PACKAGING_MACHINE_MENU.get(), containerId);
        this.data = data;

        addSlot(new SlotItemHandler(handler, 0, 35, 35));
        addSlot(new SlotItemHandler(handler, 1, 62, 35));
        addSlot(new SlotItemHandler(handler, 2, 89, 35));
        addSlot(new SlotItemHandler(handler, 3, 134, 35) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));

        addDataSlots(data);
    }

    public int getEnergyStored() { return data.get(0); }
    public int getMaxEnergy()    { return data.get(1); }
    public int getProgress()     { return data.get(2); }
    public int getMaxProgress()  { return data.get(3); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < 4) {
            if (!moveItemStackTo(stack, 4, 40, false)) return ItemStack.EMPTY;
        } else if (index < 31) {
            if (!moveItemStackTo(stack, 0, 4, false)
                    && !moveItemStackTo(stack, 31, 40, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, 4, false)
                    && !moveItemStackTo(stack, 4, 31, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }

    @Override public boolean stillValid(Player player) { return true; }
}
