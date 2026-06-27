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
            gui.blit(TEXTURE, leftPos + 79, topPos + 34 + 14 - burnProgress, 176, 14 - burnProgress, 14, burnProgress);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int barHeight = menu.getEnergyStored() * 52 / maxEnergy;
            gui.fill(leftPos + 150, topPos + 66 - barHeight, leftPos + 162, topPos + 66, 0xFFCC0000);
        }

        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        // Burn indicator (14×14 at 79,34)
        if (mx >= leftPos + 79 && mx < leftPos + 93 && my >= topPos + 34 && my < topPos + 48) {
            int maxBurn = menu.getMaxBurnTime();
            if (maxBurn > 0) {
                gui.renderTooltip(font,
                        Component.literal("Burn Time: " + menu.getBurnTime() + " / " + maxBurn + " ticks"),
                        mx, my);
                return;
            }
        }
        // Energy bar (150–162, top at 14, bottom at 66)
        if (mx >= leftPos + 150 && mx < leftPos + 162 && my >= topPos + 14 && my <= topPos + 66) {
            gui.renderTooltip(font,
                    Component.literal("Energy: " + menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"),
                    mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
