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

public class PlasmaEtcherRecipeCategory implements IRecipeCategory<PlasmaEtcherJeiRecipe> {

    public static final RecipeType<PlasmaEtcherJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "plasma_etcher", PlasmaEtcherJeiRecipe.class);

    // GUI slots: item input (56,35), item output (116,35), chlorine gas fluid tank on left
    // Crop (8,8) size (160,56) — leaves room for a fluid column on the left.
    // JEI item input:  (56-8, 35-8) = (48, 27)
    // JEI item output: (116-8, 35-8) = (108, 27)
    // JEI fluid:       (7, 5) — 16×46 tall column
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public PlasmaEtcherRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                Firearms.MOD_ID, "textures/gui/plasma_etcher.png");
        this.background = guiHelper.createDrawable(texture, 8, 8, 160, 56);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.PLASMA_ETCHER.get()));
        this.title = Component.translatable("block.firearms.plasma_etcher");
    }

    @Override public RecipeType<PlasmaEtcherJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon()  { return icon; }
    @Override public int getWidth()       { return 160; }
    @Override public int getHeight()      { return 56; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PlasmaEtcherJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 7, 5)
                .setFluidRenderer(PlasmaEtcherJeiRecipe.CHLORINE_INPUT.getAmount(), false, 16, 46)
                .addIngredient(NeoForgeTypes.FLUID_STACK, PlasmaEtcherJeiRecipe.CHLORINE_INPUT);

        builder.addSlot(RecipeIngredientRole.INPUT, 48, 19)
                .addItemStack(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 19)
                .addItemStack(recipe.getOutput());
    }
}
