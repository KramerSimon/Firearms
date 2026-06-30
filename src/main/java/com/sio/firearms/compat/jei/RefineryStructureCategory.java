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

public class RefineryStructureCategory implements IRecipeCategory<RefineryStructureJeiRecipe> {

    public static final RecipeType<RefineryStructureJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "refinery_structure", RefineryStructureJeiRecipe.class);

    private static final int W = 178;
    private static final int H = 118;

    // 3 representative layers for the 5×5×6 structure.
    // F=refinery_base, W=refinery_wall, T=refinery_top, C=controller, .=hollow
    private static final String[][] LAYERS = {
        { "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFCFF" }, // 1 — floor (controller front centre)
        { "WWWWW", "W...W", "W...W", "W...W", "WWWWW" }, // 2 — wall ring (×4, layers 1–4)
        { "TTTTT", "TTTTT", "TTTTT", "TTTTT", "TTTTT" }, // 3 — top cap (layer 5)
    };
    private static final String[] STEP_NUMS  = { "1", "2", "3" };
    private static final String[] LAYER_NAME = { "Floor", "×4 Walls", "Top" };

    private static final int CELL     = 7;
    private static final int GRID_W   = CELL * 5; // 35
    private static final int GRID_TOP = 22;
    private static final int PANEL_X0 = 4;
    private static final int PANEL_GAP = 60;

    private static final int BOM_Y   = 62;
    private static final int[] BOM_X = { 6, 50, 94, 138 };

    private static final int C_FLOOR   = 0xFF5E3A1E;
    private static final int C_WALL    = 0xFF607080;
    private static final int C_TOP     = 0xFF8A7855;
    private static final int C_CTRL    = 0xFF46C24E;
    private static final int C_HOLLOW  = 0xFF101216;
    private static final int C_BORDER  = 0xFF05060A;
    private static final int C_PANEL   = 0xFF1B1D22;
    private static final int C_PANEL_HI = 0xFF34373E;

    private final IDrawable icon;
    private final Component title;

    public RefineryStructureCategory(IGuiHelper guiHelper) {
        this.icon  = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.REFINERY_CONTROLLER.get()));
        this.title = Component.translatable("jei.firearms.refinery_structure");
    }

    @Override public RecipeType<RefineryStructureJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon()  { return icon; }
    @Override public int getWidth()  { return W; }
    @Override public int getHeight() { return H; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RefineryStructureJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[0], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.REFINERY_CONTROLLER.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[1], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.REFINERY_BASE.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[2], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.REFINERY_WALL.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[3], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.REFINERY_TOP.get()));
    }

    @Override
    public void draw(RefineryStructureJeiRecipe recipe, IRecipeSlotsView slots,
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
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
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

        bom(g, font, 0, C_CTRL,  "x" + RefineryStructureJeiRecipe.CONTROLLER_COUNT);
        bom(g, font, 1, C_FLOOR, "x" + RefineryStructureJeiRecipe.BASE_COUNT);
        bom(g, font, 2, C_WALL,  "x" + RefineryStructureJeiRecipe.WALL_COUNT);
        bom(g, font, 3, C_TOP,   "x" + RefineryStructureJeiRecipe.TOP_COUNT);

        int ny = BOM_Y + 32;
        g.drawString(font, "Build bottom→top. 5×5×6. Controller on any border of the floor.", 2, ny,      0xFF9AA0A8, false);
        g.drawString(font, "Layers 1–4 are hollow wall rings; layer 5 is a solid top cap.", 2, ny + 10, 0xFF9AA0A8, false);
        g.drawString(font, "Any structural block may be replaced by an Energy Port or Fluid Port.", 2, ny + 20, 0xFF9AA0A8, false);
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
            case 'F' -> C_FLOOR;
            case 'W' -> C_WALL;
            case 'T' -> C_TOP;
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
