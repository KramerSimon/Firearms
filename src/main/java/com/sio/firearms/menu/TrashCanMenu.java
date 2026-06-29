package com.sio.firearms.menu;

import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TrashCanMenu extends AbstractContainerMenu {

    private final SimpleContainer container;
    private final ContainerData data;

    // Client-side constructor
    public TrashCanMenu(int id, Inventory playerInv) {
        this(id, playerInv, new SimpleContainer(9), new SimpleContainerData(2));
    }

    // Server-side constructor
    public TrashCanMenu(int id, Inventory playerInv, SimpleContainer container, ContainerData data) {
        super(ModMenuTypes.TRASH_CAN_MENU.get(), id);
        this.container = container;
        this.data = data;
        addDataSlots(data);

        // 9 trash slots (3×3) — items are accepted and purged every 20 ticks
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(container, row * 3 + col, 44 + col * 18, 18 + row * 18));
            }
        }

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public int getFluidAmount() { return data.get(0); }
    public int getFluidMax()    { return data.get(1); }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem().copy();
        if (index < 9) {
            // Can't pull items back out of the trash
            return ItemStack.EMPTY;
        }
        // Shift-click from player → first trash slot
        if (!moveItemStackTo(slot.getItem(), 0, 9, false)) return ItemStack.EMPTY;
        if (slot.getItem().isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return stack;
    }
}
