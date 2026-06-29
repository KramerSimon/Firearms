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

public class VehicleGarageMenu extends AbstractContainerMenu {

    private final ContainerData data;

    // Client-side placeholder constructor
    public VehicleGarageMenu(int id, Inventory inv) {
        this(id, inv, new SimpleContainer(10), new SimpleContainerData(4));
    }

    // Server-side constructor
    public VehicleGarageMenu(int id, Inventory inv, SimpleContainer container, ContainerData data) {
        super(ModMenuTypes.VEHICLE_GARAGE_MENU.get(), id);
        this.data = data;

        // 10 input slots: 2 rows × 5 cols at (8, 18) and (8, 36)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                addSlot(new Slot(container, row * 5 + col, 8 + col * 18, 18 + row * 18));
            }
        }

        // Player inventory (3 rows × 9 cols) at (8, 96)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 96 + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 154));
        }

        addDataSlots(data);
    }

    public int getEnergyStored()      { return data.get(0); }
    public int getMaxEnergy()         { return data.get(1); }
    public int getBuildProgress()     { return data.get(2); }
    public boolean isStructureValid() { return data.get(3) == 1; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return result;

        ItemStack stack = slot.getItem();
        result = stack.copy();

        if (index < 10) {
            // Input slots → player inventory
            if (!moveItemStackTo(stack, 10, 46, true)) return ItemStack.EMPTY;
        } else {
            // Player inventory → input slots
            if (!moveItemStackTo(stack, 0, 10, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return result;
    }

    @Override
    public boolean stillValid(Player player) { return true; }
}
