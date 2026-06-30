package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class AcidBathJeiRecipe {

    private final ItemStack input;
    private final ItemStack output;
    // Sulfuric acid (250 mB) is always required — shown as a fixed fluid input
    public static final FluidStack ACID_INPUT = new FluidStack(ModFluids.SULFURIC_ACID_STILL.get(), 250);

    public AcidBathJeiRecipe(ItemStack input, ItemStack output) {
        this.input  = input;
        this.output = output;
    }

    public ItemStack getInput()  { return input; }
    public ItemStack getOutput() { return output; }

    public static List<AcidBathJeiRecipe> getAllRecipes() {
        return List.of(
            new AcidBathJeiRecipe(new ItemStack(Items.IRON_INGOT),   new ItemStack(ModItems.ETCHED_IRON.get())),
            new AcidBathJeiRecipe(new ItemStack(Items.COPPER_INGOT), new ItemStack(ModItems.ETCHED_COPPER.get())),
            new AcidBathJeiRecipe(new ItemStack(ModItems.STEEL_INGOT.get()), new ItemStack(ModItems.ETCHED_STEEL.get())),
            new AcidBathJeiRecipe(new ItemStack(ModItems.STAINLESS_STEEL_INGOT.get()), new ItemStack(ModItems.STAINLESS_PLATE.get()))
        );
    }
}
