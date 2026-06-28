package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class MetalPressJeiRecipe {

    private final ItemStack input0;
    private final ItemStack input1;
    private final ItemStack output;

    public MetalPressJeiRecipe(ItemStack input0, ItemStack input1, ItemStack output) {
        this.input0 = input0;
        this.input1 = input1;
        this.output = output;
    }

    public ItemStack getInput0() {
        return input0;
    }

    public ItemStack getInput1() {
        return input1;
    }

    public ItemStack getOutput() {
        return output;
    }

    public static List<MetalPressJeiRecipe> getAllRecipes() {
        return List.of(
                new MetalPressJeiRecipe(
                        new ItemStack(ModItems.HARDENED_STEEL_INGOT.get()),
                        ItemStack.EMPTY,
                        new ItemStack(ModItems.STEEL_ROD.get(), 2)),
                new MetalPressJeiRecipe(
                        new ItemStack(ModItems.STEEL_ROD.get()),
                        new ItemStack(ModItems.HARDENED_STEEL_INGOT.get()),
                        new ItemStack(ModItems.GUN_BARREL_BLANK.get())),
                new MetalPressJeiRecipe(
                        new ItemStack(ModItems.STEEL_ROD.get()),
                        new ItemStack(ModItems.STEEL_INGOT.get()),
                        new ItemStack(ModItems.FIRING_MECHANISM.get())),
                new MetalPressJeiRecipe(
                        new ItemStack(Items.GOLD_INGOT),
                        ItemStack.EMPTY,
                        new ItemStack(ModItems.GOLD_FOIL.get(), 4)),
                new MetalPressJeiRecipe(
                        new ItemStack(Items.DIAMOND),
                        ItemStack.EMPTY,
                        new ItemStack(ModItems.DIAMOND_SAW_BLADE.get(), 2)),
                // ── Nuclear Reactor Stage 1 ───────────────────────────────────
                new MetalPressJeiRecipe(
                        new ItemStack(ModItems.URANIUM_DIOXIDE_POWDER.get()),
                        ItemStack.EMPTY,
                        new ItemStack(ModItems.URANIUM_DIOXIDE_PELLET.get())),
                new MetalPressJeiRecipe(
                        new ItemStack(ModItems.ZIRCONIUM_INGOT.get()),
                        ItemStack.EMPTY,
                        new ItemStack(ModItems.FUEL_ROD_CLADDING.get(), 2))
        );
    }
}
