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

public class CokeOvenRecipeCategory implements IRecipeCategory<CokeOvenJeiRecipe> {

    public static final RecipeType<CokeOvenJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "coke_oven", CokeOvenJeiRecipe.class);

    // GUI slots: coal input (56,35), coke output (116,35)
    // Creosote tank is on the right side of the GUI.
    // Crop (8,8) size (160,56).
    // JEI item input:    (56-8, 35-8) = (48, 27)
    // JEI item output:   (116-8, 35-8) = (108, 27)  → shift left to leave room for fluid on right
    // JEI creosote out:  (137, 5) — 16×46 column
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public CokeOvenRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                Firearms.MOD_ID, "textures/gui/coke_oven.png");
        this.background = guiHelper.createDrawable(texture, 8, 8, 160, 56);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.COKE_OVEN_CONTROLLER.get()));
        this.title = Component.translatable("block.firearms.coke_oven_controller");
    }

    @Override public RecipeType<CokeOvenJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth()  { return 160; }
    @Override public int getHeight() { return 56; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CokeOvenJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 19)
                .addItemStack(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 19)
                .addItemStack(recipe.getOutputItem());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 137, 5)
                .setFluidRenderer(recipe.getOutputFluid().getAmount(), false, 16, 46)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getOutputFluid());
    }
}
