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

public class HangarMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public HangarMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(10), new SimpleContainerData(4));
    }

    public HangarMenu(int containerId, Inventory playerInventory,
                      SimpleContainer container, ContainerData data) {
        super(ModMenuTypes.HANGAR_MENU.get(), containerId);
        this.data = data;

        // 10 input slots arranged as 2 rows of 5 (x=8..88, y=18..36)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                addSlot(new Slot(container, row * 5 + col, 8 + col * 18, 18 + row * 18));
            }
        }

        // Player main inventory (3 rows × 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 162));
        }

        addDataSlots(data);
    }

    public int getEnergyStored()  { return data.get(0); }
    public int getMaxEnergy()     { return data.get(1); }
    public int getBuildProgress() { return data.get(2); }
    public boolean isStructureValid() { return data.get(3) == 1; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < 10) {
            if (!moveItemStackTo(stack, 10, 46, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, 10, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) { return true; }
}
