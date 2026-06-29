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

public class SpentFuelStorageMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public SpentFuelStorageMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(9) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.getItem() == ModItems.SPENT_FUEL_ROD.get();
            }
        }, new SimpleContainerData(2));
    }

    public SpentFuelStorageMenu(int containerId, Inventory playerInventory,
                                 ItemStackHandler handler, ContainerData data) {
        super(ModMenuTypes.SPENT_FUEL_STORAGE_MENU.get(), containerId);
        this.data = data;

        // 3x3 rod slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new SlotItemHandler(handler, row * 3 + col, 62 + col * 18, 17 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.getItem() == ModItems.SPENT_FUEL_ROD.get();
                    }
                });
            }
        }

        // Player inventory
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

    public boolean isStructureValid() { return data.get(0) == 1; }
    public int getRodCount()          { return data.get(1); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < 9) {
            if (!moveItemStackTo(stack, 9, 45, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, 9, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) { return true; }
}
