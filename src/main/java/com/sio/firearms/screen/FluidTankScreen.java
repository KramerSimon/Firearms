package com.sio.firearms.screen;

import com.sio.firearms.menu.FluidTankMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FluidTankScreen extends AbstractContainerScreen<FluidTankMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/fluid_tank.png");

    // Fluid bar bounds (screen-relative, within the 176x166 GUI)
    private static final int BAR_X = 80;
    private static final int BAR_Y = 14;
    private static final int BAR_W = 16;
    private static final int BAR_H = 52;

    public FluidTankScreen(FluidTankMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int cap = menu.getCapacity();
        int amount = menu.getFluidAmount();
        if (cap > 0 && amount > 0) {
            int h = amount * BAR_H / cap;
            gui.fill(leftPos + BAR_X, topPos + BAR_Y + BAR_H - h,
                     leftPos + BAR_X + BAR_W, topPos + BAR_Y + BAR_H, 0xFF4488CC);
        }

        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        int ax = leftPos + BAR_X;
        int ay = topPos + BAR_Y;
        if (mx >= ax && mx < ax + BAR_W && my >= ay && my < ay + BAR_H) {
            gui.renderTooltip(font,
                    Component.literal(menu.getFluidAmount() + " / " + menu.getCapacity() + " mB"),
                    mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
