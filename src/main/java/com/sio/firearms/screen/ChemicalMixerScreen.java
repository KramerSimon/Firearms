package com.sio.firearms.screen;

import com.sio.firearms.menu.ChemicalMixerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChemicalMixerScreen extends AbstractContainerScreen<ChemicalMixerMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/chemical_mixer.png");

    public ChemicalMixerScreen(ChemicalMixerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int maxP = menu.getMaxProgress();
        if (maxP > 0 && menu.getProgress() > 0) {
            int w = menu.getProgress() * 24 / maxP;
            gui.blit(TEXTURE, leftPos + 75, topPos + 35, 176, 0, w, 16);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        // energy bar (red)
        int maxE = menu.getMaxEnergy();
        if (maxE > 0) {
            int h = menu.getEnergyStored() * 52 / maxE;
            gui.fill(leftPos + 8, topPos + 66 - h, leftPos + 20, topPos + 66, 0xFFCC0000);
        }

        // fluid input bar (blue)
        int maxFI = menu.getFluidInMax();
        if (maxFI > 0 && menu.getFluidInAmount() > 0) {
            int h = menu.getFluidInAmount() * 52 / maxFI;
            gui.fill(leftPos + 27, topPos + 66 - h, leftPos + 39, topPos + 66, 0xFF4080FF);
        }

        // fluid output bar (yellow-green)
        int maxFO = menu.getFluidOutMax();
        if (maxFO > 0 && menu.getFluidOutAmount() > 0) {
            int h = menu.getFluidOutAmount() * 52 / maxFO;
            gui.fill(leftPos + 141, topPos + 66 - h, leftPos + 153, topPos + 66, 0xFFCCCC00);
        }

        renderTooltip(gui, mouseX, mouseY);
    }
}
