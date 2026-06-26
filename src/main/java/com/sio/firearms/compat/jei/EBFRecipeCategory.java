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

public class EBFRecipeCategory implements IRecipeCategory<EBFJeiRecipe> {

    public static final RecipeType<EBFJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "ebf", EBFJeiRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public EBFRecipeCategory(IGuiHelper guiHelper) {
        // input0 (56,17), input1 (56,53), output (116,35). Crop origin (38,9).
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "textures/gui/ebf.png");
        this.background = guiHelper.createDrawable(texture, 38, 9, 98, 55);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.EBF_CONTROLLER.get()));
        this.title = Component.translatable("block.firearms.ebf_controller");
    }

    @Override public RecipeType<EBFJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return 98; }
    @Override public int getHeight() { return 55; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EBFJeiRecipe recipe, IFocusGroup focuses) {
        // input0: abs (56,17) - crop (38,9) = JEI (18,8)
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 8)
                .addItemStack(recipe.getInput0());

        // input1 (fuel): abs (56,53) - crop (38,9) = JEI (18,44)
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 44)
                .addItemStack(recipe.getInput1());

        // output: abs (116,35) - crop (38,9) = JEI (78,26)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 26)
                .addItemStack(recipe.getOutput());
    }
}
