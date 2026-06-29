package com.sio.firearms.screen;

import com.sio.firearms.menu.SpentFuelStorageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SpentFuelStorageScreen extends AbstractContainerScreen<SpentFuelStorageMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/spent_fuel_storage.png");

    public SpentFuelStorageScreen(SpentFuelStorageMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Radiation warning indicator (red glow when rods present)
        if (menu.getRodCount() > 0) {
            gui.fill(leftPos + 8, topPos + 8, leftPos + 28, topPos + 18, 0x88FF0000);
        }

        // Structure status indicator
        if (menu.isStructureValid()) {
            gui.fill(leftPos + 8, topPos + 20, leftPos + 28, topPos + 28, 0x8800FF00);
        } else {
            gui.fill(leftPos + 8, topPos + 20, leftPos + 28, topPos + 28, 0x88FF6600);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);
        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        if (mx >= leftPos + 8 && mx < leftPos + 28 && my >= topPos + 8 && my < topPos + 18) {
            gui.renderTooltip(font,
                    Component.literal("Rods stored: " + menu.getRodCount() + "/9 — emitting radiation!"),
                    mx, my);
            return;
        }
        if (mx >= leftPos + 8 && mx < leftPos + 28 && my >= topPos + 20 && my < topPos + 28) {
            String msg = menu.isStructureValid() ? "Structure: VALID" : "Structure: INCOMPLETE (5x5x4 required)";
            gui.renderTooltip(font, Component.literal(msg), mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
