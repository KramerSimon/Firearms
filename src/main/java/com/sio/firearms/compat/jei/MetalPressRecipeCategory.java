package com.sio.firearms.compat.jei;

import com.sio.firearms.Firearms;
import com.sio.firearms.registry.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MetalPressRecipeCategory implements IRecipeCategory<MetalPressJeiRecipe> {

    public static final RecipeType<MetalPressJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "metal_press", MetalPressJeiRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public MetalPressRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "textures/gui/metal_press.png");
        this.background = guiHelper.createDrawable(texture, 30, 14, 116, 50);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.METAL_PRESS.get()));
        this.title = Component.translatable("block.firearms.metal_press");
    }

    @Override
    public RecipeType<MetalPressJeiRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 116;
    }

    @Override
    public int getHeight() {
        return 50;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MetalPressJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 14, 8)
                .addItemStack(recipe.getInput0());

        builder.addSlot(RecipeIngredientRole.INPUT, 14, 32)
                .addItemStack(recipe.getInput1());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 21)
                .addItemStack(recipe.getOutput());
    }
}
