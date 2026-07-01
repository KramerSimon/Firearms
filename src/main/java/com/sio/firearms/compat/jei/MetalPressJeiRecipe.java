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
                        new ItemStack(ModItems.FUEL_ROD_CLADDING.get(), 2)),
                // ── Tank Production Chain & new materials ─────────────────────
                new MetalPressJeiRecipe(
                        new ItemStack(ModItems.STEEL_INGOT.get()),
                        new ItemStack(ModItems.STEEL_INGOT.get()),
                        new ItemStack(ModItems.STEEL_PLATE.get())),
                // copper_ingot x2 → 4x bullet_casing
                new MetalPressJeiRecipe(
                        new ItemStack(Items.COPPER_INGOT),
                        new ItemStack(Items.COPPER_INGOT),
                        new ItemStack(ModItems.BULLET_CASING.get(), 4)),
                // pvc_pellets x2 → plastic_sheet
                new MetalPressJeiRecipe(
                        new ItemStack(ModItems.PVC_PELLETS.get()),
                        new ItemStack(ModItems.PVC_PELLETS.get()),
                        new ItemStack(ModItems.PLASTIC_SHEET.get())),
                // glass + iron_nugget → syringe
                new MetalPressJeiRecipe(
                        new ItemStack(Items.GLASS),
                        new ItemStack(Items.IRON_NUGGET),
                        new ItemStack(ModItems.SYRINGE.get())),
                // ── Gun Parts (intermediate) ───────────────────────────────────
                // rubber_sheet x2 → grip
                new MetalPressJeiRecipe(
                        new ItemStack(ModItems.RUBBER_SHEET.get()),
                        new ItemStack(ModItems.RUBBER_SHEET.get()),
                        new ItemStack(ModItems.GRIP.get())),
                // steel_ingot + spring → magazine
                new MetalPressJeiRecipe(
                        new ItemStack(ModItems.STEEL_INGOT.get()),
                        new ItemStack(ModItems.SPRING.get()),
                        new ItemStack(ModItems.MAGAZINE.get())),
                // steel_ingot + rubber_sheet → stock
                new MetalPressJeiRecipe(
                        new ItemStack(ModItems.STEEL_INGOT.get()),
                        new ItemStack(ModItems.RUBBER_SHEET.get()),
                        new ItemStack(ModItems.STOCK.get()))
        );
    }
}
