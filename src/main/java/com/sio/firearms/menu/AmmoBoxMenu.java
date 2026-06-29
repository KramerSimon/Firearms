package com.sio.firearms.menu;

import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModItems;
import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;

public class AmmoBoxMenu extends AbstractContainerMenu {

    private static final int SLOT_COUNT = 9;
    private static final int MAX_TOTAL = 256;

    private final net.minecraft.world.InteractionHand hand;
    private final SimpleContainer itemContainer = new SimpleContainer(SLOT_COUNT);

    public AmmoBoxMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, net.minecraft.world.InteractionHand.MAIN_HAND);
    }

    public AmmoBoxMenu(int containerId, Inventory playerInventory, net.minecraft.world.InteractionHand hand) {
        super(ModMenuTypes.AMMO_BOX_MENU.get(), containerId);
        this.hand = hand;

        ItemStack ammoBoxStack = playerInventory.player.getItemInHand(hand);
        ItemContainerContents contents = ammoBoxStack.get(ModDataComponents.AMMO_BOX_ITEMS.get());
        if (contents != null) {
            for (int i = 0; i < Math.min(contents.getSlots(), SLOT_COUNT); i++) {
                itemContainer.setItem(i, contents.getStackInSlot(i).copy());
            }
        }

        // 3×3 ammo grid
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final int slotIndex = row * 3 + col;
                addSlot(new Slot(itemContainer, slotIndex, 44 + col * 18, 17 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return isBullet(stack) && matchesExistingType(stack) && totalBelowMax(stack, slotIndex);
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
    }

    private static boolean isBullet(ItemStack stack) {
        return stack.getItem() == ModItems.BULLET.get()
                || stack.getItem() == ModItems.REFINED_BULLET.get()
                || stack.getItem() == ModItems.ARMOR_PIERCING_BULLET.get();
    }

    private boolean matchesExistingType(ItemStack incoming) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack existing = itemContainer.getItem(i);
            if (!existing.isEmpty()) {
                return existing.getItem() == incoming.getItem();
            }
        }
        return true;
    }

    private boolean totalBelowMax(ItemStack incoming, int targetSlot) {
        int total = incoming.getCount();
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (i == targetSlot) continue;
            total += itemContainer.getItem(i).getCount();
        }
        return total <= MAX_TOTAL;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            ItemStack ammoBoxStack = player.getItemInHand(hand);
            if (!ammoBoxStack.isEmpty()) {
                java.util.ArrayList<ItemStack> items = new java.util.ArrayList<>(SLOT_COUNT);
                for (int i = 0; i < SLOT_COUNT; i++) {
                    items.add(itemContainer.getItem(i).copy());
                }
                ammoBoxStack.set(ModDataComponents.AMMO_BOX_ITEMS.get(),
                        ItemContainerContents.fromItems(items));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < SLOT_COUNT) {
            if (!moveItemStackTo(stack, SLOT_COUNT, SLOT_COUNT + 36, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, SLOT_COUNT, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }

    public int getStoredCount() {
        int total = 0;
        for (int i = 0; i < SLOT_COUNT; i++) {
            total += itemContainer.getItem(i).getCount();
        }
        return total;
    }

    public int getMaxTotal() {
        return MAX_TOTAL;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
