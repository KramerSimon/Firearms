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

public class AssemblyBenchRecipeCategory implements IRecipeCategory<AssemblyBenchJeiRecipe> {

    public static final RecipeType<AssemblyBenchJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "assembly_bench", AssemblyBenchJeiRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public AssemblyBenchRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "textures/gui/assembly_bench.png");
        this.background = guiHelper.createDrawable(texture, 20, 10, 140, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ASSEMBLY_BENCH.get()));
        this.title = Component.translatable("block.firearms.assembly_bench");
    }

    @Override
    public RecipeType<AssemblyBenchJeiRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, AssemblyBenchJeiRecipe recipe, IFocusGroup focuses) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 2; col++) {
                int index = row * 2 + col;
                ItemStack input = recipe.getInputs().get(index);
                builder.addSlot(RecipeIngredientRole.INPUT, 10 + col * 18, 3 + row * 18)
                        .addItemStack(input);
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 21)
                .addItemStack(recipe.getOutput());
    }
}
