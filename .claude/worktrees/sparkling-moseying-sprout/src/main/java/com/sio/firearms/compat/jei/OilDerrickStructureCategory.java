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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class OilDerrickStructureCategory implements IRecipeCategory<OilDerrickStructureJeiRecipe> {

    public static final RecipeType<OilDerrickStructureJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "oil_derrick_structure", OilDerrickStructureJeiRecipe.class);

    private static final int W = 178;
    private static final int H = 118;

    // 2 representative layers for the 3×3×6 structure.
    // B=oil_derrick_base, P=oil_derrick_pillar, C=controller, .=air
    private static final String[][] LAYERS = {
        { "BBB", "BBB", "BCB" }, // 1 — base floor (controller front centre)
        { "...", ".P.", "..." }, // 2 — pillar (×5, layers 1–5, centre only)
    };
    private static final String[] STEP_NUMS  = { "1", "2" };
    private static final String[] LAYER_NAME = { "Base", "×5 Pillar" };

    private static final int CELL     = 12;
    private static final int GRID_W   = CELL * 3; // 36
    private static final int GRID_TOP = 22;
    private static final int PANEL_X0 = 4;
    private static final int PANEL_GAP = 90;

    private static final int BOM_Y   = 63;
    private static final int[] BOM_X = { 18, 80, 142 };

    private static final int C_BASE    = 0xFF3D2B1F;
    private static final int C_PILLAR  = 0xFF7A5030;
    private static final int C_CTRL    = 0xFF46C24E;
    private static final int C_HOLLOW  = 0xFF101216;
    private static final int C_BORDER  = 0xFF05060A;
    private static final int C_PANEL   = 0xFF1B1D22;
    private static final int C_PANEL_HI = 0xFF34373E;

    private final IDrawable icon;
    private final Component title;

    public OilDerrickStructureCategory(IGuiHelper guiHelper) {
        this.icon  = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.OIL_DERRICK_CONTROLLER.get()));
        this.title = Component.translatable("jei.firearms.oil_derrick_structure");
    }

    @Override public RecipeType<OilDerrickStructureJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon()  { return icon; }
    @Override public int getWidth()  { return W; }
    @Override public int getHeight() { return H; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, OilDerrickStructureJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[0], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.OIL_DERRICK_CONTROLLER.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[1], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.OIL_DERRICK_BASE.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[2], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.OIL_DERRICK_PILLAR.get()));
    }

    @Override
    public void draw(OilDerrickStructureJeiRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphics g, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        for (int li = 0; li < LAYERS.length; li++) {
            int px = PANEL_X0 + li * PANEL_GAP;

            centered(g, font, STEP_NUMS[li],  px, 1,  0xFFFFE08A);
            centered(g, font, LAYER_NAME[li], px, 11, 0xFFB9BDC4);

            g.fill(px - 1, GRID_TOP - 1, px + GRID_W + 1, GRID_TOP + GRID_W + 1, C_PANEL);
            g.fill(px - 1, GRID_TOP - 1, px + GRID_W + 1, GRID_TOP,               C_PANEL_HI);
            g.fill(px - 1, GRID_TOP - 1, px,               GRID_TOP + GRID_W + 1, C_PANEL_HI);

            String[] layer = LAYERS[li];
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int x = px + col * CELL;
                    int y = GRID_TOP + row * CELL;
                    int color = colorOf(layer[row].charAt(col));
                    g.fill(x, y, x + CELL,     y + CELL,     C_BORDER);
                    g.fill(x, y, x + CELL - 1, y + CELL - 1, color);
                    g.fill(x, y, x + CELL - 1, y + 1,        shade(color, 28));
                }
            }

            if (li < LAYERS.length - 1) {
                g.drawString(font, ">", px + GRID_W + 1, GRID_TOP + 14, 0xFF6E7278, false);
            }
        }

        bom(g, font, 0, C_CTRL,   "x" + OilDerrickStructureJeiRecipe.CONTROLLER_COUNT);
        bom(g, font, 1, C_BASE,   "x" + OilDerrickStructureJeiRecipe.BASE_COUNT);
        bom(g, font, 2, C_PILLAR, "x" + OilDerrickStructureJeiRecipe.PILLAR_COUNT);

        int ny = BOM_Y + 32;
        g.drawString(font, "Build the 3×3 base with the controller on any border position.", 2, ny,      0xFF9AA0A8, false);
        g.drawString(font, "One pillar block occupies the centre of each of the 5 layers above.", 2, ny + 10, 0xFF9AA0A8, false);
        g.drawString(font, "Crude oil is extracted from underground deposits below the structure.", 2, ny + 20, 0xFF9AA0A8, false);
    }

    private static void bom(GuiGraphics g, Font font, int i, int color, String label) {
        int sx = BOM_X[i];
        g.fill(sx, BOM_Y + 18, sx + 16, BOM_Y + 20, color);
        int tx = sx + 8 - font.width(label) / 2;
        g.drawString(font, label, tx, BOM_Y + 23, 0xFFFFFFFF, false);
    }

    private static void centered(GuiGraphics g, Font font, String s, int panelX, int y, int color) {
        int tx = panelX + GRID_W / 2 - font.width(s) / 2;
        g.drawString(font, s, tx, y, color, false);
    }

    private static int colorOf(char c) {
        return switch (c) {
            case 'B' -> C_BASE;
            case 'P' -> C_PILLAR;
            case 'C' -> C_CTRL;
            default  -> C_HOLLOW;
        };
    }

    private static int shade(int argb, int d) {
        int a  = (argb >>> 24) & 0xFF;
        int r  = Math.min(255, ((argb >> 16) & 0xFF) + d);
        int gg = Math.min(255, ((argb >>  8) & 0xFF) + d);
        int b  = Math.min(255,  (argb        & 0xFF) + d);
        return (a << 24) | (r << 16) | (gg << 8) | b;
    }
}
