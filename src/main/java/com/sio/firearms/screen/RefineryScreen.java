package com.sio.firearms.screen;

import com.sio.firearms.menu.RefineryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RefineryScreen extends AbstractContainerScreen<RefineryMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/refinery.png");

    public RefineryScreen(RefineryMenu menu, Inventory playerInventory, Component title) {
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
            gui.blit(TEXTURE, leftPos + 70, topPos + 34, 176, 0, arrowWidth, 16);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int barHeight = menu.getEnergyStored() * 52 / maxEnergy;
            gui.fill(leftPos + 7, topPos + 66 - barHeight, leftPos + 19, topPos + 66, 0xFFCC0000);
        }

        int oilHeight = menu.getOilAmount() * 52 / 10_000;
        gui.fill(leftPos + 40, topPos + 66 - oilHeight, leftPos + 52, topPos + 66, 0xFF1A1A1A);

        int fuelHeight = menu.getFuelAmount() * 52 / 10_000;
        gui.fill(leftPos + 100, topPos + 66 - fuelHeight, leftPos + 112, topPos + 66, 0xFFFF8C00);

        String status = menu.isStructureValid() ? "Structure: Valid" : "Structure: Incomplete";
        int statusColor = menu.isStructureValid() ? 0x00AA00 : 0xAA0000;
        gui.drawString(font, status, leftPos + 7, topPos + 75, statusColor, false);

        renderTooltip(gui, mouseX, mouseY);
    }
}
