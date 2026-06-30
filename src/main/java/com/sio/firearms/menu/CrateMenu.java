package com.sio.firearms.menu;

import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CrateMenu extends AbstractContainerMenu {

    public CrateMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(27));
    }

    public CrateMenu(int containerId, Inventory playerInventory, ItemStackHandler handler) {
        super(ModMenuTypes.CRATE_MENU.get(), containerId);

        // 27 crate slots — 3 rows × 9 columns
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new SlotItemHandler(handler, row * 9 + col, 8 + col * 18, 18 + row * 18));
            }
        }

        // Player main inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 86 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 144));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < 27) {
            // Crate → player inventory
            if (!moveItemStackTo(stack, 27, 63, false)) return ItemStack.EMPTY;
        } else {
            // Player → crate
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
