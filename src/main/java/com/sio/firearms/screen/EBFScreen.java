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
        this.imageWidth  = 176;
        this.imageHeight = 180;
        this.inventoryLabelY = 96;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && menu.getProgress() > 0) {
            int arrowWidth = menu.getProgress() * 22 / maxProgress;
            gui.blit(TEXTURE, leftPos + 79, topPos + 30, 176, 0, arrowWidth, 16);
        }
    }

    private String coilName(int temp) {
        return switch (temp) {
            case 800  -> "Kanthal";
            case 1200 -> "Nichrome";
            case 2000 -> "Tungsten";
            default   -> "None";
        };
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int barH = menu.getEnergyStored() * 52 / maxEnergy;
            gui.fill(leftPos + 7, topPos + 66 - barH, leftPos + 19, topPos + 66, 0xFFCC0000);
        }

        int temp = menu.getCoilTemperature();
        if (temp > 0) {
            int barH = Math.min(temp * 52 / 2000, 52);
            gui.fill(leftPos + 22, topPos + 66 - barH, leftPos + 34, topPos + 66, 0xFFFF6600);
        }

        String structStr = menu.isStructureValid() ? "Structure: OK" : "Structure: Invalid";
        int structColor = menu.isStructureValid() ? 0x00AA00 : 0xCC3300;
        gui.drawString(font, structStr, leftPos + 7, topPos + 72, structColor, false);
        gui.drawString(font, "Temp: " + temp + "°C", leftPos + 7, topPos + 82, temp > 0 ? 0xFF6600 : 0x666666, false);
        gui.drawString(font, "Coil: " + coilName(temp), leftPos + 7, topPos + 92, 0x404040, false);

        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        // Progress arrow (79–101, 30–46)
        if (mx >= leftPos + 79 && mx < leftPos + 101 && my >= topPos + 30 && my < topPos + 46) {
            gui.renderTooltip(font,
                    Component.literal("Progress: " + menu.getProgress() + " / " + menu.getMaxProgress() + " ticks"),
                    mx, my);
            return;
        }
        // Energy bar (7–19, top 14, bottom 66)
        if (mx >= leftPos + 7 && mx < leftPos + 19 && my >= topPos + 14 && my <= topPos + 66) {
            gui.renderTooltip(font,
                    Component.literal("Energy: " + menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"),
                    mx, my);
            return;
        }
        // Temperature bar (22–34, top 14, bottom 66)
        if (mx >= leftPos + 22 && mx < leftPos + 34 && my >= topPos + 14 && my <= topPos + 66) {
            gui.renderTooltip(font,
                    Component.literal("Temperature: " + menu.getCoilTemperature() + "°C"),
                    mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
