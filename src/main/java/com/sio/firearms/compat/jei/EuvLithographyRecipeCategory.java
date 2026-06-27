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

public class EuvLithographyRecipeCategory implements IRecipeCategory<EuvLithographyJeiRecipe> {

    public static final RecipeType<EuvLithographyJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "euv_lithography", EuvLithographyJeiRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public EuvLithographyRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                Firearms.MOD_ID, "textures/gui/euv_lithography_controller.png");
        this.background = guiHelper.createDrawable(texture, 8, 8, 160, 56);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.EUV_LITHOGRAPHY_CONTROLLER.get()));
        this.title = Component.translatable("block.firearms.euv_lithography_controller");
    }

    @Override public RecipeType<EuvLithographyJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon()  { return icon; }
    @Override public int getWidth()  { return 160; }
    @Override public int getHeight() { return 56; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EuvLithographyJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 7, 5)
                .setFluidRenderer(recipe.getPhotoresist().getAmount(), false, 16, 46)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getPhotoresist());

        builder.addSlot(RecipeIngredientRole.INPUT, 48, 27)
                .addItemStack(recipe.getCoatedWafer());

        builder.addSlot(RecipeIngredientRole.INPUT, 48, 44)
                .addItemStack(recipe.getPhotomask());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 27)
                .addItemStack(recipe.getPattermedWafer());
    }
}
