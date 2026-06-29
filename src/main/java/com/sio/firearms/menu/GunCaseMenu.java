package com.sio.firearms.menu;

import com.sio.firearms.item.AttachmentItem;
import com.sio.firearms.item.GunItem;
import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;


public class GunCaseMenu extends AbstractContainerMenu {

    private static final int GUN_SLOT        = 0;
    private static final int ATTACHMENT_START = 1;
    private static final int TOTAL_SLOTS      = 5; // 1 gun + 4 attachments

    private final net.minecraft.world.InteractionHand hand;
    private final SimpleContainer itemContainer = new SimpleContainer(TOTAL_SLOTS);

    public GunCaseMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, net.minecraft.world.InteractionHand.MAIN_HAND);
    }

    public GunCaseMenu(int containerId, Inventory playerInventory, net.minecraft.world.InteractionHand hand) {
        super(ModMenuTypes.GUN_CASE_MENU.get(), containerId);
        this.hand = hand;

        ItemStack caseStack = playerInventory.player.getItemInHand(hand);
        ItemContainerContents contents = caseStack.get(ModDataComponents.GUN_CASE_ITEMS.get());
        if (contents != null) {
            for (int i = 0; i < Math.min(contents.getSlots(), TOTAL_SLOTS); i++) {
                itemContainer.setItem(i, contents.getStackInSlot(i).copy());
            }
        }

        // Slot 0: gun
        addSlot(new Slot(itemContainer, GUN_SLOT, 44, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof GunItem;
            }
        });

        // Slots 1-4: attachments
        int[] attachX = {80, 98, 116, 134};
        for (int i = 0; i < 4; i++) {
            final int si = ATTACHMENT_START + i;
            addSlot(new Slot(itemContainer, si, attachX[i], 35) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() instanceof AttachmentItem;
                }
            });
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

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            ItemStack caseStack = player.getItemInHand(hand);
            if (!caseStack.isEmpty()) {
                List<ItemStack> items = new ArrayList<>(TOTAL_SLOTS);
                for (int i = 0; i < TOTAL_SLOTS; i++) {
                    items.add(itemContainer.getItem(i).copy());
                }
                caseStack.set(ModDataComponents.GUN_CASE_ITEMS.get(),
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
        if (index < TOTAL_SLOTS) {
            if (!moveItemStackTo(stack, TOTAL_SLOTS, TOTAL_SLOTS + 36, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, TOTAL_SLOTS, false)) return ItemStack.EMPTY;
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
