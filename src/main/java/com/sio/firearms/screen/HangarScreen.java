package com.sio.firearms.screen;

import com.sio.firearms.menu.HangarMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class HangarScreen extends AbstractContainerScreen<HangarMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/hangar_controller.png");

    public HangarScreen(HangarMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 200;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Energy bar (x=152, y=18..82, width=12)
        int maxE = menu.getMaxEnergy();
        if (maxE > 0 && menu.getEnergyStored() > 0) {
            int barH = menu.getEnergyStored() * 64 / maxE;
            gui.fill(leftPos + 152, topPos + 82 - barH, leftPos + 164, topPos + 82, 0xFFCC0000);
        }

        // Progress bar (x=71, y=56, width=34)
        int prog = menu.getBuildProgress();
        if (prog > 0) {
            int w = prog * 34 / com.sio.firearms.block.HangarControllerBlockEntity.BUILD_TICKS;
            gui.blit(TEXTURE, leftPos + 71, topPos + 56, 176, 0, w, 16);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        // Structure status text
        Component status = menu.isStructureValid()
                ? Component.literal("Structure: OK").withColor(0xFF00CC00)
                : Component.literal("Structure: Invalid").withColor(0xFFCC0000);
        gui.drawString(font, status, leftPos + 8, topPos + 60, 0xFFFFFF, false);

        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        // Energy bar tooltip
        if (mx >= leftPos + 152 && mx < leftPos + 165 && my >= topPos + 18 && my <= topPos + 82) {
            gui.renderTooltip(font,
                    Component.literal("Energy: " + menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"),
                    mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}