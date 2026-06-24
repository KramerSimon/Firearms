package com.sio.firearms.menu;

import com.sio.firearms.attachment.AttachmentType;
import com.sio.firearms.item.AttachmentItem;
import com.sio.firearms.item.GunItem;
import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModItems;
import com.sio.firearms.registry.ModMenuTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class GunsmithTableMenu extends AbstractContainerMenu {

    private boolean isAttachmentRecipe = false;

    private final ItemStackHandler handler = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            if (slot < 9) {
                checkRecipe();
            }
        }
    };

    public GunsmithTableMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.GUNSMITH_TABLE_MENU.get(), containerId);

        // 3x3 input grid (slots 0-8)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new SlotItemHandler(handler, row * 3 + col, 30 + col * 18, 17 + row * 18));
            }
        }

        // Output slot (slot 9)
        addSlot(new OutputSlot(handler, 9, 124, 35));

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

    private boolean slotIs(int slot, String registryName) {
        ItemStack stack = handler.getStackInSlot(slot);
        if (stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.toString().equals(registryName);
    }

    private boolean slotEmpty(int slot) {
        return handler.getStackInSlot(slot).isEmpty();
    }

    private boolean slotsEmpty(int... slots) {
        for (int s : slots) {
            if (!slotEmpty(s)) return false;
        }
        return true;
    }

    private void checkRecipe() {
        isAttachmentRecipe = false;

        // Pistol: gun_barrel(0), gun_grip(3), trigger_assembly(4), magazine(6)
        if (slotIs(0, "firearms:gun_barrel") && slotEmpty(1) && slotEmpty(2)
                && slotIs(3, "firearms:gun_grip") && slotIs(4, "firearms:trigger_assembly") && slotEmpty(5)
                && slotIs(6, "firearms:magazine") && slotEmpty(7) && slotEmpty(8)) {
            handler.setStackInSlot(9, new ItemStack(ModItems.PISTOL.get()));
            return;
        }

        // Rifle: gun_barrel(1), trigger_assembly(3), gun_grip(4), magazine(6), steel_rod(7)
        if (slotEmpty(0) && slotIs(1, "firearms:gun_barrel") && slotEmpty(2)
                && slotIs(3, "firearms:trigger_assembly") && slotIs(4, "firearms:gun_grip") && slotEmpty(5)
                && slotIs(6, "firearms:magazine") && slotIs(7, "firearms:steel_rod") && slotEmpty(8)) {
            handler.setStackInSlot(9, new ItemStack(ModItems.RIFLE.get()));
            return;
        }

        // Attachment recipes: gun in slot 0, attachment in slot 1, all others empty
        ItemStack gunStack = handler.getStackInSlot(0);
        ItemStack attachmentStack = handler.getStackInSlot(1);
        if (gunStack.getItem() instanceof GunItem
                && attachmentStack.getItem() instanceof AttachmentItem attachmentItem
                && slotsEmpty(2, 3, 4, 5, 6, 7, 8)) {

            AttachmentType type = attachmentItem.getAttachmentType();

            if (type.isRifleOnly() && !gunStack.is(ModItems.RIFLE.get())) {
                handler.setStackInSlot(9, ItemStack.EMPTY);
                return;
            }

            ItemStack result = gunStack.copy();
            result.set(ModDataComponents.ATTACHMENT.get(), type.getName());
            handler.setStackInSlot(9, result);
            isAttachmentRecipe = true;
            return;
        }

        handler.setStackInSlot(9, ItemStack.EMPTY);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
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
            if (isAttachmentRecipe) {
                handler.setStackInSlot(0, ItemStack.EMPTY);
                handler.setStackInSlot(1, ItemStack.EMPTY);
            } else {
                for (int i = 0; i < 9; i++) {
                    ItemStack input = handler.getStackInSlot(i);
                    if (!input.isEmpty()) {
                        input.shrink(1);
                    }
                }
            }
            checkRecipe();
            super.onTake(player, stack);
        }
    }
}
