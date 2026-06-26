package com.sio.firearms.screen;

import com.sio.firearms.menu.CokeOvenMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CokeOvenScreen extends AbstractContainerScreen<CokeOvenMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/coke_oven.png");

    public CokeOvenScreen(CokeOvenMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && menu.getProgress() > 0) {
            int arrowWidth = menu.getProgress() * 24 / maxProgress;
            gui.blit(TEXTURE, leftPos + 88, topPos + 34, 176, 0, arrowWidth, 16);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int maxFluid = menu.getMaxFluid();
        if (maxFluid > 0) {
            int barH = menu.getFluidAmount() * 52 / maxFluid;
            gui.fill(leftPos + 150, topPos + 66 - barH, leftPos + 162, topPos + 66, 0xFF4A2800);
        }

        renderTooltip(gui, mouseX, mouseY);
    }
}
