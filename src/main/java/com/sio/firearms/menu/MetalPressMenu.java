package com.sio.firearms.menu;

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

public class MetalPressMenu extends AbstractContainerMenu {

    private final ItemStackHandler handler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            if (slot < 2) {
                checkRecipe();
            }
        }
    };

    public MetalPressMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.METAL_PRESS_MENU.get(), containerId);

        // Input slot 0
        addSlot(new SlotItemHandler(handler, 0, 44, 22));
        // Input slot 1
        addSlot(new SlotItemHandler(handler, 1, 44, 46));
        // Output slot
        addSlot(new OutputSlot(handler, 2, 116, 35));

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

    private void checkRecipe() {
        // hardened_steel_ingot alone → 2x steel_rod
        if (slotIs(0, "firearms:hardened_steel_ingot") && slotEmpty(1)) {
            handler.setStackInSlot(2, new ItemStack(ModItems.STEEL_ROD.get(), 2));
            return;
        }

        // steel_rod + hardened_steel_ingot → gun_barrel_blank
        if (slotIs(0, "firearms:steel_rod") && slotIs(1, "firearms:hardened_steel_ingot")) {
            handler.setStackInSlot(2, new ItemStack(ModItems.GUN_BARREL_BLANK.get()));
            return;
        }

        // steel_rod + steel_ingot → firing_mechanism
        if (slotIs(0, "firearms:steel_rod") && slotIs(1, "firearms:steel_ingot")) {
            handler.setStackInSlot(2, new ItemStack(ModItems.FIRING_MECHANISM.get()));
            return;
        }

        // gold_ingot alone → 4x gold_foil
        if (slotIs(0, "minecraft:gold_ingot") && slotEmpty(1)) {
            handler.setStackInSlot(2, new ItemStack(ModItems.GOLD_FOIL.get(), 4));
            return;
        }

        // diamond alone → 2x diamond_saw_blade
        if (slotIs(0, "minecraft:diamond") && slotEmpty(1)) {
            handler.setStackInSlot(2, new ItemStack(ModItems.DIAMOND_SAW_BLADE.get(), 2));
            return;
        }

        // uranium_dioxide_powder → uranium_dioxide_pellet
        if (slotIs(0, "firearms:uranium_dioxide_powder") && slotEmpty(1)) {
            handler.setStackInSlot(2, new ItemStack(ModItems.URANIUM_DIOXIDE_PELLET.get()));
            return;
        }

        // zirconium_ingot → fuel_rod_cladding x2
        if (slotIs(0, "firearms:zirconium_ingot") && slotEmpty(1)) {
            handler.setStackInSlot(2, new ItemStack(ModItems.FUEL_ROD_CLADDING.get(), 2));
            return;
        }

        // steel_ingot + steel_ingot → steel_plate (2 ingots consumed, 1 plate per click)
        if (slotIs(0, "firearms:steel_ingot") && slotIs(1, "firearms:steel_ingot")) {
            handler.setStackInSlot(2, new ItemStack(ModItems.STEEL_PLATE.get()));
            return;
        }

        // copper_ingot + copper_ingot → 4x bullet_casing (2 ingots consumed per click)
        if (slotIs(0, "minecraft:copper_ingot") && slotIs(1, "minecraft:copper_ingot")) {
            handler.setStackInSlot(2, new ItemStack(ModItems.BULLET_CASING.get(), 4));
            return;
        }

        // pvc_pellets + pvc_pellets → plastic_sheet (2 pellets per sheet)
        if (slotIs(0, "firearms:pvc_pellets") && slotIs(1, "firearms:pvc_pellets")) {
            handler.setStackInSlot(2, new ItemStack(ModItems.PLASTIC_SHEET.get()));
            return;
        }

        // glass + iron_nugget → syringe
        if (slotIs(0, "minecraft:glass") && slotIs(1, "minecraft:iron_nugget")) {
            handler.setStackInSlot(2, new ItemStack(ModItems.SYRINGE.get()));
            return;
        }

        handler.setStackInSlot(2, ItemStack.EMPTY);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        // slots 0-1: inputs; slot 2: output
        if (index < 3) {
            if (!moveItemStackTo(stack, 3, 39, false)) return ItemStack.EMPTY;
        } else if (index < 30) {
            if (!moveItemStackTo(stack, 0, 2, false)
                    && !moveItemStackTo(stack, 30, 39, false)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, 2, false)
                    && !moveItemStackTo(stack, 3, 30, false)) return ItemStack.EMPTY;
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
            for (int i = 0; i < 2; i++) {
                ItemStack input = handler.getStackInSlot(i);
                if (!input.isEmpty()) {
                    input.shrink(1);
                }
            }
            checkRecipe();
            super.onTake(player, stack);
        }
    }
}
