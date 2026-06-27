package com.sio.firearms.screen;

import com.sio.firearms.menu.HeatTreatmentFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class HeatTreatmentFurnaceScreen extends AbstractContainerScreen<HeatTreatmentFurnaceMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/heat_treatment_furnace.png");

    public HeatTreatmentFurnaceScreen(HeatTreatmentFurnaceMenu menu, Inventory playerInventory, Component title) {
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
            gui.blit(TEXTURE, leftPos + 79, topPos + 35, 176, 0, arrowWidth, 16);
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
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        // Progress arrow (79–103, 35–51)
        if (mx >= leftPos + 79 && mx < leftPos + 103 && my >= topPos + 35 && my < topPos + 51) {
            gui.renderTooltip(font,
                    Component.literal("Progress: " + menu.getProgress() + " / " + menu.getMaxProgress() + " ticks"),
                    mx, my);
            return;
        }
        // Energy bar (152–164, top 10, bottom 62)
        if (mx >= leftPos + 152 && mx < leftPos + 164 && my >= topPos + 10 && my <= topPos + 62) {
            gui.renderTooltip(font,
                    Component.literal("Energy: " + menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"),
                    mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
