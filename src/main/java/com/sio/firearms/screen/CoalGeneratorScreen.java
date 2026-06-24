package com.sio.firearms.screen;

import com.sio.firearms.menu.CoalGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CoalGeneratorScreen extends AbstractContainerScreen<CoalGeneratorMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/coal_generator.png");

    public CoalGeneratorScreen(CoalGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int maxBurn = menu.getMaxBurnTime();
        if (maxBurn > 0) {
            int burnProgress = menu.getBurnTime() * 14 / maxBurn;
            gui.blit(TEXTURE, leftPos + 56, topPos + 36 + 14 - burnProgress, 176, 14 - burnProgress, 14, burnProgress);
        }

        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int barHeight = menu.getEnergyStored() * 52 / maxEnergy;
            gui.fill(leftPos + 152, topPos + 10 + 52 - barHeight, leftPos + 164, topPos + 10 + 52, 0xFFCC0000);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);
        renderTooltip(gui, mouseX, mouseY);

        String energyText = menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE";
        gui.drawString(font, energyText, leftPos + 8, topPos + 66, 0x404040, false);
    }
}
