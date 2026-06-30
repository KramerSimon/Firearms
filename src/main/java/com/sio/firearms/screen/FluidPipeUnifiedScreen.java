package com.sio.firearms.screen;

import com.sio.firearms.menu.FluidPipeUnifiedMenu;
import com.sio.firearms.network.SetFluidPipeFilterPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

/**
 * Unified fluid-pipe config screen — shows all 6 faces at once.
 *
 * Face selector layout (unfolded cube net):
 *          [ UP  ]
 *  [W]  [NORTH]  [E]  [SOUTH]
 *         [DOWN]
 *
 * Clicking a face button makes it the "active" face whose filter is shown
 * in the detail section below. JEI drag-drop and the Clear button both
 * target the active face. Changes propagate immediately via SetFluidPipeFilterPayload.
 */
public class FluidPipeUnifiedScreen extends AbstractContainerScreen<FluidPipeUnifiedMenu> {

    /** Ghost drag-drop zone position (relative to leftPos/topPos) — used by FluidPipeUnifiedGhostHandler. */
    public static final int GHOST_X    = 6;
    public static final int GHOST_Y    = 76;
    public static final int GHOST_SIZE = 32;

    /**
     * Face button positions [x, y] indexed by Direction.ordinal()
     * (DOWN=0, UP=1, NORTH=2, SOUTH=3, WEST=4, EAST=5).
     * Laid out as a cross/cube-net: UP above NORTH, W-N-E-S in a row, DOWN below NORTH.
     */
    private static final int[][] FACE_NET_POS = {
        {90, 44},  // 0: DOWN
        {90, 12},  // 1: UP
        {90, 28},  // 2: NORTH
        {174, 28}, // 3: SOUTH
        {48, 28},  // 4: WEST
        {132, 28}, // 5: EAST
    };

    private static final int BTN_W = 40;
    private static final int BTN_H = 14;

    private int activeFace = Direction.NORTH.ordinal();
    private final Button[] faceButtons = new Button[6];
    private Button clearButton;

    public FluidPipeUnifiedScreen(FluidPipeUnifiedMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth  = 256;
        this.imageHeight = 120;
    }

    /** Returns the ordinal of the currently-selected face (read by FluidPipeUnifiedGhostHandler). */
    public int getActiveFace() { return activeFace; }

    @Override
    protected void init() {
        super.init();

        Direction[] dirs = Direction.values();
        for (int i = 0; i < 6; i++) {
            final int ord = i;
            faceButtons[i] = addRenderableWidget(Button.builder(
                    Component.literal(capitalize(dirs[i].getSerializedName())),
                    btn -> activeFace = ord
            ).bounds(leftPos + FACE_NET_POS[i][0], topPos + FACE_NET_POS[i][1],
                     BTN_W, BTN_H).build());
        }

        clearButton = addRenderableWidget(Button.builder(
                Component.literal("Clear"),
                btn -> {
                    PacketDistributor.sendToServer(
                            new SetFluidPipeFilterPayload(menu.pos, activeFace, Optional.empty()));
                    menu.faceFilters[activeFace] = null;
                }
        ).bounds(leftPos + 120, topPos + 74, 64, BTN_H).build());
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mx, int my) {
        // Panel
        g.fill(leftPos,     topPos,     leftPos + imageWidth,     topPos + imageHeight,     0xD0101018);
        g.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xFF1A1A2E);

        // Highlight active face button with a bright border
        int[] ap = FACE_NET_POS[activeFace];
        g.fill(leftPos + ap[0] - 1, topPos + ap[1] - 1,
               leftPos + ap[0] + BTN_W + 1, topPos + ap[1] + BTN_H + 1, 0xFF8080C0);

        // Small colored dot at top-right of each face button: orange = filter set, dim = none
        for (int i = 0; i < 6; i++) {
            int[] p = FACE_NET_POS[i];
            int dotColor = menu.faceFilters[i] != null ? 0xFFFF8800 : 0xFF333355;
            g.fill(leftPos + p[0] + BTN_W - 5, topPos + p[1] + 2,
                   leftPos + p[0] + BTN_W - 1, topPos + p[1] + 6, dotColor);
        }

        // Separator between face net and detail section
        g.fill(leftPos + 4, topPos + 64, leftPos + imageWidth - 4, topPos + 65, 0xFF4A4A6A);

        // JEI drop zone
        int zx = leftPos + GHOST_X;
        int zy = topPos  + GHOST_Y;
        g.fill(zx - 2, zy - 2, zx + GHOST_SIZE + 2, zy + GHOST_SIZE + 2, 0xFF4A4A6A);
        g.fill(zx,     zy,     zx + GHOST_SIZE,     zy + GHOST_SIZE,     0xFF0D0D1F);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {
        g.drawString(font, "Fluid Pipe", 6, 4, 0xFFCCCCDD, false);

        String faceName = capitalize(Direction.values()[activeFace].getSerializedName());
        g.drawString(font, "Filter — " + faceName + ":", 6, 67, 0xFF999999, false);

        ResourceLocation fl = menu.faceFilters[activeFace];
        String label = fl != null ? fl.getPath() : "None (pass all)";
        g.drawString(font, label, GHOST_X + GHOST_SIZE + 4, GHOST_Y + 12, 0xFFEEEEFF, false);
        g.drawString(font, "Drag from JEI", GHOST_X + 1, GHOST_Y + GHOST_SIZE + 4, 0xFF555577, false);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        super.render(g, mx, my, partial);
        renderTooltip(g, mx, my);
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
