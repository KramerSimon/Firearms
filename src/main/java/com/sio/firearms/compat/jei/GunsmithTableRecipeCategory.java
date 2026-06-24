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

public class GunsmithTableRecipeCategory implements IRecipeCategory<GunsmithTableJeiRecipe> {

    public static final RecipeType<GunsmithTableJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "gunsmith_table", GunsmithTableJeiRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public GunsmithTableRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "textures/gui/gunsmith_table.png");
        this.background = guiHelper.createDrawable(texture, 20, 10, 140, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.GUNSMITH_TABLE.get()));
        this.title = Component.translatable("block.firearms.gunsmith_table");
    }

    @Override
    public RecipeType<GunsmithTableJeiRecipe> getRecipeType() {
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
        return 140;
    }

    @Override
    public int getHeight() {
        return 60;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GunsmithTableJeiRecipe recipe, IFocusGroup focuses) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                ItemStack input = recipe.getInputs().get(index);
                builder.addSlot(RecipeIngredientRole.INPUT, 10 + col * 18, 3 + row * 18)
                        .addItemStack(input);
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 21)
                .addItemStack(recipe.getOutput());
    }
}
