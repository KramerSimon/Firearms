package com.sio.firearms.menu;

import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CokeOvenMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public CokeOvenMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(2), new SimpleContainerData(4));
    }

    public CokeOvenMenu(int containerId, Inventory playerInventory,
                        ItemStackHandler handler, ContainerData data) {
        super(ModMenuTypes.COKE_OVEN_MENU.get(), containerId);
        this.data = data;

        addSlot(new SlotItemHandler(handler, 0, 56, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.COAL);
            }
        });

        addSlot(new SlotItemHandler(handler, 1, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    public int getProgress()    { return data.get(0); }
    public int getMaxProgress() { return data.get(1); }
    public int getFluidAmount() { return data.get(2); }
    public int getMaxFluid()    { return data.get(3); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        // slot 0: coal input; slot 1: coke output
        if (index < 2) {
            if (!moveItemStackTo(stack, 2, 38, false)) return ItemStack.EMPTY;
        } else if (index < 29) {
            if (!moveItemStackTo(stack, 0, 1, false)
                    && !moveItemStackTo(stack, 29, 38, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, 1, false)
                    && !moveItemStackTo(stack, 2, 29, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
