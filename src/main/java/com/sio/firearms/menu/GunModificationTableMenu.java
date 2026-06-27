package com.sio.firearms.menu;

import com.sio.firearms.attachment.AttachmentType;
import com.sio.firearms.item.AttachmentItem;
import com.sio.firearms.item.GunItem;
import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class GunModificationTableMenu extends AbstractContainerMenu {

    private final ItemStackHandler handler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            if (slot < 3) {
                checkRecipe();
            }
        }
    };

    public GunModificationTableMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.GUN_MODIFICATION_TABLE_MENU.get(), containerId);

        // Slot 0: gun input
        addSlot(new SlotItemHandler(handler, 0, 26, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof GunItem;
            }
        });

        // Slot 1: sight attachment
        addSlot(new SlotItemHandler(handler, 1, 62, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (!(stack.getItem() instanceof AttachmentItem attachmentItem)) return false;
                AttachmentType type = attachmentItem.getAttachmentType();
                return type == AttachmentType.RED_DOT || type == AttachmentType.HOLO_SIGHT
                        || type == AttachmentType.SCOPE_4X || type == AttachmentType.SCOPE_8X;
            }
        });

        // Slot 2: underbarrel attachment
        addSlot(new SlotItemHandler(handler, 2, 62, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (!(stack.getItem() instanceof AttachmentItem attachmentItem)) return false;
                AttachmentType type = attachmentItem.getAttachmentType();
                return type == AttachmentType.LASER || type == AttachmentType.FLASHLIGHT;
            }
        });

        // Slot 3: output
        addSlot(new OutputSlot(handler, 3, 124, 35));

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    private void checkRecipe() {
        ItemStack gunStack = handler.getStackInSlot(0);
        if (!(gunStack.getItem() instanceof GunItem)) {
            handler.setStackInSlot(3, ItemStack.EMPTY);
            return;
        }

        ItemStack sightStack = handler.getStackInSlot(1);
        ItemStack underbarrelStack = handler.getStackInSlot(2);

        if (sightStack.isEmpty() && underbarrelStack.isEmpty()) {
            handler.setStackInSlot(3, ItemStack.EMPTY);
            return;
        }

        ItemStack result = gunStack.copy();

        if (sightStack.getItem() instanceof AttachmentItem sightItem) {
            result.set(ModDataComponents.ATTACHMENT.get(), sightItem.getAttachmentType().getName());
        }

        if (underbarrelStack.getItem() instanceof AttachmentItem underbarrelItem) {
            result.set(ModDataComponents.UNDERBARREL_ATTACHMENT.get(), underbarrelItem.getAttachmentType().getName());
        }

        handler.setStackInSlot(3, result);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        // slots 0-2: inputs (gun, sight, underbarrel); slot 3: output
        if (index < 4) {
            if (!moveItemStackTo(stack, 4, 40, false)) return ItemStack.EMPTY;
        } else if (index < 31) {
            if (!moveItemStackTo(stack, 0, 3, false)
                    && !moveItemStackTo(stack, 31, 40, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, 3, false)
                    && !moveItemStackTo(stack, 4, 31, false)) return ItemStack.EMPTY;
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

    private class OutputSlot extends SlotItemHandler {

        public OutputSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            handler.setStackInSlot(0, ItemStack.EMPTY);
            handler.setStackInSlot(1, ItemStack.EMPTY);
            handler.setStackInSlot(2, ItemStack.EMPTY);
            checkRecipe();
            super.onTake(player, stack);
        }
    }
}
