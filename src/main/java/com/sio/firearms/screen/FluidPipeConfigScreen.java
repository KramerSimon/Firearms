package com.sio.firearms.screen;

import com.sio.firearms.menu.FluidPipeConfigMenu;
import com.sio.firearms.network.SetFluidPipeFilterPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public class FluidPipeConfigScreen extends AbstractContainerScreen<FluidPipeConfigMenu> {

    /** Ghost ingredient drop zone, relative to leftPos/topPos — read by JEI ghost handler. */
    public static final int GHOST_X    = 70;
    public static final int GHOST_Y    = 32;
    public static final int GHOST_SIZE = 36;

    public FluidPipeConfigScreen(FluidPipeConfigMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth  = 176;
        this.imageHeight = 100;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(Button.builder(
                Component.literal("Clear Filter"),
                btn -> {
                    PacketDistributor.sendToServer(
                            new SetFluidPipeFilterPayload(menu.pos, menu.face.ordinal(), Optional.empty()));
                    menu.filterFluid = null;
                }
        ).bounds(leftPos + 8, topPos + 76, 100, 18).build());
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mx, int my) {
        g.fill(leftPos,     topPos,     leftPos + imageWidth,     topPos + imageHeight,     0xD0101018);
        g.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xFF1A1A2E);

        // Drop zone
        int zx = leftPos + GHOST_X;
        int zy = topPos  + GHOST_Y;
        g.fill(zx - 2, zy - 2, zx + GHOST_SIZE + 2, zy + GHOST_SIZE + 2, 0xFF4A4A6A);
        g.fill(zx,     zy,     zx + GHOST_SIZE,     zy + GHOST_SIZE,     0xFF0D0D1F);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {
        String title = "Fluid Pipe — " + capitalize(menu.face.getSerializedName());
        g.drawString(font, title, 8, 8, 0xFFCCCCDD, false);
        g.drawString(font, "Filter Fluid (drag from JEI):", 8, 26, 0xFF999999, false);

        String label = menu.filterFluid != null ? menu.filterFluid.getPath() : "None (pass all)";
        g.drawString(font, label, GHOST_X + GHOST_SIZE + 6, GHOST_Y + 14, 0xFFEEEEFF, false);
        g.drawString(font, "Drop here", GHOST_X + 4, GHOST_Y + GHOST_SIZE + 4, 0xFF666677, false);
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
