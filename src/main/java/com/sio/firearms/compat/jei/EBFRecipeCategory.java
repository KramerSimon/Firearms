package com.sio.firearms.compat.jei;

import com.sio.firearms.Firearms;
import com.sio.firearms.registry.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class EBFRecipeCategory implements IRecipeCategory<EBFJeiRecipe> {

    public static final RecipeType<EBFJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "ebf", EBFJeiRecipe.class);

    // Crop the EBF GUI texture:
    //   origin (38, 9) captures the input slot (56,17), the arrow, and output (116,30).
    //   Power-only recipes have a single input, so the second slot row is cropped out.
    private static final int CROP_X = 38;
    private static final int CROP_Y = 9;
    private static final int BG_W   = 100;
    private static final int BG_H   = 40;

    // JEI slot positions = absolute GUI position - crop origin
    // input:  (56-38, 17-9) = (18, 8)
    // output: (116-38, 30-9) = (78, 21)
    private static final int S0_X = 18, S0_Y = 8;
    private static final int S1_X = 18, S1_Y = 30;  // additive input below primary
    private static final int OUT_X = 78, OUT_Y = 16; // output centered between S0 and S1

    // Temperature text is drawn below all slots
    private static final int TEMP_Y = 53;

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public EBFRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "textures/gui/ebf.png");
        this.background = guiHelper.createDrawable(texture, CROP_X, CROP_Y, BG_W, BG_H);
        this.icon  = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.EBF_CONTROLLER.get()));
        this.title = Component.translatable("block.firearms.ebf_controller");
    }

    @Override public RecipeType<EBFJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth()  { return BG_W; }
    @Override public int getHeight() { return TEMP_Y + 12; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EBFJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, S0_X, S0_Y)
                .addItemStack(recipe.getInput());

        if (recipe.hasAdditive()) {
            builder.addSlot(RecipeIngredientRole.INPUT, S1_X, S1_Y)
                    .addItemStack(recipe.getAdditiveInput());
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUT_X, OUT_Y)
                .addItemStack(recipe.getOutput());
    }

    @Override
    public void draw(EBFJeiRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics graphics, double mouseX, double mouseY) {
        String text = "Requires: " + recipe.getRequiredTemp() + "°C";
        int color = recipe.getRequiredTemp() >= 5000 ? 0x8800FF
                  : recipe.getRequiredTemp() >= 2000 ? 0xCC0000
                  : recipe.getRequiredTemp() >= 1200 ? 0xFF6600
                  : 0xFF9900;
        graphics.drawString(Minecraft.getInstance().font, text, 0, TEMP_Y, color, false);
    }
}
