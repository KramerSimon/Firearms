package com.sio.firearms.screen;

import com.sio.firearms.menu.EBFMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EBFScreen extends AbstractContainerScreen<EBFMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/ebf.png");

    public EBFScreen(EBFMenu menu, Inventory playerInventory, Component title) {
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
            gui.blit(TEXTURE, leftPos + 78, topPos + 30, 176, 0, arrowWidth, 16);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int barH = menu.getEnergyStored() * 52 / maxEnergy;
            gui.fill(leftPos + 7, topPos + 66 - barH, leftPos + 19, topPos + 66, 0xFFCC0000);
        }

        String structStr = menu.isStructureValid() ? "Structure: OK" : "Structure: Invalid";
        int structColor = menu.isStructureValid() ? 0x00AA00 : 0xCC3300;
        gui.drawString(font, structStr, leftPos + 7, topPos + 75, structColor, false);

        renderTooltip(gui, mouseX, mouseY);
    }
}
