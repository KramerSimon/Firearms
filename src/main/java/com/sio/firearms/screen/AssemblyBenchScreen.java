package com.sio.firearms.screen;

import com.sio.firearms.menu.AssemblyBenchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AssemblyBenchScreen extends AbstractContainerScreen<AssemblyBenchMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/assembly_bench.png");

    public AssemblyBenchScreen(AssemblyBenchMenu menu, Inventory playerInventory, Component title) {
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
            gui.blit(TEXTURE, leftPos + 88, topPos + 35, 176, 0, arrowWidth, 16);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int barH = menu.getEnergyStored() * 52 / maxEnergy;
            gui.fill(leftPos + 152, topPos + 62 - barH, leftPos + 164, topPos + 62, 0xFFCC0000);
        }

        gui.drawString(font, menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE",
                leftPos + 7, topPos + 75, 0x404040, false);

        renderTooltip(gui, mouseX, mouseY);
    }
}
