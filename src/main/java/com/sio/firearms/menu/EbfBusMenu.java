package com.sio.firearms.menu;

import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * Read-only viewer for an EBF item hatch (import / output bus) buffer. The nine
 * buffer slots are shown so you can see what's queued, but they reject all manual
 * placement and pickup — items only flow in and out through pipes / hoppers.
 */
public class EbfBusMenu extends AbstractContainerMenu {

    private static final int SLOTS = 9;

    public EbfBusMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(SLOTS));
    }

    public EbfBusMenu(int containerId, Inventory playerInventory, IItemHandler buffer) {
        super(ModMenuTypes.EBF_BUS_MENU.get(), containerId);
        // 3x3 read-only grid, centred in a 176-wide panel.
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new SlotItemHandler(buffer, col + row * 3, 62 + col * 18, 22 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) { return false; }

                    @Override
                    public boolean mayPickup(Player player) { return false; }
                });
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
