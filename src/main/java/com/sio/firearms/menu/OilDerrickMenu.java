package com.sio.firearms.menu;

import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OilDerrickMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public OilDerrickMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainerData(5));
    }

    public OilDerrickMenu(int containerId, Inventory playerInventory, ContainerData data) {
        super(ModMenuTypes.OIL_DERRICK_MENU.get(), containerId);
        this.data = data;

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

    public int getEnergyStored() {
        return data.get(0);
    }

    public int getMaxEnergy() {
        return data.get(1);
    }

    public int getFluidAmount() {
        return data.get(2);
    }

    public int getMaxFluid() {
        return data.get(3);
    }

    public boolean isStructureValid() {
        return data.get(4) == 1;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        // no machine slots — swap between player inv (0-26) and hotbar (27-35)
        if (index < 27) {
            if (!moveItemStackTo(stack, 27, 36, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, 27, false)) return ItemStack.EMPTY;
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
