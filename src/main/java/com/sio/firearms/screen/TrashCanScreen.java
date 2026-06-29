package com.sio.firearms.screen;

import com.sio.firearms.menu.TrashCanMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TrashCanScreen extends AbstractContainerScreen<TrashCanMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/trash_can.png");

    private static final int FLUID_X = 8, FLUID_Y = 14, FLUID_W = 12, FLUID_H = 52;

    public TrashCanScreen(TrashCanMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mx, int my) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Fluid bar (bottom-up, translucent red — being destroyed)
        int fluidH = menu.getFluidMax() > 0
                ? menu.getFluidAmount() * FLUID_H / menu.getFluidMax() : 0;
        if (fluidH > 0) {
            gui.fill(leftPos + FLUID_X,
                     topPos  + FLUID_Y + FLUID_H - fluidH,
                     leftPos + FLUID_X + FLUID_W,
                     topPos  + FLUID_Y + FLUID_H,
                     0xAACC2222);
        }

        // Red X overlay on every trash slot
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = leftPos + 44 + col * 18 + 1;
                int sy = topPos  + 18 + row * 18 + 1;
                gui.fill(sx,      sy,      sx + 16, sy + 2,  0x88CC2222);
                gui.fill(sx,      sy + 14, sx + 16, sy + 16, 0x88CC2222);
                gui.fill(sx,      sy,      sx + 2,  sy + 16, 0x88CC2222);
                gui.fill(sx + 14, sy,      sx + 16, sy + 16, 0x88CC2222);
            }
        }
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
        renderTooltip(gui, mx, my);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        if (mx >= leftPos + FLUID_X && mx < leftPos + FLUID_X + FLUID_W
                && my >= topPos + FLUID_Y && my < topPos + FLUID_Y + FLUID_H) {
            gui.renderTooltip(font,
                    Component.literal("Fluid: " + menu.getFluidAmount() + " / " + menu.getFluidMax() + " mB (trashing)"),
                    mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
