package com.sio.firearms.screen;

import com.sio.firearms.block.ItemPipeBlockEntity.SideMode;
import com.sio.firearms.menu.ItemPipeUnifiedMenu;
import com.sio.firearms.network.SetItemPipeModePayload;
import com.sio.firearms.network.SwitchItemPipeFacePayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Unified item-pipe config screen — all 6 faces accessible from one screen.
 *
 * Face selector layout (same cube-net as FluidPipeUnifiedScreen):
 *          [ UP  ]
 *  [W]  [NORTH]  [E]  [SOUTH]
 *         [DOWN]
 *
 * Clicking a face button sends SwitchItemPipeFacePayload to the server,
 * which saves the current 9-slot filter to the old face and loads the
 * new face's filter. The mode cycle button always targets the active face.
 * Small colored dots on face buttons show each face's current mode at a glance.
 */
public class ItemPipeUnifiedScreen extends AbstractContainerScreen<ItemPipeUnifiedMenu> {

    /**
     * Face button positions [x, y] indexed by Direction.ordinal()
     * (DOWN=0, UP=1, NORTH=2, SOUTH=3, WEST=4, EAST=5).
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

    private final Button[] faceButtons = new Button[6];
    private Button modeButton;

    public ItemPipeUnifiedScreen(ItemPipeUnifiedMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth  = 256;
        this.imageHeight = 204;
    }

    @Override
    protected void init() {
        super.init();

        Direction[] dirs = Direction.values();
        for (int i = 0; i < 6; i++) {
            final int ord = i;
            faceButtons[i] = addRenderableWidget(Button.builder(
                    Component.literal(capitalize(dirs[i].getSerializedName())),
                    btn -> PacketDistributor.sendToServer(
                            new SwitchItemPipeFacePayload(menu.pos, ord))
            ).bounds(leftPos + FACE_NET_POS[i][0], topPos + FACE_NET_POS[i][1],
                     BTN_W, BTN_H).build());
        }

        modeButton = addRenderableWidget(Button.builder(
                buildModeLabel(),
                btn -> PacketDistributor.sendToServer(
                        new SetItemPipeModePayload(menu.pos, menu.getActiveFaceOrdinal()))
        ).bounds(leftPos + 6, topPos + 70, 62, BTN_H).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        super.render(g, mx, my, partial);
        if (modeButton != null) modeButton.setMessage(buildModeLabel());
        renderTooltip(g, mx, my);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mx, int my) {
        // Panel
        g.fill(leftPos,     topPos,     leftPos + imageWidth,     topPos + imageHeight,     0xD0101018);
        g.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xFF1A1A2E);

        // Highlight active face button with a bright border
        int activeFace = menu.getActiveFaceOrdinal();
        int[] ap = FACE_NET_POS[activeFace];
        g.fill(leftPos + ap[0] - 1, topPos + ap[1] - 1,
               leftPos + ap[0] + BTN_W + 1, topPos + ap[1] + BTN_H + 1, 0xFF8080C0);

        // Colored mode dot at top-right of each face button
        Direction[] dirs = Direction.values();
        for (int i = 0; i < 6; i++) {
            int[] p = FACE_NET_POS[i];
            SideMode mode = menu.getModeForFace(dirs[i]);
            int dotColor = switch (mode) {
                case EXTRACT -> 0xFFFF8800;
                case INSERT  -> 0xFF0088FF;
                case NONE    -> 0xFF333355;
            };
            g.fill(leftPos + p[0] + BTN_W - 5, topPos + p[1] + 2,
                   leftPos + p[0] + BTN_W - 1, topPos + p[1] + 6, dotColor);
        }

        // Separator between face net and detail section
        g.fill(leftPos + 4, topPos + 62, leftPos + imageWidth - 4, topPos + 63, 0xFF4A4A6A);

        // Filter slot grid background (3×3)
        int gx = leftPos + ItemPipeUnifiedMenu.FILTER_X - 2;
        int gy = topPos  + ItemPipeUnifiedMenu.FILTER_Y - 2;
        g.fill(gx, gy, gx + 3 * 18 + 4, gy + 3 * 18 + 4, 0xFF4A4A6A);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = leftPos + ItemPipeUnifiedMenu.FILTER_X + col * 18;
                int sy = topPos  + ItemPipeUnifiedMenu.FILTER_Y + row * 18;
                g.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF0D0D1F);
            }
        }

        // Player inventory separator
        g.fill(leftPos + 8, topPos + 118, leftPos + imageWidth - 8, topPos + 119, 0xFF4A4A6A);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {
        g.drawString(font, "Item Pipe", 6, 4, 0xFFCCCCDD, false);
        g.drawString(font, "Mode:", 6, 62, 0xFF999999, false);
        g.drawString(font, "Filter:", ItemPipeUnifiedMenu.FILTER_X, 54, 0xFF999999, false);
        g.drawString(font, "Inventory", 6, 110, 0xFF999999, false);
    }

    private Component buildModeLabel() {
        SideMode mode = menu.getModeForFace(Direction.values()[menu.getActiveFaceOrdinal()]);
        return Component.literal(mode.displayName());
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
