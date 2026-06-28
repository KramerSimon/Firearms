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

public class ChemicalMixerMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public ChemicalMixerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(5), new SimpleContainerData(10));
    }

    public ChemicalMixerMenu(int containerId, Inventory playerInventory,
                              ItemStackHandler handler, ContainerData data) {
        super(ModMenuTypes.CHEMICAL_MIXER_MENU.get(), containerId);
        this.data = data;

        // slot 0 — item input A
        addSlot(new SlotItemHandler(handler, 0, 47, 17));
        // slot 1 — item input B
        addSlot(new SlotItemHandler(handler, 1, 47, 35));
        // slot 2 — bucket input (accepts any fluid container)
        addSlot(new SlotItemHandler(handler, 2, 47, 53));
        // slot 3 — empty bucket output (take-only)
        addSlot(new SlotItemHandler(handler, 3, 113, 53) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        // slot 4 — item output (take-only)
        addSlot(new SlotItemHandler(handler, 4, 113, 26) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });

        // player inventory
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));

        addDataSlots(data);
    }

    public int getEnergyStored()   { return data.get(0); }
    public int getMaxEnergy()      { return data.get(1); }
    public int getProgress()       { return data.get(2); }
    public int getMaxProgress()    { return data.get(3); }
    public int getFluidInAmount()  { return data.get(4); }
    public int getFluidInMax()     { return data.get(5); }
    public int getFluidOutAmount()  { return data.get(6); }
    public int getFluidOutMax()     { return data.get(7); }
    public int getFluidIn2Amount()  { return data.get(8); }
    public int getFluidIn2Max()     { return data.get(9); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        // slots 0-2: inputs; slots 3-4: outputs
        if (index < 5) {
            if (!moveItemStackTo(stack, 5, 41, false)) return ItemStack.EMPTY;
        } else if (index < 32) {
            if (!moveItemStackTo(stack, 0, 3, false)
                    && !moveItemStackTo(stack, 32, 41, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, 3, false)
                    && !moveItemStackTo(stack, 5, 32, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }

    @Override public boolean stillValid(Player player) { return true; }
}
