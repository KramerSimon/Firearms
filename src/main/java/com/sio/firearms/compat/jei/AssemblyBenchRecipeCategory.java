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
        // Crop the GUI texture to show the 3x3 input grid, arrow, and output slot.
        // Grid starts at (29,17), output at (116,35); background starts at (20,8) to capture everything.
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "textures/gui/assembly_bench.png");
        this.background = guiHelper.createDrawable(texture, 20, 8, 120, 68);
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
        return 120;
    }

    @Override
    public int getHeight() {
        return 68;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AssemblyBenchJeiRecipe recipe, IFocusGroup focuses) {
        // Input slots: 3x3 grid. Texture grid starts at (29,17); background origin is (20,8).
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                ItemStack input = recipe.getInputs().get(index);
                builder.addSlot(RecipeIngredientRole.INPUT, 9 + col * 18, 9 + row * 18)
                        .addItemStack(input);
            }
        }

        // Output slot: texture at (116,35); JEI relative = (96, 27)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 27)
                .addItemStack(recipe.getOutput());
    }
}
