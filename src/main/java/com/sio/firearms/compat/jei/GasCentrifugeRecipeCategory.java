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

public class GasCentrifugeRecipeCategory implements IRecipeCategory<GasCentrifugeJeiRecipe> {

    public static final RecipeType<GasCentrifugeJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "gas_centrifuge", GasCentrifugeJeiRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public GasCentrifugeRecipeCategory(IGuiHelper guiHelper) {
        // Reuse electrolysis_machine texture as placeholder; crop a 160x60 region
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "textures/gui/electrolysis_machine.png");
        this.background = guiHelper.createDrawable(texture, 0, 0, 160, 70);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.GAS_CENTRIFUGE.get()));
        this.title = Component.translatable("block.firearms.gas_centrifuge");
    }

    @Override public RecipeType<GasCentrifugeJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return 160; }
    @Override public int getHeight() { return 70; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GasCentrifugeJeiRecipe recipe, IFocusGroup focuses) {
        // UF6 input tank at x=10, y=6
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 6)
                .setFluidRenderer(recipe.getInputFluid().getAmount(), false, 16, 52)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getInputFluid());

        // Enriched UF6 output at x=100, y=6
        builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 6)
                .setFluidRenderer(recipe.getOutputFluid1().getAmount(), false, 16, 52)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getOutputFluid1());

        // Depleted UF6 output at x=130, y=6
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6)
                .setFluidRenderer(recipe.getOutputFluid2().getAmount(), false, 16, 52)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getOutputFluid2());
    }
}
