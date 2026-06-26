package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class EBFJeiRecipe {

    private final ItemStack input0;
    private final ItemStack input1;
    private final ItemStack output;

    public EBFJeiRecipe(ItemStack input0, ItemStack input1, ItemStack output) {
        this.input0 = input0;
        this.input1 = input1;
        this.output = output;
    }

    public ItemStack getInput0() { return input0; }
    public ItemStack getInput1() { return input1; }
    public ItemStack getOutput() { return output; }

    public static List<EBFJeiRecipe> getAllRecipes() {
        return List.of(
            new EBFJeiRecipe(
                new ItemStack(Items.RAW_IRON),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.STEEL_INGOT.get(), 2)),
            new EBFJeiRecipe(
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.STEEL_INGOT.get(), 3)),
            new EBFJeiRecipe(
                new ItemStack(ModItems.STEEL_INGOT.get()),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get())),
            new EBFJeiRecipe(
                new ItemStack(Items.RAW_GOLD),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(Items.GOLD_INGOT, 2)),
            new EBFJeiRecipe(
                new ItemStack(Items.COPPER_INGOT),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.CARBON_STEEL.get())),
            new EBFJeiRecipe(
                new ItemStack(ModItems.TUNGSTEN_ORE_RAW.get()),
                new ItemStack(ModItems.COAL_COKE.get()),
                new ItemStack(ModItems.TUNGSTEN_INGOT.get()))
        );
    }
}
