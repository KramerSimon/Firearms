package com.sio.firearms.screen;

import com.sio.firearms.menu.GasCentrifugeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class GasCentrifugeScreen extends AbstractContainerScreen<GasCentrifugeMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/gas_centrifuge.png");

    public GasCentrifugeScreen(GasCentrifugeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    private static String fluidName(int fluidId) {
        if (fluidId <= 0) return null;
        var fluid = BuiltInRegistries.FLUID.byId(fluidId);
        if (fluid == null || fluid == Fluids.EMPTY) return null;
        return new FluidStack(fluid, 1).getHoverName().getString();
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Energy bar at x=8, top=14, bottom=66 (52 pixels tall)
        int maxE = menu.getMaxEnergy();
        if (maxE > 0) {
            int h = menu.getEnergyStored() * 52 / maxE;
            gui.fill(leftPos + 8, topPos + 66 - h, leftPos + 20, topPos + 66, 0xFFCC0000);
        }

        // UF6 input bar at x=30, top=14, bottom=66
        int maxFI = menu.getFluidInMax();
        if (maxFI > 0 && menu.getFluidIn() > 0) {
            int h = menu.getFluidIn() * 52 / maxFI;
            gui.fill(leftPos + 30, topPos + 66 - h, leftPos + 42, topPos + 66, 0xFF90EE90);
        }

        // Progress arrow at x=76, y=35, 24px wide
        int maxP = menu.getMaxProgress();
        if (maxP > 0 && menu.getProgress() > 0) {
            int w = menu.getProgress() * 24 / maxP;
            gui.fill(leftPos + 76, topPos + 35, leftPos + 76 + w, topPos + 50, 0xFFFFFF00);
        }

        // Enriched UF6 output bar at x=118, top=14, bottom=66
        int maxO1 = menu.getFluidOut1Max();
        if (maxO1 > 0 && menu.getFluidOut1() > 0) {
            int h = menu.getFluidOut1() * 52 / maxO1;
            gui.fill(leftPos + 118, topPos + 66 - h, leftPos + 130, topPos + 66, 0xFF00FF7F);
        }

        // Depleted UF6 output bar at x=148, top=14, bottom=66
        int maxO2 = menu.getFluidOut2Max();
        if (maxO2 > 0 && menu.getFluidOut2() > 0) {
            int h = menu.getFluidOut2() * 52 / maxO2;
            gui.fill(leftPos + 148, topPos + 66 - h, leftPos + 160, topPos + 66, 0xFF808080);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);
        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        if (mx >= leftPos + 8 && mx < leftPos + 20 && my >= topPos + 14 && my <= topPos + 66) {
            gui.renderTooltip(font,
                    Component.literal("Energy: " + menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"),
                    mx, my);
            return;
        }
        if (mx >= leftPos + 30 && mx < leftPos + 42 && my >= topPos + 14 && my <= topPos + 66) {
            String text;
            int amt = menu.getFluidIn();
            if (amt == 0) {
                text = "UF6 In: Empty";
            } else {
                String name = fluidName(menu.getFluidInTypeId());
                text = "UF6 In: " + (name != null ? name + " " : "") + amt + "/" + menu.getFluidInMax() + " mB";
            }
            gui.renderTooltip(font, Component.literal(text), mx, my);
            return;
        }
        if (mx >= leftPos + 76 && mx < leftPos + 100 && my >= topPos + 35 && my <= topPos + 50) {
            gui.renderTooltip(font,
                    Component.literal("Progress: " + menu.getProgress() + " / " + menu.getMaxProgress() + " ticks"),
                    mx, my);
            return;
        }
        if (mx >= leftPos + 118 && mx < leftPos + 130 && my >= topPos + 14 && my <= topPos + 66) {
            String text;
            int amt = menu.getFluidOut1();
            if (amt == 0) {
                text = "Enriched UF6: Empty";
            } else {
                String name = fluidName(menu.getFluidOut1TypeId());
                text = "Enriched UF6: " + (name != null ? name + " " : "") + amt + "/" + menu.getFluidOut1Max() + " mB";
            }
            gui.renderTooltip(font, Component.literal(text), mx, my);
            return;
        }
        if (mx >= leftPos + 148 && mx < leftPos + 160 && my >= topPos + 14 && my <= topPos + 66) {
            String text;
            int amt = menu.getFluidOut2();
            if (amt == 0) {
                text = "Depleted UF6: Empty";
            } else {
                String name = fluidName(menu.getFluidOut2TypeId());
                text = "Depleted UF6: " + (name != null ? name + " " : "") + amt + "/" + menu.getFluidOut2Max() + " mB";
            }
            gui.renderTooltip(font, Component.literal(text), mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
