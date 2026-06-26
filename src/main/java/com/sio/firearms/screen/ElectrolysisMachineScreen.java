package com.sio.firearms.screen;

import com.sio.firearms.menu.ElectrolysisMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ElectrolysisMachineScreen extends AbstractContainerScreen<ElectrolysisMachineMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/electrolysis_machine.png");

    public ElectrolysisMachineScreen(ElectrolysisMachineMenu menu, Inventory playerInventory, Component title) {
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

        // Energy bar (red), x=8, bottom at y=66, height up to 52px
        int maxE = menu.getMaxEnergy();
        if (maxE > 0) {
            int h = menu.getEnergyStored() * 52 / maxE;
            gui.fill(leftPos + 8, topPos + 66 - h, leftPos + 20, topPos + 66, 0xFFCC0000);
        }

        // Fluid input bar (blue = water), x=25
        int maxFI = menu.getFluidInMax();
        if (maxFI > 0 && menu.getFluidIn() > 0) {
            int h = menu.getFluidIn() * 52 / maxFI;
            gui.fill(leftPos + 25, topPos + 66 - h, leftPos + 37, topPos + 66, 0xFF3399FF);
        }

        // Progress arrow, x=102 → 124, y=35
        int maxP = menu.getMaxProgress();
        if (maxP > 0 && menu.getProgress() > 0) {
            int w = menu.getProgress() * 22 / maxP;
            gui.fill(leftPos + 102, topPos + 35, leftPos + 102 + w, topPos + 50, 0xFFFFFF00);
        }

        // Output tank 1 (hydrogen/fluorine/chlorine — light blue), x=130
        int maxO1 = menu.getFluidOut1Max();
        if (maxO1 > 0 && menu.getFluidOut1() > 0) {
            int h = menu.getFluidOut1() * 52 / maxO1;
            gui.fill(leftPos + 130, topPos + 66 - h, leftPos + 142, topPos + 66, 0xFF87CEEB);
        }

        // Output tank 2 (oxygen/nitrate/hydrogen — pale blue), x=148
        int maxO2 = menu.getFluidOut2Max();
        if (maxO2 > 0 && menu.getFluidOut2() > 0) {
            int h = menu.getFluidOut2() * 52 / maxO2;
            gui.fill(leftPos + 148, topPos + 66 - h, leftPos + 160, topPos + 66, 0xFFB0E0E6);
        }

        renderTooltip(gui, mouseX, mouseY);
    }
}
