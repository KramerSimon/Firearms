package com.sio.firearms.menu;

import com.sio.firearms.registry.ModItems;
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

public class RefineryMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public RefineryMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(2), new SimpleContainerData(13));
    }

    public RefineryMenu(int containerId, Inventory playerInventory,
                        ItemStackHandler handler, ContainerData data) {
        super(ModMenuTypes.REFINERY_MENU.get(), containerId);
        this.data = data;

        // Slot 0 — rubber sheet output
        addSlot(new SlotItemHandler(handler, 0, 152, 6) {
            @Override public boolean mayPlace(ItemStack s) { return false; }
        });
        // Slot 1 — gun oil output
        addSlot(new SlotItemHandler(handler, 1, 152, 28) {
            @Override public boolean mayPlace(ItemStack s) { return false; }
        });

        // Player inventory (3×9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 7 + col * 18, 120 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 7 + col * 18, 178));
        }

        addDataSlots(data);
    }

    public int getEnergyStored()     { return data.get(0); }
    public int getMaxEnergy()        { return data.get(1); }
    public int getOilAmount()        { return data.get(2); }
    public boolean isStructureValid(){ return data.get(3) == 1; }
    public int getProgress()         { return data.get(4); }
    public int getMaxProgress()      { return data.get(5); }
    public int getOutputAmount(int i){ return (i >= 0 && i < 7) ? data.get(6 + i) : 0; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        // slots 0-1: outputs only (no inputs); player inv 2-28, hotbar 29-37
        if (index < 2) {
            if (!moveItemStackTo(stack, 2, 38, false)) return ItemStack.EMPTY;
        } else if (index < 29) {
            if (!moveItemStackTo(stack, 29, 38, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 2, 29, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) { return true; }
}
