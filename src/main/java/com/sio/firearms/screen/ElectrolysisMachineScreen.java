package com.sio.firearms.screen;

import com.sio.firearms.menu.ElectrolysisMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class ElectrolysisMachineScreen extends AbstractContainerScreen<ElectrolysisMachineMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/electrolysis_machine.png");

    public ElectrolysisMachineScreen(ElectrolysisMachineMenu menu, Inventory playerInventory, Component title) {
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
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int maxE = menu.getMaxEnergy();
        if (maxE > 0) {
            int h = menu.getEnergyStored() * 52 / maxE;
            gui.fill(leftPos + 8, topPos + 66 - h, leftPos + 20, topPos + 66, 0xFFCC0000);
        }

        int maxFI = menu.getFluidInMax();
        if (maxFI > 0 && menu.getFluidIn() > 0) {
            int h = menu.getFluidIn() * 52 / maxFI;
            gui.fill(leftPos + 25, topPos + 66 - h, leftPos + 37, topPos + 66, 0xFF3399FF);
        }

        int maxP = menu.getMaxProgress();
        if (maxP > 0 && menu.getProgress() > 0) {
            int w = menu.getProgress() * 22 / maxP;
            gui.fill(leftPos + 102, topPos + 35, leftPos + 102 + w, topPos + 50, 0xFFFFFF00);
        }

        int maxO1 = menu.getFluidOut1Max();
        if (maxO1 > 0 && menu.getFluidOut1() > 0) {
            int h = menu.getFluidOut1() * 52 / maxO1;
            gui.fill(leftPos + 130, topPos + 66 - h, leftPos + 142, topPos + 66, 0xFF87CEEB);
        }

        int maxO2 = menu.getFluidOut2Max();
        if (maxO2 > 0 && menu.getFluidOut2() > 0) {
            int h = menu.getFluidOut2() * 52 / maxO2;
            gui.fill(leftPos + 148, topPos + 66 - h, leftPos + 160, topPos + 66, 0xFFB0E0E6);
        }

        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        // Energy bar (8–20, top 14, bottom 66)
        if (mx >= leftPos + 8 && mx < leftPos + 20 && my >= topPos + 14 && my <= topPos + 66) {
            gui.renderTooltip(font,
                    Component.literal("Energy: " + menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"),
                    mx, my);
            return;
        }
        // Water input bar (25–37, top 14, bottom 66)
        if (mx >= leftPos + 25 && mx < leftPos + 37 && my >= topPos + 14 && my <= topPos + 66) {
            String text;
            int amt = menu.getFluidIn();
            if (amt == 0) {
                text = "Input: Empty";
            } else {
                String name = fluidName(menu.getFluidInTypeId());
                text = "Input: " + (name != null ? name + " " : "") + amt + "/" + menu.getFluidInMax() + " mB";
            }
            gui.renderTooltip(font, Component.literal(text), mx, my);
            return;
        }
        // Progress arrow (102–124, 35–50)
        if (mx >= leftPos + 102 && mx < leftPos + 124 && my >= topPos + 35 && my <= topPos + 50) {
            gui.renderTooltip(font,
                    Component.literal("Progress: " + menu.getProgress() + " / " + menu.getMaxProgress() + " ticks"),
                    mx, my);
            return;
        }
        // Output tank 1 (130–142, top 14, bottom 66)
        if (mx >= leftPos + 130 && mx < leftPos + 142 && my >= topPos + 14 && my <= topPos + 66) {
            String text;
            int amt = menu.getFluidOut1();
            if (amt == 0) {
                text = "Output 1: Empty";
            } else {
                String name = fluidName(menu.getFluidOut1TypeId());
                text = "Output 1: " + (name != null ? name + " " : "") + amt + "/" + menu.getFluidOut1Max() + " mB";
            }
            gui.renderTooltip(font, Component.literal(text), mx, my);
            return;
        }
        // Output tank 2 (148–160, top 14, bottom 66)
        if (mx >= leftPos + 148 && mx < leftPos + 160 && my >= topPos + 14 && my <= topPos + 66) {
            String text;
            int amt = menu.getFluidOut2();
            if (amt == 0) {
                text = "Output 2: Empty";
            } else {
                String name = fluidName(menu.getFluidOut2TypeId());
                text = "Output 2: " + (name != null ? name + " " : "") + amt + "/" + menu.getFluidOut2Max() + " mB";
            }
            gui.renderTooltip(font, Component.literal(text), mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
