package com.sio.firearms.compat.jei;

import com.sio.firearms.Firearms;
import com.sio.firearms.registry.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ElectrolysisRecipeCategory implements IRecipeCategory<ElectrolysisJeiRecipe> {

    public static final RecipeType<ElectrolysisJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "electrolysis", ElectrolysisJeiRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public ElectrolysisRecipeCategory(IGuiHelper guiHelper) {
        // Electrolysis GUI: item slot (80,35), water in bar (25,14-66), out1 bar (130,14-66), out2 bar (148,14-66).
        // Crop origin (7,8), size 158x60 captures all elements.
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "textures/gui/electrolysis_machine.png");
        this.background = guiHelper.createDrawable(texture, 7, 8, 158, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ELECTROLYSIS_MACHINE.get()));
        this.title = Component.translatable("block.firearms.electrolysis_machine");
    }

    @Override public RecipeType<ElectrolysisJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return 158; }
    @Override public int getHeight() { return 60; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ElectrolysisJeiRecipe recipe, IFocusGroup focuses) {
        // Fluid input (water tank): abs bar at (25,14) → JEI (18,6). 16x52 fluid tank.
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 6)
                .setFluidRenderer(recipe.getInputFluid().getAmount(), false, 16, 52)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getInputFluid());

        // Item input (optional catalyst): abs slot (80,35) → JEI (73,27)
        if (!recipe.getInputItem().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 73, 27)
                    .addItemStack(recipe.getInputItem());
        }

        // Output1: abs bar at (130,14) → JEI (123,6). 16x52 fluid tank.
        builder.addSlot(RecipeIngredientRole.OUTPUT, 123, 6)
                .setFluidRenderer(recipe.getOutput1().getAmount(), false, 16, 52)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getOutput1());

        // Output2: abs bar at (148,14) → JEI (141,6). 16x52 fluid tank.
        builder.addSlot(RecipeIngredientRole.OUTPUT, 141, 6)
                .setFluidRenderer(recipe.getOutput2().getAmount(), false, 16, 52)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getOutput2());
    }
}
