package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class LatheJeiRecipe {

    private final ItemStack primary;
    private final ItemStack secondary; // may be EMPTY
    private final ItemStack output;

    public LatheJeiRecipe(ItemStack primary, ItemStack secondary, ItemStack output) {
        this.primary   = primary;
        this.secondary = secondary;
        this.output    = output;
    }

    public ItemStack getPrimary()   { return primary; }
    public ItemStack getSecondary() { return secondary; }
    public ItemStack getOutput()    { return output; }

    public static List<LatheJeiRecipe> getAllRecipes() {
        return List.of(
            new LatheJeiRecipe(
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.STEEL_ROD.get(), 2)),
            new LatheJeiRecipe(
                new ItemStack(ModItems.STEEL_ROD.get()),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get()),
                new ItemStack(ModItems.GUN_BARREL_BLANK.get())),
            new LatheJeiRecipe(
                new ItemStack(ModItems.STEEL_ROD.get()),
                new ItemStack(ModItems.STEEL_INGOT.get()),
                new ItemStack(ModItems.FIRING_MECHANISM.get())),
            new LatheJeiRecipe(
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get()),
                new ItemStack(ModItems.CARBON_STEEL.get()),
                new ItemStack(ModItems.SPRING.get())),
            new LatheJeiRecipe(
                new ItemStack(ModItems.CARBON_STEEL.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.FIRING_PIN.get())),
            new LatheJeiRecipe(
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get()),
                new ItemStack(ModItems.STEEL_ROD.get()),
                new ItemStack(ModItems.BOLT.get(), 2)),
            new LatheJeiRecipe(
                new ItemStack(ModItems.STEEL_ROD.get()),
                new ItemStack(ModItems.RUBBER_SHEET.get()),
                new ItemStack(ModItems.BUFFER_TUBE.get())),
            new LatheJeiRecipe(
                new ItemStack(ModItems.TUNGSTEN_INGOT.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.TUNGSTEN_ROD.get(), 2))
        );
    }
}
