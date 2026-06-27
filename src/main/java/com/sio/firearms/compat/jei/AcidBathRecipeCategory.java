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

public class AcidBathRecipeCategory implements IRecipeCategory<AcidBathJeiRecipe> {

    public static final RecipeType<AcidBathJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "acid_bath", AcidBathJeiRecipe.class);

    // GUI slots: item input (56,35), item output (116,35)
    // Also an acid fluid tank in the GUI.
    // Crop (8,8) size (160,56) — leaves room for a fluid column on the left.
    // JEI item input:  (56-8, 35-8) = (48, 27)
    // JEI item output: (116-8, 35-8) = (108, 27)
    // JEI acid fluid:  (7, 5) — 16×46 tall column
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public AcidBathRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                Firearms.MOD_ID, "textures/gui/acid_bath.png");
        this.background = guiHelper.createDrawable(texture, 8, 8, 160, 56);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.ACID_BATH.get()));
        this.title = Component.translatable("block.firearms.acid_bath");
    }

    @Override public RecipeType<AcidBathJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth()  { return 160; }
    @Override public int getHeight() { return 56; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AcidBathJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 7, 5)
                .setFluidRenderer(AcidBathJeiRecipe.ACID_INPUT.getAmount(), false, 16, 46)
                .addIngredient(NeoForgeTypes.FLUID_STACK, AcidBathJeiRecipe.ACID_INPUT);

        builder.addSlot(RecipeIngredientRole.INPUT, 48, 19)
                .addItemStack(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 19)
                .addItemStack(recipe.getOutput());
    }
}
