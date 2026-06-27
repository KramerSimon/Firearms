package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class HeatTreatmentJeiRecipe {

    private final ItemStack primary;
    private final ItemStack secondary;
    private final ItemStack output;

    public HeatTreatmentJeiRecipe(ItemStack primary, ItemStack secondary, ItemStack output) {
        this.primary   = primary;
        this.secondary = secondary;
        this.output    = output;
    }

    public ItemStack getPrimary()   { return primary; }
    public ItemStack getSecondary() { return secondary; }
    public ItemStack getOutput()    { return output; }

    public static List<HeatTreatmentJeiRecipe> getAllRecipes() {
        return List.of(
            // steel_ingot alone → hardened_steel_ingot
            new HeatTreatmentJeiRecipe(
                new ItemStack(ModItems.STEEL_INGOT.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get())),
            // steel_ingot + coal → carbon_steel
            new HeatTreatmentJeiRecipe(
                new ItemStack(ModItems.STEEL_INGOT.get()),
                new ItemStack(Items.COAL),
                new ItemStack(ModItems.CARBON_STEEL.get()))
        );
    }
}
