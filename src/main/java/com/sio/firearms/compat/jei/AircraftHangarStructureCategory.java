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

import java.util.List;

public class AircraftHangarStructureCategory implements IRecipeCategory<AircraftHangarStructureJeiRecipe> {

    public static final RecipeType<AircraftHangarStructureJeiRecipe> RECIPE_TYPE =
            RecipeType.create(Firearms.MOD_ID, "aircraft_hangar_structure", AircraftHangarStructureJeiRecipe.class);

    private static final int W = 178;
    private static final int H = 118;

    // 3 representative layers for the 11×11×6 structure.
    // F=hangar_floor, W=hangar_wall, D=hangar_door, R=hangar_roof, C=controller, .=hollow
    private static final String[][] LAYERS = {
        { "FFFFFFFFFFF", "FFFFFFFFFFF", "FFFFFFFFFFF", "FFFFFFFFFFF", "FFFFFFFFFFF",
          "FFFFFFFFFFF", "FFFFFFFFFFF", "FFFFFFFFFFF", "FFFFFFFFFFF", "FFFFFFFFFFF",
          "FFFFFCFFFFF" }, // 1 — floor (controller front centre)
        { "WWWWWWWWWWW", "W.........W", "W.........W", "W.........W", "W.........W",
          "W.........W", "W.........W", "W.........W", "W.........W", "W.........W",
          "WWWDDDDDWWW" }, // 2 — ×4 wall ring (D=doors on front wall)
        { "RRRRRRRRRRR", "RRRRRRRRRRR", "RRRRRRRRRRR", "RRRRRRRRRRR", "RRRRRRRRRRR",
          "RRRRRRRRRRR", "RRRRRRRRRRR", "RRRRRRRRRRR", "RRRRRRRRRRR", "RRRRRRRRRRR",
          "RRRRRRRRRRR" }, // 3 — roof cap
    };
    private static final String[] STEP_NUMS  = { "1", "2", "3" };
    private static final String[] LAYER_NAME = { "Floor", "×4 Walls", "Roof" };

    private static final int CELL     = 3;
    private static final int GRID_W   = CELL * 11; // 33
    private static final int GRID_TOP = 22;
    private static final int PANEL_X0 = 4;
    private static final int PANEL_GAP = 57;

    private static final int BOM_Y   = 60;
    private static final int[] BOM_X = { 6, 50, 94, 138 };

    private static final int C_FLOOR   = 0xFF707070;
    private static final int C_WALL    = 0xFF4A4A4A;
    private static final int C_DOOR    = 0xFF3A6A90;
    private static final int C_ROOF    = 0xFF888888;
    private static final int C_CTRL    = 0xFF46C24E;
    private static final int C_HOLLOW  = 0xFF101216;
    private static final int C_BORDER  = 0xFF05060A;
    private static final int C_PANEL   = 0xFF1B1D22;
    private static final int C_PANEL_HI = 0xFF34373E;

    private final IDrawable icon;
    private final Component title;

    public AircraftHangarStructureCategory(IGuiHelper guiHelper) {
        this.icon  = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.HANGAR_CONTROLLER.get()));
        this.title = Component.translatable("jei.firearms.aircraft_hangar_structure");
    }

    @Override public RecipeType<AircraftHangarStructureJeiRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon()  { return icon; }
    @Override public int getWidth()  { return W; }
    @Override public int getHeight() { return H; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AircraftHangarStructureJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[0], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.HANGAR_CONTROLLER.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[1], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.HANGAR_FLOOR.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[2], BOM_Y)
                .addItemStacks(List.of(
                        new ItemStack(ModBlocks.HANGAR_WALL.get()),
                        new ItemStack(ModBlocks.HANGAR_DOOR.get())));
        builder.addSlot(RecipeIngredientRole.CATALYST, BOM_X[3], BOM_Y)
                .addItemStack(new ItemStack(ModBlocks.HANGAR_ROOF.get()));
    }

    @Override
    public void draw(AircraftHangarStructureJeiRecipe recipe, IRecipeSlotsView slots,
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
            for (int row = 0; row < 11; row++) {
                for (int col = 0; col < 11; col++) {
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

        bom(g, font, 0, C_CTRL,  "x" + AircraftHangarStructureJeiRecipe.CONTROLLER_COUNT);
        bom(g, font, 1, C_FLOOR, "x" + AircraftHangarStructureJeiRecipe.FLOOR_COUNT);
        bom(g, font, 2, C_WALL,  "x" + AircraftHangarStructureJeiRecipe.WALL_COUNT);
        bom(g, font, 3, C_ROOF,  "x" + AircraftHangarStructureJeiRecipe.ROOF_COUNT);

        int ny = BOM_Y + 32;
        g.drawString(font, "Build bottom→top. 11×11×6. Controller on any border of the floor layer.", 2, ny,      0xFF9AA0A8, false);
        g.drawString(font, "Layers 1–4 are hollow wall rings. Hangar Door blocks may replace any border wall.", 2, ny + 10, 0xFF9AA0A8, false);
        g.drawString(font, "An aircraft spawns at interior centre on activation. Energy/Fluid Ports OK.", 2, ny + 20, 0xFF9AA0A8, false);
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
            case 'D' -> C_DOOR;
            case 'R' -> C_ROOF;
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
